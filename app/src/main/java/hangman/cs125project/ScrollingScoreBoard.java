package hangman.cs125project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import java.util.List;
import java.util.ArrayList;

public class ScrollingScoreBoard extends AppCompatActivity {

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
        users.add("1. Geoff - 125");
        users.add("2. Chuchu - 101");
        users.add("3. Xyz - 100");
        for (int i = 4; i <= 100; i++) {
            users.add(i + ".");
        }

        return users;
    }
}
