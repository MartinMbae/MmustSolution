package studios.luxurious.mmustsolution.attendance;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.Teacher.MainActivity;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;

public class Login extends AppCompatActivity {

    private EditText code;
    private EditText pass;
    private Button loginBtn;
    SharedPref sharedPref;
    ProgressDialog progressDialog;
    String device_id;
    String originalReg = null;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_attendance);

        sharedPref = new SharedPref(Login.this);
        device_id = Utils.getDeviceUniqueID(Login.this);

        requestQueue = Volley.newRequestQueue(Login.this);

        if (sharedPref.getTeacherCode() != null) {

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        } else if (sharedPref.getStudentPhone() != null && sharedPref.getStudentEmail() != null && sharedPref.getStudentRegno() != null &&
                sharedPref.getStudent_Course_id() != null && sharedPref.getStudentFirstName() != null && sharedPref.getStudentSecondName() != null &&
                sharedPref.getStudentSurname() != null && sharedPref.getStudent_Level_id() != null && sharedPref.getStudentGender() != null &&
                sharedPref.getStudent_Course_name() != null && sharedPref.getStudent_Level_name() != null) {

            startActivity(new Intent(Login.this, studios.luxurious.mmustsolution.attendance.Student.MainActivity.class));
            finish();
        }

        //Has not logged in

        code = findViewById(R.id.reg_no_et);
        pass = findViewById(R.id.reg_password);
        loginBtn = findViewById(R.id.login_btn);

        progressDialog = new ProgressDialog(this);

        checkRegisteredAccount();


        loginBtn.setOnClickListener(v -> {

            hideKeyboard();

            String teacher_code = code.getText().toString().trim();
            String password = pass.getText().toString().trim();

            if (TextUtils.isEmpty(teacher_code)) {
                code.setError("You must fill this field");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                pass.setError("You must fill this field");
                return;
            }

            loginBtn.setEnabled(false);
            fetchTeacherData(teacher_code, password);

        });

    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    private void fetchTeacherData(String unique_number, String password) {


        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URLline = Utils.getBaseUrl() + "api/teacher/" + unique_number;

        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(Login.this, "Response was null", Toast.LENGTH_LONG).show();
                return;
            }

            parseTeacherData(response, password, unique_number);

        }, error -> {
            progressDialog.dismiss();
            loginBtn.setEnabled(true);
            dialogLogin("Error", "Error connecting to the server. Please check your internet connection and try again.");
        });

        Volley.newRequestQueue(this).add(request);

    }

    public void parseTeacherData(String response, String password, String unique_number) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                String message = jsonObject.getString("message");
                JSONObject jsn = new JSONObject(message);
                String fetched_password = jsn.getString("password");

                String md5_pass = null;
                try {
                    md5_pass = md5(password);
                    if (md5_pass.equals(fetched_password)) {

                        String fetched_phone = jsn.getString("phone");
                        String fetched_email = jsn.getString("email");
                        String fetched_teacher_code = jsn.getString("teacher_code");
                        String fetched_title = jsn.getString("title");
                        String fetched_first = jsn.getString("first_name");
                        String fetched_second = jsn.getString("second_name");
                        String fetched_device_id = jsn.getString("device_id");

                        sharedPref.setTeacherPhone(fetched_phone);
                        sharedPref.setTeacherEmail(fetched_email);
                        sharedPref.setTeacherCode(fetched_teacher_code);
                        sharedPref.setTeacherTitle(fetched_title);
                        sharedPref.setTeacherFirstName(fetched_first);
                        sharedPref.setTeacherSecondName(fetched_second);

                        progressDialog.dismiss();

                        if (fetched_device_id.equals("not_set")) {
                            setDevice_id("teacher", fetched_teacher_code, device_id, password, fetched_title + ". " + fetched_first + " " + fetched_second);
                        } else {
                            if (fetched_device_id.equals(device_id)) {


                                if (password.equalsIgnoreCase(fetched_teacher_code)) {

                                    Intent intent = new Intent(Login.this, ChangePassword.class);
                                    intent.putExtra("name", fetched_title + ". " + fetched_first + " " + fetched_second);
                                    intent.putExtra("user_type", "teacher");
                                    intent.putExtra("reg", fetched_teacher_code);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }

                            } else {

                                denyAccess();
                            }
                        }


                    } else {

                        progressDialog.dismiss();
                        loginBtn.setEnabled(true);
                        dialogLogin("Error", "Wrong credentials. Try again!!");
                        loginBtn.setEnabled(true);
                    }

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {


                loginBtn.setEnabled(false);
                fetchStudentData(unique_number, password);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String md5(String input) throws NoSuchAlgorithmException {
        String result = input;
        if (input != null) {
            MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while (result.length() < 32) { //40 for SHA-1
                result = "0" + result;
            }
        }
        return result;
    }

    public void dialogLogin(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void dialogLoginNetworkError(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Retry", (dialog, which) -> {
            checkRegisteredAccount();
        });

        builder.setNeutralButton("Exit", (dialog, which) -> {
            System.exit(0);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void fetchStudentData(String regno, String password) {

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging in");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URLline = Utils.getBaseUrl() + "api/student/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(Login.this, "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseStudentData(response, password);
                },
                error -> {
                    progressDialog.dismiss();
                    loginBtn.setEnabled(true);
                    dialogLogin("Error", "Error connecting to the server. Please check your internet connection and try again.");

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", regno);
                return params;
            }
        };


        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);

    }

    public void parseStudentData(String response, String password) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                String message = jsonObject.getString("message");
                JSONObject jsn = new JSONObject(message);
                String fetched_password = jsn.getString("password");

                String md5_pass;
                try {
                    md5_pass = md5(password);
                    if (md5_pass.equals(fetched_password)) {


                        String fetched_phone = jsn.getString("phone");
                        String fetched_email = jsn.getString("email");
                        String fetched_regno = jsn.getString("regno");
                        String fetched_course_id = jsn.getString("course_id");
                        String fetched_first = jsn.getString("firstname");
                        String fetched_second = jsn.getString("secondname");
                        String fetched_surname = jsn.getString("surname");
                        String fetched_level_id = jsn.getString("level_id");
                        String fetched_gender = jsn.getString("gender");
                        String fetched_course_name = jsn.getString("course_name");
                        String fetched_level_name = jsn.getString("level_name");
                        String fetched_device_id = jsn.getString("device_id");


                        sharedPref.setStudentPhone(fetched_phone);
                        sharedPref.setStudentEmail(fetched_email);
                        sharedPref.setStudentRegno(fetched_regno);
                        sharedPref.setStudent_Course_id(fetched_course_id);
                        sharedPref.setStudentFirstName(fetched_first);
                        sharedPref.setStudentSecondName(fetched_second);
                        sharedPref.setStudentSurname(fetched_surname);
                        sharedPref.setStudent_Level_id(fetched_level_id);
                        sharedPref.setStudentGender(fetched_gender);
                        sharedPref.setStudent_Course_name(fetched_course_name);
                        sharedPref.setStudent_Level_name(fetched_level_name);

                        progressDialog.dismiss();


                        if (fetched_device_id.equals("not_set")) {
                            setDevice_id("student", fetched_regno, device_id, password, fetched_first + " " + fetched_second + " " + fetched_surname);
                        } else {
                            if (fetched_device_id.equals(device_id)) {


                                if (password.equalsIgnoreCase(fetched_regno)) {

                                    Intent intent = new Intent(Login.this, ChangePassword.class);
                                    intent.putExtra("name", fetched_first + " " + fetched_second + " " + fetched_surname);
                                    intent.putExtra("user_type", "student");
                                    intent.putExtra("reg", fetched_regno);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    startActivity(new Intent(Login.this, studios.luxurious.mmustsolution.attendance.Student.MainActivity.class));
                                    finish();
                                }
                            } else {

                                denyAccess();
                            }
                        }


                    } else {

                        progressDialog.dismiss();
                        loginBtn.setEnabled(true);
                        dialogLogin("Error", "Wrong credentials. Try again!!");
                        loginBtn.setEnabled(true);
                    }

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {


                String message = jsonObject.getString("message");
                progressDialog.dismiss();
                loginBtn.setEnabled(true);
                dialogLogin("Error", message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void ForgotPassword(View view) {

        Intent intent = new Intent(Login.this, ForgotPassword.class);

        if (originalReg != null) {
            intent.putExtra("reg", originalReg);
        } else {

            intent.putExtra("reg", "0");
        }
        intent.putExtra("device_id", device_id);
        startActivity(intent);
    }


    public void setDevice_id(String user_type, String regnumber, String device_id, String password, String name) {

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Registering device");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URLline = Utils.getBaseUrl() + "api/DeviceInfo/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(Login.this, "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseData(response, user_type, password, regnumber, name);
                    progressDialog.dismiss();
                },
                error -> {
                    progressDialog.dismiss();
                    dialogLogin("Error", "Error connecting to the server. Please check your internet connection and try again.");

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", regnumber);
                params.put("user_type", user_type);
                params.put("device_id", device_id);
                return params;

            }
        };


        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(Login.this);
        requestQueue.add(stringRequest);

    }

    public void parseData(String response, String user_type, String password, String regno, String name) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                String message = jsonObject.getString("message");


                originalReg = regno;

                dialogBoxSuccess("Success", message, user_type, password, regno, name);

            } else {

                String message = jsonObject.getString("message");
                dialogLogin("Error", message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void dialogBoxSuccess(String title, String message, String user_type, String password, String fetched_regno, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            switch (user_type) {
                case "student":


                    if (password.equalsIgnoreCase(fetched_regno)) {

                        Intent intent = new Intent(Login.this, ChangePassword.class);
                        intent.putExtra("name", name);
                        intent.putExtra("user_type", "student");
                        intent.putExtra("reg", fetched_regno);
                        startActivity(intent);
                        finish();

                    } else {

                        startActivity(new Intent(Login.this, studios.luxurious.mmustsolution.attendance.Student.MainActivity.class));
                        finish();
                    }


                    break;
                case "teacher":


                    if (password.equalsIgnoreCase(fetched_regno)) {

                        Intent intent = new Intent(Login.this, ChangePassword.class);
                        intent.putExtra("name", name);
                        intent.putExtra("user_type", "teacher");
                        intent.putExtra("reg", fetched_regno);
                        startActivity(intent);
                        finish();

                    } else {
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    }

                    break;
                default:
                    System.exit(0);
            }
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void denyAccess() {
        dialogBoxDeny("Access Denied", "This account has been registered in another phone. If this is your new primary device please contact the school management to verify and update this change");

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(3000);
    }

    public void dialogBoxDeny(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            DBAdapter dbAdapter = new DBAdapter(Login.this);
            dbAdapter.open();
            studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter dbAdapter1 = new studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter(Login.this);
            dbAdapter1.open();
            dbAdapter1.resetDB();

        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void checkRegisteredAccount() {

        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Checking device info.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!Utils.IsConnectedToInternet(Login.this)) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setTitle("Error");
            builder.setMessage("Make sure you are connected to the internet");
            builder.setCancelable(false);
            builder.setPositiveButton("Retry", (dialog, which) -> {
                checkRegisteredAccount();
            });
            builder.setNegativeButton("Exit", (dialog, which) -> {
                System.exit(0);
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {


            String URLline = Utils.getBaseUrl() + "api/DeviceInfo/" + device_id;

            StringRequest request = new StringRequest(URLline, response -> {
                progressDialog.dismiss();
                if (response == null) {
                    Toast.makeText(Login.this, "Response was null", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        String regno = jsonObject.getString("message").trim();
                        if (!regno.equals("null")) {
                            code.setText(regno);
                            code.setFocusable(false);
                            code.setFocusableInTouchMode(false);


                            originalReg = regno;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {

                progressDialog.dismiss();
                dialogLoginNetworkError("Error", "Failed to fetch some data . Please check your internet connection and try again");
            });


            if (requestQueue == null)
                requestQueue = Volley.newRequestQueue(Login.this);
            requestQueue.add(request);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLeakables();
    }

    @Override
    protected void onPause() {
        destroyLeakables();
        super.onPause();
    }

    @Override
    public void onStop() {

        destroyLeakables();
        super.onStop();

    }

    public void destroyLeakables() {
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


    public void initializeLeakables() {
        if (progressDialog == null) progressDialog = new ProgressDialog(Login.this);
        if (requestQueue == null) requestQueue = Volley.newRequestQueue(Login.this);

    }
}
