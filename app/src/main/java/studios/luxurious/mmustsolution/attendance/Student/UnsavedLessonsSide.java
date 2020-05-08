package studios.luxurious.mmustsolution.attendance.Student;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Student.Adapters.UnsavedLessonAdapter;
import studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Utils;

public class UnsavedLessonsSide extends BaseFragment {
    View myView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    RelativeLayout relative_empty;
    ArrayList<ArrayList<Object>> lessons_from_db;

    ProgressDialog start_lesson_progressDialog;
    DBAdapter db;

    SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.student_unsaved_lesson__layout, container, false);

        setHasOptionsMenu(true);
        InitializeViews();

        showBackButton(false);
        hideFab();

        db = new DBAdapter(getActivity());
        db.open();

        sharedPref = new SharedPref(getActivity());

        start_lesson_progressDialog = new ProgressDialog(getActivity());

        lessons_from_db = db.getAllUnsavedLessons();
        relative_empty = myView.findViewById(R.id.relative_empty);


        checkIfEmpty();

        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new UnsavedLessonAdapter(lessons_from_db, UnsavedLessonsSide.this);
        recyclerView.setAdapter(mAdapter);

        return myView;
    }

    private void checkIfEmpty(){
        if (db.getNumber_of_Unsaved_Lessons() < 1){
            relative_empty.setVisibility(View.VISIBLE);
        }else{
            relative_empty.setVisibility(View.GONE);

        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }


    public void dialogLessonStatus(String title, String message) {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void StartLesson(String unit_id, String start_time, String my_attendance_time, String regno, String id) {

        start_lesson_progressDialog.setTitle("Please wait");
        start_lesson_progressDialog.setMessage("Syncing");
        start_lesson_progressDialog.setCancelable(false);
        start_lesson_progressDialog.setCanceledOnTouchOutside(false);
        start_lesson_progressDialog.show();

        String start_lesson_url = Utils.getBaseUrl()+ "api/Student_attend/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, start_lesson_url,
                response -> {
                    start_lesson_progressDialog.dismiss();
                    checkLesson(response, id);
                    if (start_lesson_progressDialog.isShowing()) start_lesson_progressDialog.dismiss();
                },
                (VolleyError error) -> {

                    dialogLessonStatus("Server Error", "An error occurred while trying to start the lesson. Please try again");
                    start_lesson_progressDialog.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("unit_id", unit_id);
                params.put("start_time", start_time);
                params.put("my_attendance_time", my_attendance_time);
                params.put("regno", regno);
                return params;
            }
        };

        if (getActivity() != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }
    }

    private void checkLesson(String response,String id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                dialogLessonStatusRefreshLessons("Success", jsonObject.getString("message"));

                int i= Integer.parseInt(id);

                db.deleteUnsavedLesson(i);

                lessons_from_db = null;
                lessons_from_db = db.getAllUnsavedLessons();

                mAdapter = new UnsavedLessonAdapter(lessons_from_db, UnsavedLessonsSide.this);
                recyclerView.setAdapter(mAdapter);
                checkIfEmpty();

                RefreshLessons();

            } else {
                dialogLessonStatus("Error", jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void RefreshLessons() {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Refreshing Lessons");
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URLline = Utils.getBaseUrl()+ "api/Student_Lesson";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        return;
                    }
                    parseRefreshedLessons(response);
                    progressDialog.dismiss();
                },
                error -> {
                    dialogLessonStatus("Error", "Error connecting to the server. Please check your internet connection and try again.");
                    progressDialog.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", sharedPref.getStudentRegno());
                return params;
            }
        };

        if (getActivity() != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }
    }

    public void parseRefreshedLessons(String response) {
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

                    db.addLesson(unitname,teacher_name,start_time,my_attendance_time,status,id,unit_code,sem_name,year_name,total_students,total_attendance,weekInt);

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void dialogLessonStatusRefreshLessons(String title, String message) {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> {

                dialog.dismiss();

                lessons_from_db = null;
                lessons_from_db = db.getAllUnsavedLessons();

                mAdapter = new UnsavedLessonAdapter(lessons_from_db, UnsavedLessonsSide.this);
                recyclerView.setAdapter(mAdapter);

                checkIfEmpty();
//            RefreshLessons();

            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
