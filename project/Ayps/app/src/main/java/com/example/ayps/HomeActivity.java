package com.example.ayps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private ViewPager2 viewPager2;

    ExploreFragment exploreFragment;
    AddSpotFragment addSpotFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.home_layout );

        bottomNavigationView = findViewById( R.id.bottom_navigation );
        viewPager2 = findViewById( R.id.viewpager2 );

        bottomNavigationView.setOnNavigationItemSelectedListener( navigationItemSelectedListener );
//        openFragments( ExploreFragment.newInstance() );


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem( R.id.explore ).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem( R.id.add_spot ).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem( R.id.profile ).setChecked(true);
                        break;
                }
            }
        });

        setupViewPager(viewPager2);

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
                            viewPager2.setCurrentItem(0, false);
                            break;
//                            openFragments( ExploreFragment.newInstance() );
//                            return true;
                        case R.id.add_spot:
                            viewPager2.setCurrentItem(1, false);
                            break;
//                            openFragments( AddSpotFragment.newInstance( "", "" ) );
//                            return true;
                        case R.id.profile:
                            viewPager2.setCurrentItem(2, false);
//                            openFragments( ProfileFragment.newInstance() );
//                            return true;
                    }
                    return false;
                }
            };

    private void setupViewPager( ViewPager2 viewPager ) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        exploreFragment = new ExploreFragment();
        addSpotFragment = new AddSpotFragment();
        profileFragment = new ProfileFragment();

        adapter.addFragment( exploreFragment );
        adapter.addFragment( addSpotFragment );
        adapter.addFragment( profileFragment );

        viewPager.setAdapter(adapter);
    }

}