package fr.thomas.androiddevforbegginers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.thomas.androiddevforbegginers.DBHandler;
import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.model.Player;
import fr.thomas.androiddevforbegginers.util.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private DatabaseHelper dbhandler;

    //Database connexion components
    private Button dbConRetry;
    private TextView dbConErrMsg;

    private Button loginButton;
    private TextView countViewText;
    private EditText username;
    private EditText password;

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.dbConErrMsg = findViewById(R.id.dbConnErrorText);
        this.dbConRetry = findViewById(R.id.dbConRetryButton);

        dbhandler = new DatabaseHelper(this);

        this.player = new Player("", dbhandler);

        loginButton = findViewById(R.id.loginButton);

        if(!dbhandler.getConnexionStatus()) {
            loginButton.setEnabled(false);
            dbConRetry.setOnClickListener(v -> {
                dbhandler.connect();
                updateDatabaseComponentsStatus();
            });
        } else {
            dbConRetry.setVisibility(View.INVISIBLE);
            dbConErrMsg.setVisibility(View.INVISIBLE);
        }

        username = findViewById(R.id.textBoxUsername);
        password = findViewById(R.id.textBoxPassword);
    }

    public void updateDatabaseComponentsStatus() {
        if(!dbhandler.getConnexionStatus()) {
            loginButton.setEnabled(false);
            dbConRetry.setOnClickListener(v -> {
                dbhandler.connect();
            });
        } else {
            dbConRetry.setVisibility(View.INVISIBLE);
            dbConErrMsg.setVisibility(View.INVISIBLE);
            loginButton.setEnabled(true);
        }
    }

    public void onLoginButtonClick(View v) {
        if(player.authenticate(username.getText().toString(), password.getText().toString())) {
            Intent gameIntent = new Intent(this, MainMenuActivity.class);
            gameIntent.putExtra("player.model", player);
            this.startActivity(gameIntent);
        }
    }
}