package com.example.ayps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.core.Amplify;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final int RC_SIGN_IN = 501;
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_NAME = "user-info";
    private static final String SECURE_KEY = "password";

    // Secure shared preferences
    private static SecurePreferences preferences;

    GoogleSignInClient mGoogleSignInClient;

    // Input data validator Singleton
    InputValidator inputValidator = InputValidator.getInstance();

    // Amplify Setup Singleton
    AmplifySetup amplifySetup;

    // Google Account
    GoogleSignInAccount account;

    // Input user data
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_username )
    EditText inputUsername;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_password )
    EditText inputPassword;

    // Sign In
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.amplify_sign_in_btn )
    Button amplifySignInBtn;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.google_sign_in_btn )
    SignInButton googleSignInBtn;

    // Sign Out
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.amplify_sign_out )
    Button amplifySignOutBtn;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.google_sign_out )
    Button googleSignOutBtn;

    // Check Login
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.check_login )
    Button checkLoginBtn;

    // Sign Up link
    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.link_sign_up )
    TextView linkSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_google_login );

        ButterKnife.bind(this );

        // Initialize Amplify
        amplifySetup = AmplifySetup.getInstance( getApplicationContext() );
        amplifySetup.setUp();

        // Initialize shared preferences
        preferences = new SecurePreferences( getApplicationContext(), PREF_NAME, SECURE_KEY, true);

        //amplifySignOut();
        //googleSignOut();


        checkLogin();

        // Login with shared preferences
//        else if ( preferences.getString(PREF_USERNAME) != null && preferences.getString(PREF_PASSWORD) != null ){
//
//            Log.i("sharedPref", "Username: " + preferences.getString(PREF_USERNAME) + "\n" + "Password: " + preferences.getString(PREF_PASSWORD) );
//            amplifySignIn( preferences.getString( PREF_USERNAME ), preferences.getString( PREF_PASSWORD ) );
//
//            Log.i("checkUserLogin", "User already logged in with amplify");
////            MainActivity.this.startActivity( new Intent( MainActivity.this, MainActivity2.class ) );
////            finish();
//        } else {
//            Log.i("checkUserLogin", "User si not logged in");
//        }

        // Set the dimensions of the sign-in button.
        googleSignInBtn.setSize( SignInButton.SIZE_STANDARD );

        // Sign-in btn Listener
        googleSignInBtn.setOnClickListener( this );
        amplifySignInBtn.setOnClickListener( this );

        // Sign-out btn Listener
        amplifySignOutBtn.setOnClickListener( this );
        googleSignOutBtn.setOnClickListener( this );

        // Sign Up link listener
        linkSignUp.setOnClickListener( this );

        // Input data listener
        inputUsername.setOnFocusChangeListener( this );
        inputPassword.setOnFocusChangeListener( this );

        // Check Login listener
        checkLoginBtn.setOnClickListener( this );
    }

    @Override
    public void onClick( View v ) {

        if ( v.getId() == R.id.amplify_sign_in_btn ) {
            amplifySignIn( inputUsername.getText().toString(), inputPassword.getText().toString() );
        } else if ( v.getId() == R.id.google_sign_in_btn) {
            googleSignIn();
        } else if ( v.getId() == R.id.amplify_sign_out ) {
            Log.i("debug", "whasa homie");
            amplifySignOut();
        } else if ( v.getId() == R.id.google_sign_out ) {
            googleSignOut();
        } else if ( v.getId() == R.id.link_sign_up ) {
            MainActivity.this.startActivity( new Intent( MainActivity.this, SignUp.class ) );
        } else if ( v.getId() == R.id.check_login ) {
            checkLogin();
        }

    }

    private void amplifySignIn( final String username, final String password ) {

        if (    !inputValidator.validateUsername( inputUsername.getText().toString(), findViewById(R.id.username_layout) )
                || !inputValidator.validatePassword( inputPassword.getText().toString(), findViewById(R.id.password_layout) ) ) {

            return;
        }

        Amplify.Auth.signIn(
                username,
                password,
                result -> {

                    saveUserInfo();

                    /**/
//                    Amplify.Auth.fetchUserAttributes(
//                            attributes -> {
//                                Log.i("AuthDemo3", "User attributes = " + attributes.toString());
//                            },
//                            error -> Log.e("AuthDemo3", "Failed to fetch user attributes.", error)
//                    );

//                    MainActivity.this.startActivity( new Intent( MainActivity.this, MainActivity2.class ) );
//                    finish();
                    Log.i("signin", result.isSignInComplete() ? "Amplify sign in succeeded: " + Amplify.Auth.getCurrentUser().getUsername() : "Sign in not complete");
                },
                error -> Log.e("signin", error.toString())
        );
    }

    private void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso );

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == RC_SIGN_IN ) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            assert account != null;
            final String msg = "Sign in successful: " + account.getEmail();
            Log.i("signin", "Google sign in succeeded: " + account.getEmail() );

            MainActivity.this.startActivity( new Intent( MainActivity.this, MainActivity2.class ) );
            finish();

        } catch ( final ApiException e ) {
            Log.w("signin", "signInResult:failed code=" + e.getStatusCode());

            Toast.makeText(this, "Sign in failure ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Amplify sign out
     */
    private void amplifySignOut() {

        if ( Amplify.Auth.getCurrentUser() != null ) {

            Amplify.Auth.signOut(
                    () -> {
                        Log.i("signout", "Amplify sign out successfully completed.");
                        MainActivity.this.startActivity( new Intent( MainActivity.this, MainActivity2.class ) );
                        finish();
                    },
                    error -> Log.e("signout", error.toString())
            );

        }

    }

    /**
     * Google sign out
     */
    private void googleSignOut() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso );

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("signout", "Google sign out succesfully completed.");
                    }
                });
    }

    private void checkLogin() {


        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this );

        if ( Amplify.Auth.getCurrentUser() != null ) {
            Log.i("checkUserLogin", "User already logged in with amplify");
            return;
            //MainActivity.this.startActivity( new Intent( MainActivity.this, MainActivity2.class ) );
            //finish();
        }

        if ( account != null ) {
            Log.i("checkUserLogin", "User already logged in with google");
            return;
//            MainActivity.this.startActivity( new Intent( MainActivity.this, MainActivity2.class ) );
//            finish();
        }

        Log.i("checkUserLogin", "User not logged in");

    }

    /**
     * Encrypts user credentials and saves them in shared preferences
     * @if sign-in with amplify
     */
    private void saveUserInfo() {

        preferences.put( PREF_USERNAME, inputUsername.getText().toString() );
        preferences.put( PREF_PASSWORD, inputUsername.getText().toString() );
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if ( v.getId() == R.id.input_username && !hasFocus ) {
            inputValidator.validateUsername( inputUsername.getText().toString(), findViewById(R.id.username_layout) );
        } else if ( v.getId() == R.id.input_password && !hasFocus ) {
            inputValidator.validatePassword( inputPassword.getText().toString(), findViewById( R.id.password_layout ) );
        }

    }

}