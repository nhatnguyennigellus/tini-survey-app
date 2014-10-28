package nkid.tini.tinisurveyapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;

import nkid.tini.data.DBAdapter;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class SendEmailService extends IntentService {

	String fromEmail, fromPass, toList;
	DBAdapter mDB;

	public SendEmailService() {
		super("SendEmailService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		return START_STICKY;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extra = intent.getExtras();
		if (extra != null) {
			this.fromEmail = extra.getString("FromEmail",
					"nkidsurveyapp@gmail.com");
			this.fromPass = extra.getString("FromPass", "nkidsurveyreport");
			this.toList = extra
					.getString("ToList",
							"khoa.do@nkidcorp.com,huy.mai@tiniplanet.com,nhat.nguyen@tiniplanet.com");

		}
		mDB = new DBAdapter(this);
		mDB.open();
		

	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		super.onCreate();
		Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
		
		CountDownTimer dailyTimer = new CountDownTimer(60000, 60000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				try {
					Calendar cal = Calendar.getInstance();
					int hour = cal.get(Calendar.HOUR_OF_DAY);
					int minute = cal.get(Calendar.MINUTE);

					if (hour == 0 && minute == 0)
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
					if (dayOfWeek == Calendar.MONDAY && hour == 0
							&& minute == 0)
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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onBind(intent);
	}

	private Mail mail;
	String body = "";

	protected void sendEmail(String title) throws MessagingException {
		mail = new Mail(this.fromEmail, this.fromPass);
		String[] destAddr = toList.split(",");
		String[] ccAddr = { "thanhhuy89vn@gmail.com" };
		mail.setTo(destAddr);
		mail.setCC(ccAddr);
		mail.setFrom("nkidsurveyapp@gmail.com");
		mail.setSubject("[Survey App] - " + title + " Report");
		body = new String();

		body += "Vote for : Parafait Touch Readers";

		if (title.equals("Daily")) {
			// mail.setSubject("[Survey App] - Daily Report");
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
		} else if (title.equals("Monthly")) {
			Calendar cal = Calendar.getInstance();
			int prevMonth = cal.get(Calendar.MONTH);
			int year = cal.get(Calendar.YEAR);
			if (prevMonth == 0) {
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
						errNoti("Gửi email thất bại. Nguyên nhân: "
								+ error.toString());
					else
						errNoti("Gửi email thất bại. Khong rõ nguyên nhân: ");
				}
			}

		}.execute();
		/*
		 * if (title.equals("Previous Day")) { title = "Daily"; }
		 */
		notiSentMail(title);

	}

	void notiSentMail(String title) {
		Toast.makeText(this, title + " mail sent!", Toast.LENGTH_SHORT).show();
	}

	void errNoti(String mes) {
		Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
	}

}
