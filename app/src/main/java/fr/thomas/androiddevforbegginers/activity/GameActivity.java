package fr.thomas.androiddevforbegginers.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.model.Game;
import fr.thomas.androiddevforbegginers.model.Player;
import fr.thomas.androiddevforbegginers.model.Question;
import fr.thomas.androiddevforbegginers.util.DatabaseHelper;

public class GameActivity extends AppCompatActivity {

    private ProgressBar questionProgressBar;
    private TextView questionText;
    private Button previousButton;
    private Button nextButton;

    private DatabaseHelper dbhelper;

    private Bundle extras;

    private Player player;
    private Game game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(fr.thomas.androiddevforbegginers.R.layout.activity_game);
        this.questionProgressBar = findViewById(R.id.questionsProgressBar);
        this.questionText = findViewById(R.id.questionText);
        this.previousButton = findViewById(R.id.previousQuestionButton);
        this.nextButton = findViewById(R.id.nextQuestionButton);

        extras = getIntent().getExtras();
        System.out.println("GameActivity create method !");
        this.dbhelper = new DatabaseHelper();



        this.player = extras.getParcelable("player.model");
        player.setDbhelper(dbhelper);

        this.game = new Game(player, dbhelper);


        game.getRandomQuestions();
        game.begin();

        for(Question question : game.getQuestions()) {
            questionText.setText(question.getLabel());
        }


    }



    


    

}
