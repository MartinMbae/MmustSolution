package studios.luxurious.mmustsolution.attendance.Student;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Student.Adapters.Units_adapter;
import studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.RecyclerTouchListener;
import studios.luxurious.mmustsolution.attendance.Utils;

public class Student_Units_Fragments extends BaseFragment {

    View myView;
    private RecyclerView core_recyclerView;
    private RecyclerView.Adapter mAdapter;
    SharedPref sharedPref;
    ArrayList<ArrayList<Object>> units_from_db;


    RelativeLayout relative_empty;
    DBAdapter db;
    SwipeRefreshLayout swipeRefreshLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.student_units_layout, container, false);

        setHasOptionsMenu(true);
        InitializeViews();

        showBackButton(false);
        hideFab();

        sharedPref = new SharedPref(getActivity());

        core_recyclerView = myView.findViewById(R.id.core_recycler_view);
        swipeRefreshLayout = myView.findViewById(R.id.swipe_refresh_layout);
        relative_empty = myView.findViewById(R.id.relative_empty);

        core_recyclerView.setNestedScrollingEnabled(false);
        db = new DBAdapter(getActivity());
        db.open();

        units_from_db = db.getSudentUnits();

        mAdapter = new Units_adapter(units_from_db);
        core_recyclerView.setAdapter(mAdapter);

        core_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        checkIfEmpty(true);


        swipeRefreshLayout.setOnRefreshListener(()-> fetchData(sharedPref.getStudentRegno()) );


        core_recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), core_recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                TextView Txt_unit_code = view.findViewById(R.id.unit_code);
                TextView Txt_unit_name = view.findViewById(R.id.unit_name);
                String code = Txt_unit_code.getText().toString().trim();
                String name = Txt_unit_name.getText().toString().trim();

                Intent single = new Intent(getActivity(), Single_Unit.class);
                single.putExtra("code", code);
                single.putExtra("name", name);
                startActivity(single);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return myView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        goBackHome();
    }



    private void checkIfEmpty(boolean refresh) {



        if (db.getNumber_of_StudentUnits() == 0 ){
            relative_empty.setVisibility(View.VISIBLE);

            if (refresh) {
                fetchData(sharedPref.getStudentRegno());
            }
        }else {
            relative_empty.setVisibility(View.GONE);
        }

    }


    public void fetchData(String regno){


        swipeRefreshLayout.setRefreshing(true);
        String URLline = Utils.getBaseUrl()+ "api/Student_units/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLline,
                response -> {
                    if (response == null) {
                        Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseData(response);

                    swipeRefreshLayout.setRefreshing(false);
                },
                error -> {
                    dialogError("Error","Error connecting to the server. Please check your internet connection and try again.");

                    swipeRefreshLayout.setRefreshing(false);
                }){
            @Override
            protected Map<String,String> getParams(){
                HashMap<String,String> params = new HashMap<>();
                params.put("regno",regno);
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


                db.deleteUnits();

                if (!jsonObject.getString("compulsory_units").equals("null")) {

                    JSONArray compulsory_units = jsonObject.getJSONArray("compulsory_units");

                    for (int i = 0; i < compulsory_units.length(); i++) {
                        JSONObject un = compulsory_units.getJSONObject(i);

                        String unit_id = un.getString("id");
                        String unit_code = un.getString("unit_code");
                        String unit_name = un.getString("unit_name");


                        db.insertUnit(unit_id, unit_code, unit_name);


                    }
                }


                if (!jsonObject.getString("optional_units").equals("null")) {



                    JSONArray my_optional_units = jsonObject.getJSONArray("optional_units");
//                JSONArray all_Optional_units = jsonObject.getJSONArray("allOptionalunits");

                    for (int i = 0; i < my_optional_units.length(); i++) {

                        JSONObject un = my_optional_units.getJSONObject(i);

                        String unit_id = un.getString("id");
                        String unit_code = un.getString("unit_code");
                        String unit_name = un.getString("unit_name");

                        db.insertUnit(unit_id, unit_code, unit_name);

                    }

                }

                units_from_db = db.getSudentUnits();

                mAdapter = new Units_adapter(units_from_db);
                core_recyclerView.setAdapter(mAdapter);

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

            builder.setNegativeButton("Try again", (dialog, which) -> {
                dialog.dismiss();
                fetchData(sharedPref.getStudentRegno());
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }



}
