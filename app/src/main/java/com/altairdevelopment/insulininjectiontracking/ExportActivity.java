package com.altairdevelopment.insulininjectiontracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ExportActivity extends Activity {
    private Calendar exportStartDate = Calendar.getInstance();
    private Calendar exportEndDate = Calendar.getInstance();
    private DateFormat dateFormat = DateFormat.getDateInstance();
    private EditText startDate;
    private EditText endDate;
    private Integer userId = 0;
    private String userFirstName;
    private String exportFilename = "iit_export.csv";
	
	// Settings
	SharedPreferences sharedPref;

	private Context myContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		
		myContext = this;
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			userId = extras.getInt("userId");
			userFirstName = extras.getString("userFirstName");			
		}
		
		TextView exportUser = (TextView) findViewById(R.id.exportUser);
		exportUser.setText(userFirstName); 
		
		setHourToStartDay(exportStartDate);
		setHourToEndDay(exportEndDate);
		
		startDate = (EditText) findViewById(R.id.startDate);
		startDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(myContext, onStartDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		startDate.setText(dateFormat.format(exportStartDate.getTime()));

		endDate = (EditText) findViewById(R.id.endDate);
		endDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(myContext, onEndDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		endDate.setText(dateFormat.format(exportEndDate.getTime()));

		Button exportButton = (Button) findViewById(R.id.exportButton);
		exportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Controls
				if (exportStartDate.after(exportEndDate)) {
					Toast.makeText(myContext, myContext.getString(R.string.errorDateMismatch), Toast.LENGTH_LONG).show();
					return;
				}
				
				// Export
				export_csv(userId, exportStartDate, exportEndDate);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.export, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_settings :
	        	Intent settings = new Intent(this, SettingsActivity.class);
	        	startActivity(settings);
	            return true;
			case android.R.id.home:
			    // To avoid destroying the previous Activity
	            onBackPressed();
	            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Date Picker management
	 */
	private OnDateSetListener onStartDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			exportStartDate.set(year, monthOfYear, dayOfMonth);
			setHourToStartDay(exportStartDate);
			startDate.setText(dateFormat.format(exportStartDate.getTime()));
		}
		
	};

	private OnDateSetListener onEndDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			exportEndDate.set(year, monthOfYear, dayOfMonth);
			setHourToEndDay(exportEndDate);
			endDate.setText(dateFormat.format(exportEndDate.getTime()));
		}
		
	};
	
	private void setHourToStartDay(Calendar _date) {
		// Hour set to 00:00:00:00
		_date.set(Calendar.HOUR, 0);
		_date.set(Calendar.MINUTE, 0);
		_date.set(Calendar.SECOND, 0);
		_date.set(Calendar.MILLISECOND, 0);
	}

	private void setHourToEndDay(Calendar _date) {
		// Hour set to 23:59:59:99 
		_date.set(Calendar.HOUR_OF_DAY, 23);
		_date.set(Calendar.MINUTE, 59);
		_date.set(Calendar.SECOND, 59);
		_date.set(Calendar.MILLISECOND, 99);
	}

	/*
	 * Export management
	 */
	private void export_csv(int userId, Calendar exportStartDate, Calendar exportEndDate) {
		InsulinInjection myInjection=new InsulinInjection();
		
		// Database
		DBInsulineTracker database;
		database = new DBInsulineTracker(this, DBInsulineTracker.DBNAME, null, DBInsulineTracker.version);
		database.openDatabase();

        database.selectInjectionsByDate(userId, exportStartDate.getTimeInMillis(), exportEndDate.getTimeInMillis());

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    	File file = new File(path, exportFilename);

		// File
        try {
			OutputStream outputStream =  new FileOutputStream(file);
        	outputStream.write(myInjection.getCSV(
        			myContext,
        			sharedPref.getString("export_date_format", "yyyy/MM/dd"),
        			sharedPref.getString("export_separator", ";"),
        			sharedPref.getString("export_delimiter", ""),
        			InsulinInjection.CSV_HEADER).getBytes());
        
	        while ((myInjection=database.getNextInjection())!= null) {
	        	outputStream.write(myInjection.getCSV(
	        			myContext, 
	        			sharedPref.getString("export_date_format", "yyyy/MM/dd"),
	        			sharedPref.getString("export_separator", ";"),
	        			sharedPref.getString("export_delimiter", ""),
	        			InsulinInjection.CSV_LINE).getBytes());
	        }
	        
	        outputStream.close();
	        sendFile("User 1",exportStartDate, exportEndDate, Uri.fromFile(file));
		} catch (FileNotFoundException e) {
			Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	/*
	 * Sending export file
	 */
	private void sendFile(String user, Calendar startDate, Calendar endDate, Uri attachement) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		//intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.export_subject)+" "+user);
		intent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.export_subject),user));
		//intent.putExtra(Intent.EXTRA_TEXT, "Export of Injection Insulin Tracking from "+dateFormat.format(startDate.getTime())+" to "+dateFormat.format(endDate.getTime()));
		intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.export_range_date),dateFormat.format(startDate.getTime()),dateFormat.format(endDate.getTime())));
		intent.putExtra(Intent.EXTRA_STREAM, attachement);
		startActivity(Intent.createChooser(intent, "Send email..."));
	}

}
