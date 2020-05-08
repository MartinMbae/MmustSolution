package studios.luxurious.mmustsolution.attendance.Teacher;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters.Lessons_adapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.RecyclerTouchListener;
import studios.luxurious.mmustsolution.attendance.Utils;

public class Single_Unit extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout = null;
    RelativeLayout relative_content, relative_empty;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    View bottom_sheet;
    ArrayList<ArrayList<Object>> lessons_from_db;
    SharedPref sharedPref;

    Calendar calendar_rightnowtime;
    DBAdapter db;


    String unit_code, unit_name,unit_id;

    TextView textView;

    Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_single__unit);






        unit_code = getIntent().getStringExtra("code");
        unit_name = getIntent().getStringExtra("name");
        unit_id = getIntent().getStringExtra("id");


        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.text);

        setSupportActionBar(toolbar);


        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setSubtitle(unit_code + " - " + unit_name);


        textView.setText(String.format("Below is a list of all the previous lectures of %s - %s", unit_code, unit_name));




        sharedPref = new SharedPref(Single_Unit.this);

     
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::RefreshLessons);

        db = new DBAdapter(Single_Unit.this);
        db.open();


        lessons_from_db = db.getAllLessonsWhereUnitID(unit_id);

        relative_content = findViewById(R.id.relative_content);
        relative_empty = findViewById(R.id.relative_empty);


        checkIfEmpty(true);

        calendar_rightnowtime = Calendar.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);


        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(Single_Unit.this);
        recyclerView.setLayoutManager(layoutManager);


        mAdapter = new Lessons_adapter(lessons_from_db);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(Single_Unit.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {


                TextView textView = view.findViewById(R.id.lesson_id);
                String lesson_id = textView.getText().toString().trim();

                Intent students = new Intent(Single_Unit.this, StudentAttendance.class);
                students.putExtra("lesson_id", lesson_id);
                startActivity(students);

            }

            @Override
            public void onLongClick(View view, final int position) {


            }
        }));

    }

    private void checkIfEmpty(boolean refresh) {
        if (db.getNumber_of_Lessons_WhereUnitID(unit_id) < 1) {
            relative_empty.setVisibility(View.VISIBLE);

            if (refresh) {
                RefreshLessons();
            }


        } else {
            relative_empty.setVisibility(View.GONE);
        }

    }


    private void RefreshLessons() {
        new Handler().postDelayed(() -> {
            if (Utils.isNetworkAvailable(Single_Unit.this)) {
                swipeRefreshLayout.setRefreshing(true);
                AllLessons();
            } else {

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Single_Unit.this, "Whoops, You have no internet connection.", Toast.LENGTH_SHORT).show();

            }

        }, 1500);


    }

    private void AllLessons() {
        String URLline = Utils.getBaseUrl()+ "api/teacher_lessons/" + sharedPref.getTeacherCode();
        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(Single_Unit.this, "Response was null", Toast.LENGTH_LONG).show();
                return;
            }
            parseAllLessons(response);
        }, error -> {
            dialogErrormessage("Error", "Failed to fetch your previous lectures. Please check your internet connection and try again", "lessons");
        });

        Volley.newRequestQueue(Single_Unit.this).add(request);

    }

    public void parseAllLessons(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("message");

                db.deleteLessons();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject js = dataArray.getJSONObject(i);

                    String id = js.getString("id");
                    String unit_id = js.getString("unit_id");
                    String teacher_code = js.getString("teacher_code");
                    String start_time = js.getString("start_time");
                    String sem_id = js.getString("sem_id");
                    String year_id = js.getString("year_id");
                    String unit_name = js.getString("unit_name");
                    String year_name = js.getString("year_name");
                    String semester_name = js.getString("semester_name");
                    String course_name = js.getString("course_name");
                    String weekNumber = js.getString("currentWeek");

                    db.addLesson(id, unit_id, teacher_code, start_time, semester_name, year_name, sem_id, year_id, unit_name, course_name,weekNumber);

                }

                swipeRefreshLayout.setRefreshing(false);
                lessons_from_db = db.getAllLessonsWhereUnitID(unit_id);
                mAdapter = null;

                mAdapter = new Lessons_adapter(lessons_from_db);
                recyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();

                checkIfEmpty(false);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialogErrormessage("Error", e.toString(), "lessons");
        }
    }

    public void dialogErrormessage(String title, String message, String list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Single_Unit.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {

            swipeRefreshLayout.setRefreshing(false);
            dialog.dismiss();
        });

        builder.setNegativeButton("Try again", (dialog, which) -> {

            if (list.equalsIgnoreCase("units")) {
                dialog.dismiss();

            } else if (list.equalsIgnoreCase("master")) {
                dialog.dismiss();
            } else if (list.equalsIgnoreCase("lessons")) {
                dialog.dismiss();
                RefreshLessons();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }



}
