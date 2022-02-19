package android.io.binobo.connector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private Button mButton_continue;
    private TextView mTextView_waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton_continue = findViewById(R.id.page0_continue_button);
        mTextView_waiting = findViewById(R.id.page0_autodetection);

        mButton_continue.setOnClickListener(v -> {
            runOnUiThread(() -> { // only for testing purposes
                try {
                    Toast.makeText(MainActivity.this, Globals.uartData.get(Globals.uartData.size()-1), Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            });

            switch(Globals.configState){
                case HIT_ENTER_TO_START: {
                    try {
                        Globals.port.write("\r".getBytes(StandardCharsets.UTF_8), 100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case LOCAL_DATA_FOUND: {
                    Intent localDataIntent = new Intent(MainActivity.this, LocalDataActivity.class);
                    startActivity(localDataIntent);
                    break;
                }
                case WIFI_CONFIG_SSID:{
                    Intent wifiConfigIntent = new Intent(MainActivity.this, WifiConfigActivity.class);
                    startActivity(wifiConfigIntent);
                    break;
                }
                default: break;
            }
        });

        mButton_continue.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
            mTextView_waiting.setText("Device attached!");
            mButton_continue.setVisibility(View.VISIBLE);

            try {
                Intent serialServiceIntent = new Intent(this, SerialService.class);
                startService(serialServiceIntent);
            }catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        super.onNewIntent(intent);
    }
}