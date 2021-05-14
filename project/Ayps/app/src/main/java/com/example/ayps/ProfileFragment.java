package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getName();

    // Firestore
    private FirebaseFirestore db;

    // Firebase auth
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;

    // Google Sign In client
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.spot_list )
    RecyclerView spotListRV;

    // Layout Components
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.sign_out )
    Button signOutBtn;

    // Layout Components
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.username )
    TextView username;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.profile_img )
    CircleImageView profileImg;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.user_saved_spots )
    CheckBox userSavedSpotsBtn;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        if ( currentUser == null ) {
            ProfileFragment.this.startActivity( new Intent( requireContext(), FirebaseSignIn.class ) );
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        loadUserProfile();

        getUserSpots( view );

        signOutBtn.setOnClickListener( this );
        userSavedSpotsBtn.setOnClickListener( this );

        // Recycler view on item click listener
        spotListRV.addOnItemTouchListener(
                new RecyclerItemClickListener( requireContext(), spotListRV, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.i(TAG, "onItemClick detected at " + position + " position."  );

                        // Open SpotList fragment
//                        ( (HomeActivity) requireActivity() ).bottomNavigationView.setSelectedItemId( R.id.fra );
//                    ProfileSpotListFragment fragment = new ProfileSpotListFragment();

                        ProfileSpotListFragment fragment = ProfileSpotListFragment.newInstance( position );
                        requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace( ( (ViewGroup) getView().getParent()).getId(), fragment, "4" )
                            .addToBackStack(null)
                            .commit();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.i(TAG, "onLongItemClick detected at " + position + " position."  );
                    }
                })
        );

        return view;
    }

    private void getUserSpots( View view ) {

        final String collectionPath = "users";
        final String orderField = "created_at";
        final int limit = 6;

        db.collection( collectionPath )
                .document( currentUser.getUid() )
                .collection( "spots" )
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

                        ArrayList<String> spotsImgsRefs = new ArrayList<>();
                        for ( QueryDocumentSnapshot doc : value ) {
//                            Log.i(TAG, doc.getString("spotImg") );
                            spotsImgsRefs.add( doc.getString( "spotImg" ) );
                        }

                        SpotGalleryAdapter adapter = new SpotGalleryAdapter( requireContext(), spotsImgsRefs );
                        spotListRV.setLayoutManager( new GridLayoutManager( view.getContext(), 3 ) );
                        spotListRV.setAdapter( adapter );

                    }
                });
    }

    private void loadUserProfile() {

        Glide.with( requireContext() )
                .load( currentUser.getPhotoUrl().toString() )
                .into( profileImg );

        username.setText( currentUser.getDisplayName() );
    }

    @Override
    public void onClick(View v) {

        if ( v.getId() == R.id.sign_out ) {

            for ( UserInfo user : currentUser.getProviderData() ) {

                Log.d(TAG, "Provider: " + user.getProviderId() );

                if  ( user.getProviderId().equals("firebase") ) {
                    googleSignOut();
                } else if ( user.getProviderId().equals("google.com") ) {
                    emailPasswordSignOut();
                }

                ProfileFragment.this.startActivity( new Intent( requireContext(), FirebaseSignIn.class ) );
                getActivity().finish();

            }
        }
    }

    /**
     * Email/Password sign out
     */
    private void emailPasswordSignOut() {
        mAuth.signOut();
    }

    /**
     * Google provider sign out
     */
    private void googleSignOut() {

        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Google user logged out successfully");
                    }
                });
    }


}