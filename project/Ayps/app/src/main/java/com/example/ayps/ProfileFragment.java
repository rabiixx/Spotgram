package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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
    @BindView( R.id.profile_img )
    CircleImageView profileImg;

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

        if ( currentUser == null ) {
            ProfileFragment.this.startActivity( new Intent( requireContext(), FirebaseSignIn.class ) );
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Load user profile picture
        Picasso.get().load( currentUser.getPhotoUrl() ).into( profileImg );

        signOutBtn.setOnClickListener( this );

        ArrayList<SpotModal> spotArrayList = new ArrayList<>();

        SpotModal spotModal = new SpotModal("title", "desc", "placeName", "locality",
                "place", "region", "country", "latitude",
                "longitude", "rabiixx12", "tags", 0);

        spotArrayList.add( spotModal );
        spotArrayList.add( spotModal );
        spotArrayList.add( spotModal );

        SpotGalleryAdapter adapter = new SpotGalleryAdapter( getActivity().getApplicationContext(), spotArrayList );
        // LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false );

        spotListRV.setLayoutManager( new GridLayoutManager( view.getContext(), 3 ) );
        spotListRV.setAdapter( adapter );

        return view;
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