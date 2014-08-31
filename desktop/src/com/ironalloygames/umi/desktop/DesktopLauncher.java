package com.ironalloygames.umi.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ironalloygames.umi.UMI;

public class DesktopLauncher {
	public static void main(String[] arg) {
		// TexturePacker.process("../../raw_assets", "../android/assets",
		// "pack");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new UMI(), config);
	}
}
