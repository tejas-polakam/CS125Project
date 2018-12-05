package hangman.cs125project;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class User {
    String username;
    int score;
    int gamesPlayed;
    int id;
    private static int userCount = 0;

    public User(String setName) {
        username = setName;
        id = userCount;
        userCount++;
    }

    public User(JSONObject userData) {

    }

}
