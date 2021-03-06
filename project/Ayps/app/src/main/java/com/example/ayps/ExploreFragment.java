package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExploreFragment extends Fragment {

    static private final String TAG = ExploreFragment.class.getName();

    // Firebase auth
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;

    FirebaseFirestore db;

    private final String collectionPath = "spots";
    private final String orderField = "created_at";
    private final int limit = 10;
    private DocumentSnapshot lastVisible;

    private SpotAdapter adapter;
    LinearLayoutManager llm;
    private ArrayList<SpotModel> spotList;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_list )
    RecyclerView spotListRV;

    public ExploreFragment() {}

    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, view );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if ( currentUser == null ) {
            ExploreFragment.this.startActivity( new Intent( requireContext(), FirebaseSignIn.class ) );
        }

        // Initialize Firestore database
        db = FirebaseFirestore.getInstance();

        llm = new LinearLayoutManager( view.getContext(), RecyclerView.VERTICAL, false );

        spotList = new ArrayList<>();

        // Load spot list
        getData(view);

        // if end of spot list reached, load new spots
        spotListRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState ) {
                super.onScrollStateChanged( recyclerView, newState );

                if (!recyclerView.canScrollVertically(1)) {
                    updateData();
                }
            }
        });

//        llm.scrollToPositionWithOffset(3, 1369);
//        llm.scrollToPosition(2););

        return view;
    }

    public void getData( View view ) {

        db.collection( collectionPath )
                .orderBy( orderField, Query.Direction.DESCENDING )
                .limit( limit )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e ) {

                        if (e != null) {
                            Log.w("debug", "Listen failed.", e);
                            return;
                        }

                        if ( value != null && value.size() != 0 ) {

                            // Get last returned element reference
                            lastVisible = value.getDocuments()
                                    .get( value.size() - 1 );

                            // Create spot list with returned data
                            for (QueryDocumentSnapshot doc : value) {
                                SpotModel spotModel = doc.toObject(SpotModel.class);
                                spotModel.setSpotId( doc.getId() );
                                spotList.add(spotModel);
                            }

                            // Initialize adapter
                            adapter = new SpotAdapter( view.getContext(), spotList, new SpotAdapter.SpotAdapterListener(){
                                @Override
                                public void openMapBtnOnClick(View v, int position) {

                                    Intent openMapIntent = new Intent( requireContext(), ShowRoute.class );
                                    openMapIntent.putExtra("DEST_LNG", spotList.get( position ).getLongitude() );
                                    openMapIntent.putExtra("DEST_LAT", spotList.get( position ).getLatitude() );

                                    startActivity( openMapIntent );
                                }

                                @Override
                                public void openGMapBtnOnClick(View v, int position) {

                                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=My+Location&daddr=" +
                                            spotList.get( position ).getLatitude() +
                                            "," + spotList.get( position ).getLongitude()
                                    );

                                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                                    mapIntent.setPackage("com.google.android.apps.maps");

                                    startActivity(mapIntent);
                                }

                                @Override
                                public void openAuthorProfileBtnClick(View v, int position) {
                                    Log.i( TAG, "Open " + spotList.get(position).getAuthorUsername() +
                                            "(" + spotList.get( position ).getAuthorId() +  ") profile");

                                    Intent openAuthorProfileIntent = new Intent( requireContext(), UserProfile.class );
                                    openAuthorProfileIntent.putExtra("AUTHOR_USERNAME", spotList.get( position ).getAuthorUsername() );
                                    openAuthorProfileIntent.putExtra("AUTHOR_ID", spotList.get(position).getAuthorId() );

                                    startActivity( openAuthorProfileIntent );
                                }

                                @Override
                                public void saveSpotBtnClick( View v, int position ) {

                                    CheckBox checkBox = (CheckBox) v;
                                    SpotModel spot = spotList.get( position );

                                    if ( checkBox.isChecked()) {
                                        spot.addSpotToSavedSpots( currentUser );
                                    } else {
                                        spot.removeSpotFromSavedSpots( currentUser );
                                    }
                                }
                            });

                            spotListRV.setLayoutManager(llm);
                            spotListRV.setAdapter(adapter);
                        }
                    }
                });
    }

    public void updateData() {

        db.collection( collectionPath )
                .orderBy( orderField, Query.Direction.DESCENDING )
                .startAfter( lastVisible )
                .limit( limit )
                .addSnapshotListener( new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e ) {

                        if (e != null) {
                            Log.w("debug", "Listen failed.", e);
                            return;
                        }

                        // Save last returned element reference
                        if ( value != null && value.size() != 0 ) {

                            lastVisible = value.getDocuments()
                                    .get( value.size() -1 );

                            // Add new elements to spot list
                            for ( QueryDocumentSnapshot doc : value ) {
                                SpotModel spotModel = doc.toObject( SpotModel.class );
                                spotModel.setSpotId( doc.getId() );
                                spotList.add( spotModel );
                            }

                            // Update adapter
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
    }
}