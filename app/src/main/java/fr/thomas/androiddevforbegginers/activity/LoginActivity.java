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
    private Button loginButton;
    private TextView countViewText;
    private EditText username;
    private EditText password;

    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbhandler = new DatabaseHelper();
        this.player = new Player("", dbhandler);

        loginButton = findViewById(R.id.loginButton);
        username = findViewById(R.id.textBoxUsername);
        password = findViewById(R.id.textBoxPassword);
    }

    public void onLoginButtonClick(View v) {
        if(player.authenticate(username.getText().toString(), password.getText().toString())) {
            Intent gameIntent = new Intent(this, MainMenuActivity.class);
            gameIntent.putExtra("player.model", player);
            this.startActivity(gameIntent);
        }
    }
}