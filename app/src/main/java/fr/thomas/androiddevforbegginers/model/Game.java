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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.thomas.androiddevforbegginers.util.DatabaseHelper;

public class Game implements IModel, Parcelable {

	private int id;
	private int score;
	private Random rand;

	private Player player;
	private ArrayList<Question> questions;

	private DatabaseHelper dbhelper;

	public Game(Player player, DatabaseHelper dbhelper) {
		this.rand = new Random();
		this.questions = new ArrayList<>();
		this.player = player;
		this.score = 0;
		this.dbhelper = dbhelper;
	}

	protected Game(Parcel in) {
		id = in.readInt();
		score = in.readInt();
		player = in.readParcelable(Player.class.getClassLoader());
		dbhelper = in.readParcelable(DatabaseHelper.class.getClassLoader());
		questions = in.readArrayList(ArrayList.class.getClassLoader());
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
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(() -> {
		try {
			Statement st = dbhelper.getStatement(0);
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
			PreparedStatement ps = dbhelper.getCon().prepareStatement(query);
			for (int i = 1; i <= qcount; i++) {
				ps.setInt(i, selectedQuestions.get(i - 1));
			}
			ResultSet rs = ps.executeQuery();

			// Afficher les questions récupérées
			while (rs.next()) {
				int questionId = rs.getInt("idquestion");
				questions.add(new Question(questionId, dbhelper));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			executorService.shutdown();
		}

		executorService.shutdown();
		System.out.println("QUESTIONS : " + questions);

		});

		while(!executorService.isShutdown()) {

		}
	}

	@Override
	public boolean insert() {
		Object lock = new Object();
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.submit(() -> {
			synchronized (lock) {
				try {
					Statement st = dbhelper.getStatement(0);

					st.executeUpdate("INSERT INTO Game (score, idplayer) VALUES ('" + score + "', " + player.getID() + ");",
							Statement.RETURN_GENERATED_KEYS);

					ResultSet res = st.getGeneratedKeys();

					if (res.next()) {
						this.id = res.getInt(1);
					}

					lock.notifyAll();
				} catch (SQLException e) {
					e.printStackTrace();
					lock.notifyAll();
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			return true;
		}
	}

	public ArrayList<Question> getQuestions() {
		return questions;
	}

	@Override
	public boolean save() {
		try {
			Statement st = dbhelper.getStatement(0);

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
			Statement st = dbhelper.getStatement(0);
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
	}
}
