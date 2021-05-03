package com.example.ayps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImportDataFirestore extends AppCompatActivity {

    private static final String TAG = ImportDataFirestore.class.getName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private FirebaseStorage storage;
    private StorageReference storageRef;


    ArrayList<Map<String, Object>> spotList = new ArrayList<>();

    private final String authorProfielImg = "https://firebasestorage.googleapis.com/v0/b/spotgram-5c6af" +
            ".appspot.com/o/images%2Fprofile%2Fdefavatar.png?" +
            "alt=media&token=953476fe-aba1-4266-9df2-add0a92db6f0";

    private final String tags = "tag1, tag2, tag3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data_firestore);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        //addSpots( createSpotList() );

        addPictures();

    }

    private void addPictures() {

        File dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/imgs" );

        File[] imgs = dir.listFiles();

        for ( File file : imgs ) {

            Uri uri = Uri.fromFile( file );

            StorageReference storageReference = storageRef.child( "images/spots/" + file.getName() );
            UploadTask uploadTask = storageReference.putFile( uri );

            uploadTask
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i("firebase", "Picture uploaded successfully");
                        }
                    })
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("firebase", "Picture upload failure");
                        }
                    })
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
    }


    private void addSpots( ArrayList<Map<String, Object>> spotList ) {

        for ( Map<String, Object> spot : spotList ) {
            // Add a new document with a generated ID
            db.collection("users").document( "EOqZUDHPi0NPvXBZHg5NGuIqu1I2" )
                    .collection("spots")
                    .add(spot)
                    .addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess( DocumentReference documentReference ) {
                            Log.d( "debug", "DocumentSnapshot added with ID: " + documentReference.getId() );

                            db.collection("spots")
                                    .document( documentReference.getId() )
                                    .set( spot )
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d( "debug", "DocumentSnapshot added with ID: " + documentReference.getId() );
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w( "debug", "Error adding document", e);
                        }
                    });
        }
    }

    private ArrayList<Map<String, Object>> createSpotList() {

        ArrayList<Map<String, Object>> spotList = new ArrayList<>();

        for ( int i = 1; i < 11; ++i ) {

            Map<String, Object> spot = new HashMap<>();

            // Post
            spot.put("title", "title" + i );
            spot.put("description", "desciption" + i );
            spot.put("spotImg", "images/spots/" + i + ".jpg");
            spot.put("numLikes", 0);
            spot.put("created_at", new Timestamp( new Date() ) );

            // MapBox
            spot.put("placeName", "1600 Amphitheatre Parkway, Mountain View, California 94043, United States");
            spot.put("locality", "Mountain View");
            spot.put("place", "Santa Clara County");
            spot.put("region", "California");
            spot.put("country", "United States");
            spot.put("latitude", "-122.08400000000002");
            spot.put("longitude", "37.4219983333333");
            spot.put("tags", tags);

            // Author
            spot.put("authorId", "EOqZUDHPi0NPvXBZHg5NGuIqu1I2");
            spot.put("authorUsername", "test");
            spot.put("authorProfileImg", authorProfielImg);

            spotList.add( spot );
        }

        return spotList;
    }
}