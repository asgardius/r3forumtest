package asgardius.page.r3forumtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        username = getIntent().getStringExtra("username");
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
                    } else {
                        success = false;
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //Test
                            if (success) {
                                Toast.makeText(getApplicationContext(), "Credenciales correctas", Toast.LENGTH_SHORT).show();

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
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}