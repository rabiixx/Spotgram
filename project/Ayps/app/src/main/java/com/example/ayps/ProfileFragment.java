package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amplifyframework.core.Amplify;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    // Google SignIn Client
    GoogleSignInClient mGoogleSignInClient;

    GoogleSignInAccount account;

    // Layout Components
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.sign_out )
    Button signOutBtn;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        signOutBtn.setOnClickListener( this );



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
                            startActivity( new Intent( getActivity(), MainActivity.class ) );
                            getActivity().finish();
                        }
                    });
        }


        if ( Amplify.Auth.getCurrentUser() != null ) {

            Amplify.Auth.signOut(
                    () -> {
                        Log.i("signout", "Amplify sign out successfully completed.");
                        startActivity( new Intent( getActivity(), MainActivity.class ) );
                        getActivity().finish();
                    },
                    error -> Log.e("signout", error.toString())
            );

        }

    }



}