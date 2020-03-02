package org.versebyverseministry.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by thomascarey on 8/06/17.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "VBVMIAppDatabase";

    public static final int VERSION = 1;
}

