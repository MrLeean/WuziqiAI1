package com.example.Wuziqi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.Wuziqi.helper.ThemeChangeUtil;

public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{

	private WuziqiPanel_AI wuziqiPanel_ai;
	public SharedPreferences mSharePreference;
	public SharedPreferences.Editor editor;
	public String userName;
	private Vibrator vibe;

	public String backgroundColor;
	public Boolean nightMode;

	public int fontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

		/**
		 * 获取theme缺省值，加载合适的主题
		 */
		//theme = Integer.parseInt(mSharePreference.getString(getString(R.string.preference_theme), "1"));
		Boolean nightOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("nightMode", false);
		String theme = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(getString(R.string.preference_theme), "1");
		ThemeChangeUtil.initTheme(nightOn, theme, this, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		//初始化棋盘View
		WuziqiPanel_AI wuziqiPanel_ai = (WuziqiPanel_AI) findViewById(R.id.wuziqi_panel);
		Context mContext = getApplicationContext();
		Activity mActivity = MainActivity.this;
		mSharePreference = PreferenceManager.getDefaultSharedPreferences(mContext);
		editor = mSharePreference.edit();
		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_1);

		//开启震动控制
		vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		Button btn_return = (Button) findViewById(R.id.wuziqi_panel_buttons_return);
		Button btn_replay = (Button) findViewById(R.id.wuziqi_panel_buttons_replay);

		//返回主界面
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(7);
				return_alert(0);
				/*Intent int1 = new Intent(MainActivity.this, WelcomeActivity.class);
				startActivity(int1);*/
			}
		});
		//重新开始新游戏
		btn_replay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(7);
				//弹出提示框要求用户再次确认
				return_alert(1);
			}
		});

		//初始化mSharePreference，获取默认值
		mSharePreference = PreferenceManager.getDefaultSharedPreferences(this);

		//设置第一次启动后加载默认值
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);

		userName = mSharePreference.getString(getString(R.string.preference_user_name), "玩家1");

		//定义棋盘密度（网格数）
		int lineNum = Integer.parseInt(mSharePreference.getString(getString(R.string.preference_line_num), "10"));
		wuziqiPanel_ai.defineLine(lineNum);

		//Dialog弹窗字体大小
		int pref_font_size = Integer.parseInt(mSharePreference.getString("fontSize", "1"));
		if (pref_font_size == 1){
			fontSize = 20;
		}else if(pref_font_size == 2){
			fontSize = 22;
		}else{
			fontSize = 24;
		}

		//定义棋盘背景颜色
		backgroundColor = mSharePreference.getString(getString(R.string.preference_bg), "1");
		int style = Integer.parseInt(backgroundColor);
		wuziqiPanel_ai.drawBackground(style);

		/*//夜间模式
		nightMode = mSharePreference.getBoolean("nightMode", false);
		wuziqiPanel_ai.nightMode = nightMode;
		if(nightMode){
			int style_night = 0;
			wuziqiPanel_ai.drawBackground(style_night);
			mRelativeLayout.setBackgroundColor(0xFF2B2B2B);
		}*/
    }

	/**
	 * 重新开始游戏代码
	 */
	public void reStart(){
		Intent int2 = getIntent();
		overridePendingTransition(0,0);
		finish();
		overridePendingTransition(0,0);
		startActivity(int2);
	}

	//关闭系统back键功能
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){

			/**
			 * 重新开始新游戏 这段代码旨在重新开启指定Activity而非重启整个APP
			 */
		case R.id.menu_retry:
			Intent replay = getIntent();
			overridePendingTransition(0, 0);
			finish();
			overridePendingTransition(0, 0);
			startActivity(replay);
			break;
		case R.id.menu_setting:
			Intent intent = new Intent(MainActivity.this, Preference.class);
			startActivity(intent);
			break;
		default:
			//对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void return_alert(int judge){
		/**
		 * 判断调用提示窗口接口的来源
		 * “0”：退出游戏界面时
		 * “1”：重新开始游戏时
		 */
		int judgeNum = judge;

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setInverseBackgroundForced(true);
		if (judgeNum == 0) {
			builder.setTitle(R.string.alertTitle).setMessage(R.string.alertMessage);
		} else {
			builder.setTitle(R.string.alertTitle).setMessage(R.string.alertMessage_1);
		}
		setPositiveButton(builder, judgeNum);
		setNegativeButton(builder);

		/**
		 * 设置AlertDialog字体大小
		 */
		AlertDialog dialog = builder.create();
		dialog.show();
		//控制 dialog 按钮字体大小
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(fontSize);
		dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(fontSize);
		TextView textView = (TextView) dialog.findViewById(android.R.id.message);
		textView.setTextSize(fontSize);
	}

	private AlertDialog.Builder setPositiveButton(final AlertDialog.Builder builder, final int judge) {

		return builder.setPositiveButton(R.string.alertQuit, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if ( judge ==0 ) {
					Intent int1 = new Intent(MainActivity.this, WelcomeActivity.class);
					startActivity(int1);
					finish();
				} else {
					reStart();
				}
			}
		});
	}

	private AlertDialog.Builder setNegativeButton(final AlertDialog.Builder builder) {
		return builder.setNegativeButton(R.string.alertCancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
	}
}




















