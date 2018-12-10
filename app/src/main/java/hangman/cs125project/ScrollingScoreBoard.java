package hangman.cs125project;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@TargetApi(28)
public class ScrollingScoreBoard extends AppCompatActivity {

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_score_board);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScrollingScoreBoard.this, HomePage.class));
            }
        });
        List<String> scoreList = getUserScores();
        ListView list = findViewById(R.id.scorelist);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.empty, R.id.textView3, scoreList);
        list.setAdapter(adapter);
    }

    private List<String> getUserScores() {
        //function to get all users, and format them into an array of strings
        //example: Player "Boo" is in first with a score of 80 -> first index of array is "1. Boo - 80"
        List<String> users = new ArrayList<>();

        //placeholder code

        List<String> usernames = new ArrayList<>();
        prefs = getSharedPreferences(User.PREFERENCES_NAME, Context.MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            usernames.add(entry.getKey());
        }
        List<User> userList = new ArrayList<>();
        for (String user : usernames) {
            userList.add(getUser(user));
        }
        userList.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                try {
                    if (o1.getScore() < o2.getScore()) {
                        return 1;
                    } else if (o1.getScore() > o2.getScore()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    Log.d(Game.TAG, e.toString());
                    throw new IllegalArgumentException();
                }
            }
        });
        int i = 1;
        for (User user : userList) {
            users.add(i + ". " + user.getUsername() + " - " + user.getScore());
            i++;
        }
        return users;
    }

    public User getUser(String name) {
        String userData = prefs.getString(name, null);
        if (userData == null) {
            Log.d(Game.TAG, "unable to find user");
            Toast.makeText(this, "user " + name + " not found.", Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            return new User(new JSONObject(userData));
        } catch (Exception e) {
            Log.d(Game.TAG, e.toString());
            return null;
        }
    }
}
