package fr.thomas.androiddevforbegginers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.thomas.androiddevforbegginers.DBHandler;
import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.control.Controller;

public class LoginActivity extends AppCompatActivity {

    private Controller controller;
    private DBHandler dbhandler;
    private Button loginButton;
    private TextView countViewText;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.controller = new Controller();

        loginButton = findViewById(R.id.loginButton);
        dbhandler = new DBHandler(this);

        username = findViewById(R.id.textBoxUsername);
        password = findViewById(R.id.textBoxPassword);
    }

    public void onLoginButtonClick(View v) {
        if(controller.playerAuth(username.getText().toString(), password.getText().toString())) {
            Intent gameIntent = new Intent(this, MainMenuActivity.class);
            gameIntent.putExtra("player.name", controller.getPlayer().getName());
            gameIntent.putExtra("player.id", controller.getPlayer().getID());
            gameIntent.putExtra("controller", controller);

            this.startActivity(gameIntent);
        }
    }
}