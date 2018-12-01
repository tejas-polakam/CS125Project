package hangman.cs125project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Button;
import android.view.*;
import android.widget.*;

public class HomePageNavigation extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        final Button nav1 = findViewById(R.id.navigation_dashboard);
        final Button nav2 = findViewById(R.id.navigation_home);
        final Button nav3 = findViewById(R.id.navigation_notifications);
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_navigation);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        final Button homeButton = findViewById(R.id.button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //code for button actions
            }
        });


    }

}
