package com.rj.notify;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {
	private static final String TAG = NotificationListener.class.getSimpleName();
	
	private static final String DEFAULT_NOTIFICATION = "content://settings/system/notification_sound";
	
	AudioSynthesis audioSynthesis;

	@Override
	public void onCreate() {
		super.onCreate();
		audioSynthesis = new AudioSynthesis();
	}
	
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.i(TAG, "**********  onNotificationPosted");
		Notification notification = sbn.getNotification();
		Log.i(TAG, String.format("ID : %s, text: %s, package: %s, sound: %s, default: %s",
				sbn.getId(),
				notification.tickerText,
				sbn.getPackageName(),
				notification.sound,
				""+notification.defaults));
		try {
			if (notificationPlaysDefaultSound(notification)) {
				audioSynthesis.onNotification("rj", sbn.getPackageName(), notification.tickerText.toString());
			}
		} catch (Exception e) {
			// We don't want to crash this listener.
			e.printStackTrace();
		}
	}
	
	private boolean notificationPlaysDefaultSound(Notification notification) {
		return (notification.sound != null 
				&& DEFAULT_NOTIFICATION.equals(notification.sound.toString())
				|| (notification.sound == null 
				    && (notification.defaults & Notification.DEFAULT_ALL) != 0));
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.i(TAG,"********** onNotificationRemoved");
		Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText +"t" + sbn.getPackageName());
	}

}
