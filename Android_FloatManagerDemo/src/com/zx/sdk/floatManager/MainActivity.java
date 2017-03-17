package com.zx.sdk.floatManager;

import android.R.string;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends Activity implements OnClickListener {
	
	private String TAG = "----MainActivity----";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
         findViewById(R.id.show_float).setOnClickListener(this);
         findViewById(R.id.hide_float).setOnClickListener(this);
         
         Log.d(TAG, "onCreate");
         FloatUtil.getInstance(this).showFloatView();
   
         
         
         
    }


    @Override
    protected void onDestroy() {
    	Log.d(TAG, "onDestroy");
//    	FloatUtil.getInstance(this).removeFloatView();
    	FloatUtil.getInstance(this).onDeleteFloat();
    	
        super.onDestroy();
    }

    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_float:
			 FloatUtil.getInstance(this).showFloatView();
			break;

		case R.id.hide_float:
			FloatUtil.getInstance(this).removeFloatView();
			break;
		}
		
	}
}

