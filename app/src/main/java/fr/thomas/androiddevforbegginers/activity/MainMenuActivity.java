package fr.thomas.androiddevforbegginers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.model.Player;

public class MainMenuActivity extends AppCompatActivity {

    private TextView textView;
    private Player player;

    private TextView playerWelcome;

    private Button playButton;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        extras = getIntent().getExtras();
        player = extras.getParcelable("player.model");

        playerWelcome = findViewById(R.id.labelPlayerWelcome);
        playButton = findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(v -> onPlayButtonClick());

        if(player != null) {
            playerWelcome.setText("Bonjour " + player.getName());
        }
    }

    public void onPlayButtonClick() {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("player.model", player);
        this.startActivity(gameIntent);
    }
}
