package studios.luxurious.mmustsolution.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;

public class ForgotPassword extends AppCompatActivity {

    Button submit_btn;
    EditText regno_et;
    String regnumber,device_id;

    ProgressDialog progressDialog;
    
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressDialog = new ProgressDialog(ForgotPassword.this);

        requestQueue = Volley.newRequestQueue(ForgotPassword.this);

        regno_et = findViewById(R.id.reg_no);
        submit_btn = findViewById(R.id.submit_btn);

        regnumber = getIntent().getStringExtra("reg").trim();
        device_id = getIntent().getStringExtra("device_id").trim();

        if (!regnumber.equals("0")){
            regno_et.setText(regnumber);
            regno_et.setFocusableInTouchMode(false);
            regno_et.setFocusable(false);
        }
        submit_btn.setOnClickListener(v -> {

            String regno = regno_et.getText().toString().trim();

            if (!TextUtils.isEmpty(regno)){
                forgotPassword();
            }else{
                regno_et.setError("Fill this field");
            }
        });
    }


    public void forgotPassword() {

        if (progressDialog == null)
            progressDialog = new ProgressDialog(ForgotPassword.this);

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Processing your request");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URLline = Utils.getBaseUrl() + "api/ForgotPassword/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(ForgotPassword.this, "Response was null", Toast.LENGTH_LONG).show();
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
                params.put("device_id", device_id);
                return params;

            }
        };


        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(ForgotPassword.this);
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


    public void dialogBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
        if (progressDialog == null) progressDialog = new ProgressDialog(ForgotPassword.this);
        if(requestQueue == null) requestQueue = Volley.newRequestQueue(ForgotPassword.this);

    }

    public void dialogBoxSuccess(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {


                    startActivity(new Intent(ForgotPassword.this, Login.class));
                    finish();

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
