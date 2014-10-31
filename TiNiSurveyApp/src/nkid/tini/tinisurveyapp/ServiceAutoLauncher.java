package nkid.tini.tinisurveyapp;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ServiceAutoLauncher extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MainActivity main = new MainActivity();
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			main.setMailAlarm();
			Toast.makeText(context, "Alarm set!", Toast.LENGTH_SHORT).show();
		}
	}

}
