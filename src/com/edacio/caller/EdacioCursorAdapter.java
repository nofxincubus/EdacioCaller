package com.edacio.caller;

import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;



public class EdacioCursorAdapter extends SimpleCursorAdapter {
    private final Context context;
    private int layout;
    public EdacioCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to) {
    	super(context, layout, c, from, to);
        this.context = context;
        this.layout = layout;
    }


    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       Cursor c = getCursor();
       final LayoutInflater inflater = LayoutInflater.from(context);
       View v = inflater.inflate(layout, parent, false);
       int nameCol = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
       String name = c.getString(nameCol);
       nameCol = c.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
       String photoID = c.getString(nameCol);
       nameCol = c.getColumnIndex(ContactsContract.Contacts.STARRED);
       int starred = c.getInt(nameCol);
       return v;
     
    }
    
    public void bindView(View v, Context context, Cursor c) {
    	
    }
    
    public static Bitmap loadContactPhoto(ContentResolver cr, long  id) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        return BitmapFactory.decodeStream(input);
    }
}
