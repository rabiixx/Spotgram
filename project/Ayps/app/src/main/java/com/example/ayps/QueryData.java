package com.example.ayps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Todo;

public class QueryData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_data);

        try {

            Amplify.addPlugin( new AWSApiPlugin() );
            Amplify.configure( getApplicationContext() );

            Log.i("MyAmplifyApp", "Initialized Amplify");

        } catch ( final AmplifyException ex ) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", ex);
        }

        fecthData();

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

    private void fecthData( ) {
        Amplify.API.query(
                ModelQuery.list(Todo.class, Todo.NAME.contains("todo")),
                response -> {
                    for (Todo todo : response.getData()) {
                        Log.i("MyAmplifyApp", todo.getName());
                    }
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );
    }

}