package com.edacio.caller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RadioActivity extends Activity implements OnClickListener{
	
	boolean handleOpened = false;
	int randomInt = 0;
	private TextView nameView;
	private TextView numberView;
	private TextView contactStationView;
	private ImageView photoView;
	private ListView stationListView;
	private ArrayAdapter<String> stationListAdapter;
	ArrayList<String> stationNames = new ArrayList<String>();;
	EdacioDB db;
	
	public class ContactList {
		public String id;
		public String name;
		public String number;
		public String photoID;
		public String station;
		public String LastContacted;
		public ContactList(String id, String name, String number, String station, String photoID, String lastContacted){
			this.id = id;
			this.name = name;
			this.number = number;
			this.photoID = photoID;
			this.station = station;
			this.LastContacted = lastContacted;
		}
	}
	ArrayList<ContactList> currentList = new ArrayList<ContactList>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.radio);
	    nameView = (TextView) findViewById(R.id.name);
	    numberView = (TextView) findViewById(R.id.phonenumber);
		photoView = (ImageView) findViewById(R.id.contactPicturesView);
		stationListView = (ListView) findViewById(R.id.stationListView);
		contactStationView = (TextView) findViewById(R.id.contactStationView);
	    this.getStations();
	    this.loadStation();
	    findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.reject).setOnClickListener(this);
        findViewById(R.id.addStations).setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId() ) {
        case R.id.call:
        	TextView phoneView = (TextView)findViewById(R.id.phonenumber);
        	try{
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				String phonenumber = phoneView.getText().toString();
				callIntent.setData(Uri.parse("tel:" + phonenumber));
				startActivity(callIntent);
        	} catch (ActivityNotFoundException e) {
                Log.e("Edacio Caller", "Call failed", e);
            }
        	break;
        case R.id.reject:
    	    Random randomGenerator = new Random();
    	    randomInt = randomGenerator.nextInt(currentList.size());
    	    setViews();
        	break;
        case R.id.addStations:
        	EditText edtx = (EditText) findViewById(R.id.addStationEdit);
        	Editable stationName = edtx.getText();
        	if (stationName.length() > 0){
        		db.addStationToContact(nameView.getText().toString(), stationName.toString());
        		stationListAdapter.add(stationName.toString());
        	}
        	else
        		Toast.makeText(this, "you are Awesome!!", Toast.LENGTH_SHORT).show();
        	break;
        case R.id.contactStationView:
        	if (stationNames.size() == 2){
        		
        	} else{
        		ArrayList<String> tempList = new ArrayList<String>();
        		for (int j = 2;j< stationNames.size();j++){
        			tempList.add(stationNames.get(j));
        		}
        		stationListAdapter = new ArrayAdapter<String>(this,R.layout.stationitem,R.id.stationText,tempList);
	       		 stationListView.setAdapter(stationListAdapter);
	       		 stationListView.setOnItemClickListener(new OnItemClickListener() {
       				public void onItemClick(AdapterView<?> arg0, View arg1,
       						int position, long arg3) {
       					String station = stationNames.get(position);
       					db.setStationPreference(RadioActivity.this, station);
       					loadStation();
       				}
       			});
        		
        	}
        	break;
        default:
        	break;
		}
	}
	
	public void nextCaller(){
		//TODO: Move to next person
	}
	
	public void onBackPressed(){
        //This is to prevent user from accidently exiting the app
        //pressing Home will exit the app
    }
	private void setViews(){
	    nameView.setText(currentList.get(randomInt).name);
	    
	    numberView.setText(currentList.get(randomInt).number);
	    if (currentList.get(randomInt).station == ""){
	    	contactStationView.setText("Add to Station");
	    }else 
	    	contactStationView.setText(currentList.get(randomInt).station);
	    
	    contactStationView.setText("Add to station");
	    if (currentList.get(randomInt).id.length() > 0)
	    	photoView.setImageBitmap(loadContactPhoto(this.getContentResolver(),currentList.get(randomInt).id));
	    else
	    	photoView.setBackgroundResource(R.drawable.ic_launcher);
	}
	public InputStream openPhoto(long contactId) {
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
	     
	     //Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
	     //InputStream photoStream = 
	     /*
	     Cursor cursor = getContentResolver().query(photoUri,
	          new String[] {Contacts.Photo.DATA15}, null, null, null);
	     if (cursor == null) {
	         return null;
	     }
	     try {
	         if (cursor.moveToFirst()) {
	             byte[] data = cursor.getBlob(0);
	             if (data != null) {
	                 return new ByteArrayInputStream(data);
	             }
	         }
	     } finally {
	         cursor.close();
	     }*/
	     return null;
	 }

	public Bitmap loadContactPhoto(ContentResolver cr, String  id) {
		Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, id);
		//Uri contactUri = Contacts.CONTENT_LOOKUP_URI;
		
        InputStream input = Contacts.openContactPhotoInputStream(getContentResolver(), contactUri);
        InputStream test = null;
        if (input == test) {
            return null;
        } else
        	return BitmapFactory.decodeStream(input);
    }
	
	private void getStations() {
		
		db = new EdacioDB(this);
		Cursor stationCursor = db.getStations();
		
		 try {
	            while( stationCursor.moveToNext()) {
	            	int nameCol = stationCursor.getColumnIndex(db.CONTACT_STATION);
	            	stationNames.add(stationCursor.getString(nameCol));
	            }
		 } catch( CursorIndexOutOfBoundsException cioobe ) {
	            Log.e( "Edacio", cioobe.getMessage() );
	            //error happened...
	        }
		 
		 
		 stationListAdapter = new ArrayAdapter<String>(this,R.layout.stationitem,R.id.stationText,stationNames);
		 stationListView.setAdapter(stationListAdapter);
		 stationListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					String station = stationNames.get(position);
					db.setStationPreference(RadioActivity.this, station);
					loadStation();
				}
			});
	    //Check Station
	   
	   
	}
	
	private void loadStation(){
		currentList.clear();
		String name = "";
	    String photoID = "";
	    String id = "";
	    String number = "";
	    String lastContacted = "";
	    String contStation = "";
	    
		Cursor cursor;
	    String station = db.getStationPreference(this);
	    TextView currStationView = (TextView) findViewById(R.id.stationName);
	    currStationView.setText("Current : " + station);
	    if (station.contains("Starred"))
	    	cursor = db.getStarredContact();
	    else if (station.contains("Everyone"))
	    	cursor = db.getContact();
	    else
	    	cursor = db.getStationContact(station);
	    try {
            while( cursor.moveToNext()) {
            	int nameCol = cursor.getColumnIndex(db.CONTACT_NAME);
                name = cursor.getString(nameCol);
                nameCol = cursor.getColumnIndex(db.CONTACT_ID);
                id = cursor.getString(nameCol);
                nameCol = cursor.getColumnIndex(db.CONTACT_PHOTO_ID);
                //String phID = cursor.getString(nameCol);
                photoID = cursor.getString(nameCol);
                /*if (phID == null)
                	photoID = (long) -1;
                else
                	photoID = Long.valueOf(phID);*/
                nameCol = cursor.getColumnIndex(db.CONTACT_STATION);
                contStation = cursor.getString(nameCol);
                if (contStation == null){
                	contStation = "";
                }
                nameCol = cursor.getColumnIndex(db.CONTACT_PHONE);
                number = cursor.getString(nameCol);
                nameCol = cursor.getColumnIndex(db.CONTACT_LAST_CONTACTED);
                lastContacted = cursor.getString(nameCol);
                currentList.add(new ContactList(id, name,number,contStation,photoID,lastContacted));
            }
            
            
        } catch( CursorIndexOutOfBoundsException cioobe ) {
            Log.e( "Edacio", cioobe.getMessage() );
            //error happened...
        }
	    Random randomGenerator = new Random();
	    randomInt = randomGenerator.nextInt(currentList.size());
	    setViews();
	}
}
