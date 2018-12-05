package hangman.cs125project;

import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;
import java.util.ArrayList;

public class Game extends AppCompatActivity {

    private static final String TAG = "Main";
    MediaPlayer music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final String hiddenWord = setGameWord();
        final List<String> wrongGuesses = new ArrayList<>();

        //Operations when user tries to guess the whole word.
        Button wordSubmit = findViewById(R.id.wordButton);
        wordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText wordField = findViewById(R.id.wordInput);
                String word = wordField.getText().toString();
                Log.d(TAG, "user guessed: " + word);
                TextView error = findViewById(R.id.Errortext);
                if (word.contains(" ") || word.length() == 0) {
                    error.setText("Word must not be empty or contain spaces");
                } else if (!word.equals(hiddenWord)) {
                    error.setText("Wrong! Guess again?");
                } else {
                    error.setText("");
                }
            }
        });

        //Operations when user tries to guess a letter.
        Button letterSubmit = findViewById(R.id.letterSubmit);
        letterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = findViewById(R.id.spinner);
                String letter = spinner.getSelectedItem().toString();
                if (!hiddenWord.contains(letter) && !wrongGuesses.contains(letter)) {
                    Snackbar.make(v, "Sorry! The word doesn't have that letter.", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    wrongGuesses.add(letter);
                } else if (wrongGuesses.contains(letter)) {
                    Snackbar.make(v, "You already tried " + letter, Snackbar.LENGTH_LONG).setAction("Action",null).show();
                } else {
                    Log.d(TAG, "yup");
                }
            }
        });

        //Operations when user tries to give up.
        Button youTried = findViewById(R.id.giveUp);
        music = MediaPlayer.create(Game.this, R.raw.music);
        youTried.setOnClickListener(new View.OnClickListener() {
            //for now all this does is rickroll the user, eventually change it to do something useful.
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Never give up.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (music.isPlaying()) {
                    music.pause();
                    music.seekTo(0);
                } else {
                    music.start();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Prevent music from playing in the background
        if (music != null){
            music.stop();
            if (isFinishing()){
                music.stop();
                music.release();
            }
        }
    }
    //function that picks a random word for the game
    private String setGameWord() {
        return "csonetwentyfive";
    }
}
