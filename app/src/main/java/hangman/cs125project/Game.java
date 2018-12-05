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

import org.w3c.dom.Text;

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
        final TextView wordDisplay = findViewById(R.id.wordDisp);
        final List<String> wrongGuesses = new ArrayList<>();
        final List<String> correctGuesses = new ArrayList<>();
        wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));

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
                TextView errorTxt = findViewById(R.id.letterError);
                TextView wrongLetters = findViewById(R.id.wrongLetterList);
                if (letter.equals(" ")) {
                    errorTxt.setText("Pick a letter.");
                }  else if (!hiddenWord.contains(letter) && !wrongGuesses.contains(letter)) {
                    errorTxt.setText("Sorry! The word doesn't have that letter.");
                    wrongGuesses.add(letter);
                    wrongLetters.setText(wrongGuesses.toString());
                } else if (wrongGuesses.contains(letter)) {
                    errorTxt.setText("You already tried letter " + letter + ".");
                } else if (correctGuesses.contains(letter)) {
                    errorTxt.setText("Pick a different letter");
                } else {
                    errorTxt.setText("");
                    correctGuesses.add(letter);
                    wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));
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
        return "hangman";
    }

    //function that turns a word into _ o r d
    private String getCurrentDisplayedWord(String word, List<String> correctGuesses) {
        String newWord = "";
        for (int i = 0; i < word.length(); i++) {
            String letter = String.valueOf(word.charAt(i));
            if (correctGuesses.contains(letter)) {
                newWord += letter + " ";
            } else {
                newWord += "_ ";
            }
        }
        Log.d(TAG, "new display: " + newWord);
        return newWord;
    }
    //TODO: add logic to detect a win/loss, add logic to change image, add proper word picker
}
