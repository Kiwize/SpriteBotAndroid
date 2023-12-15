package fr.thomas.androiddevforbegginers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.control.Controller;
import fr.thomas.androiddevforbegginers.model.Player;

public class MainMenuActivity extends AppCompatActivity {

    private TextView textView;

    private Player player;

    private TextView playerWelcome;

    private Button playButton;

    private Controller controller;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        extras = getIntent().getExtras();

        playerWelcome = findViewById(R.id.labelPlayerWelcome);
        controller = extras.getParcelable("controller");


        if (extras != null) {
            playerWelcome.setText("Bonjour " + controller.getPlayer().getName());
        }

        playButton = findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(v -> onPlayButtonClick());
    }

    public void onPlayButtonClick() {

        controller.startGame();

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("player.name", extras.getString("player.name"));
        gameIntent.putExtra("player.id",extras.getString("player.id"));
        gameIntent.putExtra("controller", controller);
        this.startActivity(gameIntent);


    }
}
