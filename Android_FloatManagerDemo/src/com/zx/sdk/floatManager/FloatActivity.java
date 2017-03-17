package com.zx.sdk.floatManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class FloatActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "-----FloatActivity-----";
	private Activity instance;
	private ImageView cancel;
	private ImageView circel_img;
	private RelativeLayout infor_ll;
	private TextView infor_count;
	private LinearLayout gift;
	private LinearLayout charge;
	private LinearLayout customer_service;
	private LinearLayout forum;
	private LinearLayout share;
	private RelativeLayout notice;
	private RelativeLayout user_center_ll;
	private LinearLayout img_ll;
	private TextView user_center;
	private TextView ttb_balance;
	private TextView yxb_balance;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_float);
		Log.d(TAG, "onCreate");
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		cancel = (ImageView) findViewById(R.id.float_cancel);
		cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.float_cancel:
			FloatUtil.getInstance(this).showFloatView();
		    finish();
			break;

		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		FloatUtil.getInstance(this).showFloatView();
		super.onDestroy();
	}
}
