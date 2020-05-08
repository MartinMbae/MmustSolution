package studios.luxurious.mmustsolution.attendance.Teacher;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.tableview.TableViewAdapter;
import studios.luxurious.mmustsolution.attendance.Teacher.tableview.TableViewListener;
import studios.luxurious.mmustsolution.attendance.Teacher.tableview.model.Cell;
import studios.luxurious.mmustsolution.attendance.Teacher.tableview.model.ColumnHeader;
import studios.luxurious.mmustsolution.attendance.Teacher.tableview.model.RowHeader;
import studios.luxurious.mmustsolution.attendance.Utils;

public class MainFragment extends Fragment {

    private List<RowHeader> m_jRowHeaderList;
    private List<ColumnHeader> m_jColumnHeaderList;
    private List<List<Cell>> m_jCellList;

    private AbstractTableAdapter m_iTableViewAdapter;
    private TableView m_iTableView;

    SharedPref sharedPref;
    String lesson_id = null;

    ArrayList<ArrayList<Object>> allstudents;
    DBAdapter dbAdapter;

    String[] header_titles = {"Regno", "Student Name", "Gender", "Status", "Attendance time"};

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setFullScreenMode();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_fragment_main, container, false);

        RelativeLayout fragment_container = view.findViewById(R.id
                .fragment_container);

        // Create Table view
        m_iTableView = createTableView();
        fragment_container.addView(m_iTableView);

        sharedPref = new SharedPref(getActivity());
        dbAdapter = new DBAdapter(getActivity());
        dbAdapter.open();

        lesson_id = sharedPref.getLesson_id();
        allstudents = new ArrayList<>();

        if (lesson_id != null) {


            allstudents = dbAdapter.getAllStudentLessons(lesson_id);
            initData();
            loadData();


        } else {
            Toast.makeText(getActivity(), "No lesson was found", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private TableView createTableView() {
        TableView tableView = new TableView(getContext());

        // Set adapter
        m_iTableViewAdapter = new TableViewAdapter(getContext());
        tableView.setAdapter(m_iTableViewAdapter);

        // Set layout params
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        tableView.setLayoutParams(tlp);

        // Set TableView listener
        tableView.setTableViewListener(new TableViewListener(tableView));
        return tableView;
    }


    private void initData() {
        m_jRowHeaderList = new ArrayList<>();
        m_jColumnHeaderList = new ArrayList<>();
        m_jCellList = new ArrayList<>();
        for (int i = 0; i < allstudents.size(); i++) {
            m_jCellList.add(new ArrayList<Cell>());
        }
    }

    private void loadData() {
        List<RowHeader> rowHeaders = getRowHeaderList();
        List<List<Cell>> cellList = getCellListForSorting(); // getCellList();
        // getRandomCellList(); //
        List<ColumnHeader> columnHeaders = getColumnHeaderList(); //getRandomColumnHeaderList(); //

        m_jRowHeaderList.addAll(rowHeaders);
        for (int i = 0; i < cellList.size(); i++) {
            m_jCellList.get(i).addAll(cellList.get(i));
        }

        // Load all data
        m_jColumnHeaderList.addAll(columnHeaders);
        m_iTableViewAdapter.setAllItems(m_jColumnHeaderList, m_jRowHeaderList, m_jCellList);

    }

    private List<RowHeader> getRowHeaderList() {


        List<RowHeader> list = new ArrayList<>();
        for (int i = 0; i < allstudents.size(); i++) {
            RowHeader header = new RowHeader(String.valueOf(i), String.valueOf(i + 1));
            list.add(header);
        }
        return list;

    }

    private List<ColumnHeader> getColumnHeaderList() {
        List<ColumnHeader> list = new ArrayList<>();

        for (int i = 0; i < header_titles.length; i++) {
            String strTitle = header_titles[i];
            ColumnHeader header = new ColumnHeader(String.valueOf(i), strTitle);
            list.add(header);
        }
        return list;
    }

    private List<List<Cell>> getCellListForSorting() {
        List<List<Cell>> list = new ArrayList<>();
        for (int i = 0; i < allstudents.size(); i++) {
            List<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < header_titles.length; j++) {

                ArrayList<Object> student = allstudents.get(i);

                String regno = (String) student.get(6);
                String name = (String) student.get(2);
                String gender = (String) student.get(4);
                String status = (String) student.get(7);
                String time = (String) student.get(3);

                String strID = j + "-" + i;
                switch (j) {
                    case 0:

                        cellList.add(new Cell(strID, regno));

                        break;
                    case 1:

                        cellList.add(new Cell(strID, name));

                        break;


                    case 2:

                        cellList.add(new Cell(strID, gender));

                        break;


                    case 3:

                        String f;
                        if (status.equalsIgnoreCase("1")) {
                            f = "present";
                        } else {
                            f = "absent";
                        }

                        cellList.add(new Cell(strID, f));

                        break;


                    case 4:

                        String att;
                        if (time.equalsIgnoreCase("0")) {
                            att = "0";
                        } else {
                            att = Utils.getTime(Long.parseLong(time));
                        }

                        cellList.add(new Cell(strID, att));

                        break;

                }


            }
            list.add(cellList);
        }

        return list;
    }


    private void setFullScreenMode() {
        // Set full screen mode
        this.getActivity().getWindow().getDecorView().setSystemUiVisibility(View
                .SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                // nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
