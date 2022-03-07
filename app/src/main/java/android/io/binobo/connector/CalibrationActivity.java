package android.io.binobo.connector;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CalibrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration_activity);

        Button calibrate = findViewById(R.id.calibrate);
        calibrate.setOnClickListener(v -> {
            try {
                Globals.port.write("\r".getBytes(StandardCharsets.UTF_8), 100);
                runOnUiThread(() -> {
                    Intent intent = new Intent(CalibrationActivity.this, FinalActivity.class);
                    startActivity(intent);
                });
            } catch (IOException ignored){}
        });
    }

}
