package studios.luxurious.mmustsolution;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import androidx.appcompat.app.AlertDialog;

import studios.luxurious.mmustsolution.Login.PhotoSelection;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class GetFullNameFirst extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    protected Context context;
    private String username;
    private String password;
    static ProgressDialog progress_dialog;
    private String FULLNAME, STUDENT_EMAIL;
    @SuppressLint("StaticFieldLeak")
    private View view;


    public GetFullNameFirst(Context context, String username, String password, View view) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress_dialog = new ProgressDialog(context);
        progress_dialog.setMessage("Trying to authenticate you. This may take time depending on the strength of your network.");
        progress_dialog.setTitle("Please wait...");
        progress_dialog.setCancelable(false);
        progress_dialog.setCanceledOnTouchOutside(false);
        progress_dialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        username = username.toUpperCase();

        Connection.Response loginFormResponse;
        try {
            String login_url = Constants.PORTAL_URL;
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";

            loginFormResponse = Jsoup.connect(login_url)
                    .method(Connection.Method.GET)
                    .userAgent(USER_AGENT)
                    .timeout(50 * 1000)
                    .execute();

            String rawData = "{username:\""+ username + "\",password:\""+ password + "\"}";

            Document document = Jsoup.connect("http://portal.mmust.ac.ke/api/login/user")
                    .header("User-Agent",USER_AGENT)
                    .header("Accept","application/json, text/plain, */*")
                    .header("Accept-Language","en-US,en;q=0.5")
                    .header("Content-Type","application/json")
                    .header("Connection","keep-alive")
                    .header("Referer","http://portal.mmust.ac.ke/login/sign-in")
                    .userAgent(USER_AGENT)
                    .ignoreContentType(true)
                    .requestBody(rawData)
                    .timeout(50 * 1000)
                    .cookies(loginFormResponse.cookies())
                    .post();

            String response = document.body().text();


            JSONObject jsonObject1 = new JSONObject(response);
            if (jsonObject1.getString("success").equals("true")) {


                String message = jsonObject1.getString("data");
                JSONObject jsnObject2 = new JSONObject(message);


                String user = jsnObject2.getString("userRegister");

                JSONObject jsnObject3 = new JSONObject(user);

                FULLNAME = jsnObject3.getString("names");
                STUDENT_EMAIL = jsnObject3.getString("email");



            }else {
               String errorMessage = jsonObject1.getString("message");

                new Handler(Looper.getMainLooper()).post(() -> showDialogTimedOut("Authentication Failed",  errorMessage));
            }


            if (FULLNAME != null) {
                SharedPref sharedPref = new SharedPref(context);
                sharedPref.setPortalFullName(FULLNAME);
                sharedPref.setPortalPassword(password);
                sharedPref.setRegNumber(username);
                sharedPref.setStudentEmail(STUDENT_EMAIL);

            }


        } catch (final Exception e) {
            e.printStackTrace();

            if (e.getMessage().contains("timeout")) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showDialogTimedOut("Timed out", "Make sure you have a strong network connection and try again");

                    }
                });


            } else if (e.getMessage().contains("Connection timed out")) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showDialogTimedOut("Something went wrong.", "University's servers took so long to respond. Please try again later.");

                    }
                });

            } else {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showDialogTimedOut("Authentication Failed", "Are you sure that you provided correct details?");

                    }
                });

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progress_dialog.dismiss();

        if (FULLNAME != null) {

            new Handler(Looper.getMainLooper()).post(() -> context.startActivity(new Intent(context, PhotoSelection.class)));


        }


    }


    void showDialogTimedOut(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Retry", (dialog, which) -> {

            new GetFullNameFirst(context, username, password, view).execute();

            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();

    }


}
