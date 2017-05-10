package com.example.Wuziqi;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.Wuziqi.adapter.GridViewImageAdapter;
import com.example.Wuziqi.helper.AppConstant;
import com.example.Wuziqi.helper.ThemeChangeUtil;
import com.example.Wuziqi.helper.Utils;
import com.example.Wuziqi.preference.MyPreferences;

import java.util.ArrayList;
import java.util.Locale;

public class GridViewActivity extends Activity {

	private Utils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	private Button btn_return;
	private Vibrator vibe;

	private int guideResourceId = 0;

	@Override
	protected void onStart() {
		super.onStart();
		//添加引导页
		addGuideImage();
	}

	/**
	 * 添加引导图片
	 */
	private void addGuideImage() {
		View view = getWindow().getDecorView().findViewById(R.id.history_gridview_root_container);
		if (view == null){
			return;
		}
		if (MyPreferences.activityIsGuided(this, this.getClass().getName())){
			return;
		}
		ViewParent viewParent = view.getParent();
		if (viewParent instanceof FrameLayout){
			final FrameLayout frameLayout = (FrameLayout) viewParent;
			if (guideResourceId != 0){
				final ImageView guideImage = new ImageView(this);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				guideImage.setLayoutParams(params);
				guideImage.setScaleType(ImageView.ScaleType.FIT_XY);
				guideImage.setImageResource(guideResourceId);
				guideImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						frameLayout.removeView(guideImage);
						MyPreferences.setIsGuided(getApplicationContext(),
								GridViewActivity.this.getClass().getName());
					}
				});
				frameLayout.addView(guideImage);
			}
		}
	}

	/**子类在onCreate中调用，设置引导图片的资源id
	 *并在布局xml的根元素上设置android:id="@id/my_content_view"
	 * @param resId
     */
	protected void setGuideResId(int resId){
		this.guideResourceId = resId;
	}

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
		setContentView(R.layout.activity_grid_view);

		/**
		 * 加载引导图片（仅限APP第一次启动时）
		 * 先判断当前系统语言
		 */
		//加载引导图片（仅限APP第一次启动时）
		isLunarSetting();

		gridView = (GridView) findViewById(R.id.grid_view);
		vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		//为gridView注册上下文菜单
		unregisterForContextMenu(gridView);

		btn_return = (Button) findViewById(R.id.btn_history_gridview_return);
		btn_return.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(7);
				finish();
			}
		});

		utils = new Utils(this);

		// Initilizing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths();

		// Gridview adapter
		adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
				columnWidth);

		// setting grid view adapter
		gridView.setAdapter(adapter);
	}

	/**
	 * 根据当前系统语言，加载不同语言版本的引导图片
	 */
	private void isLunarSetting() {
		String language = getLanguageEnv();

		if (language != null
				&& (language.trim().equals("zh-CN") || language.trim().equals("zh-TW"))){
			setGuideResId(R.drawable.guide_zh1);
		}else{
			setGuideResId(R.drawable.guide_en1);
		}
	}
	private String getLanguageEnv() {
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String country = l.getCountry().toLowerCase();
		if ("zh".equals(language)) {
			if ("cn".equals(country)) {
				language = "zh-CN";
			} else if ("tw".equals(country)) {
				language = "zh-TW";
			}
		} else if ("en".equals(language)) {
			if ("us".equals(country)) {
				language = "en-US";
			} else if ("UK".equals(country)) {
				language = "en-UK";
			}
		}
		return language;
	}
	/**
	 * 根据当前系统语言，加载不同语言版本的引导图片 （结束）
	 */

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstant.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

		gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
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