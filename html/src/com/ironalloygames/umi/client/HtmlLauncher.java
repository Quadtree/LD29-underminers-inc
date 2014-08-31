package com.ironalloygames.umi.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.ironalloygames.umi.UMI;

public class HtmlLauncher extends GwtApplication {

	@Override
	public ApplicationListener getApplicationListener() {
		return new UMI();
	}

	@Override
	public GwtApplicationConfiguration getConfig() {
		return new GwtApplicationConfiguration(1024, 768);
	}
}