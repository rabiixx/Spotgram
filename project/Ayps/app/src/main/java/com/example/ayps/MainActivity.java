package com.example.ayps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Todo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignInButton signInButton = findViewById(R.id.sign_in_button);

        // Set the dimensions of the sign-in button.
        signInButton.setSize( SignInButton.SIZE_STANDARD );

        // Google Sign-in Click Listener
        signInButton.setOnClickListener( this );

        //findViewById(R.id.sign_in_button).setOnClickListener(v -> Log.i("Sign-in", "Sign-in pressed"));

        // Google Sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*try {

            Amplify.addPlugin( new AWSCognitoAuthPlugin() );
            Amplify.addPlugin( new AWSApiPlugin() );
            Amplify.configure( getApplicationContext() );

            Log.i("MyAmplifyApp", "Initialized Amplify");

            /*Amplify.Auth.fetchAuthSession(
                    result -> Log.i("AmplifyQuickstart", result.toString()),
                    error -> Log.e("AmplifyQuickstart", error.toString())
            );

        } catch ( final AmplifyException ex ) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", ex);
        }*/


        // Register User on Cognito
        /*Amplify.Auth.signUp(
                "rabiixx",
                "Rabiixx12",
                AuthSignUpOptions.builder().userAttribute(AuthUserAttributeKey.email(), "99rubenche@gmail.com").build(),
                result -> Log.i("AuthQuickStart", "Result: " + result.toString()),
                error -> Log.e("AuthQuickStart", "Sign up failed", error)
        );*/

        // Confirm Sign Up
        /*Amplify.Auth.confirmSignUp(
                "rabiixx",
                "131371",
                result -> Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                error -> Log.e("AuthQuickstart", error.toString())
        );*/

        /*Amplify.Auth.signIn(
                "rabiixx",
                "Rabiixx12",
                result -> Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete"),
                error -> Log.e("AuthQuickstart", error.toString())
        );

        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", result.toString()),
                error -> Log.e("AmplifyQuickstart", error.toString())
        );*/



        /*Todo todo = Todo.builder()
                .name("My second todo")
                .description("todo description two")
                .build();

        Amplify.API.mutate(
                ModelMutation.create( todo ),
                response -> Log.i("MyAmplifyApp", "Added Todo with id: " + response.getData().getId()),
                error -> Log.e("MyAmplifyApp", "Create failed", error)
        );*/

    }


    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.sign_in_button:
                Toast.makeText(this, "Sign in submitted", Toast.LENGTH_SHORT).show();
                signIn();
                break;
            // ...
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            final String msg = "Sign in successful: " + account.getEmail();
            Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();
        } catch ( final ApiException e ) {
            Log.w("Sign-in", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
            Toast.makeText(this, "Sign in failure ", Toast.LENGTH_SHORT).show();
        }
    }

  /*  private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
*/
    /*public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.i("Sign-in", "Sign-in pressed");
                //signIn();
                break;
            // ...
        }
    }*/

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        if ( account == null ) {
//            // User not signed in
//            //updateUI(account);
//        } else {
//            //updateUI(account);
//        }
//    }
}