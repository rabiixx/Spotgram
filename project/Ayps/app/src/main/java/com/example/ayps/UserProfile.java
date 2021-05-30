package com.example.ayps;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    private User author;

    private User currentUserModel;

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
    @BindView( R.id.num_following )
    TextView numFollowingTV;

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

        getUserData();

        checkIfFollowing();

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

                updateAuthorFollowers(true);
                updateCurrentUserFollowing(true);

            } else {
                followBtn.setText("Follow");
                followBtn.setBackgroundColor(0xff7f50);
                followBtn.setTextColor(Color.WHITE);
                numFollowersTV.setText(
                        String.valueOf( Integer.parseInt( numFollowersTV.getText().toString() ) -1 ) );


                updateAuthorFollowers(false);
                updateCurrentUserFollowing(false);
            }
        }
    }

    private void checkIfFollowing() {

        db.collection("users")
                .document(authorId)
                .collection("followers")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot doc = task.getResult();
                            if ( doc.exists() ) {
                                followBtn.setText("Following");
                                followBtn.setBackgroundColor(Color.WHITE);
                                followBtn.setTextColor(Color.BLACK);
                            } else {
                                followBtn.setText("Follow");
                                followBtn.setBackgroundColor(0xff7f50);
                                followBtn.setTextColor(Color.WHITE);
                            }
                        }
                    }
                });
    }

    private void getUserData() {

        db.collection("users")
                .document(this.authorId)
                .collection("spots")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if ( task.isSuccessful() ) {
                            numSpotsTV.setText( String.valueOf(task.getResult().size()));
                        }
                    }
                });

        db.collection("users")
                .document(this.authorId)
                .collection("followers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if ( task.isSuccessful() ) {
                            numFollowersTV.setText( String.valueOf(task.getResult().size()));
                        }
                    }
                });

        db.collection("users")
                .document(this.authorId)
                .collection("following")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if ( task.isSuccessful() ) {
                            numFollowingTV.setText( String.valueOf(task.getResult().size()));
                        }
                    }
                });
    }



    private void updateAuthorFollowers(boolean increase) {

        db.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        User user = doc.toObject(User.class);

                        if ( increase ) {
                            db.collection("users")
                                    .document(authorId)
                                    .collection("followers")
                                    .document(user.getUserId())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        } else {
                            db.collection("users")
                                    .document(authorId)
                                    .collection("followers")
                                    .document(user.getUserId())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }

                    }
                });
    }

    private void updateCurrentUserFollowing(boolean increase) {

        db.collection("users")
                .document(authorId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        User user = doc.toObject(User.class);

                        if ( increase ) {
                            db.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("following")
                                    .document(user.getUserId())
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        } else {
                            db.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("following")
                                    .document(user.getUserId())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                        }
                    }
                });

    }
}