package com.example.Wuziqi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/7/5.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingFragment_PlusBiger extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences mSharePreference;
    RelativeLayout mRelativeLayout;
    Activity activity;

    private EditTextPreference pref_line_num;
    private CheckBoxPreference pref_night_mode;
    private ListPreference pref_font_size;
    private ListPreference pref_bg_color;


    public static final String PREF_FONT_SIZE = "fontSize";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_plusbiger);


        pref_line_num = (EditTextPreference) findPreference("lineNum");
        pref_night_mode = (CheckBoxPreference) findPreference("nightMode");
        pref_font_size = (ListPreference) findPreference("fontSize");
        pref_bg_color = (ListPreference) findPreference("background");


        /*pref_font_size.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value_string = pref_font_size.getValue();
                int value_int = Integer.parseInt(value_string);
                if(value_int == 1){
                    Log.i("存储的值为", ""+ value_int);
                    pref_line_num.setLayoutResource(R.layout.menu_small_font_size);
                }else if(value_int == 2){
                    Log.i("存储的值为", ""+ value_int);
                    pref_line_num.setLayoutResource(R.layout.menu_medium_font_size);
                }else{
                    Log.i("存储的值为", ""+ value_int);
                    pref_line_num.setLayoutResource(R.layout.menu_big_font_size);
                }
                return false;
            }
        });*/




        activity = getActivity();
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("nightMode")){
            Preference preference = findPreference(key);
            if(preference.getSummary() == "选中以开启夜间模式"){
                preference.setSummary("选中以关闭夜间模式");
            }else{
                preference.setSummary("选中以开启夜间模式");
            }
        }
        if(key.equals("fontSize")){
            Preference preference = findPreference(key);
        }
    }


}
