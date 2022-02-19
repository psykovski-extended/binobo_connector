package android.io.binobo.connector;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LocalDataActivity extends AppCompatActivity {

    private EditText mPassword;
    private EditText mSSID;
    private EditText mToken;
    private Button mUseData;
    private Button mDoNotUse;

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
        mDoNotUse = findViewById(R.id.do_not_use_data);
    }
}
