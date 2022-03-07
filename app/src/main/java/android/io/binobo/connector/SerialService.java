package android.io.binobo.connector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SerialService extends Service implements SerialInputOutputManager.Listener {

    class SerialBinder extends Binder {
        SerialService getService() { return SerialService.this; }
    }

    private final Handler mainLooper;
    private final IBinder binder;

    public SerialService() {
        mainLooper = new Handler(Looper.getMainLooper());
        binder = new SerialBinder();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return super.onStartCommand(intent, flags, startId);
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return super.onStartCommand(intent, flags, startId);
        }

        UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            SerialInputOutputManager serialInputOutputManager = new SerialInputOutputManager(port, this);
            serialInputOutputManager.start();

            Toast.makeText(this, "Connection successfully established!", Toast.LENGTH_LONG).show();

            Globals.connection = connection;
            Globals.port = port;
            Globals.usbSerialDriver = driver;
            Globals.serialIOManager = serialInputOutputManager;
        } catch (IOException e) {
            Toast.makeText(this, "Error occurred!", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNewData(byte[] data) {
        bufferData(data);
    }

    synchronized private void bufferData(byte[] data) {
        String dataIn = new String(data);

        for (char c : dataIn.toCharArray()) {
            Globals.dataBuffer.append(c);

            if (c == '\n'){
                String dataAsString = Globals.dataBuffer.toString().trim();

                Globals.configState = Configuration.getState(dataAsString);
                Globals.uartData.add(dataAsString);

                if (Globals.configState == Configuration.State.HIT_ENTER_TO_START) {
                    try {
                        Globals.port.write("\r".getBytes(StandardCharsets.UTF_8), 100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (dataAsString.startsWith("[1]")) Globals.SSID = dataAsString.substring(3);
                else if (dataAsString.startsWith("[2]")) Globals.PASSWORD = dataAsString.substring(3);
                else if (dataAsString.startsWith("[3]")) Globals.TOKEN = dataAsString.substring(3);

                Globals.dataBuffer = new StringBuilder();
            }
        }
    }

    @Override
    public void onRunError(Exception e) {
        //Toast.makeText(this, "Error occurred!", Toast.LENGTH_LONG).show();
        stopSelf();
    }
}
