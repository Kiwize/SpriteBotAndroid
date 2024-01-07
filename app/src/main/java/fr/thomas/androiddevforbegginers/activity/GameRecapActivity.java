package fr.thomas.androiddevforbegginers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.model.Answer;
import fr.thomas.androiddevforbegginers.model.Player;
import fr.thomas.androiddevforbegginers.model.Question;

public class GameRecapActivity extends AppCompatActivity {

    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_recap);

        TextView gameRecapText = findViewById(R.id.textGameRecap);
        TableLayout tableLayout = findViewById(R.id.answerTable);
        Button continueButton = findViewById(R.id.buttonContinue);

        extras = getIntent().getExtras();
        assert extras != null;
        HashMap<Question, Answer> gameHistory = (HashMap<Question, Answer>) extras.get("game.history");

        assert gameHistory != null;
        for(Map.Entry<Question, Answer> entry : gameHistory.entrySet()) {
            TableRow row = new TableRow(this);

            TextView questionLabel = new TextView(this);
            questionLabel.setText(entry.getKey().getLabel());
            questionLabel.setWidth(150);
            questionLabel.setHorizontallyScrolling(false);

            TextView answerLabel = new TextView(this);
            answerLabel.setText(String.valueOf(entry.getValue().isCorrect()));

            row.addView(questionLabel);
            row.addView(answerLabel);

            tableLayout.addView(row);
        }

        continueButton.setOnClickListener(v -> onButtonContinueClick());
    }

    private void onButtonContinueClick() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("player.model", (Player) extras.get("player.model"));
        this.startActivity(intent);
    }

}
