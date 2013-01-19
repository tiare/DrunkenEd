package control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.StringTokenizer;

public class Highscores {

	public int[] highscoresEasy, highscoresMedium, highscoresHard;

	public Highscores() {
		try {
			File fileEasy = new File(getFileName(GameSettings.GAME_EASY));
			if (!fileEasy.exists()) {
				saveFile(GameSettings.GAME_EASY, new int[] { 0, 0, 0 });
			}
			loadScoreFromFile(fileEasy, GameSettings.GAME_EASY);

			File fileMedium = new File(getFileName(GameSettings.GAME_MEDIUM));
			if (!fileMedium.exists()) {
				saveFile(GameSettings.GAME_MEDIUM, new int[] { 0, 0, 0 });
			}
			loadScoreFromFile(fileMedium, GameSettings.GAME_MEDIUM);

			File fileHard = new File(getFileName(GameSettings.GAME_HARD));
			if (!fileHard.exists()) {
				saveFile(GameSettings.GAME_HARD, new int[] { 0, 0, 0 });
			}
			loadScoreFromFile(fileHard, GameSettings.GAME_HARD);
		} catch (IOException e) {
			p("Could not write to disk: " + e.toString());
		}
	}

	public int getHighScorePos(final int gameLevel, int score) {
		int[] table = loadTableAccordingToGameLevel(gameLevel);

		for (int i = 0; i < 3; i++) {
			if (table[i] < score) {
				return i + 1;
			}
		}
		return 4;
	}
	


	public ByteBuffer getPictureFromPos(final int gameLevel, int pos) {
		try {
			ByteBuffer bb =ByteBuffer.allocateDirect(60 * 100 * 4);
			bb.rewind();

			File file = new File(getFileName(gameLevel) + pos+".bb");

			BufferedReader bufRdr = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufRdr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, ",");
				while (st.hasMoreTokens()) {
					try {
						bb.put(Byte.parseByte(st.nextToken()));
					} catch (NumberFormatException e) {
						p("malformed file: " + getFileName(gameLevel) + ", exception: " + e.toString());
						bufRdr.close();
						return null;
					}
				}
			}
			bufRdr.close();
			bb.rewind();
			return bb;
			
		} catch(Exception e) {
			return null;
		}
	}

	public void addHighscore(final int gameLevel, int score, ByteBuffer imageByteBuffer) {
		if (imageByteBuffer!=null) {
			imageByteBuffer.rewind();
		}
		
		int[] table = loadTableAccordingToGameLevel(gameLevel);

		for (int i = 0; i < 3; i++) {
			if (table[i] < score) {
				File temp  = new File(getFileName(gameLevel) + (2)+".bb");
				if (temp.exists())  {temp.delete();}
				for (int j = 1; j>=i; j--) {
					temp = new File(getFileName(gameLevel) + (j)+".bb");
				
					if (temp.exists()) {
						temp.renameTo(new File(getFileName(gameLevel) + (j+1)+".bb"));
					} 
				}
				for (int j = i + 1; j < 3; j++) {
					table[j] = table[j - 1];
				}
				table[i] = score;
				saveFile(gameLevel, table);
				if (imageByteBuffer!=null) {
					saveImageFile(gameLevel, i, imageByteBuffer);
				}
				i=3;
			}
		}

	}

	// private helper functions

	private int[] loadTableAccordingToGameLevel(final int gameLevel) {
		switch (gameLevel) {
		case GameSettings.GAME_EASY:
			return highscoresEasy;
		case GameSettings.GAME_MEDIUM:
			return highscoresMedium;
		case GameSettings.GAME_HARD:
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
					p("malformed file: " + getFileName(gameLevel) + ", exception: " + e.toString());
					score[index - 1] = 0;
				}
			}
		}
		bufRdr.close();

		switch (gameLevel) {
		case GameSettings.GAME_EASY:
			highscoresEasy = score;
			break;
		case GameSettings.GAME_MEDIUM:
			highscoresMedium = score;
			break;
		case GameSettings.GAME_HARD:
			highscoresHard = score;
			break;
		default:
			p("ERROR: gameLevel unknown");
			break;
		}

	}

	private String getFileName(final int gameLevel) {
		switch (gameLevel) {
		case GameSettings.GAME_EASY:
			return "scoreEasy.csv";
		case GameSettings.GAME_MEDIUM:
			return "scoreMedium.csv";
		case GameSettings.GAME_HARD:
			return "scoreHard.csv";
		default:
			p("ERROR: unknown gamelevel..");
			return "score" + gameLevel + ".csv";
		}
	}

	private void saveFile(final int gameLevel, int[] scores) {
		try {
			FileWriter fstream = new FileWriter(getFileName(gameLevel));
			BufferedWriter out = new BufferedWriter(fstream);
			StringBuffer toWrite = new StringBuffer();
			for (int i = 0; i < 2; i++) {
				toWrite.append(scores[i] + ",");
			}
			toWrite.append(scores[2]);
			out.write(toWrite.toString());
			out.close();
			fstream.close();
		} catch (IOException e) {
			p("ERROR: Couldn't write to file..");
		}
	}
	
	private void saveImageFile(final int gameLevel, int place, ByteBuffer bb) {
		try {
			bb.rewind();
			FileWriter fstream = new FileWriter(getFileName(gameLevel) + place+".bb");
			BufferedWriter out = new BufferedWriter(fstream);
			StringBuffer toWrite = new StringBuffer();
			for (int i = 0; i < bb.capacity(); i++) {
				toWrite.append(bb.get(i) + ",");
			}
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
