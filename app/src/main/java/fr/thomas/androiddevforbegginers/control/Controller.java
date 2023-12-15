package fr.thomas.androiddevforbegginers.control;

import java.util.ArrayList;

import fr.thomas.androiddevforbegginers.model.Game;
import fr.thomas.androiddevforbegginers.model.Player;
import fr.thomas.androiddevforbegginers.model.Question;
import fr.thomas.androiddevforbegginers.util.DatabaseHelper;

public class Controller {

    private Player player;
    private Game game;
    private DatabaseHelper dbhelper;
    private ArrayList<Question> questions;

    public Controller() {
        dbhelper = new DatabaseHelper();
        player = new Player("", this);
    }

    public boolean playerAuth(String name, String password) {
        if(player.authenticate(name, password)) {
            this.game = new Game(this, player);
            return true;
        }

        return false;
    }

    public DatabaseHelper getDatabaseHelper() {
        return dbhelper;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public Game getGame() {
        return game;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void startGame() {
        game.getRandomQuestions();
        game.begin();
    }
}
