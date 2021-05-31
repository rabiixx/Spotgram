package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileSpotListFragment extends Fragment {

    private static final String TAG = ProfileSpotListFragment.class.getName();

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Firebase Firestore
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private ArrayList<SpotModel> spotList;

    private static final String SPOT_NUM = "mSpotNum";
    private int mSpotNum;

    // Reycler View
    private ProfileSpotAdapter profileSpotAdapter;
    LinearLayoutManager llm;

    // Layout Views
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_list )
    RecyclerView spotListRV;

    // Empty Fragment Contructor
    public ProfileSpotListFragment() {}

    public static ProfileSpotListFragment newInstance( int spotNum ) {

        ProfileSpotListFragment fragment = new ProfileSpotListFragment();
        Bundle args = new Bundle();

        args.putInt( SPOT_NUM, spotNum );

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if ( getArguments() != null ) {
            mSpotNum = getArguments().getInt( SPOT_NUM );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_spot_list, container, false);

        // Initialize ButterKnife
        ButterKnife.bind(this, view );

        Log.i("ProfileFragment", "Spot Position: " + mSpotNum );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get current user
        currentUser = mAuth.getCurrentUser();

        // Check user signed in
        if ( currentUser == null ) {
            Intent intent = new Intent( getActivity(), ProfileFragment.class );
            startActivity( intent );
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        spotList = new ArrayList<>();

        // Initialize LayoutManager required by Recycler View
        llm = new LinearLayoutManager( view.getContext(), RecyclerView.VERTICAL, false );

        getUserSpots();

        return view;
    }

    /**
     * Gets current user spots
     */
    private void getUserSpots() {

        final String collection = "users";
        final String userUid = currentUser.getUid();
        final String subcollection = "spots";

        db.collection( collection ).document( userUid )
                .collection( subcollection )
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if ( task.isSuccessful() ) {

                            Log.i(TAG, "Users spots successfully fetched." );

                            for ( QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                                SpotModel spotModel = doc.toObject(SpotModel.class);
                                spotList.add( spotModel );
                            }

                            profileSpotAdapter = new ProfileSpotAdapter( requireContext(), spotList );
                            spotListRV.setLayoutManager( llm );
                            spotListRV.setAdapter( profileSpotAdapter );

                        } else {
                            Log.i(TAG, "Error getting user spots: " + task.getException().getMessage() );
                        }
                    }
                });

    }
}