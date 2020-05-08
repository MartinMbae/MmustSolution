package studios.luxurious.mmustsolution.Voting;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import studios.luxurious.mmustsolution.R;

public class VotingActivity extends AppCompatActivity {

    private Toolbar toolbar;


    ArrayList<SectionDataModel> allSampleData;
    String colorUnvoted = "#673BB7";
    String colorvoted = "#4BAA50";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        toolbar = findViewById(R.id.toolbar);

        allSampleData = new ArrayList<>();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle("G PlayStore");

        }


        createDummyData();


        RecyclerView my_recycler_view = findViewById(R.id.my_recycler_view);

        my_recycler_view.setHasFixedSize(true);

        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, allSampleData);

        my_recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        my_recycler_view.setAdapter(adapter);


    }

    public void createDummyData() {
        for (int i = 1; i <= 5; i++) {

            SectionDataModel dm = new SectionDataModel();

            dm.setHeaderTitle("Section " + i);

            ArrayList<Item> arrayList = new ArrayList<>();

            arrayList.add(new Item("Item 1", R.drawable.battle, colorUnvoted));
            arrayList.add(new Item("Item 2", R.drawable.beer, colorUnvoted));
            arrayList.add(new Item("Item 3", R.drawable.ferrari, colorUnvoted));
            arrayList.add(new Item("Item 4", R.drawable.jetpack_joyride, colorUnvoted));
            arrayList.add(new Item("Item 5", R.drawable.three_d, colorUnvoted));
            arrayList.add(new Item("Item 6", R.drawable.terraria, colorUnvoted));


            dm.setAllItemsInSection(arrayList);

            allSampleData.add(dm);

        }
    }
}