package com.ryvk.employeetracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Handler handler;
    private Runnable updateTimeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadClock();
        if(InternetChecker.checkInternet(HomeActivity.this)){
            loadFunctions();
        }else{
            Intent i = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(i);
        }

    }

    public static String convertToLocalTime(String utcDateTime) {
        ZonedDateTime utcZonedDateTime = ZonedDateTime.parse(utcDateTime);
        ZonedDateTime utcPlus530 = utcZonedDateTime.withZoneSameInstant(ZoneOffset.ofHoursMinutes(5, 30));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return utcPlus530.format(formatter);
    }

    private void loadClock(){
        //time display setup
        TextView timeTextView = findViewById(R.id.textView4);
        handler = new Handler();
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Get the current time
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
                String currentTime = sdf.format(new Date());

                // Set the current time to the TextView
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeTextView.setText(currentTime);
                    }
                });

                // Re-run the Runnable every second (1000 milliseconds)
                handler.postDelayed(this, 1000);
            }
        };

        // Start the time update
        handler.post(updateTimeRunnable);
    }

    private void loadFunctions(){

        FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        Button clockBtn = findViewById(R.id.button3);
        TextView nameField = findViewById(R.id.textView5);

        if(loggedUser != null){
            nameField.setText(loggedUser.getDisplayName());
            updateClockButton();

            clockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient okHttpClient = new OkHttpClient();

                            Gson gson = new Gson();
                            User user = new User();
                            user.setName(loggedUser.getDisplayName());
                            user.setEmail(loggedUser.getEmail());

                            RequestBody requestBody = RequestBody.create(gson.toJson(user),MediaType.get("application/json"));

                            Request request = new Request.Builder()
                                    .url("http://"+MainActivity.server_ip_port+"/api/clock/")
                                    .post(requestBody)
                                    .build();

                            try {
                                Response response = okHttpClient.newCall(request).execute();
                                String responseText = response.body().string();
                                Log.i(TAG,responseText);

                                JsonObject responseObject = gson.fromJson(responseText, JsonObject.class);
                                String message = responseObject.get("message").getAsString();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(response.isSuccessful()){
                                            Toast.makeText(HomeActivity.this,responseObject.get("message").getAsString(),Toast.LENGTH_LONG).show();
                                            updateClockButton();
                                        }else {
                                            Toast.makeText(HomeActivity.this,"Server Error occured",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();

                }
            });

        }else{
            nameField.setText("No user");
        }

        Button logoutBtn = findViewById(R.id.button4);
        if(loggedUser != null){
            logoutBtn.setVisibility(View.VISIBLE);
            logoutBtn.setText(R.string.home_btn2);
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loggedUser != null){

                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.web_server_client_id))
                            .requestEmail()
                            .build();

                    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(HomeActivity.this, gso);
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    mGoogleSignInClient.signOut();

                    Log.i(TAG, "onClick: Logout, user logged out------------------------");
                }else{
                    Log.i(TAG, "onClick: Logout . NO user ----------------------------------");
                }
                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void updateClockButton (){

        FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        Button clockBtn = findViewById(R.id.button3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Gson gson = new Gson();

                Request request = new Request.Builder()
                        .url("http://"+MainActivity.server_ip_port+"/api/clock/"+loggedUser.getEmail())
                        .build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String responseText = response.body().string();
                    Log.i(TAG,responseText);
                    JsonObject responseObject = gson.fromJson(responseText, JsonObject.class);
                    String message = responseObject.get("message").getAsString();

//                    Log.i(TAG, "update clock btn: " +responseObject.get("data").getAsJsonObject().get("email").getAsString());

                    TextView clockInText = findViewById(R.id.textView6);
                    TextView clockOutText = findViewById(R.id.textView7);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(response.isSuccessful()){
                                if(message.equals("noClockIn")){
                                    Log.i(TAG, "clock btn update: no clock in");
                                }else if(message.equals("clockedIn")){

                                    JsonObject data = responseObject.get("data").getAsJsonObject();
                                    String clockInDatetime = "Clocked In: "+ convertToLocalTime(data.get("clockIn").getAsString());
                                    clockInText.setText(clockInDatetime);
                                    clockInText.setVisibility(View.VISIBLE);

                                    clockBtn.setText(R.string.home_btn1_text2);

                                }else if(message.equals("clockedOut")){
                                    JsonObject data = responseObject.get("data").getAsJsonObject();
                                    String clockInDatetime = "Clocked In: "+ convertToLocalTime(data.get("clockIn").getAsString());
                                    String clockOutDatetime = "Clocked Out: "+ convertToLocalTime(data.get("clockOut").getAsString());
                                    clockInText.setText(clockInDatetime);
                                    clockOutText.setText(clockOutDatetime);
                                    clockInText.setVisibility(View.VISIBLE);
                                    clockOutText.setVisibility(View.VISIBLE);

                                    clockBtn.setText(R.string.home_btn1_text3);
                                }else{
                                    Toast.makeText(HomeActivity.this,"Error occured",Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(HomeActivity.this,"Server Error occured",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
    }
}