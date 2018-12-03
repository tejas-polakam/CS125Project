package hangman.cs125project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.util.Log;

public class UserNameActivity extends AppCompatActivity {

    String newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        final Button submitUsername = findViewById(R.id.submit);
        final Button backButton = findViewById(R.id.backButtonUser);
        final String TAG = "CS125Project:Main";
        //final ListView
        submitUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert code here to store username in a list
                @SuppressLint("WrongViewCast")
                EditText text = (EditText) findViewById(R.id.UserInput);
                newUser = text.getText().toString();
                Log.d(TAG, newUser);

                startActivity(new Intent(UserNameActivity.this, Game.class));
                //replace Instructions activity with actual game frame
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert code here to store username in a list

                startActivity(new Intent(UserNameActivity.this, HomePage.class));
                //replace Instructions activity with actual game frame
            }
        });

    }


}
