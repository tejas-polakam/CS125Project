package hangman.cs125project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.content.*;
import android.widget.*;


public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button ruleButton = findViewById(R.id.Rules);
        ruleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, InstructionsActivity.class));
            }
        });
        final Button scoreButton = findViewById(R.id.ScoreBoard);
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                startActivity(new Intent(HomePage.this, InstructionsActivity.class));
                //replace Instructions activity with scoreboard frame
            }
        });
    }

}
