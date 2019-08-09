package com.jcode.topquiz.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jcode.topquiz.util.Helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LeaderBoardActivity extends AppCompatActivity implements View.OnClickListener{

	public static final String BUNDLE_STATE_SORT_BY_SCORE = LeaderBoardActivity.class.getCanonicalName().concat("BUNDLE_STATE_SORT_BY_SCORE"); // This guarantees uniqueness of the keys
	public static final int SORT_BY_NAME_CLICKED = 10;
	public static final int SORT_BY_SCORE_CLICKED = 20;


	private TextView mUserTxt1, mUserTxt2, mUserTxt3, mUserTxt4, mUserTxt5;
	private Button mSortScoreBtn, mSortNameButn;
	private Map<String, Integer> mUsersAndScores;
	
	private SharedPreferences mPreferences;

	private GsonBuilder mGsonBuilder;
	private Gson mGson;

	private boolean mSortedByScores;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leader_board);
		initializeViewsAndVariables();
		if(savedInstanceState != null){
			mSortedByScores = savedInstanceState.getBoolean(BUNDLE_STATE_SORT_BY_SCORE);
		}else{
			mSortedByScores = true;
		}
		if (mSortedByScores){
			retrieveUsersAndScoresSortedByScores();

		}else{
			retrieveUsersAndScoresSortedByNames();
		}
		displayTop5Users();

		mSortScoreBtn.setOnClickListener(this);
		mSortNameButn.setOnClickListener(this);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(BUNDLE_STATE_SORT_BY_SCORE,mSortedByScores);
		super.onSaveInstanceState(outState);
	}

	private void displayTop5Users() {
		Iterator it = mUsersAndScores.entrySet().iterator();
		int counter = 0;
		String name = "";
		int score = 0;

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			name = (String) pair.getKey();
			score = (int)pair.getValue();
			String toDisplay = String.format("%s     %d",name,score);
			switch (counter){
				case 0:
					mUserTxt1.setText(toDisplay);
					mUserTxt1.setVisibility(View.VISIBLE);
					break;
				case 1:
					mUserTxt2.setText(toDisplay);
					mUserTxt2.setVisibility(View.VISIBLE);
					break;
				case 2:
					mUserTxt3.setText(toDisplay);
					mUserTxt3.setVisibility(View.VISIBLE);
					break;
				case 3:
					mUserTxt4.setText(toDisplay);
					mUserTxt4.setVisibility(View.VISIBLE);
					break;
				case 4:
					mUserTxt5.setText(toDisplay);
					mUserTxt5.setVisibility(View.VISIBLE);
					break;
			}
			it.remove(); // avoids a ConcurrentModificationException
			counter++;
		}
	}

	private void retrieveUsersAndScoresSortedByScores() {
		String jsonString = mPreferences.getString(MainActivity.PREF_KEY_USERS_AND_SCORES,null);
		if(jsonString == null){
			//No scores have been saved so far. User is playing for the first time
			mUsersAndScores = new HashMap<>();
		}else{
			//Gson gson = new Gson();
			//HashMap<String,Integer> map = mGson.fromJson(jsonString, HashMap.class);
			Map<String, Integer> map = mGson.fromJson(jsonString, new TypeToken<Map<String, Integer>>(){}.getType() );
			mUsersAndScores = sortByScore(map,true); //sort by score by default
		}
	}private void retrieveUsersAndScoresSortedByNames() {
		String jsonString = mPreferences.getString(MainActivity.PREF_KEY_USERS_AND_SCORES,null);
		if(jsonString == null){
			//No scores have been saved so far. User is playing for the first time
			mUsersAndScores = new HashMap<>();
		}else{
			//Gson gson = new Gson();
			//HashMap<String,Integer> map = mGson.fromJson(jsonString, HashMap.class);
			Map<String, Integer> map = mGson.fromJson(jsonString, new TypeToken<Map<String, Integer>>(){}.getType() );
			mUsersAndScores = sortByName(map,true); //sort by score by default
		}
	}

	private void initializeViewsAndVariables() {
		mUserTxt1 = findViewById(R.id.activity_leader_board_user1_txt);
		mUserTxt2 = findViewById(R.id.activity_leader_board_user2_txt);
		mUserTxt3 = findViewById(R.id.activity_leader_board_user3_txt);
		mUserTxt4 = findViewById(R.id.activity_leader_board_user4_txt);
		mUserTxt5 = findViewById(R.id.activity_leader_board_user5_txt);

		mSortNameButn = findViewById(R.id.activity_leader_board_sort_names_btn);
		mSortScoreBtn = findViewById(R.id.activity_leader_board_sort_scores_btn);
		mSortNameButn.setTag(SORT_BY_NAME_CLICKED);
		mSortScoreBtn.setTag(SORT_BY_SCORE_CLICKED);

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mGsonBuilder = new GsonBuilder();
		mGsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new Helper.MapDeserializerDoubleAsIntFix());
		mGson = mGsonBuilder.create();

	}

	private HashMap<String, Integer> sortByScore(Map<String, Integer> hm, boolean getTopFive)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer> > list =
				new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
			                   Map.Entry<String, Integer> o2)
			{
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		if(getTopFive){
			//Get Only Top 5
			// put data from sorted list to hashmap
			int counter = 1;
			HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
			for (Map.Entry<String, Integer> aa : list) {
				temp.put(aa.getKey(), aa.getValue());
				if(counter==5){
					break;
				}
				counter++;
			}
			return temp;

		}else{
			//Get All
			// put data from sorted list to hashmap
			HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
			for (Map.Entry<String, Integer> aa : list) {
				temp.put(aa.getKey(), aa.getValue());
			}
			return temp;
		}

	}

	private HashMap<String, Integer> sortByName(Map<String, Integer> hm, boolean getTopFive)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer> > list =
				new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
			                   Map.Entry<String, Integer> o2)
			{
				return (o1.getKey()).compareTo(o2.getKey());
			}
		});

		if(getTopFive){
			//Get Only Top 5
			// put data from sorted list to hashmap
			int counter = 1;
			HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
			for (Map.Entry<String, Integer> aa : list) {
				temp.put(aa.getKey(), aa.getValue());
				if(counter==5){
					break;
				}
				counter++;
			}
			return temp;

		}else{
			//Get All
			// put data from sorted list to hashmap
			HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
			for (Map.Entry<String, Integer> aa : list) {
				temp.put(aa.getKey(), aa.getValue());
			}
			return temp;
		}

	}

	@Override
	public void onClick(View view) {
		Map<String,Integer> map = new HashMap<>();
		switch ((int)view.getTag()){
			case SORT_BY_NAME_CLICKED:
				retrieveUsersAndScoresSortedByNames();
				map = mUsersAndScores;
				mSortedByScores = false;
				//TODO:change back to sort by score
				//mUsersAndScores = sortByName(map, true);
				displayTop5Users();
				break;
			default:
				//ASSUMPTION here is all other cases are for sort by SCORE
				retrieveUsersAndScoresSortedByScores();
				map = mUsersAndScores;
				mSortedByScores = true;
				//mUsersAndScores = sortByScore(map, true);
				//mUsersAndScores = map;
				displayTop5Users();

				break;
		}

	}
}
