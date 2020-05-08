package studios.luxurious.mmustsolution.attendance.Teacher;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters.Units_adapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.RecyclerTouchListener;
import studios.luxurious.mmustsolution.attendance.Utils;

public class Teacher_Units_Fragments extends BaseFragment {

    View myView;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    SharedPref sharedPref;
    DBAdapter db;
    ArrayList<ArrayList<Object>> units_from_db;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout relative_empty;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.teacher_units_layout, container, false);

        setHasOptionsMenu(true);
        InitializeViews();

        showBackButton(false);
        hideFab();

        sharedPref = new SharedPref(getActivity());

        recyclerView = myView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = myView.findViewById(R.id.swipeRefreshLayout);
        relative_empty = myView.findViewById(R.id.relative_empty);


        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchData(sharedPref.getTeacherCode()));

        db = new DBAdapter(getActivity());
        db.open();
        units_from_db = db.getTeacherUnits();
        mAdapter = new Units_adapter(units_from_db);
        recyclerView.setAdapter(mAdapter);

        checkIfEmpty(true);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {


                TextView Txt_unit_code = view.findViewById(R.id.unit_code);
                TextView Txt_unit_name = view.findViewById(R.id.unit_name);
                TextView Txt_unit_id = view.findViewById(R.id.unit_id);
                String unit_code = Txt_unit_code.getText().toString().trim();
                String unit_name = Txt_unit_name.getText().toString().trim();
                String unit_id = Txt_unit_id.getText().toString().trim();

                Intent single = new Intent(getActivity(), Single_Unit.class);
                single.putExtra("code", unit_code);
                single.putExtra("name", unit_name);
                single.putExtra("id", unit_id);
                startActivity(single);

            }

            @Override
            public void onLongClick(View view, final int position) {


            }
        }));


        return myView;
    }

    @Override
    public void onClick(View v) {

    }


    private void checkIfEmpty(boolean refresh) {


        if (db.getNumber_of_TeacherUnits() == 0) {

            if (refresh) {
                fetchData(sharedPref.getTeacherCode());
            } else {

                relative_empty.setVisibility(View.VISIBLE);
            }
        } else {
            relative_empty.setVisibility(View.GONE);
        }

    }


    @Override
    public void onBackPressed() {
        goBackHome();
    }


    private void fetchData(String teacher_code) {

        swipeRefreshLayout.setRefreshing(true);
        String URLline = Utils.getBaseUrl() + "api/teacher_units/" + teacher_code;

        StringRequest request = new StringRequest(URLline, response -> {
            if (response == null) {
                Toast.makeText(getActivity(), "Response was null", Toast.LENGTH_LONG).show();
                return;
            }
            parseData(response);
            swipeRefreshLayout.setRefreshing(false);

        }, error -> {
            Log.e("INFO", "Error: " + error.getMessage());
            dialogErrormessage("Error", "Error fetching your units. Check your network connection and try again later", "");
            swipeRefreshLayout.setRefreshing(false);
        });

        Volley.newRequestQueue(getActivity()).add(request);

    }


    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {


                db.deleteUnits();


                if (!jsonObject.getString("message").equals("null")) {


                    JSONArray dataArray = jsonObject.getJSONArray("message");

                    for (int i = 0; i < dataArray.length(); i++) {

                        JSONObject un = dataArray.getJSONObject(i);

                        String unit_id = un.getString("unit_id");
                        String unit_code = un.getString("unit_code");
                        String unit_name = un.getString("unit_name");

                        db.insertUnit(unit_id, unit_code, unit_name);
                    }

                }


                units_from_db = db.getTeacherUnits();
                mAdapter = new Units_adapter(units_from_db);
                recyclerView.setAdapter(mAdapter);

                checkIfEmpty(false);


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

            swipeRefreshLayout.setRefreshing(false);
            dialog.dismiss();
        });

        builder.setNegativeButton("Try again", (dialog, which) -> {
            fetchData(sharedPref.getTeacherCode());
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
