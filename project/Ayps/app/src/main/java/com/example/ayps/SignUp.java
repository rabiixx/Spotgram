package com.example.ayps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity implements View.OnFocusChangeListener {

    EditText email;
    EditText username;
    EditText password;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );

        email = findViewById( R.id.input_email );
        final TextInputLayout emailLayout = findViewById( R.id.email_layout);

        //emailLayout.setErrorIconTintList();

        username = findViewById( R.id.input_username );
        final TextInputLayout usernameLayout = findViewById( R.id.username_layout);

        password = findViewById( R.id.input_password );
        final TextInputLayout passwordLayout = findViewById( R.id.password_layout);

        final Button submit = findViewById( R.id.submit );

        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                if (    validateEmail( email.getText().toString(), emailLayout )
                        && validateUsername( username.getText().toString(), usernameLayout )
                        && validatePassword( password.getText().toString(), passwordLayout ) ) {

                    setupAmplify();

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

            }
        });

        email.setOnFocusChangeListener( this );
        username.setOnFocusChangeListener( this );
        password.setOnFocusChangeListener( this );

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

    public boolean validateEmail( final String email, final TextInputLayout emailError ) {

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

    private void setupAmplify() {

        try {

            Amplify.addPlugin( new AWSCognitoAuthPlugin() );
            Amplify.addPlugin( new AWSApiPlugin() );
            Amplify.configure( getApplicationContext() );

            Log.i("MyAmplifyApp", "Initialized Amplify");

        } catch ( final AmplifyException ex ) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", ex);
        }
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