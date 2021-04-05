package com.example.ayps;

public class SpotModal {

    private String title;
    private String description;

    private String placeName;
    private String locality;
    private String place;
    private String region;
    private String country;
    private String latitude;
    private String longitude;
    private String tags;

    private String userId;
    private String username;

    private int numLikes;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public SpotModal(String title, String description, String placeName, String locality,
                     String place, String region, String country, String latitude,
                     String longitude, String userId, String tags, Integer numLikes) {

        this.title = title;
        this.description = description;
        this.placeName = placeName;
        this.locality = locality;
        this.place = place;
        this.region = region;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.tags = tags;
        this.numLikes = numLikes;
    }
}
