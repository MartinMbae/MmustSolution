package studios.luxurious.mmustsolution.attendance.Student;


import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import studios.luxurious.mmustsolution.R;

public abstract class BaseFragment extends Fragment implements
        View.OnClickListener, MainActivity.OnBackPressedListener {


    protected FloatingActionButton fab;
    protected Toolbar toolbar;
    protected ActionBar actionBar;
    protected ActionBarDrawerToggle toggle;
    protected DrawerLayout drawer;
    protected boolean mToolBarNavigationListenerIsRegistered = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void InitializeViews() {
        fab = ((MainActivity) Objects.requireNonNull(getActivity())).findViewById(R.id.fab);
        toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        drawer = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
        toggle = ((MainActivity) getActivity()).getToggle();
        fab.setOnClickListener(this);
    }

    protected void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    protected void goBackHome() {
        replaceFragment(new Home_Fragment_Expandable());
    }

    // hide FAB button
    protected void hideFab() {
        fab.hide();
    }

    protected void showFab() {
        fab.show();
    }

    protected void showBackButton(boolean show) {

        if (show) {
            toggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(v -> BaseFragment.this.onBackPressed());
                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            actionBar.setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    protected void setTitle(int resId) {
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(resId));
    }


    @Override
    public abstract void onClick(View v);

    @Override
    public abstract void onBackPressed();
}