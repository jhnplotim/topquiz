package com.jcode.topquiz.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jcode.topquiz.model.User;
import com.jcode.topquiz.util.Helper;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {

	public static final int GAME_ACTIVITY_REQUEST_CODE = 42;
	public static final String PREF_KEY_NAME = "PREFERENCE_KEY_NAME";
	public static final String PREF_KEY_SCORE = "PREFERENCE_KEY_SCORE";
	public static final String PREF_KEY_USERS_AND_SCORES = "PREFERENCE_KEY_USERS_AND_SCORES";


	private TextView mGreetingText;
	private EditText mNameInput;
	private Button mPlayButton;
	private Button mLeaderBoardButton;
	private User mUser;
	private SharedPreferences mPreferences;
	private Map<String, Integer> mUsersAndScores;
	private GsonBuilder mGsonBuilder;
	private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

        //Disable button up until we are sure the user has provided a first name
        mPlayButton.setEnabled(false);

        out.println("MainActivity::OnCreate");

        //Retrieve the previous score if it exists
        retrieveLastScore();

        retrieveUsersAndScores();

        //monitor if the user starts typing in the edit text field
	    mNameInput.addTextChangedListener(new TextWatcher() {
		    @Override
		    public void beforeTextChanged(CharSequence s, int start, int county, int after) {

		    }

		    @Override
		    public void onTextChanged(CharSequence s, int start, int count, int after) {
				//This is where we'll check the user input
			    mPlayButton.setEnabled(s.toString().length() != 0);
		    }

		    @Override
		    public void afterTextChanged(Editable editable) {

		    }
	    });

	    mPlayButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    //The user just clicked
			    String firstname = mNameInput.getText().toString();
			    mUser.setFirstName(firstname);
			    mPreferences.edit().putString(PREF_KEY_NAME, mUser.getFirstName()).apply();
			    Intent gameActivity = new Intent(MainActivity.this, GameActivity.class);
			    //startActivity(gameActivity);
			    startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE);
		    }
	    });

	    mLeaderBoardButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    //The user just clicked to view the leader board
			    Intent leaderBoardActity = new Intent(MainActivity.this, LeaderBoardActivity.class);
			    startActivity(leaderBoardActity);
		    }
	    });
    }

	private void retrieveLastScore() {
    	String previousName = mPreferences.getString(PREF_KEY_NAME,null);
    	int previousScore = mPreferences.getInt(PREF_KEY_SCORE,0);

    	if(previousName == null && previousScore == 0){
    		//User is playing for the first time

	    }else{
    		//Game has been played before
		    updateGreetingText(previousName, previousScore);
		    mLeaderBoardButton.setVisibility(View.VISIBLE);


	    }
	}

	private void retrieveUsersAndScores(){
    	String jsonString = mPreferences.getString(PREF_KEY_USERS_AND_SCORES,null);
    	if(jsonString == null){
    		//No scores have been saved so far. User is playing for the first time
		    mUsersAndScores = new HashMap<>();
	    }else{
		    //Gson gson = new Gson();
		    //Map<String,Integer> map = mGson.fromJson(jsonString, Map.class);
		    Map<String, Integer> map = mGson.fromJson(jsonString, new TypeToken<Map<String, Integer>>(){}.getType() );
		    mUsersAndScores = map;
	    }
	}

	private void updateGreetingText(String name, int score) {
		mNameInput.setText(name);
		mNameInput.setSelection(name.length());
		String message = String.format("Welcome back %s! \n Your last score was %d, will you do better this time?",name,score);
		//mGreetingText.setText("Welcome back "+name+"! \n"+" Your last score was "+score+ ", will you do better this time?");
		mGreetingText.setText(message);
		mPlayButton.setEnabled(true);
	}

	private void initializeVariables() {
    	mGreetingText = findViewById(R.id.activity_main_greeting_txt);
    	mNameInput = findViewById(R.id.activity_main_name_input);
    	mPlayButton = findViewById(R.id.activity_main_play_btn);
		mLeaderBoardButton = findViewById(R.id.activity_main_leader_board_btn);
    	mUser = new User();
    	mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mGsonBuilder = new GsonBuilder();
		mGsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new Helper.MapDeserializerDoubleAsIntFix());
		mGson = mGsonBuilder.create();


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//compare request code and result code before proceeding
		if(requestCode == GAME_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
			int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE,0);
			mPreferences.edit().putInt(PREF_KEY_SCORE,score).apply();
			updateGreetingText(mUser.getFirstName(),score);

			//Gson gson = new Gson();
			mLeaderBoardButton.setVisibility(View.VISIBLE);
			mUsersAndScores.put(mUser.getFirstName(), score);
			String jsonVersion = mGson.toJson(mUsersAndScores);
			mPreferences.edit().putString(PREF_KEY_USERS_AND_SCORES, jsonVersion).apply();

		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		out.println("MainActivity::OnStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		out.println("MainActivity::OnResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		out.println("MainActivity::onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		out.println("MainActivity::OnStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		out.println("MainActivity::OnDestroy");
	}
}
