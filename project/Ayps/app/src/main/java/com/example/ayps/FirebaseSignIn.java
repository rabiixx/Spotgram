package com.example.ayps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseSignIn extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener
{

    private static final String TAG = FirebaseSignIn.class.getName();
    private static final int RC_SIGN_IN = 501;

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    // Data validator
    InputValidator inputValidator;

    // Google Sign In client
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseUser currentUser;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_email )
    EditText inputEmail;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.email_layout )
    TextInputLayout emailLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_password )
    EditText inputPassword;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.password_layout )
    TextInputLayout passwordLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.email_sign_in )
    Button emailSignInBtn;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.google_sign_in )
    SignInButton googleSignInBtn;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.sign_up_link )
    TextView signUpLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_sign_in );

        // Initialize Butter Knife
        ButterKnife.bind(this );

        // Initialize input validator
        inputValidator = InputValidator.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fields listeners
        inputEmail.setOnFocusChangeListener( this );
        inputPassword.setOnFocusChangeListener( this );

        // Buttons listeners
        emailSignInBtn.setOnClickListener( this );
        googleSignInBtn.setOnClickListener( this );

        signUpLink.setOnClickListener( this );

    }

    @Override
    public void onStart() {

        super.onStart();

        currentUser = mAuth.getCurrentUser();
        if ( currentUser != null ) {
            Log.d(TAG, "OnStart: user logged in");
            FirebaseSignIn.this.startActivity( new Intent( FirebaseSignIn.this, HomeActivity.class ) );
            finish();
        } else {
            Log.d(TAG, "OnStart: user not logged in");
        }

    }

    @Override
    public void onClick(View v) {

        if ( v.getId() == R.id.email_sign_in ) {

            if (    inputValidator.validateEmail( inputEmail.getText().toString(), emailLayout )
                    && inputValidator.validatePassword( inputPassword.getText().toString(), passwordLayout ) ) {

                emailSignIn();
            }
        } else if ( v.getId() == R.id.google_sign_in ){
            googleSignIn();
        } else if ( v.getId() == R.id.sign_up_link ) {
            FirebaseSignIn.this.startActivity( new Intent( FirebaseSignIn.this, FirebaseSignUp.class ) );
            finish();
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if ( v.getId() == R.id.input_email && !hasFocus ) {
            inputValidator.validateEmail( inputEmail.getText().toString(), emailLayout );
        } else if ( v.getId() == R.id.input_password && !hasFocus ) {
            inputValidator.validatePassword( inputPassword.getText().toString(), passwordLayout );
        }

    }

    private void emailSignIn() {

        mAuth.signInWithEmailAndPassword(inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            Log.d(TAG, "Sing in provider: " + currentUser.getProviderId() );

                            FirebaseSignIn.this.startActivity( new Intent( FirebaseSignIn.this, HomeActivity.class ) );
                            finish();

                        } else {

                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseSignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithGoogle:success");
                            currentUser = mAuth.getCurrentUser();

                            // Is user sign in for first time, register user
                            if ( task.getResult().getAdditionalUserInfo().isNewUser() ) {
                                addUser();
                            }



                            Log.d(TAG, "Sing in provider: " + currentUser.getProviderId() );

                            FirebaseSignIn.this.startActivity( new Intent( FirebaseSignIn.this, HomeActivity.class ) );
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });

    }

    private void addUser() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        User user = new User(
                currentUser.getUid(),
                currentUser.getDisplayName(),
                currentUser.getEmail(),
                currentUser.getPhotoUrl().toString(),
                "google.com"
        );

        db.collection("users")
                .document( currentUser.getUid() )
                .set( user )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added. " );
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e.getCause());
                    }
                });
    }

    // Clears focus from EditTexts when touching outside
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}