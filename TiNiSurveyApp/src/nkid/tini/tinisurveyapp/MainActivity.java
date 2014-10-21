package nkid.tini.tinisurveyapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;

import nkid.tini.data.DBAdapter;
import nkid.tini.data.Poll;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {

	
	ImageButton imgbLike, imgbDontCare, imgbDontLike;
	
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


		imgbLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToDB(1);
				thanksNoti();
			}
		});

		imgbDontCare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToDB(0);
				thanksNoti();
			}
		});

		imgbDontLike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addToDB(-1);
				thanksNoti();
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

					if (hour == 23 && minute == 55)
						sendEmail("Daily");
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
					if (dayOfWeek == Calendar.MONDAY && hour == 23 && minute == 55)
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
					if (dayOfMonth == 1 && hour == 23 && minute == 55)
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

	@SuppressLint("SimpleDateFormat")
	public void addToDB(int vote) {
		String name = "Parafait_Touch_Readers";

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String datetime = sdf.format(date);

		Poll poll = new Poll(name, "", vote, datetime);

		mDB.addPoll(poll);
	}

	void thanksNoti() {
		String mes = "Cám ơn bạn đã bình chọn!";
		Toast.makeText(this, mes, Toast.LENGTH_LONG).show();
	}

	String body = "";

	protected void sendEmail(String title) throws MessagingException {
		
		mail = new Mail("brightsunnigellus@gmail.com", "dathanielcarlfrancius");
		String[] destAddr = { "nhat.nguyen@tiniplanet.com" };
		mail.setTo(destAddr);
		mail.setFrom("survey@nkidcorp.com");
		mail.setSubject(title + " nKid Survey Report");
		body = new String();

		if (title.equals("Daily")) {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String curDate = sdf.format(date);

			int LikeNo = mDB.getTodayResult(curDate, 1);
			int DontLikeNo = mDB.getTodayResult(curDate, -1);
			int DontCareNo = mDB.getTodayResult(curDate, 0);

			body += "Poll result for today " + curDate + ":";
			body += "\n- Like : " + LikeNo;
			body += "\n- Don't Like : " + DontLikeNo;
			body += "\n- Don't Care : " + DontCareNo;
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

			body += "\n\nThis month's results:";
			body += "\n- Like : " + MonthLikeNo;
			body += "\n- Don't Like : " + MonthDontLikeNo;
			body += "\n- Don't Care : " + MonthDontCareNo;
		} else if (title.equals("Weekly")) {
			int WeekLikeNo = mDB.getResultByWeek(1);
			int WeekDontLikeNo = mDB.getResultByWeek(-1);
			int WeekDontCareNo = mDB.getResultByWeek(0);
			
			body += "\n\nThis week's results:";
			body += "\n- Like : " + WeekLikeNo;
			body += "\n- Don't Like : " + WeekDontLikeNo;
			body += "\n- Don't Care : " + WeekDontCareNo;
		}

		int OvrLikeNo = mDB.getOverallResults(1);
		int OvrDontLikeNo = mDB.getOverallResults(-1);
		int OvrDontCareNo = mDB.getOverallResults(0);

		body += "\n\nOverall results:";
		body += "\n- Like : " + OvrLikeNo;
		body += "\n- Don't Like : " + OvrDontLikeNo;
		body += "\n- Don't Care : " + OvrDontCareNo;


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
		notiSentMail();

	}

	void notiSentMail() {
		Toast.makeText(this, "Email sent!", Toast.LENGTH_LONG).show();
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
