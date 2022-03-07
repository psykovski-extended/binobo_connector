package android.io.binobo.connector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LocalDataActivity extends AppCompatActivity {

    private static final String TAG = LocalDataActivity.class.getSimpleName();
    private EditText mPassword;
    private EditText mSSID;
    private EditText mToken;
    private Button mUseData;
    private Button mDoNotUse;
    private Thread mThread;
    private boolean isDecisionMade = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stored_data_found_activity);

        mPassword = findViewById(R.id.password_sd);
        mSSID = findViewById(R.id.ssid_sd);
        mToken = findViewById(R.id.token_sd);

        mPassword.setText(Globals.PASSWORD);
        mSSID.setText(Globals.SSID);
        mToken.setText(Globals.TOKEN);

        mUseData = findViewById(R.id.use_date);
        mUseData.setOnClickListener(v -> {
            if(!isDecisionMade) {
                try {
                    Globals.port.write("y\r".getBytes(StandardCharsets.UTF_8), 100); // validate data --> some kind of progress-bar needed!
                    mUseData.setText("Validating data...");
                    mDoNotUse.setWidth(1);
                    mUseData.setWidth(300);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.mThread = new Thread(() -> {
                    try {
                        boolean isDoneOrHasError = false;
                        while(!isDoneOrHasError) {
                            switch(Globals.configState){
                                case WIFI_DATA_VALID:
                                case TOKEN_VALIDATING: {
                                    runOnUiThread(() -> {
                                        mSSID.setTextColor(Color.parseColor("#00A572"));
                                        mPassword.setTextColor(Color.parseColor("#00A572"));
                                    }); break;
                                }
                                case WIFI_DATA_INVALID: {
                                    isDoneOrHasError = true;
                                    runOnUiThread(() -> {
                                        try {
                                            Globals.port.write("\r".getBytes(StandardCharsets.UTF_8), 100);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent = new Intent(LocalDataActivity.this, WifiConfigActivity.class);
                                        startActivity(intent);
                                    });
                                    break;
                                }
                                case TOKEN_INVALID:
                                case TOKEN: {
                                    isDoneOrHasError = true;
                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(LocalDataActivity.this, TokenActivity.class);
                                        startActivity(intent);
                                        mThread.interrupt();
                                    });
                                    break;
                                }
                                case TOKEN_VALID:
                                case CALIBRATION:
                                case CALIBRATION_ZERO_POS: {
                                    isDoneOrHasError = true;
                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(LocalDataActivity.this, CalibrationActivity.class);
                                        startActivity(intent);
                                        mThread.interrupt();
                                    });
                                    break;
                                }
                            }
                            try {
                                Thread.sleep(1000L);
                                runOnUiThread(() -> {
                                    Toast.makeText(LocalDataActivity.this, Globals.configState.toString() + Globals.uartData.get(Globals.uartData.size()-1), Toast.LENGTH_SHORT).show();
                                });
                            } catch (InterruptedException ie) {
                                isDoneOrHasError = true;
                                runOnUiThread(() -> {
                                    Toast.makeText(LocalDataActivity.this, "Error occurred in validation-thread!", Toast.LENGTH_LONG).show();
                                    Log.e(TAG, ie.getMessage());
                                });
                            }
                        }
                    } catch (Exception ignored) {}
                });
                mThread.start();
                isDecisionMade = true;
            }
        });
        mDoNotUse = findViewById(R.id.do_not_use_data);
        mDoNotUse.setOnClickListener(v -> {
            try {
                Globals.port.write("n\r".getBytes(StandardCharsets.UTF_8), 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // goto wifi config
            Intent intent = new Intent(LocalDataActivity.this, WifiConfigActivity.class);
            startActivity(intent);
        });
    }
}
