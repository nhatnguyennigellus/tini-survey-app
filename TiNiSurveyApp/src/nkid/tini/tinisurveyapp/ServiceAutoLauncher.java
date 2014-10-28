package nkid.tini.tinisurveyapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ServiceAutoLauncher extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Intent serviceIntent = new Intent(context, AlarmReceiver.class);
			PendingIntent pendingIntent = 
					PendingIntent.getBroadcast(context, 0, serviceIntent, 0);
			AlarmManager manager = 
					(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			int interval = 8000;
			manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
					interval, pendingIntent);
			Toast.makeText(context, "Service starts", Toast.LENGTH_SHORT).show();
		}
	}

}
