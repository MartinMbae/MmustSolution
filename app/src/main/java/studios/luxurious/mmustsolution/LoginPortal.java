package studios.luxurious.mmustsolution;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPortal extends AppCompatActivity {

    private EditText reg, pass;
    FirebaseAuth mAuth;
    TextView loginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginportal);

        reg = findViewById(R.id.loginreg);
        pass = findViewById(R.id.loginPass);
        loginText = findViewById(R.id.loginText);
        mAuth = FirebaseAuth.getInstance();

//        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
//        loginText.setTypeface(pacifico);


    }

    public void LoginButtonPortal(View view) {

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().delete();
        }

        String n = reg.getText().toString().trim().toUpperCase();
        String p = pass.getText().toString();

        if (!TextUtils.isEmpty(n) && !TextUtils.isEmpty(p)) {


            Intent f = new Intent(LoginPortal.this, Portal.class);
            f.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            f.putExtra("regno", n);
            f.putExtra("password", p);
            startActivity(f);
            finish();
        } else {
            Toast.makeText(this, "Fill in all the required fields", Toast.LENGTH_SHORT).show();
        }
    }

}