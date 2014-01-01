package com.rj.notify;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();
    }
    
    private void setupButtons() {
        ((Button)findViewById(R.id.security_settings)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSecuritySettings();
			}
		});
        ((Button)findViewById(R.id.notification_settings)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSoundSettings();
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_settings:
			break;
		case R.id.test_notification:
			createNotification();
			break;
    	}
    	return super.onMenuItemSelected(featureId, item);
    }
    
    private void createNotification() {
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
        ncomp.setContentTitle("My Notification");
        ncomp.setContentText("Notification Listener Service Example");
        ncomp.setTicker("This is a test notification.");
        ncomp.setSmallIcon(R.drawable.ic_launcher);
        ncomp.setDefaults(Notification.DEFAULT_ALL);
        ncomp.setAutoCancel(true);
        nManager.notify((int)System.currentTimeMillis(),ncomp.build());
    }
    
    private void gotoSecuritySettings() {
    	Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
    	startActivity(intent);
    }
    
    private void gotoSoundSettings() {
//    	startActivityForResult(new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS, 0));
    	Intent intent=new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS);
    	startActivity(intent);
    }
    
}
