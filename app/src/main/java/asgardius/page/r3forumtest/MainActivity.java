package asgardius.page.r3forumtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    Button signUp, login;
    EditText user, pwd;
    URL endpoint;
    HttpsURLConnection myConnection;
    boolean success;
    String myData;
    SQLiteDatabase db;
    MyDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDbHelper(this);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            // Database Queries
            try {
                String query = "SELECT username FROM account";
                Cursor cursor = db.rawQuery(query,null);
                if (cursor.moveToNext()){
                    mainScreen(cursor.getString(0));
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        user = (EditText)findViewById(R.id.username);
        pwd = (EditText)findViewById(R.id.password);
        signUp = (Button)findViewById(R.id.signup);
        login = (Button)findViewById(R.id.login);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                SignUpForm();
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                if(user.getText().toString().equals("") || pwd.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Introduzca el usuario y contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    login(user.getText().toString().toLowerCase(), pwd.getText().toString());
                }
            }
        });
    }

    private void login (String username, String password) {

        Thread login = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here
                    endpoint = new URL("https://desktop.asgardius.company/test/restful/items/check.php");
                    myConnection = (HttpsURLConnection) endpoint.openConnection();
                    myConnection.setRequestProperty("User-Agent", "r3-forum-test");
                    myConnection.setRequestMethod("POST");
                    // Create the data
                    myData = "{\n" +
                            "\"id\": \""+username+"\",\n" +
                            "\"password\": \""+password+"\"\n" +
                            "}";
                    // Enable writing
                    myConnection.setDoOutput(true);
                    // Write the data
                    myConnection.getOutputStream().write(myData.getBytes());
                    System.out.println(myConnection.getResponseCode());
                    if (myConnection.getResponseCode() == 201) {
                        success = true;
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
                                    db = dbHelper.getWritableDatabase();
                                    db.execSQL("DELETE FROM account");
                                    db.execSQL("INSERT INTO account VALUES (\""+username+"\", \""+password+"\")");
                                    db.close();
                                    mainScreen(username);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),"Base de datos corrupta", Toast.LENGTH_SHORT).show();
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

    }

    private void mainScreen (String username) {

        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);

    }

    private void SignUpForm () {

        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);

    }
}