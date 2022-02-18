package android.io.binobo.connector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
            switch(Globals.configState){
                case LOCAL_DATA_FOUND: {
                    Intent localDataIntent = new Intent(MainActivity.this, LocalDataActivity.class);
                    startActivity(localDataIntent);
                    break;
                }
                case WIFI_CONFIG_SSID:{
                    Intent wifiConfigIntent = new Intent(MainActivity.this, WifiConfigActivity.class);
                    break;
                }
                default: break;
            }
        });

        mButton_continue.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
            mTextView_waiting.setText("Device attached!");
            mButton_continue.setVisibility(View.VISIBLE);

            Intent serialServiceIntent = new Intent(this, SerialService.class);
            startService(serialServiceIntent);
        }
    }
}