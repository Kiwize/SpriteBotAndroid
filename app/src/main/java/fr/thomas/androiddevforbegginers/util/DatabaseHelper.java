package fr.thomas.androiddevforbegginers.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import fr.thomas.androiddevforbegginers.control.Controller;

public class DatabaseHelper implements Parcelable {

    private String bdname = "";
    private String url = "";
    private String username = "";
    private String password = "";

    private Connection con;

    private int ACTIVE_STATEMENT_COUNT = 2;
    private ArrayList<Statement> activeStatements;

    public DatabaseHelper() {

        //TODO : Move to external source (DB)
        bdname = "quizzgame_proto0";
        url="jdbc:mysql://192.168.122.19:3306/";
        username="_gateway";
        password="dev";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                this.url += this.bdname;
                this.con = DriverManager.getConnection(url, username, password);

                activeStatements = new ArrayList<Statement>();
                for (int i = 0; i < ACTIVE_STATEMENT_COUNT; i++) {
                    activeStatements.add(con.createStatement());
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    protected DatabaseHelper(Parcel in) {
        bdname = in.readString();
        url = in.readString();
        username = in.readString();
        password = in.readString();
        ACTIVE_STATEMENT_COUNT = in.readInt();

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                this.url += this.bdname;
                this.con = DriverManager.getConnection(url, username, password);

                activeStatements = new ArrayList<Statement>();
                for (int i = 0; i < ACTIVE_STATEMENT_COUNT; i++) {
                    activeStatements.add(con.createStatement());
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static final Creator<DatabaseHelper> CREATOR = new Creator<DatabaseHelper>() {
        @Override
        public DatabaseHelper createFromParcel(Parcel in) {
            return new DatabaseHelper(in);
        }

        @Override
        public DatabaseHelper[] newArray(int size) {
            return new DatabaseHelper[size];
        }
    };

    public Statement create() {
        try {
            return con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection getCon() {
        return con;
    }

    public Statement getStatement(int id) {
        if(id <= ACTIVE_STATEMENT_COUNT - 1 && id >= 0) {
            System.out.println(activeStatements);
            return activeStatements.get(id);
        }
        throw new IllegalArgumentException("Invalid statement ID !");
    }

    public void close() {
        try {
            this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(bdname);
        dest.writeString(url);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(ACTIVE_STATEMENT_COUNT);
    }
}
