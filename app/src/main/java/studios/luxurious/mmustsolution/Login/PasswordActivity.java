package studios.luxurious.mmustsolution.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.GetFullNameFirst;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class PasswordActivity extends AppCompatActivity {

    TextInputEditText editTextPassword;
    Button buttonSignIn;


    Boolean firstTime = true;

    String regnumber;
    ProgressDialog progressDialog;
    SharedPref sharedPref;


    int num = 1;

    String login_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        editTextPassword = findViewById(R.id.editTextPortalPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        regnumber = getIntent().getStringExtra("regno");

        sharedPref = new SharedPref(PasswordActivity.this);

        progressDialog = new ProgressDialog(PasswordActivity.this);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = editTextPassword.getText().toString().trim();

                if (password.isEmpty()) {

                    editTextPassword.setError("Please provide a valid password");
                    editTextPassword.requestFocus();
                    return;
                }


                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                new GetFullNameFirst(PasswordActivity.this, regnumber, password,buttonSignIn).execute();

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });

    }




}
