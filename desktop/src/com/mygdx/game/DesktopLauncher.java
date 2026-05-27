package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.bevrfarlbt.NoExit.MyGdxGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("DrunkenOrbit");

		// Задаем размер окна в соответствии с разрешением, прописанным в MyGdxGame
		config.setWindowedMode(MyGdxGame.SCR_WIDTH, MyGdxGame.SCR_HEIGHT);

		// Разрешаем пользователю изменять размер окна (по желанию)
		config.setResizable(true);

		new Lwjgl3Application(new MyGdxGame(), config);
	}
}