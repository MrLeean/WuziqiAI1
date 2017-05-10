package com.example.Wuziqi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.example.Wuziqi.helper.ThemeChangeUtil;

/**
 * Created by Administrator on 2016/7/5.
 */
public class Preference extends PreferenceActivity {
    Context mContext;
    SharedPreferences mSharePreference;
    SharedPreferences.Editor editor;
    private boolean nightMode;
    private PreferenceScreen mPreferenceScreen;

    String layoutStyle;

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

       //初始化mSharePreference，获取默认值
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(this);



        layoutStyle = mSharePreference.getString("fontSize", "1");
        int style = Integer.parseInt(layoutStyle);
        mContext = getApplicationContext();
        if(style == 1){
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
        }else if(style == 2){
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment_Biger()).commit();
        }else{
            getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment_PlusBiger()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*final Intent intent = this.getIntent();*/
        Intent intent = new Intent(Preference.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
