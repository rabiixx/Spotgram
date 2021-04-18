package com.example.ayps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.home_layout);

        bottomNavigationView = findViewById( R.id.bottom_navigation );

        bottomNavigationView.setOnNavigationItemSelectedListener( navigationItemSelectedListener );
        openFragments( ExploreFragment.newInstance() );

    }

    public void openFragments( Fragment fragment ) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace( R.id.container, fragment );
        transaction.addToBackStack( null );
        transaction.commit();

    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch ( item.getItemId() ) {
                        case R.id.explore:
                            openFragments( ExploreFragment.newInstance() );
                            return true;
                        case R.id.add_spot:
                            openFragments( AddSpotFragment.newInstance( "", "" ) );
                            return true;
                        case R.id.profile:
                            openFragments( ProfileFragment.newInstance( "", "" ) );
                            return true;
                    }

                    return false;
                }
            };

}