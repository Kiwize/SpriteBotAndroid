package fr.thomas.androiddevforbegginers.util;

import android.app.AlertDialog;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseHelper {

    private String bdname = "";
    private String url = "";
    private String username = "";
    private String password = "";

    private boolean connexionStatus = false;

    private final Object lock;

    private Connection con;

    private final int ACTIVE_STATEMENT_COUNT = 2;
    private ArrayList<Statement> activeStatements;

    public DatabaseHelper(AppCompatActivity context) {
        lock = new Object();
       connect();
    }

    protected DatabaseHelper(Parcel in) {
        lock = new Object();
        connect();
    }

    public boolean getConnexionStatus() {
        return(connexionStatus && con != null);
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

    public void connect() {
        bdname = "quizzgame_proto0";
        url = "jdbc:mysql://192.168.1.100:3306/";
        username = "_gateway";
        password = "dev";

        long BEGIN_TIME = System.currentTimeMillis();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

            executorService.submit(() -> {
                synchronized (lock) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        lock.notifyAll();
                        executorService.shutdownNow();
                    }

                    try {
                        this.url += this.bdname;
                        DriverManager.setLoginTimeout(5); // Wait 5 seconds for database connexion to complete.
                        this.con = DriverManager.getConnection(url, username, password);
                        activeStatements = new ArrayList<Statement>();
                        for (int i = 0; i < ACTIVE_STATEMENT_COUNT; i++) {
                            activeStatements.add(con.createStatement());
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        lock.notifyAll();
                        executorService.shutdownNow();
                    }

                    lock.notifyAll();
                    executorService.shutdown();
                }
            });

        synchronized (lock) {
            try {
                lock.wait();
                if (System.currentTimeMillis() - BEGIN_TIME >= 4000) {
                    executorService.shutdown();
                    con.close();
                    con = null;
                    System.err.println("Database connexion timeout after " + (System.currentTimeMillis() - BEGIN_TIME) + " milliseonds.");
                    connexionStatus = false;
                } else {
                    connexionStatus = true;
                }
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
        }



    }
}
