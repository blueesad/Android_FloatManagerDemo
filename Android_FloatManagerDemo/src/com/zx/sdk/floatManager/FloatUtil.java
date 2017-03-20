package com.zx.sdk.floatManager;

import android.R.integer;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatUtil {
	private static final String TAG = "-----FloatUtil-----";
	private static FloatUtil instance = null;
	private Context mContext;
	private ImageView mView;
	private WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;
	private float oldX; // 相对于自身左上角的x坐标值
	private float oldY;
	private int screen_widht; // 屏幕宽度
	private int screen_height;
	private int rawX = 0; // 相对于屏幕左上角的x坐标值
	private boolean isShowFloat = false; // 浮标是否显示标记
	private boolean isMove = false;
	public static final int MESSAGE_START_ANIMATION = 0;
	public static final int MESSAGE_CLEAR_ANIMATION = 1;
	public static final int MESSAGE_RESET_ANIMATION = 2;
	private TimeCount count;
	private float xInScreen;
	private float yInScreen;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_START_ANIMATION:
				startAnimation();
				break;
			case MESSAGE_CLEAR_ANIMATION:
				if (count.isStart) {
					count.cancel();
				}
				mView.clearAnimation();
				break;
			case MESSAGE_RESET_ANIMATION:
				if (count.isStart) {
					count.cancel();
					count.start();
				} else {
					count.start();
				}

				break;

			default:
				break;
			}

		};
	};

	public static synchronized FloatUtil getInstance(Context context) {
		if (instance == null) {
			instance = new FloatUtil(context);
		}
		return instance;
	}

	public FloatUtil(Context context) {
		init(context);
	}

	private void init(Context context) {
		Log.d(TAG + "init", "init");
		this.mContext = context;
		this.count = new TimeCount(5000L, 1000L);

		createFloatView();
		// 横屏
		((Activity) context)
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		isShowFloat = true;
	}

	public void createFloatView() {
		Log.d(TAG + "createFloatView", "createFloatView");
		Log.d(TAG, "mContext = " + this.mContext);
		// 得到窗口管理器
		this.mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		// 屏幕宽度
		screen_widht = mWindowManager.getDefaultDisplay().getWidth();
		screen_height = mWindowManager.getDefaultDisplay().getHeight();
		Log.d(TAG, "mWindowManager = " + this.mWindowManager);

		// 创建一个用来做镜像(可以理解为悬浮窗)的ImageView
		this.mView = new ImageView(mContext);
		// iv.setOnTouchListener(mContext);
		this.mView.setImageResource(R.drawable.ic_launcher);

		// 得到参数对象，用来设置给上面创建的镜像控件
		this.mParams = new WindowManager.LayoutParams();

		this.mParams.type = 2;
		Log.d(TAG, "浮标type = " + this.mParams.type);
		// 镜像的宽，这里设置为由内容填充
		this.mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// 镜像的高，这里设置为由内容填充
		this.mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// 调整悬浮窗口至左上角
		this.mParams.gravity = Gravity.LEFT | Gravity.CENTER;
		// 设置镜像的透明度
		this.mParams.alpha = 1F;
		// 设置镜像的背景为透明的，如果不设置，背景是黑色的
		// this.mParams.format = PixelFormat.TRANSLUCENT;
		this.mParams.format = 1;
		// 设置这个镜像不抢占焦点
		// this.mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		this.mParams.flags = 40;
		// 镜像的初始位置 从屏幕左上角开始，但是不包括状态栏
		this.mParams.x = 0;
		this.mParams.y = 0;

		// 吧镜像添加到窗口中，这个窗口
		this.mWindowManager.addView(mView, mParams);
		mHandler.sendEmptyMessage(MESSAGE_RESET_ANIMATION);
		this.mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent motionEvent) {
				switch (motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isMove = false;
					Log.d(TAG, "按下" + " isMove =" + isMove);
					// 记录按下的坐标
					oldX = motionEvent.getX(); // 获取相对于自身左上角的x坐标值
					oldY = motionEvent.getY();

					// 恢复mView的位置、透明度
					recoveryView();

					// 清除动画
					mHandler.sendEmptyMessage(MESSAGE_CLEAR_ANIMATION);

					break;
				case MotionEvent.ACTION_MOVE:
					isMove = true;
					
					//用下列方法会抖动
//					Log.d(TAG, "移动" + " x =" + motionEvent.getX() + "  y = "
//							+ motionEvent.getY() + " isMove =" + isMove);
//					// 改变镜像的x 和 y的值
//					mParams.x += (int) (motionEvent.getX() - oldX);
//					mParams.y += (int) (motionEvent.getY() - oldY);
//					// 更新镜像
//					mWindowManager.updateViewLayout(mView, mParams);

					xInScreen = motionEvent.getRawX();
					yInScreen = (motionEvent.getRawY() - screen_height/2);
					mParams.x = ((int)(xInScreen - oldX));
				    mParams.y = ((int)(yInScreen - oldY));
				    mWindowManager.updateViewLayout(mView, mParams);
				    mHandler.sendEmptyMessage(MESSAGE_CLEAR_ANIMATION);

					break;

				case MotionEvent.ACTION_UP:
					Log.d(TAG, "抬起" + " isMove =" + isMove);
					rawX = (int) motionEvent.getRawX() - mView.getWidth() / 2; // 获取相对于屏幕左上角的x坐标值
					/**
					 * 抬起时浮标靠边的动画
					 */
					ObjectAnimator animator = ObjectAnimator.ofFloat(mView,
							"xxx", 0, 1).setDuration(300);
					animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							float progress = Float.parseFloat(animation
									.getAnimatedValue().toString());
							// 抬起时浮标自动使其靠边
							if (rawX > screen_widht / 2) {
								int rightX = screen_widht - mView.getWidth();
								mParams.x = (int) (rawX + rightX * progress - rawX
										* progress); // 靠屏幕右边 rightX

							} else {

								mParams.x = (int) (rawX - rawX * progress); // 靠屏幕左边
																			// 0
							}
							if (mView.getParent() != null) {
								mWindowManager.updateViewLayout(mView, mParams);
							}

						}
					});
					animator.start();

					break;
				}
				mHandler.sendEmptyMessage(MESSAGE_RESET_ANIMATION);
				return false;
			}
		});
		mView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isMove) {
					Log.d(TAG, "-----点击浮标,进入用户中心-----");
					Intent intent = new Intent(FloatUtil.this.mContext,
							FloatActivity.class);
					// intent.addisShowFloats(268435456);
					mContext.startActivity(intent);
					// Log.d(TAG, "mView.getParent = " + mView.getParent());
					removeFloatView();
				}
			}
		});

		Log.d(TAG, "mWindowManager:" + mWindowManager + "----" + "mView:"
				+ mView);
	}

	public void showFloatView() {

		Log.d(TAG + "showFloatView", "显示浮标");
		Log.d(TAG, "mView = " + mView);
		Log.d(TAG, "mWindowManager = " + mWindowManager);
		Log.d(TAG, "mParams = " + mParams);

		if (!isShowFloat) {
			if (this.mView != null) {
				if (mView.getParent() == null) {
					// createFloatView();

					// this.mWindowManager = (WindowManager)
					// mContext.getSystemService(Context.WINDOW_SERVICE);

					this.mWindowManager.addView(this.mView, this.mParams);
				} else {
					this.mWindowManager.updateViewLayout(mView, mParams);
				}
			}
			isShowFloat = true;
			mHandler.sendEmptyMessage(MESSAGE_RESET_ANIMATION);

			// 恢复mView的位置、透明度
			recoveryView();
		}
	}

	private void recoveryView() {
		mView.setX(0);
		mParams.alpha = 1F;
		mWindowManager.updateViewLayout(mView, mParams);
	}

	public void removeFloatView() {
		Log.d("-------removeFloatView--------", "mContext------->" + mContext);
		if (isShowFloat) {
			Log.d(TAG + "removeFloatView", "隐藏浮标");
			mHandler.sendEmptyMessage(MESSAGE_CLEAR_ANIMATION);
			mWindowManager.removeViewImmediate(mView);
			isShowFloat = false;

		}

	}

	public void onDeleteFloat() {
		Log.d("-------onDeleteFloat--------", "mContext------->" + mContext);
		if (isShowFloat) {
			mWindowManager.removeViewImmediate(mView);
		}

		mWindowManager = null;
		mView = null;
		instance = null;
	}

	/**
	 * 无操作时自动掩藏浮标一半
	 */
	private void startAnimation() {
		Log.e(TAG + "startAnimation", "---startAnimation---");
		if (mView == null) {
			return;
		}
		ObjectAnimator animator = ObjectAnimator.ofFloat(mView, "XXX", 0, 1)
				.setDuration(550);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float progress = Float.parseFloat(animation.getAnimatedValue()
						.toString());
				mView.setRotation(720 * progress);
				int halfWidth = mView.getWidth() / 2;
				if (mParams.x == 0) {
					mView.setTranslationX((int) (0 - halfWidth * progress)); // 靠屏幕左边
				} else {
					mView.setTranslationX((int) (0 + halfWidth * progress)); // 靠屏幕右边
				}
				mParams.alpha = (float) (1 - progress * 0.4);
				if (mView.getParent() != null) {
					mWindowManager.updateViewLayout(mView, mParams);
				}

			}
		});
		animator.start();

	}

	private class TimeCount extends CountDownTimer {
		boolean isStart = false;

		/**
		 * @param millisInFuture
		 *            设置倒计时的总时间
		 * @param countDownInterval
		 *            倒计时时的时间间隔
		 */
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void onFinish() {
			FloatUtil.this.mHandler.sendEmptyMessage(MESSAGE_START_ANIMATION);
			this.isStart = false;
		}

		public void onTick(long millisUntilFinished) {
			Log.e("-----TimeCount-----", "---倒计时---：" + millisUntilFinished
					/ 1000L + "秒");
			this.isStart = true;
		}
	}
	
	//获取 状态栏 高度
	  private float getStatusBarHeight()
	  {
	    float statusBarHeight = 0.0F;
	    Rect frame = new Rect();
	    this.mView.getWindowVisibleDisplayFrame(frame);
	    statusBarHeight = frame.top;
	    return statusBarHeight;
	  }
}
