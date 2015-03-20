package com.altairdevelopment.insulininjectiontracking;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ReleveActivity extends Activity {
	// Constants
	final static int NO_SELECTION = 0;
	final static int MAX_ROWS = 30;
	
	private int      selectedType = 0;
	private Calendar selectedDate = Calendar.getInstance();
	private String   selectedDose = "0";
	private int      selectedArea = NO_SELECTION;
	
	private Context myContext;
	
	// Areas
	private ArrayList<ImageView> listAreas;
	private double areaPosition[][]={
			{0.12,0.25}, // Right arm
			{0.60,0.25}, // Left arm
			{0.25,0.60}, // Right leg
			{0.52,0.60}, // Left leg
			{0.38,0.45}, // Stomach
			{0.52,0.50}, // Right buttock
			{0.25,0.50}  // Left buttock
			};
	private double areaProportion=0.25;
	
	// Views
	private EditText editDate;
	private EditText editTime;
	private Spinner spinnerTypes;
	private Spinner spinnerDoses;
	private ImageButton buttonCancel;
	private ImageButton buttonOK;
	private LinearLayout form;
	
	// Layouts
	private RelativeLayout bodyLayout;
	private RelativeLayout bodyBackLayout;
	private TableLayout insulinTable;
	
	// Database
	private DBInsulineTracker database;
	
	// Current selected injection
	private TableRow currentInsulinInjection;
	
	// Current user
	private long userId = -1;
	private String userFirstName = "";
	
	// Adapters
	private ArrayAdapter<String> spinnerTypesAdapter;
    private ArrayAdapter<Float> spinnerDosesAdapter;
    private SpinnerTypesListener spinnerTypesListener;

    // Working arrays
    private List<Integer> managedTypesValues = new ArrayList<Integer>();

    // Settings
    private SharedPreferences sharedPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_releve);

		myContext = this;
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// getting parameters from calling Activity
		Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
		    userId = extras.getLong("userId");
		    userFirstName = extras.getString("userFirstName");
		}
		
		this.setTitle(this.getString(R.string.title_activity_releve)+" "+userFirstName);
		
		listAreas = new ArrayList<ImageView>();
		
		ImageView rightArm;
		rightArm = (ImageView) findViewById(R.id.imageSelRightArm);
		rightArm.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		rightArm.setTag(R.id.tag_area, InsulinInjection.RIGHT_ARM);
		listAreas.add(rightArm);

		ImageView leftArm;
		leftArm = (ImageView) findViewById(R.id.imageSelLeftArm);
		leftArm.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		leftArm.setTag(R.id.tag_area, InsulinInjection.LEFT_ARM);
		listAreas.add(leftArm);

		ImageView rightLeg;
		rightLeg = (ImageView) findViewById(R.id.imageSelRightLeg);
		rightLeg.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		rightLeg.setTag(R.id.tag_area, InsulinInjection.RIGHT_LEG);
		listAreas.add(rightLeg);

		ImageView leftLeg;
		leftLeg = (ImageView) findViewById(R.id.imageSelLeftLeg);
		leftLeg.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		leftLeg.setTag(R.id.tag_area, InsulinInjection.LEFT_LEG);
		listAreas.add(leftLeg);
		
		ImageView stomach;
		stomach = (ImageView) findViewById(R.id.imageSelStomach);
		stomach.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		stomach.setTag(R.id.tag_area, InsulinInjection.STOMACH);
		listAreas.add(stomach);

		ImageView rightButtock;
		rightButtock = (ImageView) findViewById(R.id.imageSelRightButtock);
		rightButtock.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		rightButtock.setTag(R.id.tag_area, InsulinInjection.RIGHT_BUTTOCK);
		listAreas.add(rightButtock);

		ImageView leftButtock;
		leftButtock = (ImageView) findViewById(R.id.imageSelLeftButtock);
		leftButtock.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				selectionOnClick(v);
			}
		});
		leftButtock.setTag(R.id.tag_area, InsulinInjection.LEFT_BUTTOCK);
		listAreas.add(leftButtock);
		
		buttonOK = (ImageButton) findViewById(R.id.buttonOK);
		buttonOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InsulinInjection ii = new InsulinInjection(0, userId, selectedDate, selectedType, selectedDose, selectedArea);
				if (database.insertInjection(ii)>-1) {
					loadInsulinInjections();
					majImageSelection(NO_SELECTION);
				} else {
					Toast.makeText(myContext, myContext.getText(R.string.error_insert_ii), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		buttonCancel = (ImageButton) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				majImageSelection(NO_SELECTION);
			}
		});
		
		insulinTable = (TableLayout) findViewById(R.id.injectionTable);
		
		editDate = (EditText) findViewById(R.id.editDate);
		editDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(myContext, onDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		editTime = (EditText) findViewById(R.id.editTime);
		editTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new TimePickerDialog(myContext, onTimeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true).show();
			}
		});

        spinnerTypesAdapter = new ArrayAdapter<String> (this,android.R.layout.simple_dropdown_item_1line);

		spinnerTypes = (Spinner) findViewById(R.id.spinnerTypes);
        spinnerTypes.setAdapter(spinnerTypesAdapter);
        spinnerTypesListener = new SpinnerTypesListener();
        spinnerTypes.setOnItemSelectedListener(spinnerTypesListener);

		spinnerDosesAdapter = new ArrayAdapter<Float> (this,android.R.layout.simple_dropdown_item_1line);
		
		spinnerDoses = (Spinner) findViewById(R.id.spinnerDoses);
		spinnerDoses.setAdapter(spinnerDosesAdapter);
		spinnerDoses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selectedDose = String.valueOf(parent.getItemAtPosition(pos));
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		
		form = (LinearLayout) findViewById(R.id.form);
		bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
		bodyBackLayout = (RelativeLayout) findViewById(R.id.bodyBackLayout);
		
		setViewVisibilities(View.INVISIBLE, 0);
		
		database = new DBInsulineTracker(this, DBInsulineTracker.dbname, null, DBInsulineTracker.version);
		database.openDatabase();
		
		loadInsulinInjections();
	}

    @Override
    protected void onStart() {
        super.onStart();

        // Create insulin types spinner
        spinnerTypesAdapter.clear();
        managedTypesValues.clear();

        if (sharedPref.getBoolean("insulin_rapid_managed", true)) {
            spinnerTypesAdapter.add(getResources().getString(R.string.insulin_rapid));
            managedTypesValues.add(InsulinInjection.RAPID_ACTING);
        }

        if (sharedPref.getBoolean("insulin_short_managed", true)) {
            spinnerTypesAdapter.add(getResources().getString(R.string.insulin_short));
            managedTypesValues.add(InsulinInjection.SHORT_ACTING);
        }


        if (sharedPref.getBoolean("insulin_intermediate_managed", true)) {
            spinnerTypesAdapter.add(getResources().getString(R.string.insulin_intermediate));
            managedTypesValues.add(InsulinInjection.INTERMEDIATE_ACTING);
        }

        if (sharedPref.getBoolean("insulin_long_managed", true)) {
            spinnerTypesAdapter.add(getResources().getString(R.string.insulin_long));
            managedTypesValues.add(InsulinInjection.LONG_ACTING);
        }

        if (sharedPref.getBoolean("insulin_premixed_managed", true)) {
            spinnerTypesAdapter.add(getResources().getString(R.string.insulin_premixed));
            managedTypesValues.add(InsulinInjection.PRE_MIXED);
        }

        spinnerTypesListener.onItemSelected(null, null, 0, 0);
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.releve, menu);
		return true;
	}
	    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_share :
				Intent share = new Intent(myContext, ExportActivity.class);
				share.putExtra("userId",userId);
				share.putExtra("userFirstName",userFirstName);
				startActivity(share);
				return true;
			case R.id.action_settings :
	        	Intent settings = new Intent(this, SettingsActivity.class);
	        	startActivity(settings);
	            return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	@Override
//	public void onStart() {
//		super.onStart();
//		
//		RelativeLayout bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
//		
//		for (ImageView iView : listAreas) {
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iView.getLayoutParams();
//			params.setMargins((int) (bodyLayout.getWidth()*areaPosition[(Integer) iView.getTag(R.id.tag_area)-1][0]), 
//							  (int) (bodyLayout.getHeight()*areaPosition[(Integer) iView.getTag(R.id.tag_area)-1][1]),0,0);
//			params.width = (int) (bodyLayout.getWidth()*areaProportion);
//		}
//	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		RelativeLayout parent;
		RelativeLayout.LayoutParams params;
		for (ImageView iView : listAreas) {
			parent = (RelativeLayout) iView.getParent();
			if (parent.getWidth() > 0) {
				params = (RelativeLayout.LayoutParams) iView.getLayoutParams();
				params.setMargins((int) (parent.getWidth()*areaPosition[(Integer) iView.getTag(R.id.tag_area)-1][0]), 
								  (int) (parent.getHeight()*areaPosition[(Integer) iView.getTag(R.id.tag_area)-1][1]),0,0);
				params.width = (int) (parent.getWidth()*areaProportion);
			}
		}
	}

    private class SpinnerTypesListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            selectedType = managedTypesValues.get(pos);

            float min=0;
            float max=10;
            float step=0.5f;

            switch (selectedType) {
                case 0 :
                    min = Float.parseFloat(sharedPref.getString("insulin_rapid_min", "0"));
                    max = Float.parseFloat(sharedPref.getString("insulin_rapid_max", "10f"));
                    step = Float.parseFloat(sharedPref.getString("insulin_rapid_step", "0.5f"));
                    break;
                case 1 :
                    min = Float.parseFloat(sharedPref.getString("insulin_short_min", "0"));
                    max = Float.parseFloat(sharedPref.getString("insulin_short_max", "10f"));
                    step = Float.parseFloat(sharedPref.getString("insulin_short_step", "0.5f"));
                    break;
                case 2 :
                    min = Float.parseFloat(sharedPref.getString("insulin_intermediate_min", "0"));
                    max = Float.parseFloat(sharedPref.getString("insulin_intermediate_max", "10f"));
                    step = Float.parseFloat(sharedPref.getString("insulin_intermediate_step", "0.5f"));
                    break;
                case 3 :
                    min = Float.parseFloat(sharedPref.getString("insulin_long_min", "0"));
                    max = Float.parseFloat(sharedPref.getString("insulin_long_max", "10f"));
                    step = Float.parseFloat(sharedPref.getString("insulin_long_step", "0.5f"));
                    break;
                case 4 :
                    min = Float.parseFloat(sharedPref.getString("insulin_premixed_min", "0"));
                    max = Float.parseFloat(sharedPref.getString("insulin_premixed_max", "10f"));
                    step = Float.parseFloat(sharedPref.getString("insulin_premixed_step", "0.5f"));
                    break;
            }

            List<Float> doses = new ArrayList<Float>();

            for (float i=min; i<=max; i+=step) {
                doses.add(i);
            }

            spinnerDosesAdapter.clear();
            spinnerDosesAdapter.addAll(doses);
            spinnerDoses.setSelection(Math.round(spinnerDoses.getCount()/2));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

	private void selectionOnClick(View v) {
		selectedArea = getSelection(selectedArea,(Integer) v.getTag(R.id.tag_area));
		majImageSelection(selectedArea);
	}
		
	private int getSelection(int currentSelection, int newSelection) {
		if (currentSelection == newSelection) {
			return NO_SELECTION;
		} else {
			return newSelection;
		}
	}
	
	private void majImageSelection(int newSelection) {
		for (ImageView area: listAreas) {
			if (newSelection == (Integer) area.getTag(R.id.tag_area)) {
				area.setImageResource(R.drawable.selected_square);
			} else {
				area.setImageResource(R.drawable.non_selected_square);
			}
		}
		
		if (newSelection==NO_SELECTION) {
			setViewVisibilities(View.INVISIBLE, newSelection);
		} else {
			selectedDate = Calendar.getInstance();
			majDateTime();
			setViewVisibilities(View.VISIBLE, newSelection);
		}
	}
	
	/*
	 * Visibility settings for the form fields
	 */
	private void setViewVisibilities(int _visibility, int _selectedArea) {
		if (_visibility==View.VISIBLE) {			
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) form.getLayoutParams();
			params.weight = 1f;
			
			if (_selectedArea < 6) {
				// The selected area is on the body's front 
				params = (LinearLayout.LayoutParams) bodyBackLayout.getLayoutParams();
				params.weight = 0f;
			} else {
				// The selected area is on the body's back 
				params = (LinearLayout.LayoutParams) bodyLayout.getLayoutParams();
				params.weight = 0f;
			}
		} else {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bodyLayout.getLayoutParams();
			params.weight = 1f;
			params = (LinearLayout.LayoutParams) bodyBackLayout.getLayoutParams();
			params.weight = 1f;
			params = (LinearLayout.LayoutParams) form.getLayoutParams();
			params.weight = 0f;
		}
	}
	
	/*
	 * Population of the Injection Table
	 */
	@SuppressLint("InflateParams")
	private void loadInsulinInjections() {
		insulinTable.removeAllViews();
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TableRow myRow;

		database.selectInjections(userId, MAX_ROWS);
		InsulinInjection injection = database.getNextInjection();
		while (injection!=null) {
			myRow = (TableRow) inflater.inflate(R.layout.injection_row, null);
//			myRow.setOnClickListener(new OnClickListener() {				
//				@Override
//				public void onClick(View v) {
//					TextView injectionID = (TextView) v.findViewById(R.id.injectionID);
//					Toast.makeText(myContext, injectionID.getText(), Toast.LENGTH_SHORT).show();				
//				}
//			});
			myRow.setOnLongClickListener(new OnLongClickListener() {				
				@Override
				public boolean onLongClick(View v) {
					currentInsulinInjection = (TableRow) v;             	   
					TextView injectionDate = (TextView) currentInsulinInjection.findViewById(R.id.injectionDate);
					TextView injectionTime = (TextView) currentInsulinInjection.findViewById(R.id.injectionTime);
					AlertDialog deleteDialog =  deleteInjectionDialog(injectionDate.getText(), injectionTime.getText());
					deleteDialog.show();
					return false;
				}
			});
			
			setInjectionRow(myRow, injection);
            insulinTable.addView(myRow);

			View v = new View(this);
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            v.setBackgroundResource(R.color.base_color);            
            insulinTable.addView(v);
            
			injection = database.getNextInjection();
		}
		
		database.closeCursor();
	}
	
	private AlertDialog deleteInjectionDialog(CharSequence _date, CharSequence _time) 
	{
        AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
        builder.setTitle(_date+" "+_time)
        	   .setMessage(R.string.dialog_deleteInjection_msg)
               .setPositiveButton(R.string.dialog_deleteInjection_delete, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Delete the current TableRow record currentInsulinInjection
                	   TextView injectionID = (TextView) currentInsulinInjection.findViewById(R.id.injectionID);
                	   database.deleteInjection(Integer.parseInt(injectionID.getText().toString()));
                	   loadInsulinInjections();
                   }
               })
               .setNegativeButton(R.string.dialog_deleteInjection_cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
	}
	
	/*
	 * Population of an Injection Row from Insulin Injection class
	 */
	private void setInjectionRow(View row, InsulinInjection injection) {
		// Format : Date Time Insulin type Dose Area
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

		// injection ID (invisible)
		TextView injectionID = (TextView) row.findViewById(R.id.injectionID);
		injectionID.setText(String.valueOf(injection.getId()));
		
		// injection date
		TextView injectionDate = (TextView) row.findViewById(R.id.injectionDate);
		injectionDate.setText(dateFormat.format(injection.getDate().getTime()));
		
		// injection time
		TextView injectionTime = (TextView) row.findViewById(R.id.injectionTime);
		injectionTime.setText(timeFormat.format(injection.getDate().getTime()));
		
		// insulin type
		TextView insulinType = (TextView) row.findViewById(R.id.insulinType);
		insulinType.setText(getResources().getStringArray(R.array.types)[injection.getType()]);

		// injection dose
		TextView injectionDose = (TextView) row.findViewById(R.id.injectionDose);
		injectionDose.setText(String.valueOf(injection.getDose()));
		
		// injection area
		TextView injectionArea = (TextView) row.findViewById(R.id.injectionArea);
		injectionArea.setText(InsulinInjection.getTextFromArea(myContext, injection.getArea()));
	}


	/*
	 * Date and Time Picker management
	 */
	private OnDateSetListener onDateSetListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			selectedDate.set(year, monthOfYear, dayOfMonth);
			majDateTime();
		}
		
	};
	
	private OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			selectedDate.set(
					selectedDate.get(Calendar.YEAR), 
					selectedDate.get(Calendar.MONTH), 
					selectedDate.get(Calendar.DAY_OF_MONTH), 
					hourOfDay, 
					minute);
			majDateTime();
		}
	
	};
	
	private void majDateTime() {
		DateFormat dateFormat = DateFormat.getDateInstance();
		DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
		editDate.setText(dateFormat.format(selectedDate.getTime()));
		editTime.setText(timeFormat.format(selectedDate.getTime()));
	}
}
