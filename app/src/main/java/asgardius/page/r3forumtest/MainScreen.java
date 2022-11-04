package asgardius.page.r3forumtest;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainScreen extends AppCompatActivity {
    String username;
    HttpsURLConnection myConnection;
    String myData, usertype;
    URL endpoint;
    Intent intent;
    boolean success;
    BufferedReader br;
    StringBuilder sb;
    JSONObject jsonObj;
    SQLiteDatabase db;
    MyDbHelper dbHelper;
    TextView id ,email, nacionalidad, nacimiento;
    Button logout, notification, edit, delete, viewlocation;
    LinearLayout adminactions;
    Uri crashsound, fileuri;
    TextView textView;
    TelephonyManager telephonyManager;
    private Location lastLocation;
    private LocationManager locManager;
    ImageView profile;

    private final LocationListener locListener = new LocationListener() {
        public void onLocationChanged(Location loc) {
            updateLocation(loc);
        }

        public void onProviderEnabled(String provider) {
            updateLocation();
        }

        public void onProviderDisabled(String provider) {
            updateLocation();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        textView = findViewById(R.id.location);
        profile = (ImageView) findViewById(R.id.userPicture);
        /*telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE,ACCESS_FINE_LOCATION}, 100);
        } else {
            textView.setText(""+telephonyManager.getCellLocation());
        }*/
        username = getIntent().getStringExtra("username");
        id = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        nacionalidad = (TextView) findViewById(R.id.nacionalidad);
        nacimiento = (TextView) findViewById(R.id.nacimiento);
        logout = (Button)findViewById(R.id.logout);
        notification = (Button)findViewById(R.id.notification);
        adminactions = (LinearLayout) findViewById(R.id.linearLayoutAdmin);
        edit = (Button)findViewById(R.id.editaccount);
        delete = (Button)findViewById(R.id.deleteaccount);
        viewlocation = (Button)findViewById(R.id.view_location);
        dbHelper = new MyDbHelper(this);
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        startRequestingLocation();
        crashsound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.crash);
        id.setText(username);
        Thread login = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    endpoint = new URL("https://desktop.asgardius.company/test/restful/items/read.php");
                    //System.out.println(endpoint);
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
                        //System.out.println(sb.toString());
                        jsonObj = new JSONObject(sb.toString());
                    } else {
                        success = false;
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Test
                            if (success) {
                                //Toast.makeText(getApplicationContext(), "Credenciales correctas", Toast.LENGTH_SHORT).show();
                                try {
                                    email.setText((String) jsonObj.getJSONArray("items").getJSONObject(0).getString("email"));
                                    nacionalidad.setText((String) jsonObj.getJSONArray("items").getJSONObject(0).getString("country"));
                                    nacimiento.setText((String) jsonObj.getJSONArray("items").getJSONObject(0).getString("birthdate"));
                                    usertype = (String) jsonObj.getJSONArray("items").getJSONObject(0).getString("permission");
                                    if (usertype.equals("admin")) {
                                        adminactions.setVisibility(View.VISIBLE);
                                    }
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
        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                performFileSearch("Select file to upload");
            }
        });
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
        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                //Toast.makeText(getApplicationContext(),"editar", Toast.LENGTH_SHORT).show();
                accountEdit();
            }
        });
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                //Toast.makeText(getApplicationContext(),"eliminar", Toast.LENGTH_SHORT).show();
                accountDelete();
            }
        });
        viewlocation.setOnClickListener(this::viewLocation);
    }

    private void mainMenu() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);

    }

    private void accountEdit() {

        Intent intent = new Intent(this, AccountEdit.class);
        startActivity(intent);

    }

    private void accountDelete() {

        Intent intent = new Intent(this, AccountDel.class);
        startActivity(intent);

    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startRequestingLocation();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

    private void startRequestingLocation() {
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }

        final String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission);
            return;
        }

        // GPS enabled and have permission - start requesting location updates
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

    private void updateLocation() {
        // Trigger a UI update without changing the location
        updateLocation(lastLocation);
    }

    private void updateLocation(Location location) {
        boolean locationEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean waitingForLocation = locationEnabled && !validLocation(location);
        boolean haveLocation = locationEnabled && !waitingForLocation;

        if (haveLocation) {
            String newline = System.getProperty("line.separator");
            viewlocation.setEnabled(true);
            textView.setText("Ubicación obtenida");
            lastLocation = location;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    try {
                        textView.setText(""+telephonyManager.getCellLocation());
                    } catch (Exception e) {
                        textView.setText("Ubicación desactivada");
                    }
                }
        }
    }

    private boolean validLocation(Location location) {
        if (location == null) {
            return false;
        }

        // Location must be from less than 30 seconds ago to be considered valid
        if (Build.VERSION.SDK_INT < 17) {
            return System.currentTimeMillis() - location.getTime() < 30e3;
        } else {
            return SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos() < 30e9;
        }
    }

    private String getAccuracy(Location location) {
        float accuracy = location.getAccuracy();
        if (accuracy < 0.01) {
            return "?";
        } else if (accuracy > 99) {
            return "99+";
        } else {
            return String.format(Locale.US, "%2.0fm", accuracy);
        }
    }

    private String getLatitude(Location location) {
        return String.format(Locale.US, "%2.5f", location.getLatitude());
    }

    private String getDMSLatitude(Location location) {
        double val = location.getLatitude();
        return String.format(Locale.US, "%.0f° %2.0f′ %2.3f″ %s",
                Math.floor(Math.abs(val)),
                Math.floor(Math.abs(val * 60) % 60),
                (Math.abs(val) * 3600) % 60,
                val > 0 ? "N" : "S"
        );
    }

    private String getDMSLongitude(Location location) {
        double val = location.getLongitude();
        return String.format(Locale.US, "%.0f° %2.0f′ %2.3f″ %s",
                Math.floor(Math.abs(val)),
                Math.floor(Math.abs(val * 60) % 60),
                (Math.abs(val) * 3600) % 60,
                val > 0 ? "E" : "W"
        );
    }

    private String getLongitude(Location location) {
        return String.format(Locale.US, "%3.5f", location.getLongitude());
    }

    private String formatLocation(Location location, String format) {
        return MessageFormat.format(format,
                getLatitude(location), getLongitude(location));
    }

    public void viewLocation(View view) {
        if (!validLocation(lastLocation)) {
            return;
        }

        String uri = formatLocation(lastLocation, "geo:{0},{1}?q={0},{1}");

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(Intent.createChooser(intent, getString(R.string.view_location_via)));
    }

    private void performFileSearch(String messageTitle) {
        //uri = Uri.parse("content://com.android.externalstorage.documents/document/home");
        intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        //intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setType("*/*");
        ((Activity) this).startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code OPEN_DIRECTORY_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                // The document selected by the user won't be returned in the intent.
                // Instead, a URI to that document will be contained in the return intent
                // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
                if (resultData != null && resultData.getData() != null) {
                    fileuri = resultData.getData();
                    System.out.println(fileuri.toString());
                    Toast.makeText(getApplicationContext(),fileuri.toString(), Toast.LENGTH_SHORT).show();
                    //System.out.println("File selected successfully");
                    //System.out.println("content://com.android.externalstorage.documents"+file.getPath());
                } else {
                    Toast.makeText(MainScreen.this, getResources().getString(R.string.file_path_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                //System.out.println("User cancelled file browsing {}");
                finish();
            }
        }
    }
}