package studios.luxurious.mmustsolution.attendance.Teacher;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters.UnsavedLessonAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Utils;

public class UnsavedLessonsSide extends BaseFragment {
    View myView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    RelativeLayout relative_empty;
    ArrayList<ArrayList<Object>> lessons_from_db;


    ProgressDialog start_lesson_progressDialog;
    DBAdapter db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.teacher_pending_lessons_units, container, false);

        setHasOptionsMenu(true);
        InitializeViews();

        showBackButton(false);
        hideFab();

        db = new DBAdapter(getActivity());
        db.open();

        start_lesson_progressDialog = new ProgressDialog(getActivity());

        lessons_from_db = db.getAllUnsavedLessons();
        relative_empty = myView.findViewById(R.id.relative_empty);


        checkIfEmpty();

        recyclerView = myView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new UnsavedLessonAdapter(lessons_from_db, getActivity(), UnsavedLessonsSide.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void StartLesson(String unit_id, String teacher_code, String starttime, String sem_id, String year_id,String id) {

        start_lesson_progressDialog.setTitle("Please wait");
        start_lesson_progressDialog.setMessage("Syncing");
        start_lesson_progressDialog.setCancelable(false);
        start_lesson_progressDialog.setCanceledOnTouchOutside(false);
        start_lesson_progressDialog.show();

        String start_lesson_url = Utils.getBaseUrl()+ "api/Lessons/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, start_lesson_url,
                response -> {
                    start_lesson_progressDialog.dismiss();
                    checkLesson(response,id);
                },
                (VolleyError error) -> {

                    dialogLessonStatus("Server Error", "An error occurred while trying to start the lesson. Please try again");
                    start_lesson_progressDialog.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("unit_id", unit_id);
                params.put("teacher_code", teacher_code);
                params.put("start_time", starttime);
                params.put("sem_id", sem_id);
                params.put("year_id", year_id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


    }

    private void checkLesson(String response,String id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                dialogLessonStatusRefreshLessons("Success", "Lesson was successfully sent to the online database");

                int i= Integer.parseInt(id);

                db.deleteUnsavedUnit(i);

                lessons_from_db = null;
                lessons_from_db = db.getAllUnsavedLessons();

                mAdapter = new UnsavedLessonAdapter(lessons_from_db, getActivity(), UnsavedLessonsSide.this);
                recyclerView.setAdapter(mAdapter);

                checkIfEmpty();

                AllLessons();

            } else {
                dialogLessonStatus("Error", "Lesson could not start. Please try again");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void AllLessons() {

        if (getActivity() != null) {
            SharedPref sharedPref = new SharedPref(getActivity());
            String URLline = Utils.getBaseUrl() + "api/teacher_lessons/" + sharedPref.getTeacherCode();
            StringRequest request = new StringRequest(URLline, response -> {
                if (response == null) {
                    Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                    return;
                }
                parseAllLessons(response);
            }, error -> {
            });

            if (getActivity()!= null){ Volley.newRequestQueue(getActivity()).add(request);}
        }
    }


    public void parseAllLessons(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {


                db.deleteLessons();

                if (!jsonObject.getString("message").equals("null")) {

                    JSONArray dataArray = jsonObject.getJSONArray("message");
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
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void dialogLessonStatusRefreshLessons(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();

            lessons_from_db = null;
            lessons_from_db = db.getAllUnsavedLessons();

            mAdapter = new UnsavedLessonAdapter(lessons_from_db, getActivity(), UnsavedLessonsSide.this);
            recyclerView.setAdapter(mAdapter);

            checkIfEmpty();
//            RefreshLessons();

        });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
