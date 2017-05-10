package com.example.Wuziqi.helper;

import android.app.Activity;

import com.example.Wuziqi.R;

/**
 * Created by Administrator on 2016/11/12.
 */

public class ThemeChangeUtil {
    /**
     * 选择主题
     */
    public static void initTheme(Boolean nightOn, String themeIndex, Activity activity, int isWelcomeActivity) {
        if (nightOn){
            activity.setTheme(R.style.MyNightTheme);
        } else {
            if ( isWelcomeActivity == 1){
                switch (themeIndex) {
                    case "1":
                        activity.setTheme(R.style.HMyTheme);
                        break;
                    case "2":
                        activity.setTheme(R.style.HMySecondTheme);
                        break;
                    case "3":
                        activity.setTheme(R.style.HMyThirdTheme);
                        break;
                    case "4":
                        activity.setTheme(R.style.HMyFourthTheme);
                        break;
                    default:
                        activity.setTheme(R.style.HMyTheme);
                        break;
                }
            } else{
                switch (themeIndex) {
                    case "1":
                        activity.setTheme(R.style.MyTheme);
                        break;
                    case "2":
                        activity.setTheme(R.style.MySecondTheme);
                        break;
                    case "3":
                        activity.setTheme(R.style.MyThirdTheme);
                        break;
                    case "4":
                        activity.setTheme(R.style.MyFourthTheme);
                        break;
                    default:
                        activity.setTheme(R.style.MyTheme);
                        break;
                }
            }
        }
    }
}
