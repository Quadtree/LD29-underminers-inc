package com.ironalloygames.umi;

public class GA {

	static boolean gaEnabled = false;

	private static native void doTrackEvent(String action, String label, double value) /*-{
		$wnd.ga('send', 'event', 'Underminers Inc', action, label, value);
	}-*/;

	public static void enable() {
		gaEnabled = true;
	}

	public static void trackEvent(String action, String label, double value) {
		if (gaEnabled)
			doTrackEvent(action, label, value);
	}
}
