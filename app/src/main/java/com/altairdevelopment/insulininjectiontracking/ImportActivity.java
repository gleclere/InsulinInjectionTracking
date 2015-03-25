package com.altairdevelopment.insulininjectiontracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImportActivity extends Activity {
    private final String importFilename = "iit_export.csv";
    private int userId = 0;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        context = this;

        String userFirstName = null;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userId = extras.getInt("userId");
            userFirstName = extras.getString("userFirstName");
        }

        TextView importUser = (TextView) findViewById(R.id.importUser);
        importUser.setText(userFirstName);

        Button importButton = (Button) findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                import_data(userId);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
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

    private void import_data(int _id) {
        int nbLignesImportees = 0;

        // Settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        InsulinInjection myInjection=new InsulinInjection();

        // Database
        DBInsulineTracker database;
        database = new DBInsulineTracker(this, DBInsulineTracker.dbname, null, DBInsulineTracker.version);
        database.openDatabase();

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Reader file = null;

        // File
        String next[] = {};

        // Date format
        String dateTimeFormat = sharedPref.getString("export_date_format", "yyyy/MM/dd")+" "
                               +sharedPref.getString("export_time_format", "HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        char[] separator=sharedPref.getString("export_separator", ";").toCharArray();
        char[] delimiter=sharedPref.getString("export_delimiter", "").toCharArray();


        try {
            file = new InputStreamReader(new FileInputStream(path+"/"+importFilename));
            CSVReader reader = new CSVReader(
                    file,
                    separator[0],
                    ((delimiter.length>0)?delimiter[0]:'\u0000'),
                    1);

            while(true) {
                next = reader.readNext();
                if (next != null) {
                    if (next.length==6) {
                        calendar.setTime(dateFormat.parse(next[1] + " " + next[2]));
                        // Inserting record in database
                        myInjection.setId(_id);
                        myInjection.setDate(calendar);
                        myInjection.setType(InsulinInjection.getTypeFromText(context, next[3]));
                        myInjection.setDose(Float.valueOf(next[4]));
                        myInjection.setArea(InsulinInjection.getAreaFromText(context, next[5]));

                        if (database.insertInjection(myInjection) > -1) {
                            nbLignesImportees += 1;
                        }
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, String.format(getResources().getText(R.string.nb_import).toString(),nbLignesImportees), Toast.LENGTH_LONG).show();
    }
}
