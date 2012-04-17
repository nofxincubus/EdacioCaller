package com.edacio.caller;

import java.util.Date;
import java.text.SimpleDateFormat;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

public class EdacioDB {
	private static final String TAG = "VenusDB";
	//BASIC FUNCTIONALITY AND VARIABLES
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    
    //Preference AKA settings
    private static final String PREFS_NAME = "EDACIO_PREFS";
    
    //DATABASENAME
    private static final String DB_NAME = "EdacioDb";
    //COLUMN DEFINITION
    private static final String KEY_ID = "_ID";
    public static final String CONTACT_NAME = "DISPLAY_NAME";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String CONTACT_PHONE = "PHONE_NUMBER";
    public static final String CONTACT_PHOTO_ID = "PHOTO_ID";
    public static final String CONTACT_STARRED = "STARRED";
    public static final String CONTACT_STATION = "STATION";
    public static final String CONTACT_LAST_CONTACTED = "LAST_CONTACTED";
    private static final String DB_TABLE = "EdacioTable";
    private static final String DB_STATION_TABLE = "StationTable";
    private static final int DB_VERSION = 1;
    
    //SQL COMMAND STRING
    private static final String DB_TABLE_CREATE =
        "CREATE TABLE " +
        DB_TABLE +
        " (" +
        KEY_ID +
        " integer primary key autoincrement, " +
        CONTACT_ID +
        " varchar (40) not null, " +
        CONTACT_NAME +
        " varchar (40) not null, " +
        CONTACT_PHONE +
        " varchar (40) not null, " +
        CONTACT_PHOTO_ID +
        " varchar (30), " + 
        CONTACT_STARRED +
        " integer not null, " +
        CONTACT_STATION +
        " varchar (40), " +
        CONTACT_LAST_CONTACTED +
        " varchar (40)); ";
  //SQL COMMAND STRING
    private static final String DB_STATION_TABLE_CREATE =
        "CREATE TABLE " +
        DB_STATION_TABLE +
        " (" +
        KEY_ID +
        " integer primary key autoincrement, " +
        CONTACT_STATION +
        " varchar (40) not null); ";
    
    //
    private static final String DB_TABLE_DROP =
        "DROP TABLE IF EXISTS " +
        DB_TABLE;

    //
    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper( Context context ) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        public void onCreate( SQLiteDatabase db ) {
            db.execSQL( DB_TABLE_CREATE );
            db.execSQL( DB_STATION_TABLE_CREATE );
            ContentValues storedValue1 = new ContentValues();
            storedValue1.put(CONTACT_STATION, "Everyone");
            ContentValues storedValue2 = new ContentValues();
            storedValue2.put(CONTACT_STATION, "Starred");
            db.insert(DB_STATION_TABLE, null, storedValue1);
            db.insert(DB_STATION_TABLE, null, storedValue2);
            
        }
        public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
            Log.w( TAG, "Upgrading database from version " +
                        oldVersion +
                        " to " +
                        newVersion +
                        ", which will destroy all old data" );
            db.execSQL( DB_TABLE_DROP );
            onCreate( db );
        }
    }
    
    public EdacioDB( Context c ) {
        mContext = c;
        open();
    }
    
    /*
    public static void setFirstRun( SQLiteDatabase db ) {
        ContentValues storedValues = new ContentValues();
        db.insert( DB_TABLE, null, storedValues );
    }
    
    //
    public void setFirstRun() {
        mDatabase.execSQL( DB_TABLE_DROP );
        mDatabase.execSQL( DB_TABLE_CREATE );
    }*/
    
    //Opening Database called when this class is created
    public EdacioDB open() throws SQLException {
        mDbHelper = new DatabaseHelper( mContext );
        mDatabase = mDbHelper.getWritableDatabase();
        return this;
    }

    //Closing Db for cleanup
    public void close() {
        mDbHelper.close();
    }
    
    //Inserting contact info into app database
    public void addContact(String id, String contactName, String phoneNumber, String photoID, String starred,String lastContacted) {
        ContentValues storedValues = new ContentValues();
        storedValues.put( CONTACT_ID, id );
        storedValues.put( CONTACT_NAME, contactName );
        storedValues.put( CONTACT_PHONE, phoneNumber );
        storedValues.put( CONTACT_PHOTO_ID, photoID );
        storedValues.put( CONTACT_STARRED, starred );
        storedValues.put( CONTACT_LAST_CONTACTED, lastContacted );
        mDatabase.insert(DB_TABLE, null, storedValues);
    }
    
  //Retrieving info from starred database
    public Cursor getContact() {
        Cursor c = mDatabase.query( DB_TABLE,
                new String[] {CONTACT_ID,CONTACT_NAME,CONTACT_PHONE,CONTACT_PHOTO_ID,CONTACT_STATION,CONTACT_STARRED,CONTACT_LAST_CONTACTED},
                null,
                null,
                null,
                null,
                null,
                "900" );
        return c;
    }
    
    //Retrieving info from starred database
    public Cursor getStarredContact() {
    	String selection = CONTACT_STARRED + " = '1'";
        Cursor c = mDatabase.query( DB_TABLE,
                new String[] {CONTACT_ID,CONTACT_NAME,CONTACT_PHONE,CONTACT_PHOTO_ID,CONTACT_STATION,CONTACT_STARRED,CONTACT_LAST_CONTACTED},
                selection,
                null,
                null,
                null,
                null,
                "900" );
        return c;
    }
    
    //Retrieving info from station contacts database
    public Cursor getStationContact(String stationname) {
    	String station = CONTACT_STATION + " = '" + stationname + "'";
        Cursor c = mDatabase.query( DB_TABLE,
                new String[] {CONTACT_ID,CONTACT_NAME,CONTACT_PHONE,CONTACT_PHOTO_ID,CONTACT_STATION,CONTACT_STARRED,CONTACT_LAST_CONTACTED},
                station,
                null,
                null,
                null,
                null,
                "900" );
        return c;
    }
    
    //Retrieving info from station contacts database
    public Cursor getStations() {
        Cursor c = mDatabase.query( DB_STATION_TABLE,
                new String[] {CONTACT_STATION},
                null,
                null,
                null,
                null,
                null,
                "100" );
        return c;
    }
    
  //Inserting Station info into a contact
    public void addStationToContact(String contactName,String stationName) {
        ContentValues storedValues = new ContentValues();
        storedValues.put(CONTACT_STATION, stationName);
        String whereClause = CONTACT_NAME + "=" + "'" + contactName + "';";
        mDatabase.update(DB_TABLE, storedValues, whereClause, null);
        String station = CONTACT_STATION + " = '" + stationName + "'";
        Cursor c = mDatabase.query( DB_STATION_TABLE,
                new String[] {CONTACT_STATION},
                station,
                null,
                null,
                null,
                null,
                "100" );
        if (c.getCount() == 0)
        	mDatabase.insert(DB_STATION_TABLE, null, storedValues);
    }
    
    private SharedPreferences getSharedPreferences(Context ctx)
    {
      return ctx.getSharedPreferences(PREFS_NAME, 0);
    }
    
    public void createDefaultPreferences(Context ctx) {
        SharedPreferences settings = getSharedPreferences(ctx);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Station", "Starred");
        editor.commit();
      }
    
    public void setStationPreference(Context ctx, String value)
    {
      SharedPreferences settings = getSharedPreferences(ctx);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString("Station", value);
      editor.commit();
    }
    public String getStationPreference(Context ctx)
    {
      SharedPreferences settings = getSharedPreferences(ctx);
      return settings.getString("Station", "Starred");
    }

}
