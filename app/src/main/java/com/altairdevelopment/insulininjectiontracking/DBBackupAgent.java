package com.altairdevelopment.insulininjectiontracking;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

import java.io.File;

/**
 * Created by gleclere on 20/03/2015.
 */
public class DBBackupAgent extends BackupAgentHelper {
    // A key to uniquely identify the set of backup data
    static final String FILES_BACKUP_KEY = "2IT_database";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        FileBackupHelper helper = new FileBackupHelper(this,
                DBInsulineTracker.DBNAME);
        addHelper(FILES_BACKUP_KEY, helper);
    }

    @Override
    public File getFilesDir(){
        File path = getDatabasePath(DBInsulineTracker.DBNAME);
        return path.getParentFile();
    }
}
