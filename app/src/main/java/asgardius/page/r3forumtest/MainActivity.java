package asgardius.page.r3forumtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button signUp = (Button)findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //buttonaction
                SignUpForm();
            }
        });
    }

    private void SignUpForm () {

        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);

    }
}