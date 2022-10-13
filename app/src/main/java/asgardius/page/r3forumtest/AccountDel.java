package asgardius.page.r3forumtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AccountDel extends AppCompatActivity {
    EditText user;
    Button delAccount;
    boolean success;
    HttpsURLConnection myConnection;
    URL endpoint;
    String myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_del);
        user = (EditText)findViewById(R.id.username);
        delAccount = (Button)findViewById(R.id.delete);

        delAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                if(user.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(),"Seleccione un usuario", Toast.LENGTH_SHORT).show();
                }else {
                    Thread login = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try  {
                                //Your code goes here
                                endpoint = new URL("https://desktop.asgardius.company/test/restful/items/delete.php");
                                myConnection = (HttpsURLConnection) endpoint.openConnection();
                                myConnection.setRequestProperty("User-Agent", "r3-forum-test");
                                myConnection.setRequestMethod("POST");
                                // Create the data
                                myData = "{\n" +
                                        "\"id\": \""+user.getText().toString().toLowerCase()+"\"\n" +
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
                                            Toast.makeText(getApplicationContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Usuario inexistente", Toast.LENGTH_SHORT).show();
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