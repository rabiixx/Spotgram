package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SavedSpotsFragment extends Fragment {

    private static final String TAG = SavedSpotsFragment.class.getName();

    private LinearLayoutManager llm;
    private SavedSpotAdapter savedSpotAdapter;
    private ArrayList<SpotModel> savedSpotsList;

    // Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db;

    // Views
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.saved_spots )
    RecyclerView savedSpotsRV;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.saved_spots_search_box )
    TextInputLayout savedSpotsSearchBox;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.search_box_input )
    TextInputEditText searchBoxInput;

    public SavedSpotsFragment() {}

    public static SavedSpotsFragment newInstance() {
        SavedSpotsFragment fragment = new SavedSpotsFragment();
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
        View view = inflater.inflate(R.layout.fragment_saved_spots, container, false);

        ButterKnife.bind(this, view );

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if ( currentUser == null ) {
            startActivity( new Intent( requireContext(), FirebaseSignIn.class ) );
        }

        db = FirebaseFirestore.getInstance();

        llm = new LinearLayoutManager( view.getContext(), RecyclerView.VERTICAL, false );
        savedSpotsList = new ArrayList<>();

        getUserSavedSpots();

        savedSpotAdapter = new SavedSpotAdapter(requireContext(), savedSpotsList,
                new SavedSpotAdapter.SavedSpotAdapterListener() {
                    @Override
                    public void openGMapBtnOnClick(View v, int pos) {

                        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=My+Location&daddr=" +
                                savedSpotsList.get(pos).getLatitude() + "," +
                                savedSpotsList.get(pos).getLongitude());

                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                        mapIntent.setPackage("com.google.android.apps.maps");

                        startActivity(mapIntent);
                    }

                    @Override
                    public void removeSpotFromSavedSpotsOnClick(View v, int pos) {
                        savedSpotsList.get(pos).removeSpotFromSavedSpots(currentUser);
                    }

                });

        savedSpotsRV.setLayoutManager(llm);
        savedSpotsRV.setAdapter(savedSpotAdapter);

        searchBoxInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if ( editable.length() == 0) {
                    getUserSavedSpots();
                }

                db.collection("users")
                        .document( currentUser.getUid() )
                        .collection("savedSpots")
                        .whereGreaterThanOrEqualTo("title", editable.toString() )
                        .whereLessThanOrEqualTo("title", editable.toString() + "\uf8ff")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("debug", "Listen failed.", e);
                                    return;
                                }

                                savedSpotsList.clear();

                                if (value != null && value.size() != 0) {
                                    for (DocumentSnapshot doc : value.getDocuments() ) {
                                        SpotModel spot = doc.toObject(SpotModel.class);
                                        spot.setSpotId( doc.getId() );
                                        savedSpotsList.add(spot);
                                    }
                                }

                                if ( savedSpotAdapter != null ) {
                                    savedSpotAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        });

        return view;
    }


    private void getUserSavedSpots() {

            db.collection("users")
                    .document( currentUser.getUid() )
                    .collection("savedSpots")
                    .orderBy("created_at", Query.Direction.DESCENDING )
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("debug", "Listen failed.", e);
                                return;
                            }

                            savedSpotsList.clear();

                            if (value != null && value.size() != 0) {
                                for (DocumentSnapshot doc : value.getDocuments() ) {
                                    SpotModel spot = doc.toObject(SpotModel.class);
                                    spot.setSpotId( doc.getId() );
                                    savedSpotsList.add(spot);
                                }
                            }

                            if ( savedSpotAdapter != null ) {
                                savedSpotAdapter.notifyDataSetChanged();
                            }
                        }
                    });
    }
}