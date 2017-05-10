package com.example.Wuziqi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Wuziqi.helper.ThemeChangeUtil;

import java.io.File;
import java.util.Locale;

public class WelcomeActivity extends Activity {

	private WuziqiPanel_AI wuziqiPanel_ai;
	private MainActivity mainActivity;
	private ScoreList scoreList;
	private ScoreRecord scoreRecord;
	private Context mContext;

	private SharedPreferences mSharePreference;
	private SharedPreferences.Editor editor;

	private Button button_play;
	private Button button_settings;
	private Button button_multiplay;
	private Button button_history;

	private Vibrator vibe;

	public String[] high_score_list = new String[10];

	private int fontSize;
	private int LevelOfUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/**
		 * 获取theme缺省值，加载合适的主题
		 */
		//theme = Integer.parseInt(mSharePreference.getString(getString(R.string.preference_theme), "1"));
		Boolean nightOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("nightMode", false);
		String theme = PreferenceManager.getDefaultSharedPreferences(this)
				.getString(getString(R.string.preference_theme), "1");
		ThemeChangeUtil.initTheme(nightOn, theme, this, 1);

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_activity);
		//ImageView iv = (ImageView) findViewById(R.id.level_indicator);
		TextView badgeIndicator = (TextView) findViewById(R.id.level_indicator);
		setLogo();

		/*
		初始化用户等级
		 */
		/*setLevel();*/
		//AchievementRecord.initAchievement(setLevel(), iv);
		setBadge(setLevel(), badgeIndicator);


		mContext = getApplicationContext();
		scoreRecord = new ScoreRecord(mContext);

		//初始化mSharePreference，获取默认值
		mSharePreference = PreferenceManager.getDefaultSharedPreferences(this);
		//设置第一次启动后加载默认值
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);
		//字体大小
		int pref_font_size = Integer.parseInt(mSharePreference.getString("fontSize", "1"));
		if (pref_font_size == 1){
			fontSize = 20;
		}else if(pref_font_size == 2){
			fontSize = 22;
		}else{
			fontSize = 24;
		}

		vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		/**
		 * 创建该软件在外部存储空间下的文件夹
		 */
		File sd = Environment.getExternalStorageDirectory();
		String path = sd.getPath() + "/Wuziqi/Screenshot";
		File file = new File(path);
		if(!file.exists()){
			file.mkdir();
		}

		button_play = (Button) findViewById(R.id.btn_play);
		button_settings = (Button) findViewById(R.id.btn_settings);
		button_multiplay = (Button) findViewById(R.id.btn_play_2);
		button_history = (Button) findViewById(R.id.btn_history);

		button_play.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(10);
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

		button_multiplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(10);
				Intent intent = new Intent(WelcomeActivity.this, MainActivityMultiPlayer.class);
				startActivity(intent);
				finish();
			}
		});

		button_history.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(7);
				Intent intent = new Intent(WelcomeActivity.this, GridViewActivity.class);
				startActivity(intent);
			}
		});

		button_settings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(7);
				Intent intent = new Intent(WelcomeActivity.this, Preference.class);
				startActivity(intent);
			}
		});

	}

	/*初始化勋章系统*/
	private void setBadge(int i, TextView badgeIndicator) {
		switch (i){
			case 0:
				badgeIndicator.setText("1");
				break;
			case 1:
				badgeIndicator.setText("2");
				break;
			case 2:
				badgeIndicator.setText("3");
				break;
			case 3:
				badgeIndicator.setText("4");
				break;
            case 4:
                badgeIndicator.setText("5");
                break;
            case 5:
                badgeIndicator.setText("6");
                break;
            case 6:
                badgeIndicator.setText("7");
                break;
			default:
				badgeIndicator.setText("1");
				break;
		}
	}

	private int setLevel() {
		SharedPreferences sharePreferences = getSharedPreferences("SP_wuziqi", Context.MODE_PRIVATE);
		int gameFreq = sharePreferences.getInt("Game_freq",0);
		if (gameFreq<=10){
			LevelOfUser = 0;
		}else if (gameFreq>10 && gameFreq<=20){
			LevelOfUser = 1;
		}else if (gameFreq>20 && gameFreq<=30){
			LevelOfUser = 2;
		}else if (gameFreq>30 && gameFreq<=40){
			LevelOfUser = 3;
		}else if (gameFreq>40 && gameFreq<=50){
            LevelOfUser = 4;
        } else if (gameFreq>50 && gameFreq<=60){
            LevelOfUser = 5;
        }else{
            LevelOfUser = 6;
        }
		return LevelOfUser;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){

			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setInverseBackgroundForced(true);
			builder.setTitle(R.string.wel_quit_dialog_title)
					.setMessage(R.string.wel_quit_dialog_message);
			setPositiveButton(builder);
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
		return false;
	}

	private AlertDialog.Builder setPositiveButton(final AlertDialog.Builder builder) {
		return builder.setPositiveButton(R.string.alertQuit, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
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

	private void setLogo(){
		ImageView imageView = (ImageView) findViewById(R.id.wel_logo);
		String currentLanguage = Locale.getDefault().getLanguage();
		if ("zh".equals(currentLanguage)){
			imageView.setImageResource(R.drawable.logo_zh);
		}else{
			imageView.setImageResource(R.drawable.logo_en);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		high_score_list = scoreRecord.getArray(this, high_score_list.length);
	}


}
