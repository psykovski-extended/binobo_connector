package android.io.binobo.connector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenActivity extends AppCompatActivity {

    private boolean wasButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_activity);

        EditText token = findViewById(R.id.token);
        Button validate = findViewById(R.id.validate);

        validate.setOnClickListener(v -> {
            if(!wasButtonPressed) {
                wasButtonPressed = true;
                try {
                    Globals.port.write((token.getText().toString() + "\r").getBytes(StandardCharsets.UTF_8), 500);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                runOnUiThread(() -> {
                    validate.setText("Validating...");
                });

                if(isNetworkAvailable()) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.setHostnameVerifier$okhttp((hostname, session) -> true); // this is not an error..
                    OkHttpClient client = builder.build();
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("https://www.binobo.io/roboData/rest_api/validate_token").newBuilder();
                    urlBuilder.addQueryParameter("token", token.getText().toString());
                    String url = urlBuilder.build().toString();

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(() -> {
                                Toast.makeText(TokenActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                token.setText(e.toString());
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            runOnUiThread(() -> {
                                Toast.makeText(TokenActivity.this, "success", Toast.LENGTH_SHORT).show();
                            });
                            String jsonData = response.body().string();
                            JSONObject json;
                            try {
                                json = new JSONObject(jsonData);
                                if(json.getString("status").equals("SUCCESS")) {
                                    try {
                                        Globals.port.write((token.getText().toString() + "\r").getBytes(StandardCharsets.UTF_8), 500);
                                    } catch (IOException ignored) {}
                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(TokenActivity.this, CalibrationActivity.class);
                                        startActivity(intent);
                                    });
                                } else {
                                    wasButtonPressed = false;
                                    runOnUiThread(() -> {
                                        Toast.makeText(TokenActivity.this, "Token invalid, please try again!", Toast.LENGTH_SHORT).show();
                                        token.setTextColor(Color.parseColor("#ff6961"));
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = networkInfo != null && networkInfo.isConnected();
        if(!isAvailable) {
            Toast.makeText(this, "Network unavailable!", Toast.LENGTH_LONG).show();
        }
        return isAvailable;

    }

}
