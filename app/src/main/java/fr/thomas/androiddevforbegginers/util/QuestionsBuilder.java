package fr.thomas.androiddevforbegginers.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import fr.thomas.androiddevforbegginers.control.Controller;
import fr.thomas.androiddevforbegginers.model.Answer;
import fr.thomas.androiddevforbegginers.model.Question;

public class QuestionsBuilder {

	/*
	public static ArrayList<Question> loadQuestions(Controller controller) {
		try {
			DatabaseHelper db = new DatabaseHelper();
			ArrayList<Question> questions = new ArrayList<Question>();

			Statement st = db.getCon().createStatement();
			ResultSet res = st.executeQuery("SELECT idquestion, label FROM Question ;");

			while (res.next()) {
				questions.add(new Question(res.getInt("idquestion"), controller));
			}

			db.close();

			return questions;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Stubdata des questions
	 * 
	 * @param controller contrôleur de jeu
	 * @param filePath       chemin du fichier
	 * @return ArrayList<Question> Questions chargées depuis le fichier
	 * @author Thomas PRADEAU
	 */
	public static ArrayList<Question> readQuestions(Controller controller, String filePath) {
		try {
			ArrayList<Question> questions = new ArrayList<Question>();
			String content = readFromInputStream(filePath);
			String questionsStringArray[] = content.split("\n");

			for (String stringQuestion : questionsStringArray) {
				String elements[] = stringQuestion.split("[|]");

				ArrayList<Answer> answers = new ArrayList<Answer>();

				for (int i = 1; i < elements.length; i++) {
					boolean response = false;

					if (elements[i].charAt(elements[i].length() - 1) == '$') {
						response = true;
						elements[i] = elements[i].substring(0, elements[i].length() - 1);
					}
					answers.add(new Answer(getCharForNumber(i).toLowerCase().charAt(0), elements[i], response));
				}

				Question questionModel = new Question(controller, elements[0], answers);
				questions.add(questionModel);
			}

			return questions;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Lecture depuis fichier
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private static String readFromInputStream(String path) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

	public static String getCharForNumber(int i) {
		return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
	}

}
