package hangman.cs125project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.util.*;

public class UserNameActivity extends AppCompatActivity {
    EditText nameInput;
    String name;

    int count;
    private static final String TAG = "Main";
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        nameInput = findViewById(R.id.userNameInput);
        final Button submitUsername = findViewById(R.id.submit);
        final Button backButton = findViewById(R.id.backButtonUser);

        submitUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                name = nameInput.getText().toString();
                if (validateName(name)) {
                    Log.d(TAG, "new username " + name);

                    //pass the name on to the next class
                    Intent game = new Intent(UserNameActivity.this, Game.class);
                    game.putExtra("currentUsername", name);

                    startActivity(game);
                } else {
                    TextView error = findViewById(R.id.errormsg);
                    error.setText("Please enter a valid username");
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserNameActivity.this, HomePage.class));
            }
        });
    }

    //Check to make sure the username is valid.
    private boolean validateName(String name) {
        return (name.length() > 0) && !name.equals("default") && name.length() < 40;
    }

}
