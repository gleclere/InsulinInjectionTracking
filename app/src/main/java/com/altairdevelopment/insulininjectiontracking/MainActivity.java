package com.altairdevelopment.insulininjectiontracking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	// Context
	private Context myContext;

	// Layouts
	private TableLayout userTable;
	
	// Database
	private DBInsulineTracker database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        myContext = this;
        
        userTable = (TableLayout) findViewById(R.id.userTable);
        
		database = new DBInsulineTracker(this, DBInsulineTracker.dbname, null, DBInsulineTracker.version);
		database.openDatabase();
		loadUsers();
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
        	Intent settings = new Intent(this, SettingsActivity.class);
        	startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	/*
	 * Population of the User Table
	 */
	@SuppressLint("InflateParams")
	private void loadUsers() {
		userTable.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TableRow myRow;

		database.selectUsers();
		User user = database.getNextUser();
		while (user!=null) {
			myRow = (TableRow) inflater.inflate(R.layout.user_row, null);
			myRow.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					TextView userIDView  = (TextView) v.findViewById(R.id.userId);
					TextView userFirstNameView  = (TextView) v.findViewById(R.id.userFirstName);

					Intent myIntent = new Intent(myContext, ReleveActivity.class);
					myIntent.putExtra("userId", userIDView.getText());
					myIntent.putExtra("userFirstName", userFirstNameView.getText());
					startActivity(myIntent);		
				}
			});
			
			TextView userIDView  = (TextView) myRow.findViewById(R.id.userId);
			userIDView.setText(String.valueOf(user.getUserId()));

			TextView userFirstNameView  = (TextView) myRow.findViewById(R.id.userFirstName);
			userFirstNameView.setText(user.getUserFirstName());

			TextView userLastNameView  = (TextView) myRow.findViewById(R.id.userLastName);
			userLastNameView.setText(user.getUserLastName());

            userTable.addView(myRow);
            
			user = database.getNextUser();
		}
		
		database.closeCursor();
	}
}
