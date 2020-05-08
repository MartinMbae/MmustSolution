package studios.luxurious.mmustsolution.attendance.Teacher;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Utils;

public class StudentAttendance extends AppCompatActivity {

    String lesson_id = null;
    DBAdapter db;
    SharedPref sharedPref;
    boolean can_parse = true;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_student_attendance_table);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lesson_id = getIntent().getStringExtra("lesson_id");

        sharedPref = new SharedPref(StudentAttendance.this);

        sharedPref.setLesson_id(lesson_id);

        db = new DBAdapter(this);
        db.open();


        if (db.getNumber_of_StudentLessons(lesson_id) == 0) {

            getStudents(lesson_id);
        } else {

            MainFragment mFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_container, mFragment, MainFragment.class.getSimpleName()).commit();

        }


    }


    private void getStudents(String lesson_id) {

        String URLline = Utils.getBaseUrl()+ "api/TeacherLessonStudent/" + lesson_id;
        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(this, "Response was null", Toast.LENGTH_LONG).show();
                return;
            }

            if (can_parse) {
                parseStudents(response, lesson_id);
            }
        }, error -> {

            Toast.makeText(this, "Error while trying to retrieve students. Please try again", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, URLline, Toast.LENGTH_LONG).show();
        });

        Volley.newRequestQueue(this).add(request);

    }

    public void parseStudents(String response, String lesson_id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("message");


                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject js = dataArray.getJSONObject(i);

                    String regno = js.getString("regno");
                    String status = js.getString("status");
                    String time = js.getString("time");
                    String name = js.getString("name");
                    String gender = js.getString("gender");
                    String phone = js.getString("phone");
                    String email = js.getString("email");

                    db.deleteStudentLesson(lesson_id, regno);
                    db.addStudentLesson(lesson_id, name, time, gender, phone, regno, status);

                }


                if (db.getNumber_of_StudentLessons(lesson_id) == 0) {
                    getStudents(lesson_id);
                } else {

                    MainFragment mFragment = new MainFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.activity_container, mFragment, MainFragment.class.getSimpleName()).commit();
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialogBox("Error", e.toString());
        }
    }


    public void dialogBox(String title, String message, String list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentAttendance.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
        });

        builder.setNegativeButton("Try again", (dialog, which) -> {
            getStudents(lesson_id);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void dialogBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentAttendance.this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:

                getStudents(lesson_id);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        can_parse = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        can_parse = false;
        super.onDestroy();
    }
}
//{
//    "success": true,
//    "present": 0,
//    "absent": 3,
//    "message": [
//        {
//            "regno": "BSC-21/2018/2019",
//            "status": "0",
//            "time": "0",
//            "name": "Daniel Ochieng' Nyabuto",
//            "gender": "M",
//            "phone": "0777777",
//            "email": "daniel@dd.com"
//        },
//        {
//            "regno": "BSC-22/2018/2019",
//            "status": "0",
//            "time": "0",
//            "name": "Mary Wanjiru Nder",
//            "gender": "F",
//            "phone": "0703303",
//            "email": "mary@gmail.com"
//        },
//        {
//            "regno": "BSC-23/2018/2019",
//            "status": "0",
//            "time": "0",
//            "name": "Judy Nzoa Wambua",
//            "gender": "F",
//            "phone": "0987654",
//            "email": "judy@gmail.com"
//        }
//    ]
//}
