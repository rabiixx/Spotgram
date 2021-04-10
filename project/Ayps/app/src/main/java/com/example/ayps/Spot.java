package com.example.ayps;

import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

import org.json.JSONException;
import org.json.JSONObject;

public class Spot {


    public void getSpot( final String id ) {

        final String path = "/spot/" + id;

        RestOptions options = RestOptions.builder()
                .addPath( path )
                .build();

        Amplify.API.get(options,
                restResponse -> Log.i("MyAmplifyApp", "GET succeeded: " + restResponse),
                apiFailure -> Log.e("MyAmplifyApp", "GET failed.", apiFailure)
        );

    }

    public void postSpot( final byte[] data ) {

        final String path = "/spot";

        RestOptions options = RestOptions.builder()
                .addPath( path )
                .addBody( data )
                .build();


        Amplify.API.post(options,
                response -> Log.i("MyAmplifyApp", "POST succeeded: " + response),
                error -> Log.e("MyAmplifyApp", "POST failed.", error)
        );

    }

    public void updateSpot( final byte[] data ) {

        final String path = "/spot";

        RestOptions options = RestOptions.builder()
                .addPath( path )
                .addBody( data )
                .build();

        Amplify.API.put(options,
                response -> Log.i("MyAmplifyApp", "PUT succeeded: " + response),
                error -> Log.e("MyAmplifyApp", "PUT failed.", error)
        );
    }

    public void deleteSpot( final String id ) {

        final String path = "/spot/" + id;

        RestOptions options = RestOptions.builder()
                .addPath( path )
                .build();

        Amplify.API.delete(options,
                response -> Log.i("MyAmplifyApp", "DELETE succeeded: " + response),
                error -> Log.e("MyAmplifyApp", "DELETE failed.", error)
        );

    }

    /*private Boolean amplifySetUp() {

        try {
            // Initialize Amplify API plugin
            Amplify.addPlugin( new AWSApiPlugin() );

            // Initialize Amplify Auth plugin
            Amplify.addPlugin( new AWSCognitoAuthPlugin() );

            // Configure Amplify
            Amplify.configure( getApplicationContext() );

            return Boolean.TRUE;

        } catch ( final AmplifyException ex ) {
            return Boolean.FALSE;
        }
    }*/

}
