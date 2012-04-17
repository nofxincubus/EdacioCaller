package com.edacio.caller;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactListActivity extends Activity {
	
	private ListView mContactList;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	EdacioDB edaciodb;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        //setContentView(R.layout.listview);
	        mContactList = (ListView) findViewById(R.id.list);
	        edaciodb = new EdacioDB(this);
	        edaciodb.createDefaultPreferences(this);
	        populateContactList();
	    }
	 private void populateContactList() {
	        // Build adapter with contact entries
	        Cursor cursor = getContacts();
	        
	        try {
	            
	            while( cursor.moveToNext() ) {
	            	int nameCol = cursor.getColumnIndex(ContactsContract.Contacts._ID);
	                String id = cursor.getString(nameCol);
	            	nameCol = cursor.getColumnIndex(ContactsContract.Contacts.STARRED);
	                String starred = cursor.getString(nameCol);
	            	nameCol = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	                String name = cursor.getString(nameCol);
	                nameCol = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
	                String photoID = cursor.getString(nameCol);
	                nameCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	                String number = cursor.getString(nameCol);
	                nameCol = cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED);
	                String lastContacted = cursor.getString(nameCol);
	                edaciodb.addContact(id, name,number,photoID,starred,lastContacted);
	            }
	        } catch( CursorIndexOutOfBoundsException cioobe ) {
	            Log.e( "Edacio", cioobe.getMessage() );
	            //error happened...
	        }
	        Intent intent = null;
            intent = new Intent( getApplicationContext(), TutorialActivity.class );
            startActivity( intent );
            finish();

	        /*
	        String[] fields = new String[] {
	                ContactsContract.Data.DISPLAY_NAME
	        };
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contactitem, cursor,
	                fields, new int[] {R.id.contactEntryText});
	        mContactList.setAdapter(adapter);*/
	    }
	 
	 private Cursor getContacts()
	    {
	        // Run query
	        //Uri uri = ContactsContract.Contacts.CONTENT_URI;
	        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	        String[] projection = new String[] {
	                ContactsContract.CommonDataKinds.Phone._ID,
	                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	                ContactsContract.CommonDataKinds.Phone.NUMBER,
	                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
	                ContactsContract.CommonDataKinds.Phone.STARRED,
	                ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED
	        };
	        String selection = null;
	        //String selection = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " = '" +
	        //        "1" + "'";
	        String[] selectionArgs = null;
	        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	        
	        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	    }
}
