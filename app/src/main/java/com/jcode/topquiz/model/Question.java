package com.jcode.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by otimj on 8/4/2019.
 */
public class Question implements Parcelable {
	private String mQuestion;
	private List<String> mChoiceList;
	private int mAnswerIndex;

	public Question(String question, List<String> choiceList, int answerIndex) {

		this.setQuestion(question);
		this.setChoiceList(choiceList);
		this.setAnswerIndex(answerIndex);
	}

	protected Question(Parcel in) {
		mQuestion = in.readString();
		mChoiceList = in.createStringArrayList();
		mAnswerIndex = in.readInt();
	}

	public static final Creator<Question> CREATOR = new Creator<Question>() {
		@Override
		public Question createFromParcel(Parcel in) {
			return new Question(in);
		}

		@Override
		public Question[] newArray(int size) {
			return new Question[size];
		}
	};

	public String getQuestion() {
		return mQuestion;
	}

	public void setQuestion(String question) {
		mQuestion = question;
	}

	public List<String> getChoiceList() {
		return mChoiceList;
	}

	public void setChoiceList(List<String> choiceList) {
		if (choiceList == null) {
			throw new IllegalArgumentException("Array cannot be null");
		}
		else{
			if(choiceList.size() == 0){
				throw new IllegalArgumentException("Array cannot be empty");
			}
		}

		mChoiceList = choiceList;
	}

	public int getAnswerIndex() {
		return mAnswerIndex;
	}

	public void setAnswerIndex(int answerIndex) {
		if (answerIndex < 0 || answerIndex >= mChoiceList.size()) {
			throw new IllegalArgumentException("Answer index is out of bound");
		}

		mAnswerIndex = answerIndex;
	}

	@Override
	public String toString() {
		return "Question{" +
				"mQuestion='" + mQuestion + '\'' +
				", mChoiceList=" + mChoiceList +
				", mAnswerIndex=" + mAnswerIndex +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(mQuestion);
		parcel.writeStringList(mChoiceList);
		parcel.writeInt(mAnswerIndex);
	}
}
