package asgardius.page.r3forumtest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import asgardius.page.r3forumtest.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {
    private DatePicker datePicker;
    private Calendar calendar;
    private Button dateGet;
    private int year, month, day;
    private TextView dateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }
}