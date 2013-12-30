package com.rj.notify;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {
	private String TAG = this.getClass().getSimpleName();
	
	AudioSynthesis audioSynthesis;

	@Override
	public void onCreate() {
		super.onCreate();
		audioSynthesis = new AudioSynthesis();
	}
	
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.i(TAG, "**********  onNotificationPosted");
		Log.i(TAG, "ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText + "t" + sbn.getPackageName());
		audioSynthesis.onNotification("rj", sbn.getPackageName(), sbn.getNotification().tickerText.toString());
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.i(TAG,"********** onNotificationRemoved");
		Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().tickerText +"t" + sbn.getPackageName());
	}

}
