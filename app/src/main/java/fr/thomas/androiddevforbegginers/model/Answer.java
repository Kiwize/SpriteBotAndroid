package fr.thomas.androiddevforbegginers.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Answer implements Parcelable {
	
	private String label;
	private boolean isCorrect;
	private char qchar;
	
	public Answer(char qchar, String label, boolean isCorrect) {
		this.label = label;
		this.isCorrect = isCorrect;
		this.qchar = qchar;
	}

	protected Answer(Parcel in) {
		label = in.readString();
		isCorrect = in.readByte() != 0;
		qchar = (char) in.readInt();
	}

	public static final Creator<Answer> CREATOR = new Creator<Answer>() {
		@Override
		public Answer createFromParcel(Parcel in) {
			return new Answer(in);
		}

		@Override
		public Answer[] newArray(int size) {
			return new Answer[size];
		}
	};

	public String getLabel() {
		return label;
	}
	
	public boolean isCorrect() {
		return isCorrect;
	}
	
	public char getQchar() {
		return qchar;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeString(label);
		dest.writeByte((byte) (isCorrect ? 1 : 0));
		dest.writeInt((int) qchar);
	}
}
