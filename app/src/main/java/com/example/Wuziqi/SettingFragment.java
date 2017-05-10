package com.example.Wuziqi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.io.File;

/**
 * Created by Administrator on 2016/7/5.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    Activity activity;
    public SharedPreferences.OnSharedPreferenceChangeListener listener;
    public static final String PREF_LINE_NUM = "lineNum";
    public static final String PREF_FONT_SIZE = "fontSize";
    public static final String PREF_NIGHT_MODE = "nightMode";
    public static final String PREF_BACKGROUND = "background";
    public static final String PREF_CLEAR_FILE = "clearCusfile";
    public static final String mPath =
            Environment.getExternalStorageDirectory().toString() + "/Wuziqi/Screenshot/";


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        activity = getActivity();
        PreferenceManager.getDefaultSharedPreferences(activity).registerOnSharedPreferenceChangeListener(this);

        Preference prefClear = findPreference(PREF_CLEAR_FILE);
        prefClear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                cleanCustomCache(mPath);
                return false;
            }
        });
    }

    /**
     * 改变主题
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                getActivity();
                final Intent intent = getActivity().getIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
            }
        };
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("nightMode")){
            Preference preference = findPreference(key);}
        if(key.equals("fontSize")){
            Preference preference = findPreference(key);}
    }


    /** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
    public static void cleanCustomCache(String customeFilePath) {
        deleteFilesByDirectory(new File(customeFilePath));
    }
    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
