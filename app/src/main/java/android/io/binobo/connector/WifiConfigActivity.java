package android.io.binobo.connector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WifiConfigActivity extends AppCompatActivity {

    public static final String TAG = WifiConfigActivity.class.getSimpleName();

    private EditText mSSID;
    private EditText mPassword;
    private boolean wasButtonPressed, isInputValidOrHasError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_wifi_activity);

        mSSID = findViewById(R.id.ssid);
        mPassword = findViewById(R.id.password);

        Button connect_button = findViewById(R.id.connect_button);

        connect_button.setOnClickListener(v -> {
            if (!wasButtonPressed) {
                try {
                    Globals.port.write((mSSID.getText().toString() + "\r").getBytes(StandardCharsets.UTF_8), 500);
                    Globals.port.write((mPassword.getText().toString() + "\r").getBytes(StandardCharsets.UTF_8), 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    connect_button.setText("Validating...");
                });

                new Thread(() -> {
                    OUTER:
                    while(!isInputValidOrHasError) {
                        switch (Globals.configState) {
                            case WIFI_DATA_VALID:
                            case TOKEN: {
                                isInputValidOrHasError = true;
                                runOnUiThread(() -> {
                                    Intent intent = new Intent(WifiConfigActivity.this, TokenActivity.class);
                                    startActivity(intent);
                                });
                                break;
                            }
                            case WIFI_DATA_INVALID: {
                                wasButtonPressed = false;
                                isInputValidOrHasError = true;
                                runOnUiThread(() -> {
                                    try {
                                        Globals.port.write("\r".getBytes(StandardCharsets.UTF_8), 100);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    mSSID.setTextColor(Color.parseColor("#ff6961"));
                                    mPassword.setTextColor(Color.parseColor("#ff6961"));
                                    connect_button.setText("connect");
                                    Toast.makeText(WifiConfigActivity.this, "WIFI Data invalid! Please try again!", Toast.LENGTH_SHORT).show();
                                });
                                break OUTER;
                            }
                        }
                        try {
                            Thread.sleep(1500);
                        }catch (InterruptedException ie) {
                            wasButtonPressed = false;
                            runOnUiThread(() -> {
                                Toast.makeText(WifiConfigActivity.this, "Error occurred in validation thread! Please try again", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, ie.getMessage());
                            });
                        }
                    }
                }).start();
            }

        });
    }

}
