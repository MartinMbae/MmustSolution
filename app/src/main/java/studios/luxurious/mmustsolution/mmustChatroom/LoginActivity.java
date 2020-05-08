package studios.luxurious.mmustsolution.mmustChatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class LoginActivity extends AppCompatActivity {

    private TextView toptext;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private Button login;
    private ProgressDialog pdialog;

    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toptext = findViewById(R.id.toptext);
        toptext.setTypeface(pacifico);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        sharedPref = new SharedPref(this);


        String regno = sharedPref.getRegNumber();
        String passwordString = sharedPref.getPortalPassword();

        email.setText(regno);
        password.setText(passwordString);


        signinwithmail();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinwithmail();
            }
        });

    }

    private void signinwithmail() {
        String useremail = email.getText().toString();
        String userpassword = password.getText().toString();

        if (TextUtils.isEmpty(useremail)) {
            email.setError("Please enter your email");
        } else if (TextUtils.isEmpty(userpassword)) {
            password.setError("Please enter your password");
        } else {
            pdialog = new ProgressDialog(LoginActivity.this);
            pdialog.setMessage("Please wait...");
            pdialog.setIndeterminate(true);
            pdialog.setCancelable(false);
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.show();


            useremail = useremail + Constants.EMAIL_EXTENSION;


            mAuth.signInWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startactivity();
                                pdialog.dismiss();
                            } else {
                                String message = task.getException().toString();
                                if (message.contains("password is invalid")) {
                                    pdialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                                } else if (message.contains("There is no user")) {
                                    pdialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Account doesn't exists.Please Register", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    pdialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Unable to Login !", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }


    private void startactivity() {
        Intent intent = new Intent(LoginActivity.this, ChatRoom.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
