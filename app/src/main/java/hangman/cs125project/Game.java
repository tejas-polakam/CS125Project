package hangman.cs125project;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.*;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Game extends AppCompatActivity {

    public static final String TAG = "Main";
    private static final String RANDOM_WORD_API_KEY = "jecgaa";

    private static RequestQueue requestQueue;

    private List<String> words;
    private String hiddenWord;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    User user;

    MediaPlayer music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Get a word from the api.
        requestQueue = Volley.newRequestQueue(this);
        hiddenWord = "hangman";
        final List<String> wrongGuesses = new ArrayList<>();
        final List<String> correctGuesses = new ArrayList<>();
        final TextView wordDisplay = findViewById(R.id.wordDisp);
        getWordsFromAPI(RANDOM_WORD_API_KEY, 1, new VolleyCallback() {
            @Override
            public void onSuccess() {
                hiddenWord = words.get(0);
                wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));
            }
        });

        //Find username from intent (see UserNameActivity)
        String username = "default";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("currentUsername");
        }
        Log.d(TAG, "User: " + username);

        //Get user or create one if it doesn't exist
        prefs = getSharedPreferences(User.PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (prefs.contains(username)) {
            user = getUser(username);
            Log.d(TAG, "found user: " + user.getUsername() + " score: " + user.getScore());
        } else {
            editor = prefs.edit();
            user = new User(username);
            editor.putString(username, user.submitUserInfo());
            editor.commit();
            Log.d(TAG, "new user " + username);
            Log.d(TAG, getUser(username).toString());
        }

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
                    //Place operations to do in case of a win here.
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
                if (findWinState(10, hiddenWord, 5, correctGuesses) == 1) {
                    user.addToScore(1);
                    Log.d(TAG, user.getUsername() + " gained some points");
                    editor = prefs.edit();
                    editor.putString(user.getUsername(), user.submitUserInfo());
                    editor.commit();
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

    //function to get a new api key, call if the last one didn't work
    private static String getNewAPIKey() {
        try {
            URL url = new URL("https://random-word-api.herokuapp.com/key");
            Document doc = Jsoup.parse(url, 2000);
            Elements body = doc.select("body");
            Log.d(TAG, "api key" + body.toString());
            return body.toString();
        } catch (Exception e) {
            return null;
        }
    }


    //function to get random words from the API

    public void getWordsFromAPI(String key, final int number, final VolleyCallback callback) {
        String url = "https://random-word-api.herokuapp.com/word?key=" + key + "&number=" + number;
        try {
            JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            words = new ArrayList<>();
                            String w = response.get(i).toString();
                            words.add(w);
                            Log.d(TAG, "add word :" + w);
                        }
                        callback.onSuccess();
                    } catch (JSONException e) {
                        words = new ArrayList<>();
                        words.add("wrong API key");
                        Log.d(TAG, "wrong api key " + e.toString());
                        callback.onSuccess();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, error.toString());
                }
            });
            requestQueue.add(request);
        } catch (Exception other) {
            other.printStackTrace();
        }
    }

    //function that picks a random word for the game
    private void setGameWord(String key) {
        try {
            getWordsFromAPI(key, 1, new VolleyCallback() {
                @Override
                public void onSuccess() {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "setGameWord() error: " + e.toString());
        }
    }

    private String setGameWord() {
        setGameWord(RANDOM_WORD_API_KEY);
        while (words.get(0) != null && words.get(0).equals("wrong API key")) {
            setGameWord(getNewAPIKey());
        }
        if (words.get(0) != null) {
            return words.get(0);
        } else {
            return "hangman";
        }
    }

    //TODO: add logic to detect a win/loss, add logic to change image
    //Detect the state of the game.
    //return -1 for loss, 0 for game not ended, and 1 for game won.
    private int findWinState(int lossThreshold, String hiddenWord,
                                 int numWrongGuesses, List<String> correctGuesses) {
        if (numWrongGuesses > lossThreshold) {
            return -1;
        }
        if (getCurrentDisplayedWord(hiddenWord, correctGuesses).contains("_")) {
            return 0;
        }
        return 1;
    }

    //Gets a user from SharedPreferences object
    public User getUser(String name) {
        String userData = prefs.getString(name, null);
        if (userData == null) {
            Log.d(TAG, "unable to find user");
            Toast.makeText(this, "user " + name + " not found.", Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            return new User(new JSONObject(userData));
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return null;
        }
    }
}
