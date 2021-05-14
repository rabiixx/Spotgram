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

        fm.beginTransaction().add(R.id.container, savedSpotsFragment, "4").hide( savedSpotsFragment ).commit();
        fm.beginTransaction().add(R.id.container, profileFragment, "3").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.container, addSpotFragment, "2").hide(addSpotFragment).commit();
        fm.beginTransaction().add(R.id.container, exploreFragment, "1").commit();
//        fm.beginTransaction().add(R.id.container, profileSpotListFragment, "4").commit();

        //        openFragments( ExploreFragment.newInstance() );

    }

    /*public void openFragments( Fragment fragment ) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace( R.id.container, fragment );
        transaction.addToBackStack( null );
        transaction.commit();

    }*/

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    if ( item.getItemId() == R.id.explore ) {
                        Log.i(TAG, "ExploreFragment selected");
                        fm.beginTransaction().hide(active).show(exploreFragment).commit();
                        active = exploreFragment;
                        return true;
                    } else if ( item.getItemId() == R.id.add_spot ) {

                        Log.i(TAG, "AddSpotFragement selected");
                        if ( fm.findFragmentByTag("2") == null ) {
                            Log.i(TAG, "AddSporFragment does not exits");
                            addSpotFragment = new AddSpotFragment();
                            fm.beginTransaction().hide(active).commit();
                            fm.beginTransaction().add(R.id.container, addSpotFragment, "1").commit();
                        } else {
                            Log.i(TAG, "AddSporFragment does exist");
                            fm.beginTransaction().hide(active).show(addSpotFragment).commit();
                        }

                        active = addSpotFragment;
                        return true;
                    } else if ( item.getItemId() == R.id.profile) {
                        fm.beginTransaction().hide(active).show(profileFragment).commit();
                        active = profileFragment;
                        return true;
                    } else if ( item.getItemId() == R.id.saved_spots ) {
                        fm.beginTransaction().hide(active).show( savedSpotsFragment ).commit();
                        active = savedSpotsFragment;
                        return true;
                    }
                    return false;
                }
            };


}