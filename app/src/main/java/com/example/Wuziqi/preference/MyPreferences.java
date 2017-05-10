package com.example.Wuziqi.preference;

import android.content.Context;

/**
 * Created by Administrator on 2016/10/9.
 */

public class MyPreferences {

    //存储偏好设置文件名
    public static final String SHAREDPREFERENCE_NAME = "my_pref";
    //引导界面KEY
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

    /**
     * 判断activity是否被引导
     */
    public static boolean activityIsGuided(Context context, String className){
        if (context == null || className == null || "".equalsIgnoreCase(className)){
            return false;
        }
        String[] classNames = context.getSharedPreferences(SHAREDPREFERENCE_NAME,
                context.MODE_WORLD_READABLE).getString(KEY_GUIDE_ACTIVITY,
                "").split("\\|");
        for (String name : classNames){
            if (className.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * 设置该activity被引导过了。 将类名已  |a|b|c这种形式保存为value，因为偏好中只能保存键值对
     */
    public static void setIsGuided( Context context, String className){
        if (context == null || className == null || "".equalsIgnoreCase(className)){
            return;
        }
        String classNames = context.getSharedPreferences(SHAREDPREFERENCE_NAME,
                Context.MODE_WORLD_READABLE).getString(KEY_GUIDE_ACTIVITY, "");
        StringBuilder sb = new StringBuilder(className).append("|")
                .append(className);
        context.getSharedPreferences(SHAREDPREFERENCE_NAME, Context.MODE_WORLD_READABLE)
                .edit()
                .putString(KEY_GUIDE_ACTIVITY, sb.toString())
                .commit();
    }
}
