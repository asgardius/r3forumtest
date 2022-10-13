package asgardius.page.r3forumtest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AccountEdit extends AppCompatActivity {
    Button editUser;
    EditText user, pwd, pwdc, nacion, fname, lname, mail, dated, datem, datey;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch setAdmin;
    URL endpoint;
    HttpsURLConnection myConnection;
    boolean success;
    String myData, permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);
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
        setAdmin = (Switch)findViewById(R.id.permission);
        editUser = (Button)findViewById(R.id.edituser);

        editUser.setOnClickListener(new View.OnClickListener(){
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
                                if(setAdmin.isChecked()) {
                                    permission = "admin";
                                } else {
                                    permission = "user";
                                }
                                endpoint = new URL("https://desktop.asgardius.company/test/restful/items/update.php");
                                myConnection = (HttpsURLConnection) endpoint.openConnection();
                                myConnection.setRequestProperty("User-Agent", "r3-forum-test");
                                myConnection.setRequestMethod("POST");
                                // Create the data
                                myData = "{\n" +
                                        "\"id\": \""+user.getText().toString().toLowerCase()+"\",\n" +
                                        "\"firstname\": \""+fname.getText().toString()+"\",\n" +
                                        "\"lastname\":\""+lname.getText().toString()+"\",\n" +
                                        "\"email\":\""+mail.getText().toString().toLowerCase()+"\",\n" +
                                        "\"password\": \""+pwd.getText().toString()+"\",\n" +
                                        "\"country\":\""+nacion.getText().toString()+"\",\n" +
                                        "\"birthdate\": \""+datey.getText().toString()+"-"+datem.getText().toString()+"-"+dated.getText().toString()+"\",\n" +
                                        "\"permission\":\""+permission+"\"\n" +
                                        "}";
                                // Enable writing
                                myConnection.setDoOutput(true);
                                // Write the data
                                myConnection.getOutputStream().write(myData.getBytes());
                                System.out.println(myConnection.getResponseCode());
                                if (myConnection.getResponseCode() == 200) {
                                    success = true;
                                } else {
                                    success = false;
                                }

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        //Test
                                        if (success) {
                                            Toast.makeText(getApplicationContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Usuario invalido", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    private void mainMenu() {

        Intent intent = new Intent(this, MainScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);

    }
}