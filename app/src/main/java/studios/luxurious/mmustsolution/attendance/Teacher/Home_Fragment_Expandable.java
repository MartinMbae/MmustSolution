package studios.luxurious.mmustsolution.attendance.Teacher;


import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.CryptUtil;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters.Lesson_Expandable_Adapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.bluetooth.BluetoothController;
import studios.luxurious.mmustsolution.attendance.Utils;

public class Home_Fragment_Expandable extends BaseFragment {

    View myView;
    RelativeLayout relative_content, relative_empty;
    ArrayAdapter<String> units_spinner_adapter;
    ArrayList<String> unit_names = new ArrayList<>();
    ArrayList<String> unit_ids = new ArrayList<>();
    ArrayList<String> unit_codes = new ArrayList<>();
    private BluetoothController bluetooth;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    View bottom_sheet;
    ArrayList<ArrayList<Object>> lessons_from_db;
    long start_time_timestamp = 0;
    long right_now_timestamp = 0;

    SharedPref sharedPref;

    Calendar calendar_startTime;
    Calendar calendar_rightnowtime;

    ProgressBar units_progressbar;
    ProgressBar master_progressbar;
    ProgressDialog start_lesson_progressDialog;
    TextView semester, academic_year;
    DBAdapter db;

    CardView cardActiveLesson;
    Button endActiveLesson;

    private ExpandableListView expandableListView;
    private Lesson_Expandable_Adapter lessonExpandableAdapter;
    private List<String> listDataGroup;
    private HashMap<String, ArrayList<ArrayList<Object>>> listDataChild;

    List<String> startedUnitNames, startedUnitCodes;
    String startedUnitIds = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.teacher_home_fragment_expandable, container, false);

        sharedPref = new SharedPref(getActivity());


        endActiveLesson = myView.findViewById(R.id.endLesson);
        cardActiveLesson = myView.findViewById(R.id.cardActiveLesson);


        InitializeViews();
        setHasOptionsMenu(true);
        showBackButton(false);
        showFab();


        InitViews();
        initListeners();
        initObjects();
        initListData();


        checkIfEmpty(true);


        if (db.getNumber_of_TeacherUnits() < 1) {
            fetchUnits(sharedPref.getTeacherCode());
        }


        return myView;
    }


    private void InitViews() {

        expandableListView = myView.findViewById(R.id.lessons_expandableListView);

        relative_empty = myView.findViewById(R.id.relative_empty);

        startedUnitCodes = new ArrayList<>();
        startedUnitNames = new ArrayList<>();

        db = new DBAdapter(getActivity());
        db.open();
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
        } else {

            refreshCurrentLesson();
        }

        endActiveLesson.setOnClickListener(v -> {

            dialogEndLesson("End Lesson", "Are you sure that you want to end this lesson.\n This action is irreversible");
        });

        this.bluetooth = new BluetoothController(getActivity(), BluetoothAdapter.getDefaultAdapter());

        start_lesson_progressDialog = new ProgressDialog(getActivity());

        relative_content = myView.findViewById(R.id.relative_content);
        relative_empty = myView.findViewById(R.id.relative_empty);


        calendar_rightnowtime = Calendar.getInstance();
        bottom_sheet = myView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);


    }


    private void initListeners() {

        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            if (((MainActivity) getActivity()).isDrawerOpen()) {

                return false;
            }
            TextView textView = v.findViewById(R.id.lesson_id);
            String lesson_id = textView.getText().toString().trim();

            Intent students = new Intent(getActivity(), StudentAttendance.class);
            students.putExtra("lesson_id", lesson_id);
            startActivity(students);

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

        ArrayList<String> weeks = db.getAllDistinctWeeks();

        listDataGroup.clear();


        for (int i = 0; i < weeks.size(); i++) {

            listDataGroup.add("Week " + weeks.get(i));

            lessons_from_db = db.getAllLessonsWhereWeek(weeks.get(i));
            listDataChild.put(listDataGroup.get(i), lessons_from_db);

        }


        lessonExpandableAdapter.notifyDataSetChanged();
    }

    private void refreshCurrentLesson() {


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


            String bluetoothName = getMyBluetoothName();

            try {


                String[] fetched = bluetoothName.split("split");
                String unit_ids = fetched[0].trim();
                String start_time = fetched[1].trim();


                String decrypted_unit_ids = CryptUtil.decrypt(unit_ids);
                String decrypted_start_time = CryptUtil.decrypt(start_time);

                String[] unit_idss = decrypted_unit_ids.split(",");

                HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();

                for (int i = 0; i < unit_idss.length; i++) {

                    ArrayList<String> details = db.getUnitCodeandName(unit_idss[i]);

                    hashMap.put(i, details);

                }


                if (hashMap.size() > 0) {

                    String list = "";
                    for (int i = 0; i < hashMap.size(); i++) {

                        ArrayList<String> unit = hashMap.get(i);

                        list = list + (i + 1) + ". " + unit.get(0) + " " + unit.get(1) + "\n";

                    }
                    if (bluetoothAdapter.isEnabled() && (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {

                        cardActiveLesson.setVisibility(View.VISIBLE);

                    TextView activeTitle, activeUnitname, activestartTime;

                    activeTitle = myView.findViewById(R.id.title);
                    activeUnitname = myView.findViewById(R.id.activeUnit_name);
                    activestartTime = myView.findViewById(R.id.activeStartTime);
//
                    if (hashMap.size() == 1) {

                        String title = "You have an ongoing lecture";
                        activeTitle.setText(title);
                    } else {

                        String title = "You have " + hashMap.size() + " ongoing lectures";
                        activeTitle.setText(title);
                    }
                    activeUnitname.setText(list);
                    activestartTime.setText(String.format("Started at %s", Utils.getTime(Long.parseLong(decrypted_start_time))));

                    } else {

                        cardActiveLesson.setVisibility(View.GONE);
                        dialogTurnONBluetooth("Not Discoverable", "Your device keeps losing bluetooth discoverability. Please turn it on again");


                    }
                }

                long now = System.currentTimeMillis();
                if ((Long.parseLong(decrypted_start_time) + (3600000)) < now) {
                    endLesson();
                }

            } catch (Exception e) {
                cardActiveLesson.setVisibility(View.GONE);

            }


    }

    private void endLesson() {


        String device_Name = Build.MODEL;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.setName(device_Name);

        BluetoothController bluetoothController = new BluetoothController(getActivity(), bluetoothAdapter);
        bluetoothController.turnOffBluetooth();


        new Handler().postDelayed(this::refreshCurrentLesson,2000);
    }

    private void showTime(String time, EditText editText) {

        editText.setText(time);
    }


    private void checkIfEmpty(boolean refresh) {
        if (db.getNumber_of_Lessons() < 1) {

            if (refresh) {
                RefreshLessons();
            } else {

                relative_empty.setVisibility(View.VISIBLE);
            }


        } else {
            relative_empty.setVisibility(View.GONE);
        }

    }


    private void RefreshLessons() {
        new Handler().postDelayed(() -> {
            if (Utils.isNetworkAvailable(getActivity())) {
                AllLessons();
            } else {
                Toast.makeText(getActivity(), "Whoops, You have no internet connection.", Toast.LENGTH_SHORT).show();

            }

        }, 1500);
    }


    @Override
    public void onClick(View v) {


        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled()) {


            if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                dialogTurnONBluetooth("Not Discoverable", "Make sure that your bluetooth device can be discovered by other devices and try again.");


            } else {

                showBottomSheetDialog();
            }
        } else {


            dialogTurnONBluetooth("Turn your bluetooth on", "Make sure that your bluetooth is turned on before you continue with the next step");
        }
    }

    public String getMyBluetoothName() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        String bluetoothName = bluetoothAdapter.getName();

        if (bluetoothName != null) {
            return bluetoothName;
        } else {
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        ((MainActivity) Objects.requireNonNull(getActivity())).exit();
    }

    private void showBottomSheetDialog() {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View bottom_sheet_view = getLayoutInflater().inflate(R.layout.teacher_start_lesson_bottom_sheet, null);
        units_progressbar = bottom_sheet_view.findViewById(R.id.progressbar_units);
        master_progressbar = bottom_sheet_view.findViewById(R.id.masterProgressbar);

        semester = bottom_sheet_view.findViewById(R.id.semester);
        academic_year = bottom_sheet_view.findViewById(R.id.academic_year);

        if (db.getNumber_of_TeacherUnits() < 1) {
            units_progressbar.setVisibility(View.VISIBLE);
            fetchUnits(sharedPref.getTeacherCode());
        } else {
            units_progressbar.setVisibility(View.GONE);

            unit_names.clear();
            unit_ids.clear();
            unit_codes.clear();


            ArrayList<ArrayList<Object>> units_from_db = db.getTeacherUnits();

            for (int i = 0; i < units_from_db.size(); i++) {

                ArrayList<Object> lesson = units_from_db.get(i);

                String unit_id = (String) lesson.get(0);
                String unit_code = (String) lesson.get(1);
                String unit_name = (String) lesson.get(2);

                unit_names.add(unit_name);
                unit_ids.add(unit_id);
                unit_codes.add(unit_code);

            }


        }

        if (sharedPref.getCurrent_sem() == null || sharedPref.getCurrent_semid() == null || sharedPref.getCurrent_year() == null || sharedPref.getCurrent_yearid() == null) {

            fetchMasterData();
        } else {
            semester.setText(sharedPref.getCurrent_sem());
            academic_year.setText(sharedPref.getCurrent_year());
            master_progressbar.setVisibility(View.GONE);
        }


        MultiSpinnerSearch searchMultiSpinnerUnlimited = bottom_sheet_view.findViewById(R.id.searchMultiSpinnerUnlimited);

        searchMultiSpinnerUnlimited.setEmptyTitle("Not Data Found!");
        searchMultiSpinnerUnlimited.setSearchHint("Search Unit");

        final EditText start_timer = bottom_sheet_view.findViewById(R.id.start_time);
        final CardView start_lesson_card = bottom_sheet_view.findViewById(R.id.card);


        final TextView date = bottom_sheet_view.findViewById(R.id.date);

        String date_string = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());

        date.setText(date_string);

        calendar_startTime = Calendar.getInstance();


        right_now_timestamp = calendar_rightnowtime.getTimeInMillis();


        if (start_time_timestamp == 0) {
            start_time_timestamp = calendar_startTime.getTimeInMillis();

        } else {

            calendar_startTime.setTimeInMillis(start_time_timestamp);
            start_time_timestamp = calendar_startTime.getTimeInMillis();

        }


        showTime(Utils.getTime(calendar_startTime.getTimeInMillis()), start_timer);

        start_timer.setOnClickListener(v -> {

            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(start_time_timestamp);
            int hour = startCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = startCalendar.get(Calendar.MINUTE);


            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getActivity(), (timePicker, selectedHour, selectedMinute) -> {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar1.set(Calendar.MINUTE, selectedMinute);


                start_time_timestamp = calendar1.getTimeInMillis();

                showTime(Utils.getTime(start_time_timestamp), start_timer);


            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });


        units_spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, unit_names) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

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


        final List<KeyPairBoolData> listArray0 = new ArrayList<>();

        for (int i = 0; i < unit_names.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(unit_names.get(i));
            h.setSelected(false);
            listArray0.add(h);
        }


        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                startedUnitCodes.clear();
                startedUnitIds = null;
                startedUnitNames.clear();

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {


                        startedUnitCodes.add(unit_codes.get(i));
                        startedUnitNames.add(unit_names.get(i));

                        if (startedUnitIds == null) {
                            startedUnitIds = unit_ids.get(i);
                        } else {

                            startedUnitIds = startedUnitIds + "," + unit_ids.get(i);
                        }

                    }

                }

            }
        });


        start_lesson_card.setOnClickListener(v -> {


            if (startedUnitIds == null) {
                Toast.makeText(getActivity(), "Please select Unit", Toast.LENGTH_SHORT).show();
            } else if (right_now_timestamp > start_time_timestamp) {
                Toast.makeText(getActivity(), "Your start time cannot be earlier than now", Toast.LENGTH_SHORT).show();
            } else {


                String lessonStartTime = String.valueOf(start_time_timestamp);

                try {
                    String idss = CryptUtil.encrypt(startedUnitIds);
                    String timeeeee = CryptUtil.encrypt(lessonStartTime);


                    String bt_name = idss + "split" + timeeeee;
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    bluetoothAdapter.setName(bt_name);


                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                String response = "Lesson has been set to start at " + Utils.getTime(start_time_timestamp) + ". Attendance will only be posible for the next one hour as long as your bluetooth remains turned on.";

                dialogmessage("Success", response);
                mBottomSheetDialog.dismiss();


                if (Utils.IsConnectedToInternet(getActivity())) {
                    StartLesson(startedUnitIds, sharedPref.getTeacherCode(), String.valueOf(start_time_timestamp), sharedPref.getCurrent_semid(), sharedPref.getCurrent_yearid());
                } else {

                    db.addUnsavedLesson(startedUnitIds, sharedPref.getTeacherCode(), String.valueOf(start_time_timestamp), sharedPref.getCurrent_semid(), sharedPref.getCurrent_yearid());

                }


                new Handler().postDelayed(this::refreshCurrentLesson, 2000);


            }


        });


        if (mBottomSheetDialog != null) {
            mBottomSheetDialog = null;
        }
        mBottomSheetDialog = new BottomSheetDialog(getActivity());
        mBottomSheetDialog.setContentView(bottom_sheet_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mBottomSheetDialog.show();

        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }


    public void StartLesson(String unit_id, String teacher_code, String starttime, String sem_id, String year_id) {

        start_lesson_progressDialog.setTitle("Please wait");
        start_lesson_progressDialog.setMessage("Starting lesson");
        start_lesson_progressDialog.setCancelable(false);
        start_lesson_progressDialog.setCanceledOnTouchOutside(false);
        start_lesson_progressDialog.show();

        String start_lesson_url = Utils.getBaseUrl() + "api/Lessons/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, start_lesson_url,
                response -> {
                    start_lesson_progressDialog.dismiss();
                    checkLesson(response, teacher_code, starttime, sem_id, year_id);
                },
                (VolleyError error) -> {


                    db.addUnsavedLesson(startedUnitIds, teacher_code, starttime, sem_id, year_id);
                    dialogmessage("Internet error", "An error occurred while trying to start the lesson. The lesson has been saved in the pending lessons page. Please look for a good network connection and sync the lesson");
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


    private void checkLesson(String response, String teacher_code, String starttime, String sem_id, String year_id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                dialogmessage("Success", "Lesson was successfully started");

                RefreshLessons();

            } else {
                db.addUnsavedLesson(startedUnitIds, teacher_code, starttime, sem_id, year_id);

                dialogmessage("Internet error", "An error occurred while trying to start the lesson. The lesson has been saved in the pending lessons page. Please look for a good network connection and sync the lesson");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void AllLessons() {
        String URLline = Utils.getBaseUrl() + "api/teacher_lessons/" + sharedPref.getTeacherCode();
        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                return;
            }
            parseAllLessons(response);
        }, error -> {
            dialogErrormessage("Error", "Failed to fetch your previous lectures. Please check your internet connection and try again", "lessons");
        });

        Volley.newRequestQueue(getActivity()).add(request);

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

                        db.addLesson(id, unit_id, teacher_code, start_time, semester_name, year_name, sem_id, year_id, unit_name, course_name, weekNumber);

                    }
                }

                initListData();

                checkIfEmpty(false);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialogErrormessage("Error", e.toString(), "lessons");
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

                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }, error -> {

            master_progressbar.setVisibility(View.GONE);
            dialogErrormessage("Error", "Failed to fetch some data . Please check your internet connection and try again", "master");

        });

        Volley.newRequestQueue(getActivity()).add(request);

    }

    private void fetchUnits(String teacher_code) {

        String URLline = Utils.getBaseUrl() + "api/SimpleTeacher_Units/" + teacher_code;

        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                return;
            }

            parseUnitsData(response);

            try {
                units_progressbar.setVisibility(View.GONE);
            } catch (Exception ignored) {
            }

        }, error -> {

            try {
                units_progressbar.setVisibility(View.GONE);
            } catch (Exception ignored) {
            }
            dialogErrormessage("Error", "Failed to fetch your units. Please check your internet connection and try again", "units");


        });

        Volley.newRequestQueue(getActivity()).add(request);

    }

    public void parseUnitsData(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("message");

                db.deleteUnits();
                unit_ids.clear();
                unit_codes.clear();
                unit_names.clear();

                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject un = dataArray.getJSONObject(i);

                    String unit_id = un.getString("unit_id");
                    String unit_code = un.getString("unit_code");
                    String unit_name = un.getString("unit_name");

                    db.insertUnit(unit_id, unit_code, unit_name);

                }


                unit_names.clear();
                unit_ids.clear();
                unit_codes.clear();


                ArrayList<ArrayList<Object>> units_from_db = db.getTeacherUnits();

                for (int i = 0; i < units_from_db.size(); i++) {

                    ArrayList<Object> lesson = units_from_db.get(i);

                    String unit_id = (String) lesson.get(0);
                    String unit_code = (String) lesson.get(1);
                    String unit_name = (String) lesson.get(2);

                    unit_names.add(unit_name);
                    unit_ids.add(unit_id);
                    unit_codes.add(unit_code);

                }


                try {
                    units_spinner_adapter.notifyDataSetChanged();
                } catch (Exception ignored) {
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void dialogErrormessage(String title, String message, String list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
        });

        builder.setNegativeButton("Try again", (dialog, which) -> {

            if (list.equalsIgnoreCase("units")) {
                dialog.dismiss();
                fetchUnits(sharedPref.getTeacherCode());

            } else if (list.equalsIgnoreCase("master")) {
                dialog.dismiss();
                fetchMasterData();
            } else if (list.equalsIgnoreCase("lessons")) {
                dialog.dismiss();
                RefreshLessons();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void dialogmessage(String title, String message) {
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


    public void dialogEndLesson(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("End Lesson", (dialog, which) -> {

            endLesson();
            dialog.dismiss();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {

            dialog.dismiss();
        });


        AlertDialog alert = builder.create();
        alert.show();
    }


    public void dialogTurnONBluetooth(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Turn on", (dialog, which) -> {


            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.enable();


            Intent t = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            t.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivityForResult(t, 0);

            new Handler().postDelayed(this::refreshCurrentLesson,7000);

            dialog.dismiss();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });


        AlertDialog alert = builder.create();
        alert.show();
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
}
