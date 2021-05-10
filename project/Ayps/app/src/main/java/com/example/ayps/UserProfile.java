package com.example.ayps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserProfile.class.getName();

    private String authorUsername;
    private String authorId;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_list )
    RecyclerView spotListRV;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.username )
    TextView authorUsernameTV;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.num_spots )
    TextView numSpotsTV;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.num_followers )
    TextView numFollowersTV;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.num_follows )
    TextView numFollowsTV;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.follow_btn )
    Button followBtn;

    // Firestore
    private FirebaseFirestore db;

    // Firebase auth
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        authorUsername = getIntent().getStringExtra("AUTHOR_USERNAME");
        authorId = getIntent().getStringExtra("AUTHOR_ID");

        authorUsernameTV.setText( authorUsername );

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        if ( currentUser == null ) {
            UserProfile.this.startActivity( new Intent( this, FirebaseSignIn.class ) );
        }

        // Get profile user
        final String profileUser = getIntent().getStringExtra("USER");

        spotListRV.addOnItemTouchListener(
                new RecyclerItemClickListener( this, spotListRV, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.i(TAG, "onItemClick detected at " + position + " position."  );
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.i(TAG, "onLongItemClick detected at " + position + " position."  );
                    }
                })
        );

        getUserSpots();

        followBtn.setOnClickListener(this);


    }

    private void getUserSpots() {

        final String collectionPath = "users";
        final String orderField = "created_at";

        db.collection( collectionPath )
                .document( authorId )
                .collection( "spots" )
                .orderBy( orderField )
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    ArrayList<String> spotsImgsRefs = new ArrayList<>();
                    for ( QueryDocumentSnapshot doc : value ) {
                        spotsImgsRefs.add( doc.getString( "spotImg" ) );
                    }

                    numSpotsTV.setText( String.valueOf(spotsImgsRefs.size()) );

                    SpotGalleryAdapter adapter = new SpotGalleryAdapter( UserProfile.this, spotsImgsRefs );
                    spotListRV.setLayoutManager( new GridLayoutManager( UserProfile.this, 3 ) );
                    spotListRV.setAdapter( adapter );

                });
    }


    @Override
    public void onClick(View view) {
        if ( view.getId() == R.id.follow_btn ) {

            if ( followBtn.getText().toString().toLowerCase().equals("follow") ) {
                followBtn.setText("Following");
                followBtn.setBackgroundColor(Color.WHITE);
                followBtn.setTextColor(Color.BLACK);
                numFollowersTV.setText(
                        String.valueOf( Integer.parseInt( numFollowersTV.getText().toString() ) + 1 ) );
            } else {
                followBtn.setText("Follow");
                followBtn.setBackgroundColor(0xff7f50);
                followBtn.setTextColor(Color.WHITE);
                numFollowersTV.setText(
                        String.valueOf( Integer.parseInt( numFollowersTV.getText().toString() ) -1 ) );
            }
        }
    }
}