package com.example.ayps;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Todo;

public class MyAmplifyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {

            Amplify.addPlugin( new AWSApiPlugin() );
            Amplify.configure( getApplicationContext() );

            Log.i("MyAmplifyApp", "Initialized Amplify");

        } catch ( final AmplifyException ex ) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", ex);
        }

        /*Todo todo = Todo.builder()
                .name("My first todo")
                .description("todo descripction")
                .build();

        Amplify.API.mutate(
                ModelMutation.create( todo ),
                response -> Log.i("MyAmplifyApp", "Added Todo with id: " + response.getData().getId()),
                error -> Log.e("MyAmplifyApp", "Create failed", error)
        );*/
    }


}
