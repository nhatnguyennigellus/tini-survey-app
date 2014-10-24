package nkid.tini.tinisurveyapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;

import nkid.tini.data.DBAdapter;
import nkid.tini.data.Poll;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
	TextView tvLike, tvDontCare, tvDontLike;
	
	static DBAdapter mDB;
	private Mail mail;

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
		
		showDailyPoll();
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		imgbLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToDB(1);
				thanksNoti();
				showDailyPoll();
			}
		});

		imgbDontCare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToDB(0);
				thanksNoti();
				showDailyPoll();
			}
		});

		imgbDontLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToDB(-1);
				thanksNoti();
				showDailyPoll();
			}
		});

		CountDownTimer dailyTimer = new CountDownTimer(60000, 60000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				try {
					Calendar cal = Calendar.getInstance();
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int minute = cal.get(Calendar.MINUTE);

					if (hour == 0 && minute == 0)
						sendEmail("Previous Day");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
				this.start();
			}
		}.start();

		CountDownTimer weeklyTimer = new CountDownTimer(60000, 60000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				try {
					Calendar cal = Calendar.getInstance();
					int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int minute = cal.get(Calendar.MINUTE);
					if (dayOfWeek == Calendar.MONDAY && hour == 0 && minute == 0)
						sendEmail("Weekly");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFinish() {

				this.start();
			}
		}.start();

		CountDownTimer monthlyTimer = new CountDownTimer(60000, 60000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				try {
					Calendar cal = Calendar.getInstance();
					int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int minute = cal.get(Calendar.MINUTE);
					if (dayOfMonth == 1 && hour == 0 && minute == 0)
						sendEmail("Monthly");
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFinish() {

				this.start();
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
		Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
	}

	String body = "";

	protected void sendEmail(String title) throws MessagingException {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		mail = new Mail(pref.getString("FromEmail", ""), pref.getString("FromPass", ""));
		String[] destAddr = pref.getString("ToList", "").split(",");//{ "khoa.do@nkidcorp.com" };
		//String[] destAddr = { "nhat.nguyen@tiniplanet.com" };
		String[] ccAddr = { "huy.mai@tiniplanet.com" };
		mail.setTo(destAddr);
		mail.setCC(ccAddr);
		
		mail.setFrom("nkidsurveyapp@gmail.com");
		mail.setSubject("[Survey App] - " + title + " Report");
		body = new String();

		body += "Vote for : Parafait Touch Readers";
		if (title.equals("Previous Day")) {
			mail.setSubject("[Survey App] - Daily Report");
			Date date = new Date();
			Date yesterday = new Date(date.getTime() - (1000 * 60 * 60 * 24));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String curDate = sdf.format(yesterday);

			int LikeNo = mDB.getTodayResult(curDate, 1);
			int DontLikeNo = mDB.getTodayResult(curDate, -1);
			int DontCareNo = mDB.getTodayResult(curDate, 0);

			
			body += "\n\t- Like : " + LikeNo;
			body += "\n\t- Don't Like : " + DontLikeNo;
			body += "\n\t- Don't Care : " + DontCareNo;
		} else if (title.equals("Daily")) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String curDate = sdf.format(date);

			int LikeNo = mDB.getTodayResult(curDate, 1);
			int DontLikeNo = mDB.getTodayResult(curDate, -1);
			int DontCareNo = mDB.getTodayResult(curDate, 0);

			
			body += "\n\t- Like : " + LikeNo;
			body += "\n\t- Don't Like : " + DontLikeNo;
			body += "\n\t- Don't Care : " + DontCareNo;
		} else if (title.equals("Monthly")) {
			Calendar cal = Calendar.getInstance();
			int prevMonth = cal.get(Calendar.MONTH); 
			int year = cal.get(Calendar.YEAR);
			if(prevMonth == 0) 
			{
				prevMonth = 12;
				year--;
			}
			
			int MonthLikeNo = mDB.getResultByMonth(prevMonth, year, 1);
			int MonthDontLikeNo = mDB.getResultByMonth(prevMonth, year, -1);
			int MonthDontCareNo = mDB.getResultByMonth(prevMonth, year, 0);

			body += "\n\t- Like : " + MonthLikeNo;
			body += "\n\t- Don't Like : " + MonthDontLikeNo;
			body += "\n\t- Don't Care : " + MonthDontCareNo;
		} else if (title.equals("Weekly")) {
			int WeekLikeNo = mDB.getResultByWeek(1);
			int WeekDontLikeNo = mDB.getResultByWeek(-1);
			int WeekDontCareNo = mDB.getResultByWeek(0);
			
			body += "\n\t- Like : " + WeekLikeNo;
			body += "\n\t- Don't Like : " + WeekDontLikeNo;
			body += "\n\t- Don't Care : " + WeekDontCareNo;
		}

		mail.setBody(body);
		new AsyncTask<Void, Void, Void>() {

			@Override
			public Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					mail.send();

				} catch (MessagingException e) {
					// TODO Auto-generated catch block

				}
				return null;
			}

		}.execute();
		if (title.equals("Previous Day")) {
			title = "Daily";
		}
		notiSentMail(title);

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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		final Dialog dlgConfig = new Dialog(this);
		final SharedPreferences pref = getPreferences(MODE_PRIVATE);
		if (id == R.id.miConfig) {
			dlgConfig.setContentView(R.layout.config_dialog);
			dlgConfig.setTitle("Cấu hình");
			final EditText txtEmail = (EditText)dlgConfig.findViewById(R.id.txtFromEmail);
			final EditText txtPassword = (EditText)dlgConfig.findViewById(R.id.txtFromPass);
			final EditText txtToList = (EditText)dlgConfig.findViewById(R.id.txtToList);
			final EditText txtPIN = (EditText)dlgConfig.findViewById(R.id.txtPIN);
			Button btnTest = (Button)dlgConfig.findViewById(R.id.btnTestMail);
			Button btnOK = (Button)dlgConfig.findViewById(R.id.btnConfigOK);
			Button btnCancel = (Button)dlgConfig.findViewById(R.id.btnConfigCancel);
			
			txtEmail.setText(pref.getString("FromEmail", "tinisurveyapp@gmail.com"));
			txtPassword.setText(pref.getString("FromPass", "tinisurveyreport"));
			txtToList.setText(pref.getString("ToList", "khoa.do@nkidcorp.com,huy.mai@tiniplanet.com,nhat.nguyen@tiniplanet.com"));
			btnCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dlgConfig.cancel();
				}
			});
			
			btnOK.setOnClickListener(new View.OnClickListener() {
				
				@SuppressLint("CommitPrefEdits")
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if (txtPIN.getText().toString().equals("09092014")) {
						
						SharedPreferences.Editor editor = pref.edit();
						editor.putString("FromEmail", txtEmail.getText().toString());
						editor.putString("FromPass", txtPassword.getText().toString());
						editor.putString("ToList", txtToList.getText().toString());
						
						editor.commit();
						errNoti("Cấu hình thành công");
						dlgConfig.cancel();
					} else {
						errNoti("Mã PIN chưa đúng! Vui lòng nhập lại");
						
					}
				}
			});
			
			btnTest.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						sendEmail("Daily");
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			dlgConfig.show();
		}
		return super.onOptionsItemSelected(item);
	}
}
