package fr.thomas.androiddevforbegginers.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.thomas.androiddevforbegginers.util.BCrypt;
import fr.thomas.androiddevforbegginers.util.DatabaseHelper;

public class Player implements IModel, Parcelable {

	private int id;
	private String name;
	private String password;
	private DatabaseHelper dbhelper;

	public Player(String name, DatabaseHelper dbhelper) {
		this.name = name;
		this.dbhelper = dbhelper;
	}

	protected Player(Parcel in) {
		id = in.readInt();
		name = in.readString();
		password = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(password);
	}

	public void setDbhelper(DatabaseHelper dbhelper) {
		this.dbhelper = dbhelper;
	}

	public int getHighestScore() {
		try {
			Statement st = dbhelper.getStatement(0);
			ResultSet set = st.executeQuery("SELECT Game.score FROM Game WHERE Game.idplayer = " + this.getID());

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

	public static final Creator<Player> CREATOR = new Creator<Player>() {
		@Override
		public Player createFromParcel(Parcel in) {
			return new Player(in);
		}

		@Override
		public Player[] newArray(int size) {
			return new Player[size];
		}
	};

	public String getName() {
		return name;
	}

	public boolean authenticate(String name, String password) {
		try {
			UserAuthentication userAuth = new UserAuthentication(id, password, name);
			Thread thread = new Thread(userAuth);
			thread.start();
			thread.join();

			System.out.println("Authenticate : " + userAuth.isConnexionStatus());

			if(userAuth.isConnexionStatus()) {
				this.id = userAuth.getId();
				this.name = userAuth.getName();
				this.password = userAuth.getPassword();
			}

			return userAuth.isConnexionStatus();

		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean updatePassword(String newPassword) {
		final String encrypted = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		try {
			Statement st = dbhelper.getStatement(0);
			st.execute("UPDATE Player SET Player.password = '" + encrypted + "' WHERE Player.idplayer = " + id + ";");

			return true;
		} catch (final SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean insert() {
		try {
			Statement st = dbhelper.getStatement(0);
			boolean res = st.execute("INSERT INTO Player (name) VALUES ('" + name + "');");
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean save() {
		try {
			Statement st = dbhelper.getStatement(0);
			boolean res = st.execute("UPDATE Player SET name = '" + name + "' WHERE Player.idplayer=" + id + ";");
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getID() {
		return id;
	}

	public class UserAuthentication implements Runnable {

		private volatile int id;
		private volatile String password;
		private volatile String name;

		private volatile boolean connexionStatus;

		public UserAuthentication(int id, String password, String name) {
			this.id = id;
			this.password = password;
			this.name = name;
		}

		@Override
		public void run() {
			try {
				Statement st = dbhelper.getStatement(0);
				ResultSet set = st.executeQuery("SELECT idplayer, name FROM Player WHERE name = '" + name + "';");

				if (!set.next()) {
					System.err.println("Unknown user");
					connexionStatus = false;
				} else {
					set = st.executeQuery("SELECT idplayer, name, password FROM Player WHERE name = '" + name + "';");
					if (set.next()) {
						if (BCrypt.checkpw(password, set.getString("password"))) {
							this.id = set.getInt("idplayer");
							this.password = set.getString("password");
							this.name = set.getString("name");
						} else {
							/*
							controller.getCurrentContext().runOnUiThread(() -> {
								Toast.makeText(controller.getCurrentContext(), "Invalid credentials !", Toast.LENGTH_SHORT).show();
							});

							 */
							connexionStatus = false;
							return;
						}
					}
/*
					controller.getCurrentContext().runOnUiThread(() -> {
						//Update UI stuff
						Toast.makeText(controller.getCurrentContext(), "Connexion successful !", Toast.LENGTH_SHORT).show();

					});

 */
					connexionStatus = true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				/*
				controller.getCurrentContext().runOnUiThread(() -> {
					Toast.makeText(controller.getCurrentContext(), "An unknown error as occurred :[ !", Toast.LENGTH_SHORT).show();
				});
				 */
				connexionStatus = false;
			}
		}

		public boolean isConnexionStatus() {
			return connexionStatus;
		}

		public String getPassword() {
			return password;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}
	}
}
