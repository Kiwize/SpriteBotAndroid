package fr.thomas.androiddevforbegginers.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.thomas.androiddevforbegginers.control.Controller;

public class Game implements IModel, Parcelable {

	private int id;
	private int score;
	private Player player;
	private Random rand;
	private Controller controller;

	public Game(Controller controller, Player player) {
		this.rand = new Random();
		this.player = player;
		this.controller = controller;
		this.score = 0;
	}

	protected Game(Parcel in) {
		id = in.readInt();
		score = in.readInt();
		player = in.readParcelable(Player.class.getClassLoader());
		controller = in.readParcelable(Controller.class.getClassLoader());
	}

	public static final Creator<Game> CREATOR = new Creator<Game>() {
		@Override
		public Game createFromParcel(Parcel in) {
			return new Game(in);
		}

		@Override
		public Game[] newArray(int size) {
			return new Game[size];
		}
	};

	/**
	 * Débute la partie, pose toutes les questions une par une.
	 * 
	 * @author Thomas PRADEAU
	 */
	public void begin() {
		//this.controller.getPlayView().resetPointer();
		//this.controller.getPlayView().setVisible(true);
		//this.controller.getPlayView().loadUIQuestions(controller.getQuestions());
	}

	public void addScore(int amount) {
		score += amount;
	}
	
	public void setScore(int amount) {
		score = amount;
	}

	/**
	 * Choisis des questions aléatoires parmis toutes les questions.
	 *
	 * @author Thomas PRADEAU
	 */
	public void getRandomQuestions() {
		// DIFFICULTY : 1 = 5 Questions / 2 = 10 Questions / 3 = 30 Questions.
		ArrayList<Question> cq = new ArrayList<>();
		try {
			System.out.println(controller);
			Statement st = controller.getDatabaseHelper().getStatement(0);
			int count = 0;
			ResultSet set = st.executeQuery("SELECT Count(*) as total FROM Question;");
			if (set.next()) {
				count = set.getInt("total");
			}

			List<Integer> randomNumbers = new ArrayList<>();
			for (int i = 1; i <= count; i++) {
				randomNumbers.add(i);
			}
			Collections.shuffle(randomNumbers);
			int qcount = 5;
			int lim = rand.nextInt(count - qcount + 1);

			List<Integer> selectedQuestions = randomNumbers.subList(lim, lim + 5);

			// Récupérer les questions correspondantes à ces nombres

			String sqlParams = "";
			for (int i = 0; i < qcount - 1; i++) {
				sqlParams += "?,";
			}

			sqlParams += "?";

			String query = "SELECT idquestion, label FROM Question WHERE idquestion IN (" + sqlParams + ")";
			PreparedStatement ps = controller.getDatabaseHelper().getCon().prepareStatement(query);
			for (int i = 1; i <= qcount; i++) {
				ps.setInt(i, selectedQuestions.get(i - 1));
			}
			ResultSet rs = ps.executeQuery();

			// Afficher les questions récupérées
			while (rs.next()) {
				int questionId = rs.getInt("idquestion");
				cq.add(new Question(questionId, this.controller));
			}

			controller.setQuestions(cq);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean insert() {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);

			st.executeUpdate("INSERT INTO Game (score, idplayer) VALUES ('" + score + "', " + player.getID() + ");",
					Statement.RETURN_GENERATED_KEYS);

			ResultSet res = st.getGeneratedKeys();

			if (res.next()) {
				this.id = res.getInt(1);
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean save() {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);

			boolean res = st.execute("UPDATE Game SET score = " + score + ", idplayer=" + player.getID()
					+ " WHERE Game.idgame=" + id + ";");

			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getHighestScore(Player player) {
		try {
			Statement st = controller.getDatabaseHelper().getStatement(0);
			ResultSet set = st.executeQuery("SELECT Game.score FROM Game WHERE Game.idplayer = " + player.getID());

			int bestScore = 0;

			while (set.next()) {
				if (bestScore < set.getInt("score"))
					bestScore = set.getInt("score");
			}

			return bestScore;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(score);
		dest.writeParcelable(player, flags);
		dest.writeParcelable(controller, flags);
	}
}
