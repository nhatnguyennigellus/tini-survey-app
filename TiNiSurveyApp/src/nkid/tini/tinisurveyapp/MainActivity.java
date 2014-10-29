package nkid.tini.tinisurveyapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;

import nkid.tini.data.DBAdapter;
import nkid.tini.data.Poll;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {

	ImageButton imgbLike, imgbDontCare, imgbDontLike;
	TextView tvLike, tvDontCare, tvDontLike, tvHeader;

	static DBAdapter mDB;
	private Mail mail;
	private Boolean canClick = true;
	
	private PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mDB = new DBAdapter(this);
		mDB.open();

		imgbLike = (ImageButton) this.findViewById(R.id.imgbLike);
		imgbDontCare = (ImageButton) this.findViewById(R.id.imgbDontCare);
		imgbDontLike = (ImageButton) this.findViewById(R.id.imgbDontLike);

		tvLike = (TextView) this.findViewById(R.id.tvLikeDaily);
		tvDontCare = (TextView) this.findViewById(R.id.tvDontCareDaily);
		tvDontLike = (TextView) this.findViewById(R.id.tvDontLikeDaily);
		tvHeader = (TextView) this.findViewById(R.id.tvHeader);
		DateFormat df = new DateFormat();
		String header = df.format("dd-MM-yyyy", new Date()).toString();
		tvHeader.setText(header);
		showDailyPoll();/*
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		Intent intent = new Intent(this, SendEmailService.class);
		intent.putExtra("FromEmail", pref.getString("FromEmail", "nkidsurveyapp@gmail.com"));
		intent.putExtra("FromPass", "nkidsurveyreport");
		intent.putExtra("ToList", pref.getString("ToList", 
				"khoa.do@nkidcorp.com,huy.mai@tiniplanet.com,nhat.nguyen@tiniplanet.com"));
		startService(intent);
		*/setMailAlarm();
		new Intent(this, AlarmReceiver.class);
		
		imgbLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (canClick) {
					imgbLike.setImageResource(R.drawable.like2);
					addToDB(1);
					thanksNoti();
					showDailyPoll();
					waiting(imgbLike, R.drawable.like1);
				}
			}
		});
		

		imgbDontCare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (canClick) {
					imgbDontCare.setImageResource(R.drawable.dontcare2);
					addToDB(0);
					thanksNoti();
					showDailyPoll();
					waiting(imgbDontCare, R.drawable.dontcare1);
				}
			}
		});

		imgbDontLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (canClick) {
					imgbDontLike.setImageResource(R.drawable.dontlike2);
					addToDB(-1);
					thanksNoti();
					showDailyPoll();
					waiting(imgbDontLike, R.drawable.dontlike1);
				}
			}
		});


		
	}

	private void setMailAlarm() {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		String fromEmail = pref.getString("FromEmail", "nkidsurveyapp@gmail.com");
		String fromPass = pref.getString("FromPass", "nkidsurveyreport");
		String toList = pref.getString("ToList", 
				"khoa.do@nkidcorp.com,huy.mai@tiniplanet.com,nhat.nguyen@tiniplanet.com");
		AlarmManager manager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
		
		
		// Set daily alarm
		Intent intentDaily = new Intent(this, AlarmReceiver.class);
		intentDaily.putExtra("FromEmail", fromEmail);
		intentDaily.putExtra("FromPass", fromPass);
		intentDaily.putExtra("ToList", toList);
		intentDaily.putExtra("MailType", "Daily");
		pendingIntent = 
				PendingIntent.getBroadcast(this, 1, intentDaily, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar cal = Calendar.getInstance();
		DateFormat df = new DateFormat();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntent);
		
		//Set monthly alarm
		Intent intentMonthly = new Intent(this, AlarmReceiver.class);
		intentMonthly.putExtra("FromEmail", fromEmail);
		intentMonthly.putExtra("FromPass", fromPass);
		intentMonthly.putExtra("ToList", toList);
		intentMonthly.putExtra("MailType", "Monthly");
		pendingIntent = 
				PendingIntent.getBroadcast(this, 2, intentMonthly, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar calMonth = Calendar.getInstance();
		calMonth.setTimeInMillis(System.currentTimeMillis());
		calMonth.add(Calendar.MONTH, 1);
		calMonth.set(Calendar.DATE, 1);
		calMonth.set(Calendar.HOUR_OF_DAY, 0);
		calMonth.set(Calendar.MINUTE, 0);
		manager.set(AlarmManager.RTC_WAKEUP, calMonth.getTimeInMillis(), pendingIntent);
		
		//Set weekly alarm
		Intent intentWeekly = new Intent(this, AlarmReceiver.class);
		intentWeekly.putExtra("FromEmail", fromEmail);
		intentWeekly.putExtra("FromPass", fromPass);
		intentWeekly.putExtra("ToList", toList);
		intentWeekly.putExtra("MailType", "Weekly");
		pendingIntent = 
				PendingIntent.getBroadcast(this, 3, intentWeekly, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar calWeek = Calendar.getInstance();
		calWeek.setTimeInMillis(System.currentTimeMillis());
		calWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calWeek.add(Calendar.WEEK_OF_MONTH, 1);
		calWeek.set(Calendar.HOUR_OF_DAY, 0);
		calWeek.set(Calendar.MINUTE, 0);
		manager.set(AlarmManager.RTC_WAKEUP, calWeek.getTimeInMillis(), pendingIntent);
	}

	private void waiting(final ImageButton button, final int resId) {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		canClick = false;
		int waitingTime = pref.getInt("WaitingTime", 2) * 1000;
		new CountDownTimer(waitingTime, 1000) {
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				canClick = true;
				button.setImageResource(resId);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
			}

		}.start();
	}

	private void showDailyPoll() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String curDate = sdf.format(date);

		int LikeNo = mDB.getTodayResult(curDate, 1);
		int DontLikeNo = mDB.getTodayResult(curDate, -1);
		int DontCareNo = mDB.getTodayResult(curDate, 0);
		tvLike.setText(LikeNo + "");
		tvDontLike.setText(DontLikeNo + "");
		tvDontCare.setText(DontCareNo + "");
	}

	@SuppressLint("SimpleDateFormat")
	public void addToDB(int vote) {
		String name = "Parafait_Touch_Readers";
		Poll poll = new Poll(name, "", vote, "");
		mDB.addPoll(poll);
	}

	void thanksNoti() {
		String mes = "Cám ơn bạn đã bình chọn!";
		Toast.makeText(this, mes, 2).show();
	}

	String body = "";

	protected void sendEmail(String title) throws MessagingException {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		mail = new Mail(pref.getString("FromEmail", "nkidsurveyapp@gmail.com"), pref.getString(
				"FromPass", "nkidsurveyreport"));
		String[] destAddr = pref.getString("ToList", "huy.mai@tiniplanet.com").split(",");
		String[] ccAddr = {"thanhhuy89vn@gmail.com"};
		mail.setTo(destAddr);
		mail.setCC(ccAddr);
		mail.setFrom("nkidsurveyapp@gmail.com");
		mail.setSubject("[Survey App] - " + title + " Report");
		body = new String();

		body += "Vote for : Parafait Touch Readers";
		if (title.equals("Daily")) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String curDate = sdf.format(date);

			int LikeNo = mDB.getTodayResult(curDate, 1);
			int DontLikeNo = mDB.getTodayResult(curDate, -1);
			int DontCareNo = mDB.getTodayResult(curDate, 0);

			body += "\n\t- Like : " + LikeNo;
			body += "\n\t- Don't Like : " + DontLikeNo;
			body += "\n\t- Don't Care : " + DontCareNo;
		}
		mail.setBody(body);
		new AsyncTask<Void, Void, Boolean>() {
			Exception error;
			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					mail.send();
					return true;
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					error = e;
					return false;
				}
			}
			
			@Override
		    protected void onPostExecute(Boolean result) {
		        if (result) {
		        	errNoti("Gửi email thành công");
		         } else {
		            if (error != null)
		                errNoti("Gửi email thất bại. Nguyên nhân: " + error.toString());
		            else 
		            	errNoti("Gửi email thất bại. Khong rõ nguyên nhân: ");
		        }
		    }

		}.execute();
		if (title.equals("Previous Day")) {
			title = "Daily";
		}
	}
	
	void notiSentMail(String title) {
		Toast.makeText(this, title + " mail sent!", Toast.LENGTH_SHORT).show();
	}

	void errNoti(String mes) {
		Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mDB.close();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		final Dialog dlgPIN = new Dialog(this);
		final SharedPreferences pref = getPreferences(MODE_PRIVATE);
		dlgPIN.setContentView(R.layout.pin_input_dialog);
		dlgPIN.setTitle("Nhập PIN");
		final EditText txtPIN = (EditText) dlgPIN
				.findViewById(R.id.txtPIN);
		Button btnOK = (Button) dlgPIN.findViewById(R.id.btnConfigOK);
		Button btnCancel = (Button) dlgPIN
				.findViewById(R.id.btnConfigCancel);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dlgPIN.cancel();
			}
		});

		
		if (id == R.id.miConfig) {
			
			btnOK.setOnClickListener(new View.OnClickListener() {

				@SuppressLint("CommitPrefEdits")
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (txtPIN.getText().toString().equals("09092014")) {
						dlgPIN.cancel();
						goConfig();
						
					} else {
						errNoti("Mã PIN chưa đúng! Vui lòng nhập lại");

					}
				}

				
			});
			
		} else if (id == R.id.miSendTest) {
			btnOK.setOnClickListener(new View.OnClickListener() {

				@SuppressLint("CommitPrefEdits")
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (txtPIN.getText().toString().equals("09092014")) {
						dlgPIN.cancel();
						try {
							sendEmail("Daily");
						} catch (MessagingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else {
						errNoti("Mã PIN chưa đúng! Vui lòng nhập lại");

					}
				}

				
			});
		}
		
		dlgPIN.show();
		return super.onOptionsItemSelected(item);
	}
	
	private void goConfig() {
		Intent intent = new Intent(this, ConfigActivity.class);
		startActivityForResult(intent, 1);
	}
	
	@SuppressLint("ShowToast")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1) {
			SharedPreferences prefSet = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences prefMain = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = prefMain.edit();
			
			editor.putString("FromEmail", prefSet.getString("FromEmail", ""));
			editor.putString("FromPass", prefSet.getString("FromPass", ""));
			editor.putString("ToList", prefSet.getString("ToList", ""));
			editor.putInt("WaitingTime", Integer.parseInt(prefSet.getString("WaitingTime", "2")));
			
			editor.commit();
			Toast.makeText(this, "Cấu hình thành công!", Toast.LENGTH_SHORT).show();
		}
		
		
	}
}
