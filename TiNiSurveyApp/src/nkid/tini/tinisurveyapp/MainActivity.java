package nkid.tini.tinisurveyapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
		showDailyPoll();
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
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
		                errNoti("Gửi email thất bại. Nguyên nhân: " + error.toString());
		            else 
		            	errNoti("Gửi email thất bại. Khong rõ nguyên nhân: ");
		        }
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
			final EditText txtEmail = (EditText) dlgConfig
					.findViewById(R.id.txtFromEmail);
			final EditText txtPassword = (EditText) dlgConfig
					.findViewById(R.id.txtFromPass);
			final EditText txtToList = (EditText) dlgConfig
					.findViewById(R.id.txtToList);
			final EditText txtWatingTime = (EditText) dlgConfig
					.findViewById(R.id.txtWaitingTime);
			final EditText txtPIN = (EditText) dlgConfig
					.findViewById(R.id.txtPIN);
			Button btnTest = (Button) dlgConfig.findViewById(R.id.btnTestMail);
			Button btnOK = (Button) dlgConfig.findViewById(R.id.btnConfigOK);
			Button btnCancel = (Button) dlgConfig
					.findViewById(R.id.btnConfigCancel);

			txtEmail.setText(pref.getString("FromEmail",
					"nkidsurveyapp@gmail.com"));
			txtPassword.setText(pref.getString("FromPass", "nkidsurveyreport"));
			txtWatingTime.setText(String.valueOf(pref.getInt("WaitingTime", 2)));
			txtToList
					.setText(pref
							.getString("ToList",
									"huy.mai@tiniplanet.com,nhat.nguyen@tiniplanet.com"));
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
						editor.putString("FromEmail", txtEmail.getText()
								.toString());
						editor.putString("FromPass", txtPassword.getText()
								.toString());
						editor.putString("ToList", txtToList.getText()
								.toString());
						int waitingTime;
						try {
							waitingTime = Integer.parseInt(txtWatingTime.getText().toString());
						}
						catch (Exception error){
							waitingTime = 2;
						}
						if (waitingTime < 1)
							waitingTime = 1;
						editor.putInt("WaitingTime",waitingTime);
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
						// Check pin vaild
						if (txtPIN.getText().toString().equals("09092014")) {
							sendEmail("Daily");
						} else {
							errNoti("Wrong pin");
						}
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
