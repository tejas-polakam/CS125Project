package hangman.cs125project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;


public class HomePage extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mTextView = (TextView) findViewById(R.id.text);
        //final Button startButton = findViewById(R.id.startButton);
        final Button instructionsButton = findViewById(R.id.Rules);
        final Button scoreboardButton = findViewById(R.id.ScoreBoard);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, InstructionsActivity.class));
                //replace Instructions activity with actual game frame
            }
        });
        scoreboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, ScrollingScoreBoard.class));
            }
        });
        /*
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, InstructionsActivity.class));
                //replace Instructions activity with actual game frame
            }
        });
        */

    }
}
