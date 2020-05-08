package studios.luxurious.mmustsolution.attendance;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.Student.MainActivity;

public class ChangePassword extends AppCompatActivity {

    TextView welcome_tv, decription_tv;
    EditText current_et, new_et, confirm_et;
    Button submit;

    String new_password, old_password, regnumber, user_type, name;
    ProgressDialog progressDialog;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        welcome_tv = findViewById(R.id.welcome_text);
        decription_tv = findViewById(R.id.description);
        current_et = findViewById(R.id.current_password);
        new_et = findViewById(R.id.password);
        confirm_et = findViewById(R.id.confirm_password);
        submit = findViewById(R.id.submit_btn);


        requestQueue = Volley.newRequestQueue(ChangePassword.this);

        progressDialog = new ProgressDialog(ChangePassword.this);
//
        name = getIntent().getStringExtra("name");
        user_type = getIntent().getStringExtra("user_type");
        regnumber = getIntent().getStringExtra("reg");


        welcome_tv.setText(String.format("Welcome %s", name));


        submit.setOnClickListener(v -> {

            new_password = new_et.getText().toString().trim();
            String confirm_password = confirm_et.getText().toString().trim();

            if (!new_password.equals(confirm_password)) {
                confirm_et.setError("Passwords failed to match");
            } else {

                old_password = current_et.getText().toString().trim();


                try {
                    String old_md5 = Utils.md5(old_password);
                    String new_md5 = Utils.md5(new_password);

                    changePassword(old_md5, new_md5);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();

                    dialogBox("Error", "Error while encrypting your password. Please try again");
                }

            }

        });

    }


    public void dialogBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void dialogBoxSuccess(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            switch (user_type) {
                case "student":

                    startActivity(new Intent(ChangePassword.this, MainActivity.class));
                    finish();
                    break;
                case "teacher":

                    startActivity(new Intent(ChangePassword.this, studios.luxurious.mmustsolution.attendance.Teacher.MainActivity.class));
                    finish();
                    break;
                default:
                    System.exit(0);


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void changePassword(String old_password, String new_password) {

        if (progressDialog == null)
            progressDialog = new ProgressDialog(ChangePassword.this);

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Processing your request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URLline = Utils.getBaseUrl() + "api/ChangePassword/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(ChangePassword.this, "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseData(response);
                    progressDialog.dismiss();
                },
                error -> {
                    progressDialog.dismiss();
                    dialogBox("Error", "Error connecting to the server. Please check your internet connection and try again.");

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", regnumber);
                params.put("user_type", user_type);
                params.put("oldpassword", old_password);
                params.put("newpassword", new_password);
                return params;

            }
        };


        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(ChangePassword.this);
        requestQueue.add(stringRequest);

    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                String message = jsonObject.getString("message");

                dialogBoxSuccess("Success", message);

            } else {

                String message = jsonObject.getString("message");
                dialogBox("Error", message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStop() {

        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        initializeLeakables();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeLeakables();
    }


    public void initializeLeakables(){
        if (progressDialog == null) progressDialog = new ProgressDialog(ChangePassword.this);
        if(requestQueue == null) requestQueue = Volley.newRequestQueue(ChangePassword.this);

    }

}
