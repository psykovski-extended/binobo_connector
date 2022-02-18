package android.io.binobo.connector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.provider.SyncStateContract;
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

    private enum QueueType {Connect, Read}

    private static class QueueItem {
        QueueType type;
        byte[] data;

        QueueItem(QueueType type, byte[] data) { this.type=type; this.data=data; }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

            port.write("\n".getBytes(StandardCharsets.UTF_8), 1); // get initial state

            Globals.connection = connection;
            Globals.port = port;
            Globals.usbSerialDriver = driver;
            Globals.serialIOManager = serialInputOutputManager;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onNewData(byte[] data) {
        bufferData(data);
    }

    synchronized private void bufferData(byte[] data) {
        Globals.dataBuffer.append(new String(data));
        if (Globals.dataBuffer.toString().contains("\n")){
            String dataAsString = Globals.dataBuffer.toString().trim();

            // only for testing purpose
            Toast.makeText(this, dataAsString, Toast.LENGTH_LONG).show();

            Globals.configState = Configuration.getState(dataAsString);
            Globals.uartData.add(dataAsString);

            if (dataAsString.startsWith("[1]")) Globals.SSID = dataAsString.substring(3);
            else if (dataAsString.startsWith("[2]")) Globals.PASSWORD = dataAsString.substring(3);
            else if (dataAsString.startsWith("[3]")) Globals.TOKEN = dataAsString.substring(3);

            Globals.dataBuffer = new StringBuilder();
        }
    }

    @Override
    public void onRunError(Exception e) {
        Toast.makeText(this, "Error occurred!", Toast.LENGTH_LONG).show();
    }
}
