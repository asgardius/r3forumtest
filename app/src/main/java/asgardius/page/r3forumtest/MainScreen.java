package asgardius.page.r3forumtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainScreen extends AppCompatActivity {
    String username;
    HttpsURLConnection myConnection;
    String myData;
    URL endpoint;
    boolean success;
    BufferedReader br;
    StringBuilder sb;
    JSONObject jsonObj;
    SQLiteDatabase db;
    MyDbHelper dbHelper;
    TextView id ,email, nacionalidad, nacimiento;
    Button logout, notification;
    Uri crashsound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        username = getIntent().getStringExtra("username");
        id = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        nacionalidad = (TextView) findViewById(R.id.nacionalidad);
        nacimiento = (TextView) findViewById(R.id.nacimiento);
        logout = (Button)findViewById(R.id.logout);
        notification = (Button)findViewById(R.id.notification);
        dbHelper = new MyDbHelper(this);
        crashsound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.crash);
        id.setText(username);
        Thread login = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    endpoint = new URL("https://desktop.asgardius.company/test/restful/items/read.php");
                    myConnection = (HttpsURLConnection) endpoint.openConnection();
                    myConnection.setRequestProperty("User-Agent", "r3-forum-test");
                    myConnection.setRequestMethod("POST");
                    // Create the data
                    myData = "{\n" +
                            "\"id\": \""+username+"\"\n" +
                            "}";
                    // Enable writing
                    myConnection.setDoOutput(true);
                    // Write the data
                    myConnection.getOutputStream().write(myData.getBytes());
                    System.out.println(myConnection.getResponseCode());
                    if (myConnection.getResponseCode() == 200) {
                        success = true;
                        br = new BufferedReader(new InputStreamReader((myConnection.getInputStream())));
                        sb = new StringBuilder();
                        String output;
                        while ((output = br.readLine()) != null) {
                            sb.append(output);
                        }
                        System.out.println(sb.toString());
                        jsonObj = new JSONObject(sb.toString());
                    } else {
                        success = false;
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Test
                            if (success) {
                                Toast.makeText(getApplicationContext(), "Credenciales correctas", Toast.LENGTH_SHORT).show();
                                try {
                                    email.setText((String) jsonObj.getJSONArray("items").getJSONObject(0).getString("email"));
                                    nacionalidad.setText((String) jsonObj.getJSONArray("items").getJSONObject(0).getString("country"));
                                    nacimiento.setText((String) jsonObj.getJSONArray("items").getJSONObject(0).getString("birthdate"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Conexión fallida", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(getApplicationContext(),getResources().getString(R.string.media_list_fail), Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        });
        login.start();
        notification.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

                    NotificationChannel channel= new NotificationChannel("Test","Test Notification",NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager manager =getSystemService(NotificationManager.class);
                    AudioAttributes audio = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    channel.setSound(crashsound, audio);
                    manager.createNotificationChannel(channel);
                }
                String message="Su nave ha chocado con un asteroide";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainScreen.this,"Test");
                builder.setContentTitle("Prueba");
                builder.setContentText(message);
                builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                builder.setAutoCancel(true);
                builder.setSound(crashsound);
                NotificationManagerCompat managerCompat=NotificationManagerCompat.from(MainScreen.this);
                managerCompat.notify(1,builder.build());
            }
        });
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                Thread logout = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            db = dbHelper.getWritableDatabase();
                            db.execSQL("DELETE FROM account");
                            db.close();
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //Test
                                    mainMenu();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Conexión fallida", Toast.LENGTH_SHORT).show();
                                }
                            });
                            //Toast.makeText(getApplicationContext(),getResources().getString(R.string.media_list_fail), Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                    }
                });
                logout.start();
            }
        });
    }

    private void mainMenu() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);

    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}