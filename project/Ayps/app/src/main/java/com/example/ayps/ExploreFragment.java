package com.example.ayps;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_list )
    RecyclerView spotListRV;

    static private final String TAG = ExploreFragment.class.getName();

    FirebaseFirestore db;

    private final String collectionPath = "spots";
    private final String orderField = "created_at";
    private final int limit = 2;
    private DocumentSnapshot lastVisible;

    private SpotAdapter adapter;
    LinearLayoutManager llm;
    private ArrayList<SpotModel> spotList;

    public ExploreFragment() {}

    /**
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        // Initialize Firestore database
        db = FirebaseFirestore.getInstance();

        llm = new LinearLayoutManager( view.getContext(), RecyclerView.VERTICAL, false );

        spotList = new ArrayList<>();

        // Load spot list
        getData();

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

    public void getData(  ) {

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
                            assert value != null;
                            lastVisible = value.getDocuments()
                                    .get(value.size() - 1);

                            // Create spot list with returned data
                            for (QueryDocumentSnapshot doc : value) {
                                SpotModel spotModel = doc.toObject(SpotModel.class);
                                spotList.add(spotModel);
                            }

                            // Initialize adapter
                            adapter = new SpotAdapter(requireContext(), spotList);
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
                                spotList.add( spotModel );
                            }

                            // Update adapter
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
    }


    public void getSpotList() {

        final String collectionPath = "spots";
        final String orderField = "created_at";
        final int limit = 5;

        db.collection( collectionPath )
                .orderBy( orderField )
                .limit( limit )
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent( @Nullable QuerySnapshot value,
                                         @Nullable FirebaseFirestoreException e ) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<String> cities = new ArrayList<>();
                        for ( QueryDocumentSnapshot doc : value ) {
                            if ( doc.get("title") != null ) {
                                cities.add(doc.getString("name"));
                            }
                        }
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });

    }


}