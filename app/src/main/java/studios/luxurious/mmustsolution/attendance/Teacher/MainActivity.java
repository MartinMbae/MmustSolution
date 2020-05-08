package studios.luxurious.mmustsolution.attendance.Teacher;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = new SharedPref(this);


        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navView = navigationView.getHeaderView(0);
        TextView profPic = navView.findViewById(R.id.header_name);
        String teachername = sharedPref.getTeacherTitle() + " " + sharedPref.getTeacherFirstName() + " " + sharedPref.getTeacherSecondName();
        profPic.setText(teachername);

        showFragment(new Home_Fragment_Expandable());

    }

    protected ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void exit() {
        super.onBackPressed();
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:

                showFragment(new Home_Fragment_Expandable());

                break;

            case R.id.nav_units:
                showFragment(new Teacher_Units_Fragments());
                break;

            case R.id.nav_pending_lessons:
                showFragment(new UnsavedLessonsSide());
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public boolean isDrawerOpen(){
        return drawer.isDrawerOpen(GravityCompat.START);
    }


}
