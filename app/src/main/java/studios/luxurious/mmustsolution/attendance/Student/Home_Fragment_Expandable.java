package studios.luxurious.mmustsolution.attendance.Student;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Student.Adapters.Lesson_Expandable_Adapter;
import studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Student.bluetooth.BluetoothController;
import studios.luxurious.mmustsolution.attendance.Utils;

public class Home_Fragment_Expandable extends BaseFragment {

    View myView;

    private BluetoothController bluetooth;

    String newUnit_name = null;
    String newUnit_id = null;


    private ExpandableListView expandableListView;
    private Lesson_Expandable_Adapter lessonExpandableAdapter;
    private List<String> listDataGroup;
    private HashMap<String, ArrayList<ArrayList<Object>>> listDataChild;


    SharedPref sharedPref;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    View bottom_sheet;


    ArrayList<String> ids, codes, names;

    ArrayAdapter<String> units_spinner_adapter;


    ProgressBar units_progressbar;
    ProgressBar master_progressbar;

    TextView semester, academic_year;
    DBAdapter db;

    RelativeLayout relative_empty;


    ProgressDialog progressDialog;

    boolean lessonFound = false;


    ArrayList<ArrayList<Object>> lessons_from_db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.student_home_layout_expandable, container, false);


        InitializeViews();
        setHasOptionsMenu(true);
        showBackButton(false);
        showFab();


        InitViews();
        initListeners();
        initObjects();
        initListData();


        checkIfEmpty(true);

        return myView;
    }

    private void InitViews() {

        expandableListView = myView.findViewById(R.id.lessons_expandableListView);

        relative_empty = myView.findViewById(R.id.relative_empty);
        progressDialog = new ProgressDialog(getActivity());

        ids = new ArrayList<>();
        codes = new ArrayList<>();
        names = new ArrayList<>();
        bottom_sheet = myView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);


        this.bluetooth = new BluetoothController(getActivity(), BluetoothAdapter.getDefaultAdapter(), Home_Fragment_Expandable.this);

        sharedPref = new SharedPref(getActivity());
        db = new DBAdapter(getActivity());
        db.open();

        if (getActivity() != null) {
            units_spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, names) {

                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }

                @Override
                public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

                    View view1 = super.getDropDownView(position, convertView, parent);
                    TextView textView = (TextView) view1;

                    if (position == 0) {
                        textView.setTextColor(Color.GRAY);
                    } else {

                        textView.setTextColor(Color.BLACK);
                    }

                    return view1;
                }
            };
        }

        if (getActivity() != null) {
            boolean hasBluetooth = getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
            if (!hasBluetooth) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                dialog.setTitle(getString(R.string.bluetooth_not_available_title));
                dialog.setMessage(getString(R.string.bluetooth_not_available_message));
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog1, which) -> {
                            dialog1.dismiss();
                            getActivity().finish();
                        });
                dialog.setCancelable(false);
                dialog.show();
            }
        }

    }

    @Override
    public void onClick(View v) {
        showBottomSheetDialogNewLesson();

    }

    private void initListeners() {

        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            if (((MainActivity) getActivity()).isDrawerOpen()) {

                return false;
            }

            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(80);

            new Handler().postDelayed(() -> {

                ArrayList<Object> lessons_list = lessons_from_db.get(childPosition);

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

            return false;
        });

    }

    private void initObjects() {

        // initializing the list of groups
        listDataGroup = new ArrayList<>();

        // initializing the list of child
        listDataChild = new HashMap<>();

        // initializing the adapter object
        lessonExpandableAdapter = new Lesson_Expandable_Adapter(getActivity(), listDataGroup, listDataChild);

        // setting list adapter
        expandableListView.setAdapter(lessonExpandableAdapter);

    }


    private void initListData() {

        List<Integer> weeks = db.getDistinctStudentWeeks();


        for (int i = 0; i < weeks.size(); i++) {

            listDataGroup.add("Week " + weeks.get(i));

            lessons_from_db = db.getAllLessonsWhereWeek(weeks.get(i));
            listDataChild.put(listDataGroup.get(i), lessons_from_db);

        }


        lessonExpandableAdapter.notifyDataSetChanged();
    }

    private void checkIfEmpty(boolean refresh) {


        if (db.getNumber_of_Lessons() == 0) {
            relative_empty.setVisibility(View.VISIBLE);

            if (refresh) {
                fetchData(sharedPref.getStudentRegno());
            }
        } else {
            relative_empty.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {

        ((MainActivity) Objects.requireNonNull(getActivity())).exit();

    }

    public void fetchUnits(String regno) {

        String URLline = Utils.getBaseUrl() + "api/Student_units/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    parseUnits(response);
                },
                error -> {
                    dialogErrormessage("Error", "Error connecting to the server. Please check your internet connection and try again.", "units");

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", regno);
                return params;
            }
        };

        if (getActivity() != null) {
            RequestQueue requestQueue = Volley.newRequestQueue((getActivity()));
            requestQueue.add(stringRequest);
        }

    }

    public void parseUnits(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {


                db.deleteUnits();

                if (!jsonObject.getString("optional_units").equals("null")) {


                    JSONArray my_optional_units = jsonObject.getJSONArray("optional_units");

                    for (int i = 0; i < my_optional_units.length(); i++) {

                        JSONObject un = my_optional_units.getJSONObject(i);

                        String unit_id = un.getString("id");
                        String unit_code = un.getString("unit_code");
                        String unit_name = un.getString("unit_name");

                        db.insertUnit(unit_id, unit_code, unit_name);


                    }
                }

                if (!jsonObject.getString("compulsory_units").equals("null")) {


                    JSONArray compulsory_units = jsonObject.getJSONArray("compulsory_units");
//                JSONArray all_Optional_units = jsonObject.getJSONArray("allOptionalunits");


                    for (int i = 0; i < compulsory_units.length(); i++) {
                        JSONObject un = compulsory_units.getJSONObject(i);

                        String unit_id = un.getString("id");
                        String unit_code = un.getString("unit_code");
                        String unit_name = un.getString("unit_name");

                        db.insertUnit(unit_id, unit_code, unit_name);


                    }

                }


                names.clear();
                ids.clear();
                codes.clear();

                names.add("Select Unit");
                ids.add("0");
                codes.add("0");


                ArrayList<ArrayList<Object>> units_from_db = db.getSudentUnits();

                for (int i = 0; i < units_from_db.size(); i++) {

                    ArrayList<Object> lesson = units_from_db.get(i);

                    String unit_id = (String) lesson.get(0);
                    String unit_code = (String) lesson.get(1);
                    String unit_name = (String) lesson.get(2);

                    names.add(unit_name);
                    ids.add(unit_id);
                    codes.add(unit_code);

                }

                units_spinner_adapter.notifyDataSetChanged();


            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialogError("martin", e.getMessage());
        }
    }

    private void fetchMasterData() {

        String URLline = Utils.getBaseUrl() + "api/Master_data";
        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("success").equals("true")) {
                    JSONArray dataArray = jsonObject.getJSONArray("message");

                    JSONObject dataobj = dataArray.getJSONObject(0);
                    String currentsemester = dataobj.getString("sem_name");
                    String currentyear = dataobj.getString("year_name");
                    String current_sem_id = dataobj.getString("sem_id");
                    String current_year_id = dataobj.getString("year_id");

                    sharedPref.setCurrent_sem(currentsemester);
                    sharedPref.setCurrent_semid(current_sem_id);

                    sharedPref.setCurrent_year(currentyear);
                    sharedPref.setCurrent_yearid(current_year_id);

                    semester.setText(currentsemester);
                    academic_year.setText(currentyear);

                    master_progressbar.setVisibility(View.GONE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {

            master_progressbar.setVisibility(View.GONE);
            dialogErrormessage("Error", "Failed to fetch some data . Please check your internet connection and try again", "master");

        });

        if (getActivity() != null) {
            Volley.newRequestQueue(getActivity()).add(request);
        }

    }

    public void fetchData(String regno) {


        String URLline = Utils.getBaseUrl() + "api/Student_Lesson";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseData(response);
                },
                error -> {
                    dialogErrormessage("Error", "Error connecting to the server. Please check your internet connection and try again.", "lessons");

                }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("regno", regno);
                return params;
            }
        };

        if (getActivity() != null) {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        }
    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {


                db.deleteLessons();

                if (!jsonObject.getString("lessons").equals("null")) {

                    JSONArray lessonsArray = jsonObject.getJSONArray("lessons");

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

                        db.addLesson(unitname, teacher_name, start_time, my_attendance_time, status, id, unit_code, sem_name, year_name, total_students, total_attendance, weekInt);
                    }
                }
                initListData();

                checkIfEmpty(false);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void dialogError(String title, String message) {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = builder.create();
            alert.show();

            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    }


    private void showBottomSheetDialogNewLesson() {

        if (getActivity() != null) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            assert vibrator != null;
            vibrator.vibrate(80);

        }


        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }


        @SuppressLint("InflateParams") final View bottom_sheet_view = getLayoutInflater().inflate(R.layout.new_lesson_bottom_sheet, null);
        units_progressbar = bottom_sheet_view.findViewById(R.id.progressbar_units);
        master_progressbar = bottom_sheet_view.findViewById(R.id.masterProgressbar);


        semester = bottom_sheet_view.findViewById(R.id.semester);
        academic_year = bottom_sheet_view.findViewById(R.id.academic_year);

        if (db.getNumber_of_StudentUnits() < 1) {
            units_progressbar.setVisibility(View.VISIBLE);
            fetchUnits(sharedPref.getStudentRegno());
        } else {
            units_progressbar.setVisibility(View.GONE);

            names.clear();
            ids.clear();
            codes.clear();

            names.add("Select Unit");
            ids.add("0");
            codes.add("0");


            ArrayList<ArrayList<Object>> units_from_db = db.getSudentUnits();

            for (int i = 0; i < units_from_db.size(); i++) {

                ArrayList<Object> lesson = units_from_db.get(i);

                String unit_id = (String) lesson.get(0);
                String unit_code = (String) lesson.get(1);
                String unit_name = (String) lesson.get(2);

                names.add(unit_name);
                ids.add(unit_id);
                codes.add(unit_code);

            }

        }

        if (sharedPref.getCurrent_sem() == null || sharedPref.getCurrent_semid() == null || sharedPref.getCurrent_year() == null || sharedPref.getCurrent_yearid() == null) {

            fetchMasterData();
        } else {
            semester.setText(sharedPref.getCurrent_sem());
            academic_year.setText(sharedPref.getCurrent_year());
            master_progressbar.setVisibility(View.GONE);
        }

        Spinner spinner = bottom_sheet_view.findViewById(R.id.spinner);
        final CardView start_lesson_card = bottom_sheet_view.findViewById(R.id.card);


        final TextView date = bottom_sheet_view.findViewById(R.id.date);

        String date_string = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());

        date.setText(date_string);

        units_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(units_spinner_adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    newUnit_name = names.get(position);
                    newUnit_id = ids.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        start_lesson_card.setOnClickListener(v -> {


            if (newUnit_id == null) {
                Toast.makeText(getActivity(), "Please select Unit", Toast.LENGTH_SHORT).show();
            } else {

                if (!bluetooth.isBluetoothEnabled()) {
                    bluetooth.turnOnBluetoothAndScheduleDiscovery();
                } else {
                    bluetooth.startDiscovery();
                }

                progressDialog.setTitle("Discovering");
                progressDialog.setMessage("Checking if lesson is ongoing");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                lessonFound = false;

                checkIfdiscoveryhasfinished();

            }
        });


        if (mBottomSheetDialog != null) {
            mBottomSheetDialog = null;
        }

        if (getActivity() != null) {
            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            mBottomSheetDialog.setContentView(bottom_sheet_view);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(mBottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mBottomSheetDialog.show();

        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }


    private void showBottomSheetDialog(String date, String startTime, String unitName, String lesson_status, String teacher_name, String attendance, String unit_code, String sem_name, String year_name, String my_attendance_time) {


        if (getActivity() != null) {
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
                lesson_status_image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tick));
            } else {

                lesson_status_image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cancel));
            }


            mBottomSheetDialog = new BottomSheetDialog(getActivity());
            mBottomSheetDialog.setContentView(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Objects.requireNonNull(mBottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
        }
    }

    private long getCurrentTimeinMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public void dialogErrormessage(String title, String message, String list) {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            builder.setNegativeButton("Try again", (dialog, which) -> {

                if (list.equalsIgnoreCase("master")) {
                    dialog.dismiss();
                    fetchMasterData();
                } else if (list.equalsIgnoreCase("lessons")) {
                    dialog.dismiss();
                    fetchData(sharedPref.getStudentRegno());
                } else if (list.equalsIgnoreCase("units")) {
                    dialog.dismiss();
                    fetchUnits(sharedPref.getStudentRegno());
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public void onDestroy() {
        bluetooth.close();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }


    public void dialogmessage(String title, String unit_id, String start_timei) {


        if (getActivity() != null) {
            long timee = getCurrentTimeinMillis();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage("You have successfully marked for attendance of " + newUnit_name + " at " + Utils.getTime(timee));
            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = builder.create();
            alert.show();


            String unit_code = "Code";
            String unit_name = "unit nameee";
            ArrayList<String> details = db.getUnitCodeandName(unit_id);

            if (details.size() > 1) {
                unit_code = details.get(0);
                unit_name = details.get(1);
            }

            if (Utils.IsConnectedToInternet(getActivity())) {
                StartLesson(unit_id, start_timei, String.valueOf(timee), sharedPref.getStudentRegno(), unit_code, unit_name);
            } else {

                db.addUnsavedLesson(unit_id, start_timei, String.valueOf(timee), sharedPref.getStudentRegno(), unit_code, unit_name);

            }

            lessonFound = true;
            mBottomSheetDialog.dismiss();
            progressDialog.dismiss();
        }
    }

    public void StartLesson(String unit_id, String start_time, String my_attendance_time, String regno, String unit_code, String unit_name) {

        ProgressDialog start_lesson_progressDialog = new ProgressDialog(getActivity());
        start_lesson_progressDialog.setTitle("Please wait");
        start_lesson_progressDialog.setMessage("Syncing");
        start_lesson_progressDialog.setCancelable(false);
        start_lesson_progressDialog.setCanceledOnTouchOutside(false);
        start_lesson_progressDialog.show();

        String start_lesson_url = Utils.getBaseUrl() + "api/Student_attend/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, start_lesson_url,
                response -> {
                    start_lesson_progressDialog.dismiss();
                    checkLesson(response, unit_id, start_time, my_attendance_time, unit_code, unit_name);
                    if (start_lesson_progressDialog.isShowing())
                        start_lesson_progressDialog.dismiss();
                },
                (VolleyError error) -> {

                    db.addUnsavedLesson(unit_id, start_time, my_attendance_time, sharedPref.getStudentRegno(), unit_code, unit_name);

                    dialogError("Internet error", "An error occurred while trying to attend the lesson. The lesson has been saved in the pending lessons page. Please look for a good network connection and sync the lesson");
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

    private void checkLesson(String response, String unit_id, String start_time, String my_attendance_time, String unit_code, String unit_name) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                dialogError("Success", jsonObject.getString("message"));

                fetchData(sharedPref.getStudentRegno());
//                RefreshLessons();

            } else {
                db.addUnsavedLesson(unit_id, start_time, my_attendance_time, sharedPref.getStudentRegno(), unit_code, unit_name);

                dialogError("Internet error", "An error occurred while trying to start the lesson. The lesson has been saved in the pending lessons page. Please look for a good network connection and sync the lesson");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getUnitID() {
        return newUnit_id;
    }


    public void checkIfdiscoveryhasfinished() {

        new Handler().postDelayed(() -> {
            if (!bluetooth.isDiscovering()) {
                if (!lessonFound) {
                    dialogError("Oops!", "No lesson is ongoing for the unit selected or the lecturer has terminated the lecture.");

                    mBottomSheetDialog.dismiss();
                }
                progressDialog.dismiss();

            } else {
                checkIfdiscoveryhasfinished();
            }
        }, 5000);
    }
}
