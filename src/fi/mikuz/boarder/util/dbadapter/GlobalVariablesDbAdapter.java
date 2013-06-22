/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= *
 * Original licensed code has been modified for Boarder.                     *
 * ========================================================================= */

/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package fi.mikuz.boarder.util.dbadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GlobalVariablesDbAdapter extends DbAdapter {

    public static final String KEY_VARIABLE = "variable";
    public static final String KEY_DATA = "data";
    
    public static final String FIRST_START_KEY = "firstStart";
    public static final String TOS_VERSION_KEY = "tosVersion";
    public static final String FADE_OUT_DURATION_KEY = "fadeOutDuration";
    public static final String FADE_IN_DURATION_KEY = "fadeInDuration";
    public static final String SENSITIVE_LOGGING = "sensitiveLogging";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_TABLE = "global_variables";
    private final Context mCtx;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public GlobalVariablesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    

    public GlobalVariablesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long createVariable(String variable, String data) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_VARIABLE, variable);
        initialValues.put(KEY_DATA, data);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public long createIntVariable(String variable, int data) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_VARIABLE, variable);
        initialValues.put(KEY_DATA, data);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public long createBooleanVariable(String variable, boolean data) {
    	int intValue = data ? 1 : 0;
    	return createIntVariable(variable, intValue);
    }
    
    public boolean updateVariable(String variable, String data) {
        ContentValues args = new ContentValues();
        args.put(KEY_VARIABLE, variable);
        args.put(KEY_DATA, data);

        return mDb.update(DATABASE_TABLE, args, KEY_VARIABLE + "= '" + variable + "'", null) > 0;
    }
    
    public boolean updateIntVariable(String variable, int data) {
        ContentValues args = new ContentValues();
        args.put(KEY_VARIABLE, variable);
        args.put(KEY_DATA, data);

        return mDb.update(DATABASE_TABLE, args, KEY_VARIABLE + "= '" + variable + "'", null) > 0;
    }
    
    public boolean updateBooleanVariable(String variable, boolean data) {
    	int intValue = data ? 1 : 0;
        return updateIntVariable(variable, intValue);
    }

    public boolean deleteVariable(String variable) {

        return mDb.delete(DATABASE_TABLE, KEY_VARIABLE + "= '" + variable + "'", null) > 0;
    }
    
    public Cursor fetchAllVariables() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_VARIABLE, 
        		KEY_DATA}, null, null, null, null, null);
    }

    public Cursor fetchVariable(String variable) throws SQLException {

        Cursor mCursor =

            mDb.query(false, DATABASE_TABLE, new String[] {KEY_VARIABLE, KEY_DATA}, KEY_VARIABLE + " = '" + variable + "'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

}
