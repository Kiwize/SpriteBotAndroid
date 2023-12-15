package fr.thomas.androiddevforbegginers.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import fr.thomas.androiddevforbegginers.R;
import fr.thomas.androiddevforbegginers.control.Controller;
import fr.thomas.androiddevforbegginers.model.Player;
import fr.thomas.androiddevforbegginers.model.Question;

public class GameActivity extends AppCompatActivity {

    private Controller controller;
    private ProgressBar questionProgressBar;
    private TextView questionText;
    private Button previousButton;
    private Button nextButton;

    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(fr.thomas.androiddevforbegginers.R.layout.activity_game);

        extras = getIntent().getExtras();

        this.controller = extras.getParcelable("controller");

        for(Question question : controller.getQuestions()) {
            questionText.setText(question.getLabel());
        }

        this.questionProgressBar = findViewById(R.id.questionsProgressBar);
        this.questionText = findViewById(R.id.questionText);
        this.previousButton = findViewById(R.id.previousQuestionButton);
        this.nextButton = findViewById(R.id.nextQuestionButton);


    }



    


    

}
