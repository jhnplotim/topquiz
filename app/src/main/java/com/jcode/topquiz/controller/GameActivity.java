package com.jcode.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.jcode.topquiz.model.Question;
import com.jcode.topquiz.model.QuestionBank;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static java.lang.System.out;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

	public static final String BUNDLE_EXTRA_SCORE = GameActivity.class.getCanonicalName().concat("BUNDLE_EXTRA_SCORE"); // This guarantees uniqueness of the keys

	//To be used to restore activity upon rotation of the device
	public static final String BUNDLE_STATE_SCORE = GameActivity.class.getCanonicalName().concat("BUNDLE_STATE_SCORE"); // This guarantees uniqueness of the keys
	public static final String BUNDLE_STATE_QUESTION_NUMBER = GameActivity.class.getCanonicalName().concat("BUNDLE_STATE_QUESTION_NUMBER"); // This guarantees uniqueness of the keys
	public static final String BUNDLE_STATE_CURRENT_QUESTION = GameActivity.class.getCanonicalName().concat("BUNDLE_STATE_CURRENT_QUESTION"); // This guarantees uniqueness of the keys
	public static final String BUNDLE_STATE_QUESTION_BANK = GameActivity.class.getCanonicalName().concat("BUNDLE_STATE_QUESTION_BANK"); // This guarantees uniqueness of the keys
	//public static final String BUNDLE_STATE_ENABLE_TOUCH_EVENT = GameActivity.class.getCanonicalName().concat("BUNDLE_STATE_ENABLE_TOUCH_EVENT");

	private TextView mQuestionTxt;
	private Button mAnswer1Btn,mAnswer2Btn,mAnswer3Btn,mAnswer4Btn;
	private QuestionBank mQuestionBank;
	private Question mCurrentQuestion;

	private int mScore;
	private int mNumberOfQuestions;
	private boolean mEnableTouchEvents;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		out.println("GameActivity::onCreate");
		setContentView(R.layout.activity_game);
		initializeVariables();

		//name the answer buttons using tags
		setTagsForButtons();

		//used to restore activity in case of screen rotation
		if(savedInstanceState != null){
			mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
			mNumberOfQuestions = savedInstanceState.getInt(BUNDLE_STATE_QUESTION_NUMBER);
			mQuestionBank = savedInstanceState.getParcelable(BUNDLE_STATE_QUESTION_BANK);
			mCurrentQuestion = savedInstanceState.getParcelable(BUNDLE_STATE_CURRENT_QUESTION);
			//mEnableTouchEvents = savedInstanceState.getBoolean(BUNDLE_STATE_ENABLE_TOUCH_EVENT);
			//Fix Question Bank bug on recreation
		}else{
			//no previous state saved. therefore activity is being opened for the first time.
			mScore = 0;
			mNumberOfQuestions = 7; //Number of questions to be answered before Game ends
			//populate questions first.
			mQuestionBank = this.generateQuestions();

			//pick question to display
			//mCurrentQuestion = mQuestionBank.getQuestion();
			getQuestionToDisplay();
		}

		//enable touch events
		mEnableTouchEvents = true;

		displayQuestion(mCurrentQuestion);

		//the click listener for all buttons
		// Use the same listener for all four buttons.
		mAnswer1Btn.setOnClickListener(this);
		mAnswer2Btn.setOnClickListener(this);
		mAnswer3Btn.setOnClickListener(this);
		mAnswer4Btn.setOnClickListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(BUNDLE_EXTRA_SCORE, mScore);
		outState.putInt(BUNDLE_STATE_QUESTION_NUMBER, mNumberOfQuestions);
		outState.putParcelable(BUNDLE_STATE_QUESTION_BANK,mQuestionBank);
		outState.putParcelable(BUNDLE_STATE_CURRENT_QUESTION, mCurrentQuestion);
		//outState.putBoolean(BUNDLE_STATE_ENABLE_TOUCH_EVENT, mEnableTouchEvents);
		super.onSaveInstanceState(outState);
	}

	private void initializeVariables() {
		mQuestionTxt = findViewById(R.id.activity_game_question_txt);
		mAnswer1Btn = findViewById(R.id.activity_game_answer1_btn);
		mAnswer2Btn = findViewById(R.id.activity_game_answer2_btn);
		mAnswer3Btn = findViewById(R.id.activity_game_answer3_btn);
		mAnswer4Btn = findViewById(R.id.activity_game_answer4_btn);
	}

	private void setTagsForButtons() {
		// Use the tag property to 'name' the buttons
		mAnswer1Btn.setTag(0);
		mAnswer2Btn.setTag(1);
		mAnswer3Btn.setTag(2);
		mAnswer4Btn.setTag(3);
	}

	private void getQuestionToDisplay() {
		mCurrentQuestion = mQuestionBank.getQuestion();
	}

	private void displayQuestion(Question question) {
		// Set the text for the question text view and the four buttons
		mQuestionTxt.setText(question.getQuestion());
		mAnswer1Btn.setText(question.getChoiceList().get(0));
		mAnswer2Btn.setText(question.getChoiceList().get(1));
		mAnswer3Btn.setText(question.getChoiceList().get(2));
		mAnswer4Btn.setText(question.getChoiceList().get(3));
	}

	private QuestionBank generateQuestions() {
		Question question1 = new Question("Who created Android?",
				Arrays.asList("Andy Rubin",
						"Steve Wozniak",
						"Jake Wharton",
						"Paul Smith"),
				0);

		Question question2 = new Question("When did the first person land on the moon?",
				Arrays.asList("1958",
						"1962",
						"1967",
						"1969"),
				3);

		Question question3 = new Question("What is the house number of The Simpsons?",
				Arrays.asList("42",
						"101",
						"666",
						"742"),
				3);

		Question question4 = new Question("In which state was Bill Clinton governor before becoming President of the United States?",
				Arrays.asList("California",
						"Arkansas",
						"Boston",
						"Kampala"),
				1);

		Question question5 = new Question("What is the most spoken language in Belgium?",
				Arrays.asList("French",
						"Dutch",
						"English",
						"Mandarin"),
				1);

		Question question6 = new Question("Which country had a Prime Minister and President who were twin brothers?",
				Arrays.asList("Holland",
						"Iceland",
						"England",
						"Poland"),
				3);

		Question question7 = new Question("In what year was the Berlin wall built?",
				Arrays.asList("1950",
						"1961",
						"1996",
						"1956"),
				1);

		return new QuestionBank(Arrays.asList(question1,
				question2,
				question3,
				question4,
				question5,
				question6,
				question7));
	}

	private void endGame() {
		AlertDialog dialog =
		new AlertDialog.Builder(this)
				.setTitle("Well done!")
				.setMessage("Your score is: "+ mScore)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//close the Activity
						Intent data = new Intent();
						data.putExtra(BUNDLE_EXTRA_SCORE, mScore);
						setResult(RESULT_OK, data);
						finish();
					}
				})
				.setCancelable(false)
				.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	//region OnClickListener implementation
	@Override
	public void onClick(View view) {
		int responseIndex = (int) view.getTag();
		boolean isAnswerCorrect = (responseIndex == mCurrentQuestion.getAnswerIndex());
		if(isAnswerCorrect){
			Toast.makeText(this, "Good job!", Toast.LENGTH_SHORT).show();
            mScore++;


		}else{
			Toast.makeText(this, "Uh oh, that's wrong! :(", Toast.LENGTH_SHORT).show();
		}

		//temporarily disable the user from clicking while toast is still on
		mEnableTouchEvents = false;

		//DELAYED EXECUTION to improve user experience.
		//memory leak here. OK for this example but needs correct in future.
		//See https://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html to get more information
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				// If this is the last question, ends the game.
				// Else, display the next question.
				if(--mNumberOfQuestions == 0){
					//End the game
					endGame();
				} else {
					getQuestionToDisplay();
					displayQuestion(mCurrentQuestion);
				}
				mEnableTouchEvents = true;
			}
		},2000); // LENGTH_SHORT is usually 2 seconds long, LENGTH_LONG 3.5 seconds


	}
	//endregion


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// we must prevent the user from clicking a response until the next question appears
		//We will use the dispatchTouchEvent() method to block incoming touches.
		//This is an Activity class method which is called whenever a user touches the screen. It is a great place to temporarily ignore touches.
		//The boolean we return from this method tells the system whether the user action should be taken into account.
		// If we want to ignore the action, we return false. Otherwise, call the parent class method.
		return mEnableTouchEvents && super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onStart() {
		super.onStart();
		out.println("GameActivity::OnStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		out.println("GameActivity::OnResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		out.println("GameActivity::onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		out.println("GameActivity::OnStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		out.println("GameActivity::OnDestroy");
	}
}
