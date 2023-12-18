package fr.thomas.androiddevforbegginers.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.model.Answer;
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

    private HashMap<Question, Answer> gameHistory;
    private ArrayList<Question> gameQuestions;

    private int qpointer;

    private Question loadedQuestion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(fr.thomas.androiddevforbegginers.R.layout.activity_game);
        this.questionProgressBar = findViewById(R.id.questionsProgressBar);
        this.questionText = findViewById(R.id.questionText);
        this.previousButton = findViewById(R.id.previousQuestionButton);
        this.nextButton = findViewById(R.id.nextQuestionButton);

        extras = getIntent().getExtras();
        this.dbhelper = new DatabaseHelper();

        gameHistory = new HashMap<>();
        gameQuestions = new ArrayList<>();

        this.player = extras.getParcelable("player.model");
        player.setDbhelper(dbhelper);

        this.game = new Game(player, dbhelper);

        game.getRandomQuestions();
        game.begin();

        loadUIQuestions(game.getQuestions());
        previousButton.setOnClickListener(v -> onPreviousButtonClick());
        nextButton.setOnClickListener(v -> onNextButtonClick());

    }

    public void onPreviousButtonClick() {
        if (qpointer > 0) {
            nextButton.setEnabled(true);
            qpointer--;
            loadQuestion(gameQuestions.get(qpointer));
            questionProgressBar.setProgress(qpointer + 1, true);
        } else {
            previousButton.setActivated(false);
        }
    }

    public void onNextButtonClick() {
        if(qpointer < gameQuestions.size() - 1) {
            previousButton.setActivated(true);
            qpointer++;
            loadQuestion(gameQuestions.get(qpointer));
            questionProgressBar.setProgress(qpointer + 1, true);
        } else {
            nextButton.setActivated(false);
        }
    }

    public void loadUIQuestions(ArrayList<Question> questions) {
        this.gameQuestions = questions;
        this.qpointer = 0;
        this.questionProgressBar.setMax(gameQuestions.size());
        this.questionProgressBar.setProgress(1, true);

        for(Question question : questions) {
            this.gameHistory.put(question, null);

            int i = 0;
            questionText.setText(question.getLabel());

            for(Answer answer : question.getAnswers()) {

            }


        }

        loadQuestion(questions.get(qpointer));
    }

    public void loadQuestion(Question question) {

        //Affichage des réponses en liste déroulante

        questionText.setText(question.getLabel());
    }

    public boolean areAllQuestionsAnswered() {
        for (Answer a : gameHistory.values()) {
            if (a == null)
                return false;
        }

        return true;
    }

    public void resetPointer() {
        qpointer = 0;
    }
}
