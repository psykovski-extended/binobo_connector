package android.io.binobo.connector;

import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.Vector;

public class Globals {

    public static UsbSerialDriver usbSerialDriver;
    public static UsbDeviceConnection connection;
    public static UsbSerialPort port;
    public static SerialInputOutputManager serialIOManager;
    public static final Vector<String> uartData = new Vector<>();
    public static Configuration.State configState = Configuration.State.UNKNOWN_STATE;
    public static StringBuilder dataBuffer = new StringBuilder();
    public static String SSID;
    public static String PASSWORD;
    public static String TOKEN;

}
