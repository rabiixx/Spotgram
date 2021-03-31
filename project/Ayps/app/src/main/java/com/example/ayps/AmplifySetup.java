package com.example.ayps;

import android.content.Context;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

public class AmplifySetup {

    private static AmplifySetup amplifySetup = null;
    private static Context applicationContext = null;

    private AmplifySetup() {}

    public static AmplifySetup getInstance( final Context context ) {

        if ( amplifySetup == null ) {
            amplifySetup = new AmplifySetup();
        }

        if ( applicationContext == null ) {
            applicationContext = context;
        }

        return amplifySetup;

    }


    public void setUp() {

        try {
            // Initialize Amplify API plugin
            Amplify.addPlugin( new AWSApiPlugin() );

            // Initialize Amplify Auth plugin
            Amplify.addPlugin( new AWSCognitoAuthPlugin() );

            // Configure Amplify
            Amplify.configure( applicationContext );

            Log.i("AmplifySetup", "Initialized Amplify");

        } catch ( final AmplifyException ex ) {

            Log.e("AmplifySetup", "Could not initialize Amplify", ex );

        }

    }
}
