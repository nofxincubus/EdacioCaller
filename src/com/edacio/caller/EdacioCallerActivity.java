package com.edacio.caller;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class EdacioCallerActivity extends Activity {
    /** Called when the activity is first created. */
	private Handler splashHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = null;
            intent = new Intent( getApplicationContext(), ContactListActivity.class );
            startActivity( intent );
            super.handleMessage( msg );
            finish();
         }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Message msg = new Message();
        msg.what = 1;
        splashHandler.sendMessageDelayed(msg,  2000 );
    }
}