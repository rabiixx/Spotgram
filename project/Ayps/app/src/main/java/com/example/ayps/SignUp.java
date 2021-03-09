package com.example.ayps;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Todo;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnFocusChangeListener {

    private EditText email;
    private TextInputLayout emailLayout;

    private EditText username;
    private TextInputLayout usernameLayout;

    private EditText password;
    private TextInputLayout passwordLayout;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );

        email = findViewById( R.id.input_email );
        emailLayout = findViewById( R.id.email_layout);

        username = findViewById( R.id.input_username );
        usernameLayout = findViewById( R.id.username_layout);

        password = findViewById( R.id.input_password );
        passwordLayout = findViewById( R.id.password_layout);

        Button submit = findViewById(R.id.submit);

        email.setOnFocusChangeListener( this );
        username.setOnFocusChangeListener( this );
        password.setOnFocusChangeListener( this );

        try {

            Amplify.addPlugin( new AWSCognitoAuthPlugin() );
            Amplify.addPlugin( new AWSApiPlugin() );
            Amplify.configure( getApplicationContext() );

            Log.i("MyAmplifyApp", "Initialized Amplify");

        } catch ( final AmplifyException ex ) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", ex);
        }

        submit.setOnClickListener(v -> {

            if (    validateEmail( email.getText().toString(), emailLayout )
                    && validateUsername( username.getText().toString(), usernameLayout )
                    && validatePassword( password.getText().toString(), passwordLayout ) ) {

                signUp();
            }

        });

    }

    @Override
    public void onFocusChange( View v, boolean hasFocus ) {

        if ( v.getId() == R.id.input_email && !hasFocus ) {
            validateEmail( email.getText().toString(), findViewById( R.id.email_layout ) );
        } else if ( v.getId() == R.id.input_username && !hasFocus ) {
            validateUsername( username.getText().toString(), findViewById( R.id.username_layout ) );
        } else if ( v.getId() == R.id.input_password && !hasFocus ) {
            validatePassword( password.getText().toString(), findViewById( R.id.password_layout ) );
        }

    }

    private void signUp() {

        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email.getText().toString().trim())
                .build();

        Amplify.Auth.signUp(
                username.getText().toString().trim(),
                password.getText().toString().trim(),
                options,
                result -> Log.i("AuthQuickStart", "Result: " + result.toString()),
                error -> Log.e("AuthQuickStart", "Sign up failed", error)
        );
    }

    private void confirmSignUp() {

        Amplify.Auth.confirmSignUp(
                username.getText().toString().trim(),
                "the code you received via email",
                result -> Log.i("ConfigSignUp", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                error -> Log.e("ConfigSignUp", error.toString())
        );

    }

    private boolean validateEmail( final String email, final TextInputLayout emailError ) {

        if ( email == null || email.isEmpty() ) {
            emailError.setError( "Email cannot be empty." );
            return Boolean.FALSE;
        }

        if ( !android.util.Patterns.EMAIL_ADDRESS.matcher( email ).matches() ) {
            emailError.setError( "Enter a valid email" );
            return Boolean.FALSE;
        }

        if ( emailError.isErrorEnabled() )
            emailError.setError("");

        return Boolean.TRUE;
    }

    private boolean validateUsername( final String username, final TextInputLayout usernameError ) {

        if ( username.isEmpty() ) {
            usernameError.setError("Username cannot be empty.");
            return Boolean.FALSE;
        }

        final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]{3,}$";
        Pattern pattern = Pattern.compile( USERNAME_PATTERN );
        Matcher matcher = pattern.matcher( username );

        if ( !matcher.matches() ) {
            usernameError.setError("Enter a valid username.");
            return Boolean.FALSE;
        }

        if ( usernameError.isErrorEnabled() )
            usernameError.setError("");

        return Boolean.TRUE;
    }

    private boolean validatePassword( final String password, final TextInputLayout passwordError ) {

        if ( password.isEmpty() ) {
            passwordError.setError("Password cannot be empty.");
            return Boolean.FALSE;
        }

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern pattern = Pattern.compile( PASSWORD_PATTERN );
        Matcher matcher = pattern.matcher( password );

        if ( !matcher.matches() ) {
            passwordError.setError("Enter a valid password.");
            return Boolean.FALSE;
        }

        if ( passwordError.isErrorEnabled() )
            passwordError.setError("");

        return  Boolean.TRUE;
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

    // Password RegEx
    //  ^                   # start-of-string
    //  (?=.*[0-9])         # a digit must occur at least once
    //  (?=.*[a-z])         # a lower case letter must occur at least once
    //  (?=.*[A-Z])         # an upper case letter must occur at least once
    //  (?=.*[@#$%^&+=])    # a special character must occur at least once you can replace with your special characters
    //  (?=\\S+$)           # no whitespace allowed in the entire string
    //  .{4,}               # anything, at least six places though
    //  $                   # end-of-string

}