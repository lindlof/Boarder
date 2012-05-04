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

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class LoginDbAdapter extends DbAdapter {

    public static final String KEY_VARIABLE = "variable";
    public static final String KEY_DATA = "data";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_TABLE = "login";
    private final Context mCtx;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LoginDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public LoginDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void putLogin(String variable, String data) {
        ContentValues args = new ContentValues();
        args.put(KEY_VARIABLE, variable);
        args.put(KEY_DATA, data);
        
        if (fetchLogin(variable).getCount() > 0) {
        	updateLogin(variable, data);
        } else {
        	createLogin(variable, data);
        }
    }

    public long createLogin(String variable, String data) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_VARIABLE, variable);
        initialValues.put(KEY_DATA, data);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteLogin(String variable) {

        return mDb.delete(DATABASE_TABLE, KEY_VARIABLE + "= '" + variable + "'", null) > 0;
    }
    
    public Cursor fetchAllLogins() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_VARIABLE, 
        		KEY_DATA}, null, null, null, null, null);
    }

    public Cursor fetchLogin(String variable) throws SQLException {

        Cursor mCursor =

            mDb.query(false, DATABASE_TABLE, new String[] {KEY_VARIABLE, KEY_DATA}, KEY_VARIABLE + " = '" + variable + "'", null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean updateLogin(String variable, String data) {
        ContentValues args = new ContentValues();
        args.put(KEY_VARIABLE, variable);
        args.put(KEY_DATA, data);

        return mDb.update(DATABASE_TABLE, args, KEY_VARIABLE + "= '" + variable + "'", null) > 0;
    }
}
