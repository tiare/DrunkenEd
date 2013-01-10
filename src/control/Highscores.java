package control;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class Highscores {

	public int[] highscoresEasy, highscoresMedium, highscoresHard;
	public static final int GAME_EASY = 0, GAME_MEDIUM = 1, GAME_HARD = 2;

	public Highscores() {
		try {
			File fileEasy = new File(getFileName(GAME_EASY));
			if (!fileEasy.exists()) {
				saveFile(fileEasy, GAME_EASY, new int[] { 0, 0, 0 });
			}
			loadScoreFromFile(fileEasy, GAME_EASY);

			File fileMedium = new File(getFileName(GAME_MEDIUM));
			if (!fileMedium.exists()) {
				saveFile(fileMedium, GAME_MEDIUM, new int[] { 0, 0, 0 });
			}
			loadScoreFromFile(fileMedium, GAME_MEDIUM);

			File fileHard = new File(getFileName(GAME_HARD));
			if (!fileHard.exists()) {
				saveFile(fileHard, GAME_HARD, new int[] { 0, 0, 0 });
			}
			loadScoreFromFile(fileHard, GAME_HARD);
		} catch (IOException e) {
			p("Could not write to disk: " + e.toString());
		}
	}

	public boolean isNewHighScoreAndAdd(final int gameLevel, int score) {
		int[] table = loadTableAccordingToGameLevel(gameLevel);

		for (int i = 0; i < 3; i++) {
			if (table[i] < score) {
				for (int j = i + 1; j < 3; j++) {
					table[j] = table[j - 1];
				}
				table[i] = score;
				saveFile(new File(getFileName(gameLevel)), gameLevel, table);
				return true;
			}
		}
		return false;

	}

	// private helper functions

	private int[] loadTableAccordingToGameLevel(final int gameLevel) {
		switch (gameLevel) {
		case GAME_EASY:
			return highscoresEasy;
		case GAME_MEDIUM:
			return highscoresMedium;
		case GAME_HARD:
			return highscoresHard;
		default:
			p("ERROR: gameLevel unknown");
			return new int[] { 0, 0, 0 };
		}
	}

	private void loadScoreFromFile(File file, final int gameLevel) throws IOException {
		int[] score = new int[] { 0, 0, 0 };

		BufferedReader bufRdr = new BufferedReader(new FileReader(file));
		String line = null;
		int index = 0;
		while ((line = bufRdr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, ",");
			while (st.hasMoreTokens()) {
				try {
				score[index++] = Integer.parseInt(st.nextToken());
				} catch (NumberFormatException e) {
					p("malformed file: "+getFileName(gameLevel)+", exception: "+e.toString());
					score[index-1] = 0;
				}
			}
		}
		bufRdr.close();

		switch (gameLevel) {
		case GAME_EASY:
			highscoresEasy = score;
			break;
		case GAME_MEDIUM:
			highscoresMedium = score;
			break;
		case GAME_HARD:
			highscoresHard = score;
			break;
		default:
			p("ERROR: gameLevel unknown");
			break;
		}

	}

	private String getFileName(final int gameLevel) {
		switch (gameLevel) {
		case GAME_EASY:
			return "scoreEasy.csv";
		case GAME_MEDIUM:
			return "scoreMedium.csv";
		case GAME_HARD:
			return "scoreHard.csv";
		default:
			p("ERROR: unknown gamelevel..");
			return "score" + gameLevel + ".csv";
		}
	}

	private void saveFile(File file, final int gameLevel, int[] scores) {
		try {
			FileWriter fstream = new FileWriter(getFileName(gameLevel));
			BufferedWriter out = new BufferedWriter(fstream);
			StringBuffer toWrite = new StringBuffer();
			for (int i = 0; i < 2 ; i++) {
				toWrite.append(scores[i]+",");
			}
			toWrite.append(scores[2]);
			out.write(toWrite.toString());
			out.close();
			fstream.close();
		} catch (IOException e) {
			p("ERROR: Couldn't write to file..");
		}
	}

	private static void p(String p) {
		System.out.println(p);
	}
}
