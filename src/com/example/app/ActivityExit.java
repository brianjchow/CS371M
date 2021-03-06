package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ActivityExit extends ActionBarActivity implements View.OnClickListener {
	
//	private Button yes;
//	private Button no;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_exit);
		Button yes = (Button) findViewById(R.id.yes);
		Button no = (Button) findViewById(R.id.no_get_new_Room);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
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

		if (id == R.id.exit) {
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void getRoomRec() {
//		startActivityForResult(new Intent(this, ActivityFindRoomLater.class), 0);
		startActivity(new Intent(ActivityExit.this, ActivityFindRoomLater.class));
		finish();
	}

	private void exitApp() {
		Intent intent = new Intent(ActivityExit.this, ActivityMain.class );
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Constants.EXIT, true);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
			case R.id.yes:
				exitApp();
				break;
				
			case R.id.no_get_new_Room:
				getRoomRec();
				break;
		}		
	}

}		// end of file




