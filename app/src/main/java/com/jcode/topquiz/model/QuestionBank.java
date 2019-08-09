package com.jcode.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

/**
 * Created by otimj on 8/4/2019.
 */
public class QuestionBank implements Parcelable {
	private List<Question> mQuestionList;
	private int mNextQuestionIndex;


	public QuestionBank(List<Question> questionList) {
		// Shuffle the question list before storing it
		mQuestionList = questionList;

		// Shuffle the question list
		Collections.shuffle(mQuestionList);

		mNextQuestionIndex = 0;
	}

	protected QuestionBank(Parcel in) {
		mNextQuestionIndex = in.readInt();
		in.readTypedList(mQuestionList,Question.CREATOR);
	}

	public static final Creator<QuestionBank> CREATOR = new Creator<QuestionBank>() {
		@Override
		public QuestionBank createFromParcel(Parcel in) {
			return new QuestionBank(in);
		}

		@Override
		public QuestionBank[] newArray(int size) {
			return new QuestionBank[size];
		}
	};

	public Question getQuestion() {
		// Loop over the questions and return a new one at each call

		// Ensure we loop over the questions
		if (mNextQuestionIndex == mQuestionList.size()) {
			mNextQuestionIndex = 0;
		}

		// Please note the post-incrementation
		return mQuestionList.get(mNextQuestionIndex++);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(mNextQuestionIndex);
		parcel.writeTypedList(mQuestionList);
	}
}
