package com.dannygrove.mtls;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ServerDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mtls.db";
    public static final String TAG = "ServerListDBHelper";
    private static ServerDBHelper sInstance;

    private static final String SQL_CREATE_SERVER_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ServerEntry.TABLE_NAME + " (" +
                    ServerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ServerEntry.COLUMN_NAME_SERVER_NAME + " TEXT UNIQUE," +
                    ServerEntry.COLUMN_NAME_URL + " TEXT," +
                    ServerEntry.COLUMN_NAME_ISSUER + " TEXT," +
                    ServerEntry.COLUMN_NAME_LIFETIME + " INTEGER," +
                    ServerEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    ServerEntry.COLUMN_NAME_FINGERPRINT + " TEXT," +
                    ServerEntry.COLUMN_NAME_COUNTRY + " TEXT," +
                    ServerEntry.COLUMN_NAME_STATE + " TEXT," +
                    ServerEntry.COLUMN_NAME_LOCALITY + " TEXT," +
                    ServerEntry.COLUMN_NAME_ORGANIZATION_NAME + " TEXT" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ServerEntry.TABLE_NAME;
    // ...

    public static synchronized ServerDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new ServerDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private ServerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SERVER_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Server> getServers() {
        List<Server> list = new ArrayList<Server>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(ServerEntry.TABLE_NAME, null, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    list.add(Server.fromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get servers from database.");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }

    public void addServer(Server server) {
        Log.i(TAG, "Adding Server");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ServerEntry.COLUMN_NAME_SERVER_NAME, server.name);
        values.put(ServerEntry.COLUMN_NAME_URL, server.url);
        values.put(ServerEntry.COLUMN_NAME_ORGANIZATION_NAME, server.organization_name);
        values.put(ServerEntry.COLUMN_NAME_ISSUER, server.issuer);
        long id = db.insert(ServerEntry.TABLE_NAME, null, values);
        Log.d(TAG, "Created record with ID: " + id);
    }

    public void removeServer(Server server) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "_id = ?";
        String[] selectionArgs = { Long.toString(server.id) };
        int deletedRows = db.delete(ServerEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        Log.d(TAG, "Deleted " + deletedRows + " Records");
    }
}
