package hangman.cs125project;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import org.json.*;

public class User implements Comparable<User> {
    String username;
    private int score;
    SharedPreferences prefs;

    public static final String PREFERENCES_NAME = "UserPrefs";

    public User(String setName) {
        username = setName;
        score = 0;
    }

    public User(JSONObject userData) {
        try {
            this.username = userData.getString("username");
            this.score = userData.getInt("score");
        } catch (Exception e) {
            Log.d(Game.TAG, e.toString());
        }
    }

    //Add/subtract points from the user's score.
    public void addToScore(int number) {
        this.score += number;
    }

    //Set user's score.
    public void setScore(int number) {
        this.score = number;
    }

    public int getScore() {
        return this.score;
    }
    public String getUsername() {
        return this.username;
    }

    //Get class as json object
    public JSONObject userAsJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("username", username);
            jsonObject.putOpt("score", score);
        } catch (JSONException e) {
            Log.d(Game.TAG, e.toString());
        }
        return jsonObject;
    }

    //Parse the user as a json String.
    public String submitUserInfo() {
        return userAsJSON().toString();
    }

    //Implement Comparable to make scoreboard sorting easier.
    public int compareTo(User other) {
        try {
            if (this.score < other.getScore()) {
                return -1;
            } else if (this.score > other.getScore()) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            Log.d(Game.TAG, e.toString());
            throw new IllegalArgumentException();
        }
    }

}
