package com.edacio.caller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class TutorialActivity extends Activity implements OnClickListener{
	
	boolean handleOpened = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tutorial);
	    findViewById(R.id.start).setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch( v.getId() ) {
	        case R.id.start:
	        	Intent intent = null;
	            intent = new Intent( getApplicationContext(), RadioActivity.class );
	            startActivity( intent );
	            finish();
	        	break;
	        default:
	        	break;
		}
	}
	
	public void onBackPressed(){
        //This is to prevent user from accidently exiting the app
        //pressing Home will exit the app
    }
	
}
