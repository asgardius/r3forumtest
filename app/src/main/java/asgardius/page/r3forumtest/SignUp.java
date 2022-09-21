package asgardius.page.r3forumtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SignUp extends AppCompatActivity {
    Button signUp;
    EditText user, pwd, pwdc, nacion, fname, lname, mail, dated, datem, datey;
    URL endpoint;
    HttpsURLConnection myConnection;
    boolean success;
    String myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user = (EditText)findViewById(R.id.username);
        pwd = (EditText)findViewById(R.id.password);
        pwdc = (EditText)findViewById(R.id.passwordConfirm);
        nacion = (EditText)findViewById(R.id.nacionalidad);
        fname = (EditText)findViewById(R.id.firstname);
        mail = (EditText)findViewById(R.id.email);
        dated = (EditText)findViewById(R.id.Date);
        datem = (EditText)findViewById(R.id.Month);
        datey = (EditText)findViewById(R.id.Year);
        lname = (EditText)findViewById(R.id.lastname);
        signUp = (Button)findViewById(R.id.signup);

        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                if(user.getText().toString().equals("") || pwd.getText().toString().equals("") ||
                        pwdc.getText().toString().equals("") || nacion.getText().toString().equals("") ||
                        fname.getText().toString().equals("") || lname.getText().toString().equals("") ||
                        mail.getText().toString().equals("") || dated.getText().toString().equals("") ||
                        datem.getText().toString().equals("") || datey.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else if(!pwd.getText().toString().equals(pwdc.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }else {
                    Thread login = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {
                                //Your code goes here
                                endpoint = new URL("https://desktop.asgardius.company/test/restful/items/create.php");
                                myConnection = (HttpsURLConnection) endpoint.openConnection();
                                myConnection.setRequestProperty("User-Agent", "r3-forum-test");
                                myConnection.setRequestMethod("POST");
                                // Create the data
                                myData = "{\n" +
                                        "\"id\": \""+user.getText().toString()+"\",\n" +
                                        "\"firstname\": \""+fname.getText().toString()+"\",\n" +
                                        "\"lastname\":\""+lname.getText().toString()+"\",\n" +
                                        "\"email\":\""+mail.getText().toString()+"\",\n" +
                                        "\"password\": \""+pwd.getText().toString()+"\",\n" +
                                        "\"country\":\""+nacion.getText().toString()+"\",\n" +
                                        "\"birthdate\": \""+datey.getText().toString()+"-"+datem.getText().toString()+"-"+dated.getText().toString()+"\"\n" +
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
                                            Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show();
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
                                finish();
                            }
                        }
                    });

                    login.start();
                }
            }
        });
    }
}