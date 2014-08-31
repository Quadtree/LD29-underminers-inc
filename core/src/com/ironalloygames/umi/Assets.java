package com.ironalloygames.umi;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	AssetManager am;

	NinePatch np;

	HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

	public Assets() {
		am = new AssetManager();

		am.load("pack.atlas", TextureAtlas.class);
		am.finishLoading();

		TextureRegion patches[] = new TextureRegion[9];

		patches[NinePatch.BOTTOM_CENTER] = getSprite("dialog_bottom");
		patches[NinePatch.BOTTOM_LEFT] = getSprite("dialog_ll");
		patches[NinePatch.BOTTOM_RIGHT] = getSprite("dialog_lr");
		patches[NinePatch.MIDDLE_CENTER] = getSprite("dialog_center");
		patches[NinePatch.MIDDLE_LEFT] = getSprite("dialog_left");
		patches[NinePatch.MIDDLE_RIGHT] = getSprite("dialog_right");
		patches[NinePatch.TOP_CENTER] = getSprite("dialog_top");
		patches[NinePatch.TOP_LEFT] = getSprite("dialog_ul");
		patches[NinePatch.TOP_RIGHT] = getSprite("dialog_ur");

		np = new NinePatch(patches);
	}

	public BitmapFont getFont(String name) {
		if (!am.isLoaded(name + ".fnt")) {
			am.load(name + ".fnt", BitmapFont.class);
			am.finishLoading();
		}

		return am.get(name + ".fnt");
	}

	public Music getMusic(String name) {
		if (!am.isLoaded(name + ".ogg")) {
			am.load(name + ".ogg", Music.class);
			am.finishLoading();
		}

		return am.get(name + ".ogg");
	}

	public Sound getSound(String name) {
		if (!am.isLoaded(name + ".ogg")) {
			am.load(name + ".ogg", Sound.class);
			am.finishLoading();
		}

		return am.get(name + ".ogg");
	}

	public Sprite getSprite(String name) {
		if (!sprites.containsKey(name)) {
			sprites.put(name, am.get("pack.atlas", TextureAtlas.class).createSprite(name));
		}

		return sprites.get(name);
	}

	public Texture getTexture(String name) {
		if (!am.isLoaded(name + ".png")) {
			am.load(name + ".png", Texture.class);
			am.finishLoading();
		}

		return am.get(name + ".png");
	}
}
