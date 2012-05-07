package fi.mikuz.boarder.util.dbadapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import fi.mikuz.boarder.gui.SoundboardMenu;

public abstract class DbAdapter {

    /**
     * Database creation sql statement
     */
    private static final String CREATE_TABLE_BOARDS =
            "create table boards (_id integer primary key autoincrement, "
            + "title text not null, local integer not null);";
    
    private static final String CREATE_TABLE_LOGIN = 
    		"create table login (variable text not null, data text not null);";
    
    private static final String CREATE_TABLE_GLOBAL_VARIABLES = 
    		"create table global_variables (variable text not null, data text not null);";

    private static final String DATABASE_NAME = "boarder.db";
    private static final int DATABASE_VERSION = 68;

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        	db.execSQL(CREATE_TABLE_BOARDS);
            db.execSQL(CREATE_TABLE_LOGIN);
            db.execSQL(CREATE_TABLE_GLOBAL_VARIABLES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //TODO Why to drop all databases
            Log.w(SoundboardMenu.TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS boards");
            db.execSQL("DROP TABLE IF EXISTS login");
            db.execSQL("DROP TABLE IF EXISTS global_variables");
            onCreate(db);
        }
    }
}
