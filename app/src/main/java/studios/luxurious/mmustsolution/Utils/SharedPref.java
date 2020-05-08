package studios.luxurious.mmustsolution.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private Context ctx;
    private SharedPreferences default_prefence;

    public SharedPref(Context context) {
        this.ctx = context;
        default_prefence = context.getSharedPreferences("Mmust", Context.MODE_PRIVATE);
    }

    public void setRegNumber(String regno) {
        default_prefence.edit().putString("regno", regno).apply();
    }

    public String getRegNumber() {
        return default_prefence.getString("regno", null);
    }

    public void setStudentEmail(String studentEmail) {
        default_prefence.edit().putString("studentEmail", studentEmail).apply();
    }

    public String getStudentEmail() {
        return default_prefence.getString("studentEmail", null);
    }

    public void setCurrentVersion(String version) {
        default_prefence.edit().putString("version", version).apply();
    }

    public String getStoredVersion() {
        return default_prefence.getString("version", null);
    }


    public void setPortalPassword(String regno) {
        default_prefence.edit().putString("password", regno).apply();
    }

    public void clearRegno() {
        default_prefence.edit().remove("regno").apply();

    }

    public void clearPassword() {

        default_prefence.edit().remove("password").apply();

    }

    public String getPortalPassword() {
        return default_prefence.getString("password", null);
    }


    public void setPortalFullName(String fullname) {
        default_prefence.edit().putString("fullName", fullname).apply();
    }

    public String getPortalFullName() {
        return default_prefence.getString("fullName", null);
    }


    public void setRatings(int ratings) {
        default_prefence.edit().putInt("ratings", ratings).apply();
    }

    public int getRatings() {
        return default_prefence.getInt("ratings", 0);
    }


    public void setFirstTimePortal(String firstTimePortal) {
        default_prefence.edit().putString("FirstTime", firstTimePortal).apply();
    }

    public String getFirstTimePortal() {
        return default_prefence.getString("FirstTime", null);
    }


    public void setNotificationToken(String token) {
        default_prefence.edit().putString("token", token).apply();
    }

    public String getNotificationToken() {
        return default_prefence.getString("token", null);
    }


    public void setGuestUsername(String username) {
        default_prefence.edit().putString("guestUsername", username).apply();
    }

    public String getGuestUsername() {
        return default_prefence.getString("guestUsername", null);
    }

    public void setReferralCode(String code) {
        default_prefence.edit().putString("ReferralCode", code).apply();
    }

    public String getReferralCode() {
        return default_prefence.getString("ReferralCode", null);
    }

    public void clearPortalName() {

        default_prefence.edit().remove("fullName").apply();

    }

    public void setIsGuest(boolean isGuest) {

        default_prefence.edit().putBoolean("isGuest", isGuest).apply();
    }

    public boolean getIsGuest() {
        return default_prefence.getBoolean("isGuest", false);

    }


    public void setIsFirstTime(boolean isFirstTime) {

        default_prefence.edit().putBoolean("isFirstTime", isFirstTime).apply();
    }

    public boolean getIsFirstTime() {
        return default_prefence.getBoolean("isFirstTime", true);

    }


}
