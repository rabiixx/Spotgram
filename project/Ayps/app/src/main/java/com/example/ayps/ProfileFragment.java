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

    // Google SignIn Client
    GoogleSignInClient mGoogleSignInClient;

    GoogleSignInAccount account;

    // FIREBASE
    private FirebaseStorage storage;
    private StorageReference storageRef;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        /*StorageReference storageReference = storageRef.child( "images/0debff2f-1aea-4b5a-88d8-bbeafd5dec3e" );

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("firebase", "URI: " + uri.toString() );
                Picasso.get().load( uri.toString() ).into( profileImg );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("firebase", "Error obtaining picture url: " + e.getCause() );
            }
        });*/

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext() );
        if ( account != null ) {
            Picasso.get().load( account.getPhotoUrl() ).into( profileImg );
        }


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
            signOut();
        }

    }

    private void signOut() {

        account = GoogleSignIn.getLastSignedInAccount( getActivity().getApplicationContext() );

        if ( account != null ) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient( getActivity().getApplicationContext(), gso );

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener( getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("signout", "Google sign out succesfully completed.");
//                            startActivity( new Intent( getActivity(), MainActivity.class ) );
//                            getActivity().finish();
                        }
                    });
        }


    }



}