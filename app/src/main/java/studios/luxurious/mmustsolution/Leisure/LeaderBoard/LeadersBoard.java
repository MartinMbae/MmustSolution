package studios.luxurious.mmustsolution.Leisure.LeaderBoard;


import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import studios.luxurious.mmustsolution.R;

public class LeadersBoard extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leader_board_tabb);

        TabLayout tabLayout = findViewById(R.id.myTabs);
        tabLayout.addTab(tabLayout.newTab().setText("Catch the ball"));
        tabLayout.addTab(tabLayout.newTab().setText("Flappy Bird"));
        tabLayout.addTab(tabLayout.newTab().setText("Balloon POP"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = findViewById(R.id.mypage);
        LeaderBoardPagerAdapter adapter = new LeaderBoardPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


}

