package ru.gdgnn.dumpspeakers.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Speaker {

    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    private String name;
    private String city;
    private String section;
    private String job;
    private String speech;
    private String photo;
    private int numRatings;
    private double avgRating;

    public Speaker() {}

    public Speaker(String name, String city, String section, String speech, String job,
            String photo, int numRatings, double avgRating) {
        this.name = name;
        this.city = city;
        this.section = section;
        this.speech = speech;
        this.job = job;
        this.numRatings = numRatings;
        this.photo = photo;
        this.avgRating = avgRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
