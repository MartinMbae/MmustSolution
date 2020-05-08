package studios.luxurious.mmustsolution.attendance.Student;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Student.Adapters.Lessons_Adapter;
import studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Utils;

public class Single_Unit extends AppCompatActivity {


    private RecyclerView lessons_recyclerView;
    private RecyclerView.Adapter lessons_mAdapter;
    SharedPref sharedPref;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    View bottom_sheet;
    DBAdapter db;


    ProgressDialog progressDialog;

    ArrayList<ArrayList<Object>> lessons_from_db;

    SwipeRefreshLayout swipeRefreshLayout;

    String unit_code, unit_name;

    TextView textView;

    Toolbar toolbar;
    RelativeLayout relative_empty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__unit);

        unit_code = getIntent().getStringExtra("code");
        unit_name = getIntent().getStringExtra("name");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.text);

        setSupportActionBar(toolbar);

        sharedPref = new SharedPref(this);

        relative_empty = findViewById(R.id.relative_empty);


        toolbar.setTitle(getResources().getString(R.string.app_name));
        toolbar.setSubtitle(unit_code + " - " + unit_name);


        textView.setText(String.format("Below is a list of all the previous lectures of %s - %s", unit_code, unit_name));


        progressDialog = new ProgressDialog(Single_Unit.this);
        lessons_recyclerView = findViewById(R.id.lessons_recycler_view);


        db = new DBAdapter(this);
        db.open();

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        lessons_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, lessons_recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, final int position) {


                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert vibrator != null;
                vibrator.vibrate(80);

                new Handler().postDelayed(() -> {

                    ArrayList<Object> lessons_list = lessons_from_db.get(position);

                    String unit_name = (String) lessons_list.get(0);
                    String teacher_name = (String) lessons_list.get(1);
                    String start_time = (String) lessons_list.get(2);
                    String my_attendance_time = (String) lessons_list.get(3);
                    String status = (String) lessons_list.get(4);
//                    String lesson_id =  (String) lessons_list.get(5);
                    String unit_code = (String) lessons_list.get(6);
                    String sem_name = (String) lessons_list.get(7);
                    String year_name = (String) lessons_list.get(8);
                    String total_students = (String) lessons_list.get(9);
                    String total_attendance = (String) lessons_list.get(10);

                    long start_time_long = Long.parseLong(start_time);

                    String date_string = new SimpleDateFormat("EEE, dd/MMM/yyyy", Locale.getDefault()).format(new Date(start_time_long));
                    String start_string = Utils.getTime(start_time_long);
                    long my_time = Long.parseLong(my_attendance_time);

                    if (my_time > 200) {
                        my_attendance_time = Utils.getTime(my_time);
                    }

                    String att = total_attendance + " of " + total_students + " students";
                    showBottomSheetDialog(date_string, start_string, unit_name, status, teacher_name, att, unit_code, sem_name, year_name, my_attendance_time);
                }, 200);

            }
        }));


        lessons_from_db = db.getAllLessonsWhereUnitCode(unit_code);

        lessons_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lessons_recyclerView.setNestedScrollingEnabled(false);

        lessons_mAdapter = new Lessons_Adapter(lessons_from_db, this);
        lessons_recyclerView.setAdapter(lessons_mAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchData(sharedPref.getStudentRegno()));


        if (db.getNumber_of_LessonsWhereUnitCode(unit_code) < 1) {

            relative_empty.setVisibility(View.VISIBLE);
            fetchData(sharedPref.getStudentRegno());
        }else {

            relative_empty.setVisibility(View.GONE);
        }



    }


    private void checkIfEmpty(){



        if (db.getNumber_of_LessonsWhereUnitCode(unit_code) < 1) {
           relative_empty.setVisibility(View.VISIBLE);
        }else{

            relative_empty.setVisibility(View.GONE);
        }

    }

    public void fetchData(String regno) {

        swipeRefreshLayout.setRefreshing(true);

        String URLline = Utils.getBaseUrl()+ "api/Student_Lesson";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(this, "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseData(response);
                    swipeRefreshLayout.setRefreshing(false);
                    checkIfEmpty();
                },
                error -> {
                    dialogErrormessage("Error", "Error connecting to the server. Please check your internet connection and try again.", "lessons");

                    swipeRefreshLayout.setRefreshing(false);
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", regno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                JSONArray lessonsArray = jsonObject.getJSONArray("lessons");

                db.deleteLessons();

                for (int i = 0; i < lessonsArray.length(); i++) {

                    JSONObject object = lessonsArray.getJSONObject(i);


                    String unitname = object.getString("unit_name");
                    String teacher_name = object.getString("teacher_name");
                    String start_time = object.getString("start_time");
                    String my_attendance_time = object.getString("my_attendance_time");
                    String status = object.getString("status");
                    String id = object.getString("id");
                    String unit_code = object.getString("unit_code");
                    String sem_name = object.getString("sem_name");
                    String year_name = object.getString("year_name");
                    String total_students = object.getString("total_students");
                    String total_attendance = object.getString("total_attendance");

                    String week = object.getString("current_week");

                    int weekInt = Integer.parseInt(week);

                    db.addLesson(unitname, teacher_name, start_time, my_attendance_time, status, id, unit_code, sem_name, year_name, total_students, total_attendance,weekInt);
                }

                lessons_from_db.clear();
                lessons_from_db = db.getAllLessonsWhereUnitCode(unit_code);
                lessons_mAdapter = new Lessons_Adapter(lessons_from_db, this);
                lessons_recyclerView.setAdapter(lessons_mAdapter);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = lessons_recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


    private void showBottomSheetDialog(String date, String startTime, String unitName, String lesson_status, String teacher_name, String attendance, String unit_code, String sem_name, String year_name, String my_attendance_time) {


        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.student_previous_lesson__bottom_sheet, null);


        ((TextView) view.findViewById(R.id.date)).setText(date);
        ((TextView) view.findViewById(R.id.semesterString)).setText(sem_name);
        ((TextView) view.findViewById(R.id.academic_year)).setText(year_name);
        ((TextView) view.findViewById(R.id.unit_code)).setText(unit_code);
        ((TextView) view.findViewById(R.id.unit_name)).setText(unitName);
        ((TextView) view.findViewById(R.id.lecturer)).setText(teacher_name);
        ((TextView) view.findViewById(R.id.start_time)).setText(startTime);
        ((TextView) view.findViewById(R.id.attendance)).setText(attendance);
        ((TextView) view.findViewById(R.id.my_time)).setText(my_attendance_time);

        ImageView lesson_status_image = view.findViewById(R.id.lesson_image);

        if (lesson_status.equalsIgnoreCase("1")) {
            lesson_status_image.setImageDrawable(Single_Unit.this.getResources().getDrawable(R.drawable.tick));
        } else {

            lesson_status_image.setImageDrawable(Single_Unit.this.getResources().getDrawable(R.drawable.cancel));
        }


        mBottomSheetDialog = new BottomSheetDialog(Single_Unit.this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Objects.requireNonNull(mBottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);

    }

    private long getCurrentTimeinMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public void dialogErrormessage(String title, String message, String list) {

        if (Single_Unit.this != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Single_Unit.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            builder.setNegativeButton("Try again", (dialog, which) -> {

                if (list.equalsIgnoreCase("master")) {
                    dialog.dismiss();
                } else if (list.equalsIgnoreCase("lessons")) {
                    dialog.dismiss();
                    fetchData(sharedPref.getStudentRegno());
                } else if (list.equalsIgnoreCase("units")) {
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
