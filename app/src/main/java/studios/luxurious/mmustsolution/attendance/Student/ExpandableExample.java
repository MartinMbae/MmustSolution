package studios.luxurious.mmustsolution.attendance.Student;


import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.Student.Adapters.Lesson_Expandable_Adapter;

public class ExpandableExample extends AppCompatActivity {

    private ExpandableListView expandableListView;

    private Lesson_Expandable_Adapter lessonExpandableAdapter;

    private List<String> listDataGroup;

    private HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_expandable_layout);

        initViews();
        initListeners();
        initObjects();
        initListData();

    }


    /**
     * method to initialize the views
     */
    private void initViews() {

        expandableListView = findViewById(R.id.expandableListView);

    }

    /**
     * method to initialize the listeners
     */
    private void initListeners() {

        // ExpandableListView on child click listener
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        listDataGroup.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataGroup.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        // ExpandableListView Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataGroup.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ExpandableListView Group collapsed listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataGroup.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    /**
     * method to initialize the objects
     */
    private void initObjects() {

        // initializing the list of groups
        listDataGroup = new ArrayList<>();

        // initializing the list of child
        listDataChild = new HashMap<>();

        // initializing the adapter object
//        lessonExpandableAdapter = new Lesson_Expandable_Adapter(this, listDataGroup, listDataChild);

        // setting list adapter
        expandableListView.setAdapter(lessonExpandableAdapter);

    }

    /*
     * Preparing the list data
     *
     * Dummy Items
     */
    private void initListData() {


        // Adding group data
        listDataGroup.add("Alcohol");
        listDataGroup.add("Coffee");

        List<String> alcoholList = new ArrayList<>();
        alcoholList.add("one");
        alcoholList.add("two");
        alcoholList.add("three");
        alcoholList.add("four");

        List<String> coffeeList = new ArrayList<>();

        coffeeList.add("hrfhedj");
        coffeeList.add("wasyujhzm");
        coffeeList.add("iosdzx");
        coffeeList.add("wiasl");


        listDataChild.put(listDataGroup.get(0), alcoholList);
        listDataChild.put(listDataGroup.get(1), coffeeList);

        lessonExpandableAdapter.notifyDataSetChanged();
    }

}
