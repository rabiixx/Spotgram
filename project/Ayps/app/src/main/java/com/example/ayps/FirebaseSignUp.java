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

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseSignUp extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    // Firebase auth
    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;

    // Logger TAG
    private static final String TAG = FirebaseSignUp.class.getName();

    // Input data validator Singleton
    InputValidator inputValidator;

    // Firestore
    FirebaseFirestore db;

    // Secure shared preferences
    private static SecurePreferences preferences;

    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_NAME = "user-info";
    private static final String SECURE_KEY = "password";

    /**
     * Layout elements Start
     */

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_email )
    EditText inputEmail;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.email_layout )
    TextInputLayout emailLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_username )
    EditText inputUsername;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.username_layout )
    TextInputLayout usernameLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_password )
    EditText inputPassword;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.password_layout )
    TextInputLayout passwordLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.input_confirm_password )
    EditText inputConfirmPassword;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.confirm_password_layout )
    TextInputLayout confirmPasswordLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.sign_up )
    Button signUpBtn;

    @SuppressLint("NonConstantResourceId")
    @BindView( R.id.sign_in_link )
    TextView signInLink;

    /**
     * Layout elements End
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_sign_up );

        // Initialize Butter Knife
        ButterKnife.bind(this );

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize input validator
        inputValidator = InputValidator.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        signInLink.setOnClickListener( this );
        signUpBtn.setOnClickListener( this );

        inputEmail.setOnFocusChangeListener( this );
        inputUsername.setOnFocusChangeListener( this );
        inputPassword.setOnFocusChangeListener( this );

    }

    /**
     * Sign Up new User
     */
    public void signUp() {

        mAuth.createUserWithEmailAndPassword( inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim() )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i(TAG, "User: " + user.getDisplayName() );

                            addUser();

//                            FirebaseSignUp.this.startActivity( new Intent( FirebaseSignUp.this, MainActivity2.class ) );
//                            finish();

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException() );
                            Toast.makeText(FirebaseSignUp.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onFocusChange( View v, boolean hasFocus ) {

        if ( v.getId() == R.id.input_email && !hasFocus ) {
            inputValidator.validateEmail( inputEmail.getText().toString(), emailLayout );
        } else if ( v.getId() == R.id.input_username && !hasFocus ) {
            inputValidator.validateUsername( inputUsername.getText().toString(), usernameLayout );
        } else if ( v.getId() == R.id.input_password && !hasFocus ) {
            inputValidator.validatePassword( inputPassword.getText().toString(), passwordLayout );
        }

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



    private void addUser() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        User user = new User(
                currentUser.getUid(),
                inputUsername.getText().toString(),
                currentUser.getEmail(),
                "defavatar.png"
                );

        db.collection("users")
                .add( user )
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e.getCause());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.sign_up ) {

            if (    inputValidator.validateEmail( inputEmail.getText().toString(), emailLayout )
                    && inputValidator.validateUsername( inputUsername.getText().toString(), usernameLayout )
                    && inputValidator.validatePassword( inputPassword.getText().toString(), passwordLayout ) ) {

                signUp();
            }

        } else if ( v.getId() == R.id.sign_in_link ) {
            FirebaseSignUp.this.startActivity( new Intent( FirebaseSignUp.this, FirebaseSignIn.class ) );
            finish();
        }
    }
}