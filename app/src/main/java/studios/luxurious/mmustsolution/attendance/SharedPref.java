package studios.luxurious.mmustsolution.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SharedPref {

    private Context ctx;
    private SharedPreferences default_prefence;

    public SharedPref(Context context) {
        this.ctx = context;
        default_prefence = context.getSharedPreferences("attendance",Context.MODE_PRIVATE);
    }

    public void setStudentRegno(String regno) {
        default_prefence.edit().putString("regno", regno).apply();
    }

    public String getStudentRegno() {
        return default_prefence.getString("regno", null);
    }


    public void setStudentFirstName(String student_fname) {
        default_prefence.edit().putString("firstname", student_fname).apply();
    }

    public String getStudentFirstName() {
        return default_prefence.getString("firstname", null);
    }


    public void setStudentSecondName(String student_sname) {
        default_prefence.edit().putString("secondname", student_sname).apply();
    }

    public String getStudentSecondName() {
        return default_prefence.getString("secondname", null);
    }


    public void setStudentSurname(String student_surname) {
        default_prefence.edit().putString("surname", student_surname).apply();
    }

    public String getStudentSurname() {
        return default_prefence.getString("surname", null);
    }


    public void setStudentGender(String gender) {
        default_prefence.edit().putString("gender", gender).apply();
    }

    public String getStudentGender() {
        return default_prefence.getString("gender", null);
    }


    public void setStudentEmail(String student_email) {
        default_prefence.edit().putString("email", student_email).apply();
    }

    public String getStudentEmail() {
        return default_prefence.getString("email", null);
    }


    public void setStudentPhone(String student_phone) {
        default_prefence.edit().putString("phone", student_phone).apply();
    }

    public String getStudentPhone() {
        return default_prefence.getString("phone", null);
    }


    public void setStudent_Level_id(String level_id) {
        default_prefence.edit().putString("level_id", level_id).apply();
    }

    public String getStudent_Level_id() {
        return default_prefence.getString("level_id", null);
    }


    public void setStudent_Course_id(String course_id) {
        default_prefence.edit().putString("course_id", course_id).apply();
    }

    public String getStudent_Course_id() {
        return default_prefence.getString("course_id", null);
    }


    public void setStudent_Level_name(String level_name) {
        default_prefence.edit().putString("level_name", level_name).apply();
    }

    public String getStudent_Level_name() {
        return default_prefence.getString("level_name", null);
    }


    public void setStudent_Course_name(String course_name) {
        default_prefence.edit().putString("course_name", course_name).apply();
    }

    public String getStudent_Course_name() {
        return default_prefence.getString("course_name", null);
    }

//
//    public void setCurrent_year(String current_year) {
//        default_prefence.edit().putString("current_year", current_year).apply();
//    }
//
//    public String getCurrent_year() {
//        return default_prefence.getString("current_year",null);
//    }
//
//
//
//    public void setCurrent_yearid(String current_yearid) {
//        default_prefence.edit().putString("current_year_id", current_yearid).apply();
//    }
//
//    public String getCurrent_yearid() {
//        return default_prefence.getString("current_year_id",null);
//    }
//
//
//
//    public void setCurrent_sem(String current_sem) {
//        default_prefence.edit().putString("current_sem", current_sem).apply();
//    }
//
//    public String getCurrent_sem() {
//        return default_prefence.getString("current_sem",null);
//    }
//
//
//    public void setCurrent_semid(String current_sem_id) {
//        default_prefence.edit().putString("current_sem_id", current_sem_id).apply();
//    }
//
//    public String getCurrent_semid() {
//        return default_prefence.getString("current_sem_id",null);
//    }
//
//


    public void setTeacherCode(String teacher_code) {

        default_prefence.edit().putString("teacher_code", teacher_code).apply();
    }

    public String getTeacherCode() {
        return default_prefence.getString("teacher_code", null);
    }


    public void setTeacherFirstName(String teacher_fname) {

//        tinyDB.putString("teacher_code",teacher_fname);
        default_prefence.edit().putString("teacher_fname", teacher_fname).apply();
    }

    public String getTeacherFirstName() {

//        return tinyDB.getString("teacher_fname");
        return default_prefence.getString("teacher_fname", null);
    }


    public void setTeacherSecondName(String teacher_sname) {
        default_prefence.edit().putString("teacher_sname", teacher_sname).apply();
    }

    public String getTeacherSecondName() {
        return default_prefence.getString("teacher_sname", null);
    }


    public void setTeacherTitle(String teacher_title) {
        default_prefence.edit().putString("teacher_title", teacher_title).apply();
    }

    public String getTeacherTitle() {
        return default_prefence.getString("teacher_title", null);
    }


    public void setTeacherEmail(String teacher_email) {
        default_prefence.edit().putString("teacher_email", teacher_email).apply();
    }

    public String getTeacherEmail() {
        return default_prefence.getString("teacher_email", null);
    }


    public void setTeacherPhone(String teacher_phone) {
        default_prefence.edit().putString("teacher_phone", teacher_phone).apply();
    }

    public String getTeacherPhone() {
        return default_prefence.getString("teacher_phone", null);
    }


    public void setCurrent_year(String current_year) {
        default_prefence.edit().putString("current_year", current_year).apply();
    }

    public String getCurrent_year() {
        return default_prefence.getString("current_year", null);
    }


    public void setCurrent_yearid(String current_yearid) {
        default_prefence.edit().putString("current_year_id", current_yearid).apply();
    }

    public String getCurrent_yearid() {
        return default_prefence.getString("current_year_id", null);
    }


    public void setCurrent_sem(String current_sem) {
        default_prefence.edit().putString("current_sem", current_sem).apply();
    }

    public String getCurrent_sem() {
        return default_prefence.getString("current_sem", null);
    }


    public void setCurrent_semid(String current_sem_id) {
        default_prefence.edit().putString("current_sem_id", current_sem_id).apply();
    }

    public String getCurrent_semid() {
        return default_prefence.getString("current_sem_id", null);
    }


    public void setLesson_id(String lesson_id) {

        default_prefence.edit().putString("lesson_id", lesson_id).apply();
    }

    public String getLesson_id() {
        return default_prefence.getString("lesson_id", null);
    }







}
