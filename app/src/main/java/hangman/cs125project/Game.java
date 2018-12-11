package hangman.cs125project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.android.volley.DefaultRetryPolicy;
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
    public static final String PACKAGE_NAME = "hangman.cs125project";
    private static final int MAX_WRONG_GUESSES = 8;

    private static RequestQueue requestQueue;

    private List<String> words;
    private String hiddenWord;
    private int numWrongGuesses = 0;
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
        final ImageView image = findViewById(R.id.animation_goes_here);
        getWordsFromAPI(RANDOM_WORD_API_KEY, 1, new VolleyCallback() {
            @Override
            public void onSuccess() {
                hiddenWord = words.get(0);
                wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));
            }
        });
        //Redo if word has dashes
        if (hiddenWord.contains("-")) {
            getWordsFromAPI(RANDOM_WORD_API_KEY, 1, new VolleyCallback() {
                @Override
                public void onSuccess() {
                    hiddenWord = words.get(0);
                    wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));
                }
            });
        }
        wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));

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
            Log.d(TAG, getUser(username).submitUserInfo());
        }

        //Operations when user tries to guess the whole word.
        Button wordSubmit = findViewById(R.id.wordButton);
        wordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText wordField = findViewById(R.id.wordInput);
                String word = wordField.getText().toString().toLowerCase();
                Log.d(TAG, "user guessed: " + word);
                TextView error = findViewById(R.id.Errortext);
                if (word.contains(" ") || word.length() == 0) {
                    error.setText("Word must not be empty or contain spaces");
                } else if (!word.equals(hiddenWord)) {
                    error.setText("Wrong! Guess again?");
                    numWrongGuesses++;
                    setImage(image, numWrongGuesses);
                    int gameState = findWinState(MAX_WRONG_GUESSES, hiddenWord, numWrongGuesses, correctGuesses);
                    if (gameState == -1) {
                        displayEndGame(false);
                    }
                } else {
                    error.setText("");
                    //operations to do in case of a win here.
                    user.addToScore(hiddenWord.length());
                    Log.d(TAG, user.getUsername() + " gained some points");
                    editor = prefs.edit();
                    editor.putString(user.getUsername(), user.submitUserInfo());
                    editor.commit();
                    displayEndGame(true);
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
                    numWrongGuesses++;
                    setImage(image, numWrongGuesses);
                } else if (wrongGuesses.contains(letter)) {
                    errorTxt.setText("You already tried letter " + letter + ".");
                } else if (correctGuesses.contains(letter)) {
                    errorTxt.setText("Pick a different letter");
                } else {
                    errorTxt.setText("");
                    correctGuesses.add(letter);
                    wordDisplay.setText(getCurrentDisplayedWord(hiddenWord, correctGuesses));
                }

                int gameState = findWinState(MAX_WRONG_GUESSES, hiddenWord, numWrongGuesses, correctGuesses);
                if (gameState == 1) {
                    user.addToScore(hiddenWord.length());
                    Log.d(TAG, user.getUsername() + " gained some points");
                    editor = prefs.edit();
                    editor.putString(user.getUsername(), user.submitUserInfo());
                    editor.commit();
                    displayEndGame(true);
                } else if (gameState == -1) {
                    displayEndGame(false);
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
            request.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (Exception other) {
            other.printStackTrace();
        }
    }

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

    //Updates the image at the top of the screen.
    private void setImage(ImageView view, int wrongGuesses) {
        Resources res = getResources();
        int resourceId;
        if (wrongGuesses < 9) {
            resourceId = res.getIdentifier("hangman_" + wrongGuesses, "drawable", getPackageName());
        } else {
            resourceId = res.getIdentifier("hangman_lost", "drawable", getPackageName());
        }
        view.setImageResource(resourceId);
    }

    //Displays a message for when the user wins or loses
    private void displayEndGame(boolean won) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        if (won) {
            String foo = getString(R.string.wintext) + "\n" + hiddenWord.length() + " points were added to your score.";
            alertDialogBuilder.setMessage(foo);
        } else {
            String foo = getString(R.string.losstext) + " " + hiddenWord;
            alertDialogBuilder.setMessage(foo);
        }


        alertDialogBuilder.setPositiveButton("New Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Game.this, Game.class);
                intent.putExtra("currentUsername", user.getUsername());
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("Back to Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Game.this, HomePage.class));
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alertDialog.dismiss();
                startActivity(new Intent(Game.this, HomePage.class));
            }
        });
        alertDialog.show();
    }
}
