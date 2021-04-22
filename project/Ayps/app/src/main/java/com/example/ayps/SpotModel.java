package com.example.ayps;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotModel {

    static private final String TAG = SpotModel.class.getName();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String spotId;

    // Post data
    private String title;
    private String description;
    private String spotImg;
    private Timestamp created_at;
    private int numLikes = 0;

    // Spot location data
    private String placeName;
    private String locality;
    private String place;
    private String region;
    private String country;
    private String latitude;
    private String longitude;
    private String tags;

    // Post author data
    private String authorId;
    private String authorUsername;
    private String authorProfileImg;


    public SpotModel() { }

    public SpotModel(String title, String description, String spotImg,
                     String placeName, String locality, String place, String region,
                     String country, String latitude, String longitude, String tags,
                     String authorId, String authorUsername, String authorProfileImg ) {

        this.title = title;
        this.description = description;
        this.spotImg = spotImg;

        // MapBox
        this.placeName = placeName;
        this.locality = locality;
        this.place = place;
        this.region = region;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = tags;

        // Author
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.authorProfileImg = authorProfileImg;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    /**
     * Add spot document
     */
    public void addSpot() {

        Map<String, Object> spot = new HashMap<>();

        // Post
        spot.put("title", this.title);
        spot.put("description", this.description);
        spot.put("spotImg", this.spotImg);
        spot.put("numLikes", this.numLikes);
        spot.put("created_at", new Timestamp( new Date() ) );

        // MapBox
        spot.put("placeName", this.placeName);
        spot.put("locality", this.locality);
        spot.put("place", this.place);
        spot.put("region", this.region);
        spot.put("country", this.country);
        spot.put("latitude", this.latitude);
        spot.put("longitude", this.longitude);
        spot.put("tags", this.tags);

        // Author
        spot.put("authorId", this.authorId);
        spot.put("authorUsername", this.authorUsername);
        spot.put("authorProfileImg", this.authorProfileImg);

        // Add a new document with a generated ID
        db.collection("users").document( currentUser.getUid() )
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

    public void getSpotList() {

        final String collectionPath = "spots";
        final String orderField = "created_at";
        final int limit = 5;


        db.collection( collectionPath )
                .orderBy( orderField )
                .limit( limit )
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent( @Nullable QuerySnapshot value,
                                         @Nullable FirebaseFirestoreException e ) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<String> cities = new ArrayList<>();
                        for ( QueryDocumentSnapshot doc : value ) {
                            if ( doc.get("title") != null ) {
                                cities.add(doc.getString("name"));
                            }
                        }
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });

       }


    public static String getTAG() {
        return TAG;
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpotImg() {
        return spotImg;
    }

    public void setSpotImg(String spotImg) {
        this.spotImg = spotImg;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorProfileImg() {
        return authorProfileImg;
    }
}
