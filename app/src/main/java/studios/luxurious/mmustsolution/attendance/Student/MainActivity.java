package studios.luxurious.mmustsolution.attendance.Student;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.SharedPref;
import studios.luxurious.mmustsolution.attendance.Student.Utils.DBAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_activity_main);
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


        CircleImageView profPic = navView.findViewById(R.id.imageView);

        String gender = sharedPref.getStudentGender();

        if (gender.equalsIgnoreCase("F")){
            profPic.setImageDrawable(getResources().getDrawable(R.drawable.student_female));
        }else{
            profPic.setImageDrawable(getResources().getDrawable(R.drawable.student_male));
        }


        TextView name = navView.findViewById(R.id.header_name);
        TextView regnumber = navView.findViewById(R.id.header_regno);
        TextView course = navView.findViewById(R.id.header_subName);
        TextView year = navView.findViewById(R.id.header_finalName);
        String studentname = sharedPref.getStudentFirstName() + " "+sharedPref.getStudentSecondName() +" "+sharedPref.getStudentSurname();
        name.setText(studentname);
        course.setText(sharedPref.getStudent_Course_name());
        year.setText(sharedPref.getStudent_Level_name());
        regnumber.setText(sharedPref.getStudentRegno());

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


    public boolean isDrawerOpen(){
        return drawer.isDrawerOpen(GravityCompat.START);
    }

    public void exit(){
        super.onBackPressed();
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:

               showFragment(new Home_Fragment_Expandable());

                break;

            case R.id.nav_first_layout:
                showFragment(new Student_Units_Fragments());
                break;

            case R.id.nav_third_layout:
                showFragment(new UnsavedLessonsSide());
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
