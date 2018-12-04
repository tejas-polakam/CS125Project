package hangman.cs125project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;

public class UserNameActivity extends AppCompatActivity {
    EditText nameInput;
    String name;
    ListView display;
    List<String>  list;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        nameInput = findViewById(R.id.userNameInput);
        final Button submitUsername = findViewById(R.id.submit);
        final Button backButton = findViewById(R.id.backButtonUser);
        //final ListView
        submitUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                name = nameInput.getText().toString();
                list.add(name);
                //figure out shared preferences api
                startActivity(new Intent(UserNameActivity.this, InstructionsActivity.class)); //replace Instructions activity with actual game frame

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
