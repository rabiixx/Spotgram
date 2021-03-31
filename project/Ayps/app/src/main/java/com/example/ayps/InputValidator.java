package com.example.ayps;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {

    private static InputValidator inputValidator = null;

    private InputValidator() {}

    public static InputValidator getInstance() {

        if ( inputValidator == null ) {
            inputValidator = new InputValidator();
        }

        return inputValidator;

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

    public boolean validateUsername( final String username, final TextInputLayout usernameError ) {

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

    public boolean validatePassword( final String password, final TextInputLayout passwordError ) {

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
}
