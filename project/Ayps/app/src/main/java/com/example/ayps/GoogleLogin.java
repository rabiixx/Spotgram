package com.example.ayps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthChannelEventName;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.InitializationStatus;
import com.amplifyframework.hub.HubChannel;
import com.google.android.gms.common.SignInButton;

public class GoogleLogin extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        /*if  ( !amplifySetUp() ) {
            this.finishAffinity();
        }*/

        /*Amplify.Auth.fetchAuthSession(
                onSuccess -> Log.i("fetchAuthSession", onSuccess.toString()),
                onFailure -> Log.e("dsf", onFailure.toString() )
        );

        Amplify.Auth.fetchUserAttributes(
                onSuccess -> Log.i("fetchUserAttributes", onSuccess.toString()),
                onFailure -> Log.e("dsf", onFailure.toString())
        );*/

        /*if ( Amplify.Auth.getCurrentUser() != null ) {
            Toast.makeText(this, "User logged in", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "User logged out", Toast.LENGTH_SHORT).show();
        }*/

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        Button signOutButton = findViewById( R.id.sign_out_button );
        Button checkAuth = findViewById( R.id.check_auth );
        Button signUp = findViewById( R.id.sign_up );
        Button fetchData = findViewById( R.id.fetch_data );
        Button fragments = findViewById( R.id.fragments );

        // Set the dimensions of the sign-in button.
        signInButton.setSize( SignInButton.SIZE_STANDARD );

        // Google Sign-in Click Listener
        signInButton.setOnClickListener( this );
        signOutButton.setOnClickListener( this );
        checkAuth.setOnClickListener( this );
        signUp.setOnClickListener( this );
        fetchData.setOnClickListener( this );
        fragments.setOnClickListener( this );
    }

    // Listener Manager
    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.sign_in_button:
                Toast.makeText(this, "Sign in submitted", Toast.LENGTH_SHORT).show();
                signIn();
                break;
            case R.id.sign_out_button:
                Toast.makeText(this, "Logout submitted", Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.check_auth:
                Toast.makeText(this, "Check Auth", Toast.LENGTH_SHORT).show();
                checkAuth();
                break;
            case R.id.sign_up:
                startActivity( new Intent(this, SignUp.class) );
                break;
            case R.id.fetch_data:
                startActivity( new Intent(this, QueryData.class) );
                break;
            case R.id.fragments:
                startActivity( new Intent( this, MainActivity2.class ) );
        }
    }


    private void signIn() {
        Toast.makeText(this, "Debug: Sign in", Toast.LENGTH_SHORT).show();
        Amplify.Auth.signInWithSocialWebUI(
                AuthProvider.google(),
                this,
                result -> {
                    Log.i("signInWithSocialWebUI", "Sign in success: " + result.toString());
                },
                error -> Log.i("signInWithSocialWebUI", "Sign in error: " + error.toString())
        );
        /*Amplify.Auth.signIn(
                "rabiixx",
                "Rabiixx12",
                result -> Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete"),
                error -> Log.e("AuthQuickstart", error.toString())
        );*/
    }

    private void signOut() {
        Amplify.Auth.signOut(
                () -> Log.i("AuthQuickstart", "Signed out successfully"),
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }



    private void checkAuth() {

        //AuthUser authUser = Amplify.Auth.getCurrentUser();

        Amplify.Auth.fetchAuthSession(
                onSuccess -> Log.i("fetchAuthSession", "Success: " + onSuccess.toString()),
                onFailure -> Log.e("fetchAuthSession", "Failure: " + onFailure.toString() )
        );

        /*if ( authUser == null ) {
            Log.i( "getCurrentUser", "User is not logged  in" );
        } else {
            Log.i( "getCurrentUser", "User logged in" );
        }*/
    }

    // Initialize Amplify API
    private Boolean amplifySetUp(  ) {

        try {
            // Initialize Amplify API plugin
            Amplify.addPlugin( new AWSApiPlugin() );

            // Initialize Amplify Auth plugin
            Amplify.addPlugin( new AWSCognitoAuthPlugin() );

            // Configure Amplify
            Amplify.configure( getApplicationContext() );

            Log.i("AmplifySetup", "Initialized Amplify");

            return Boolean.TRUE;

        } catch ( final AmplifyException ex ) {

            Log.e("AmplifySetup", "Could not initialize Amplify", ex );

            return Boolean.FALSE;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("debug", "onActivityResult");
        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data);
        }
    }

}