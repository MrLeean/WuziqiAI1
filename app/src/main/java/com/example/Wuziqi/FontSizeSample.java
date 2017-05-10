package com.example.Wuziqi;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.Wuziqi.helper.ThemeChangeUtil;

/**
 * Created by Administrator on 2016/9/19.
 */
public class FontSizeSample extends Activity{

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
        setContentView(R.layout.font_size_sample);
    }
}
