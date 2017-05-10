package com.example.Wuziqi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.Wuziqi.adapter.FullScreenImageAdapter;
import com.example.Wuziqi.helper.ThemeChangeUtil;
import com.example.Wuziqi.helper.Utils;

public class FullScreenViewActivity extends Activity {

	private Utils utils;

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
		setContentView(R.layout.activity_fullscreen_view);

		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		utils = new Utils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);

		FullScreenImageAdapter adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
				utils.getFilePaths());

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);

		//更新当前显示图片的标题名
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				refreshIndicator(position);
			}

			//刷新计时记步器
			public void refreshIndicator(int position) {
				String fileName = utils.getFileNames(position);
				String time;
				String step;
				int index_ = fileName.indexOf("_");
				int indexPot = fileName.indexOf(".");

				TextView timeText = (TextView) findViewById(R.id.fullSV_indicator_time);
				TextView stepText = (TextView) findViewById(R.id.fullSV_indicator_step);

				time = fileName.substring(0, (index_));
				step = fileName.substring((index_+1), (indexPot));

				timeText.setText(time + getResources().getString(R.string.alertMIAO));
				stepText.setText(step + getResources().getString(R.string.alertBUSHU));
			}

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	//关闭系统back键功能
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return false;
	}

}
