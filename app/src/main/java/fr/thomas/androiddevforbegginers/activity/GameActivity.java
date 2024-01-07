package fr.thomas.androiddevforbegginers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.model.Answer;
import fr.thomas.androiddevforbegginers.model.Game;
import fr.thomas.androiddevforbegginers.model.Player;
import fr.thomas.androiddevforbegginers.model.Question;
import fr.thomas.androiddevforbegginers.util.ConfigReader;
import fr.thomas.androiddevforbegginers.util.DatabaseHelper;

public class GameActivity extends AppCompatActivity {

    private ProgressBar questionProgressBar;
    private TextView questionText;
    private Button previousButton;
    private Button nextButton;

    private RadioButton firstAnswer;
    private RadioButton secondAnswer;
    private RadioButton thirdAnswer;

    private RadioGroup answerRadioGroup;

    private Player player;
    private Game game;

    private HashMap<Question, Answer> gameHistory;
    private ArrayList<Question> gameQuestions;

    private HashMap<String, Answer> answerToLabelMap;

    private int qpointer;
    private boolean isGameStarted;

    private Question loadedQuestion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(fr.thomas.androiddevforbegginers.R.layout.activity_game);
        this.questionProgressBar = findViewById(R.id.questionsProgressBar);
        this.questionText = findViewById(R.id.questionText);
        this.previousButton = findViewById(R.id.previousQuestionButton);
        this.nextButton = findViewById(R.id.nextQuestionButton);

        this.firstAnswer = findViewById(R.id.firstAnswerRadio);
        this.secondAnswer = findViewById(R.id.secondAnswerRadio);
        this.thirdAnswer = findViewById(R.id.thirdAnswerRadio);

        this.answerRadioGroup = findViewById(R.id.answerRadioGroup);

        Bundle extras = getIntent().getExtras();

        ConfigReader configReader = new ConfigReader(this);
        DatabaseHelper dbhelper = new DatabaseHelper(this, configReader.getProperties());

        gameHistory = new HashMap<>();
        gameQuestions = new ArrayList<>();
        answerToLabelMap = new HashMap<>();
        isGameStarted = false;

        assert extras != null;
        this.player = extras.getParcelable("player.model");
        assert player != null;
        player.setDbhelper(dbhelper);

        this.game = new Game(player, dbhelper);

        game.getRandomQuestions();
        game.begin();

        loadUIQuestions(game.getQuestions());
        previousButton.setOnClickListener(v -> onPreviousButtonClick());
        nextButton.setOnClickListener(v -> onNextButtonClick());

        firstAnswer.setOnClickListener(v -> onAnswerButtonClick(firstAnswer.getText().toString()));
        secondAnswer.setOnClickListener(v -> onAnswerButtonClick(secondAnswer.getText().toString()));
        thirdAnswer.setOnClickListener(v -> onAnswerButtonClick(thirdAnswer.getText().toString()));

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

        nextButton.setText("Suivant");
        nextButton.setOnClickListener(v -> onNextButtonClick());
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

        if(qpointer == gameQuestions.size() - 1) {
            nextButton.setText("Valider");
            nextButton.setOnClickListener(v -> finishGame(gameHistory));
            nextButton.setEnabled(areAllQuestionsAnswered());
        } else {
            nextButton.setText("Suivant");
            nextButton.setOnClickListener(v -> onNextButtonClick());
        }

    }

    public void onAnswerButtonClick(String answerLabel) {
        gameHistory.put(loadedQuestion, answerToLabelMap.get(answerLabel));

        if(qpointer == gameQuestions.size() - 1) {
            nextButton.setEnabled(areAllQuestionsAnswered());
        }
    }

    public void finishGame(HashMap<Question, Answer> gameHistory) {
        //TODO Show recap view
        isGameStarted = false;
        int gameScoreBuffer = 0;

        for (Map.Entry<Question, Answer> gameEntry : gameHistory.entrySet()) {
            if (gameEntry.getValue().isCorrect()) {
                gameScoreBuffer += gameEntry.getKey().getDifficulty() * 100;
            }
        }

        game.setScore(gameScoreBuffer);
        game.insert();

        //Show updated player information's on main menu.
        //Return to main menu activity for now.
        Intent gameIntent = new Intent(this, GameRecapActivity.class);
        gameIntent.putExtra("player.model", player);
        gameIntent.putExtra("game.history", gameHistory);
        this.startActivity(gameIntent);
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
                answerToLabelMap.put(answer.getLabel(), answer);
            }

        }

        loadedQuestion = questions.get(0);

        firstAnswer.setText(loadedQuestion.getAnswers().get(0).getLabel());
        secondAnswer.setText(loadedQuestion.getAnswers().get(1).getLabel());
        thirdAnswer.setText(loadedQuestion.getAnswers().get(2).getLabel());

        loadQuestion(questions.get(qpointer));
    }

    public void loadQuestion(Question question) {

        //Affichage des réponses en liste déroulante

        answerRadioGroup.clearCheck();

        firstAnswer.setText(question.getAnswers().get(0).getLabel());
        secondAnswer.setText(question.getAnswers().get(1).getLabel());
        thirdAnswer.setText(question.getAnswers().get(2).getLabel());

        questionText.setText(question.getLabel());

        loadedQuestion = question;

        try {
            if (gameHistory.get(question) != null) {
                for (int i = 0; i < answerRadioGroup.getChildCount(); i++) {
                    if (((RadioButton) answerRadioGroup.getChildAt(i)).getText().equals(gameHistory.get(question).getLabel())) {
                        ((RadioButton) answerRadioGroup.getChildAt(i)).setChecked(true);
                    }
                }
            }
        } catch (NullPointerException ex) {
            System.err.println("An error as occurred while trying to load answers.");
            ex.printStackTrace();
        }

        System.out.println(gameHistory.get(loadedQuestion));
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
