package nkid.tini.tinisurveyapp;

import java.text.SimpleDateFormat;
import java.util.Date;

import nkid.tini.data.DBAdapter;
import nkid.tini.data.Poll;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	ImageButton	imgbLike, imgbDontCare, imgbDontLike;
	static DBAdapter mDB;
	
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
	}

	public void addToDB(int vote) {
		String name = "Parafait_Touch_Readers";
		
		Date date = new Date();	
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String datetime = sdf.format(date);
		
		Poll poll = new Poll(name, "", vote, datetime); 
		
		mDB.addPoll(poll);
	}
	
	void thanksNoti(){
		String mes = "Cám ơn bạn đã bình chọn!";
    	Toast.makeText(this, mes, Toast.LENGTH_LONG).show();
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
