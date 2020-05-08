package studios.luxurious.mmustsolution.attendance;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import studios.luxurious.mmustsolution.HomeActivity;

public class Utils {

    Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivity.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivity.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivity != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public static String getTime(long time_in_millis) {

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time_in_millis);
            TimeZone timeZone = TimeZone.getTimeZone("Africa/Nairobi");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            simpleDateFormat.setTimeZone(timeZone);
            return simpleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            return "null";
        }
    }



    public static String getBaseUrl(){
        return "https://classattendancemmust.000webhostapp.com/index.php/";
    }

    public  void restartApp(){

//https://classattendancemmust.000webhostapp.com/flights/index

        SharedPref sharedPref = new SharedPref(context);

        sharedPref.setStudentRegno(null);
        sharedPref.setTeacherCode(null);
        sharedPref.setStudentPhone(null);
        sharedPref.setStudentEmail(null);
        sharedPref.setStudent_Course_id(null);
        sharedPref.setStudentFirstName(null);
        sharedPref.setStudentSecondName(null);
        sharedPref.setStudentSurname(null);
        sharedPref.setStudent_Level_id(null);
        sharedPref.setStudent_Level_name(null);
        sharedPref.setStudentGender(null);
        sharedPref.setStudent_Course_name(null);

        new Handler().postDelayed(() -> {
            Intent startApp = new Intent(context, HomeActivity.class);
            int pendindintentid = 6444;
            PendingIntent pendingIntent = PendingIntent.getActivity(context, pendindintentid, startApp, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
            System.exit(0);
        },200);
    }



    public static  String md5(String input) throws NoSuchAlgorithmException {
        String result = input;
        if(input != null) {
            MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
            md.update(input.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            while(result.length() < 32) { //40 for SHA-1
                result = "0" + result;
            }
        }
        return result;
    }

    public static String getDeviceUniqueID(Context contexttt){

        return Settings.Secure.getString(contexttt.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    public static boolean IsConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

}
