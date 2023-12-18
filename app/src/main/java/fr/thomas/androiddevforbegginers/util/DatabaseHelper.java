package fr.thomas.androiddevforbegginers.util;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import fr.thomas.androiddevforbegginers.control.Controller;

public class DatabaseHelper {

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

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                executorService.shutdownNow();
            }

            try {
                this.url += this.bdname;
                this.con = DriverManager.getConnection(url, username, password);
                System.out.println("Connexion initialization : " + con);
                activeStatements = new ArrayList<Statement>();
                for (int i = 0; i < ACTIVE_STATEMENT_COUNT; i++) {
                    activeStatements.add(con.createStatement());
                }
            } catch(SQLException ex) {
                ex.printStackTrace();
                executorService.shutdownNow();
            }

            System.out.println("Instanciation ============> " + activeStatements);
            executorService.shutdown();
        });

        while(!executorService.isShutdown()) {
            //System.out.println("Waiting for connexion...");
            System.out.println(executorService.isTerminated());
        }

    }

    protected DatabaseHelper(Parcel in) {
        bdname = "quizzgame_proto0";
        url="jdbc:mysql://192.168.122.19:3306/";
        username="_gateway";
        password="dev";
        ACTIVE_STATEMENT_COUNT = in.readInt();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                executorService.shutdownNow();
            }

            try {
                this.url += this.bdname;
                this.con = DriverManager.getConnection(url, username, password);
                System.out.println("Connexion initialization : " + con);
                activeStatements = new ArrayList<Statement>();
                for (int i = 0; i < ACTIVE_STATEMENT_COUNT; i++) {
                    activeStatements.add(con.createStatement());
                }

            } catch (SQLException ex) {
                System.err.println("Cannot provide active statements...");
                ex.printStackTrace();
                executorService.shutdownNow();
            }

            executorService.shutdown();
        });

        while(!executorService.isShutdown()) {
            //System.out.println("Waiting for connexion...");
            System.out.println(executorService.isTerminated());
        }
    }

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
}
