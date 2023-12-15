package fr.thomas.androiddevforbegginers.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import fr.thomas.androiddevforbegginers.control.Controller;
import fr.thomas.androiddevforbegginers.util.QuestionsBuilder;

public class Question implements Parcelable {

	private int id;
	private String label;
	private int difficultyLevel;
	private ArrayList<Answer> answers;
	private Controller controller;

	public Question(Controller controller, String label, ArrayList<Answer> answers) {
		this.label = label;
		this.answers = answers;
		this.controller = controller;
	}

	public Question(int id, Controller controller) {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);
			ResultSet res = st.executeQuery("SELECT * FROM Question WHERE idquestion = " + id + ";");
			this.controller = controller;
			if (!res.next()) {
				this.id = -1;
				this.label = "";
				this.difficultyLevel = 0;
				this.answers = new ArrayList<Answer>();
			} else {
				this.id = res.getInt("idquestion");
				this.label = res.getString("label");
				this.difficultyLevel = res.getInt("difficultyLevel");
				this.answers = new ArrayList<>();

				ResultSet qr = st.executeQuery("SELECT * FROM Answer WHERE Answer.idquestion = " + this.id + ";");

				int count = 1;
				while (qr.next()) {
					answers.add(new Answer(QuestionsBuilder.getCharForNumber(count).charAt(0), qr.getString("label"),
							qr.getBoolean("iscorrect")));
					count++;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected Question(Parcel in) {
		id = in.readInt();
		label = in.readString();
		difficultyLevel = in.readInt();
		controller = in.readParcelable(Controller.class.getClassLoader());
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

	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	
	public String getAnAnswer(int id) {
		return answers.get(id).getLabel();
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Pose la question au joueur
	 */
	public void ask() {
		String answersString = "";

		for (Answer answer : answers) {
			answersString += answer.getQchar() + ") " + answer.getLabel() + "\n";
		}

		boolean isValid = false;

		// Tant que la réponse choisie par le joueur n'est pas valide.
		while (!isValid) {
			//char response = controller.getView().askPlayer(label + "\n" + answersString).charAt(0);
			//controller.getView().output("Vous avez répondu : " + response);

			// Vérifie quelle réponse a été choisie par le joueur.
			for (Answer answer : answers) {

				/*
				if (answer.getQchar() == response) {
					if (answer.isCorrect()) {
						//controller.getView().output("Bravo !! C'est la bonne réponse.");
						controller.getGame().addScore(difficultyLevel * 100);
					} else
						//controller.getView().output("Dommage.. Ce n'est pas la bonne réponse..");

					isValid = true;
					return;
				}

				 */
			}

			//controller.getView().output("Réponse invalide, veuillez réessayer...");
		}
	}

	public int getDifficulty() {
		return difficultyLevel;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(label);
		dest.writeInt(difficultyLevel);
	}
}
