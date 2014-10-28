package nkid.tini.tinisurveyapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.mail.MessagingException;

import nkid.tini.data.DBAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	String fromEmail, fromPass, toList;
	DBAdapter mDB;
	String mailType;
	@Override
	public void onReceive(Context ctx, Intent intent) {
		// TODO Auto-generated method stub
		Bundle extra = intent.getExtras();
		
		if (extra != null) {
			this.fromEmail = extra.getString("FromEmail",
					"nkidsurveyapp@gmail.com");
			this.fromPass = extra.getString("FromPass", "nkidsurveyreport");
			this.toList = extra
					.getString("ToList",
							"khoa.do@nkidcorp.com,huy.mai@tiniplanet.com,nhat.nguyen@tiniplanet.com");
			this.mailType = extra.getString("MailType", "Daily");
		}
		mDB = new DBAdapter(ctx);
		mDB.open();
		
		try {
			sendEmail(ctx);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mDB.close();
	}


	private Mail mail;
	String body = "";

	protected void sendEmail(Context ctx) throws MessagingException {
		mail = new Mail(this.fromEmail, this.fromPass);
		String[] destAddr = toList.split(",");
		String[] ccAddr = { "thanhhuy89vn@gmail.com" };
		mail.setTo(destAddr);
		mail.setCC(ccAddr);
		mail.setFrom("nkidsurveyapp@gmail.com");
		mail.setSubject("[Survey App] - " + mailType + " Report");
		body = new String();

		DateFormat df = new DateFormat();
		String header = df.format("dd-MM-yyyy hh:mm:ss", new Date()).toString();
		
		body += header;
		body += "\nVote for : Parafait Touch Readers";

		if (mailType.equals("Daily")) {
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
		} else if (mailType.equals("Monthly")) {
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
		} else if (mailType.equals("Weekly")) {
			int WeekLikeNo = mDB.getResultByWeek(1);
			int WeekDontLikeNo = mDB.getResultByWeek(-1);
			int WeekDontCareNo = mDB.getResultByWeek(0);

			body += "\n\t- Like : " + WeekLikeNo;
			body += "\n\t- Don't Like : " + WeekDontLikeNo;
			body += "\n\t- Don't Care : " + WeekDontCareNo;
		}

		mail.setBody(body);
		final boolean success = true;
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

		}.execute();
		/*
		 * if (title.equals("Previous Day")) { title = "Daily"; }
		 */
		
		Toast.makeText(ctx, "Gửi email thành công", Toast.LENGTH_SHORT).show();
		

	}
}
