package studios.luxurious.mmustsolution.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;

public class RegistrationNumber extends AppCompatActivity {

    TextInputEditText editTextRegNo;
    Button buttonContinue;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_number);

        editTextRegNo = findViewById(R.id.editTextRegNumber);
        buttonContinue = findViewById(R.id.buttonContinue);


        buttonContinue.setOnClickListener(v -> {

            String regnumber = editTextRegNo.getText().toString().trim();

            if (regnumber.isEmpty()) {
                editTextRegNo.setError("Please enter a valid reg number.");
                editTextRegNo.requestFocus();
                return;
            }

            Intent intent = new Intent(RegistrationNumber.this, PasswordActivity.class);
            intent.putExtra("regno", regnumber);
            startActivity(intent);

        });

    }

    public void Login_as_Guest(View view) {
        Intent intent = new Intent(RegistrationNumber.this, LoginAsGuest.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
