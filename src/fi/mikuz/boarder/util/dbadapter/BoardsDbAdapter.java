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
public class BoardsDbAdapter extends DbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_LOCAL = "local";
    public static final String KEY_ROWID = "_id";
    
    public static final int LOCAL_WHITE = 0;
    public static final int LOCAL_YELLOW = 1;
    public static final int LOCAL_GREEN = 2;
    public static final int LOCAL_RED = 3;
    public static final int LOCAL_ORANGE = 4;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_TABLE = "boards";
    private final Context mCtx;

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public BoardsDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the boards database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public BoardsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new board using the title provided. If the board is
     * successfully created return the new rowId for that board, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the board
     * @return rowId or -1 if failed
     */
    public long createBoard(String title, int local) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_LOCAL, local);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the board with the given rowId
     * 
     * @param rowId id of board to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteBoard(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all boards in the database
     * 
     * @return Cursor over all boards
     */
    public Cursor fetchAllBoards() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, 
        		KEY_TITLE, KEY_LOCAL}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the board that matches the given rowId
     * 
     * @param rowId id of board to retrieve
     * @return Cursor positioned to matching board, if found
     * @throws SQLException if board could not be found/retrieved
     */
    public Cursor fetchBoard(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_LOCAL}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor filterableBoard(StringBuilder buffer, String[] args) throws SQLException {

        return mDb.query(true, DATABASE_TABLE, new String[] {KEY_TITLE}, 
        		buffer == null ? null : buffer.toString(), args,
                null, null, null, null);

    }

    /**
     * Update the board using the details provided. The board to be updated is
     * specified using the rowId, and it is altered to use the title 
     * value passed in
     * 
     * @param rowId id of board to update
     * @param title value to set board title to
     * @return true if the board was successfully updated, false otherwise
     */
    public boolean updateBoard(long rowId, String title, int local) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_LOCAL, local);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
