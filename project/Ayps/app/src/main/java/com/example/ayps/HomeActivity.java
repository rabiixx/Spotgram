package com.example.ayps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getName();

    BottomNavigationView bottomNavigationView;

    ExploreFragment exploreFragment = new ExploreFragment();
    AddSpotFragment addSpotFragment = new AddSpotFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SavedSpotsFragment savedSpotsFragment = new SavedSpotsFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = exploreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.home_layout );

        bottomNavigationView = findViewById( R.id.bottom_navigation );
        bottomNavigationView.setOnNavigationItemSelectedListener( navigationItemSelectedListener );

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ExploreFragment()).commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    if ( item.getItemId() == R.id.explore ) {
                        ExploreFragment exploreFragment = ExploreFragment.newInstance();
                        openFragment(exploreFragment);
                        return true;
                    } else if ( item.getItemId() == R.id.add_spot ) {
                        AddSpotFragment addSpotFragment = AddSpotFragment.newInstance();
                        openFragment(addSpotFragment);
                        return true;
                    } else if ( item.getItemId() == R.id.profile) {
                        ProfileFragment profileFragment = ProfileFragment.newInstance();
                        openFragment(profileFragment);
                        return true;
                    } else if ( item.getItemId() == R.id.saved_spots ) {
                        SavedSpotsFragment savedSpotsFragment = SavedSpotsFragment.newInstance();
                        openFragment(savedSpotsFragment);
                        return true;
                    }
                    return false;
                }
            };


    private void openFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }



}