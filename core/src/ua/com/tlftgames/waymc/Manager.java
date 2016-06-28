package ua.com.tlftgames.waymc;

import com.badlogic.gdx.assets.AssetManager;

public class Manager extends AssetManager {
	private static Manager instance;

	public static Manager getInstance() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	@Override
	public void dispose() {
		super.dispose();
		instance = null;
	}
}
