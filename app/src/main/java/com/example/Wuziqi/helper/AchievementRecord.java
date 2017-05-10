package com.example.Wuziqi.helper;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.Wuziqi.R;

/**
 * 单人模式下的胜利记录机制
 */
public class AchievementRecord {
    public static void initAchievement(int amountOfWin, ImageView imageView){
        switch (amountOfWin){
            case 0:
                imageView.setImageResource(R.drawable.lev1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.lev2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.lev3);
                break;
            case 3:
                imageView.setImageResource(R.drawable.lev4);
                break;
            default:
                imageView.setImageResource(R.drawable.lev1);
                break;
        }
    }

    public static void refreshWinOrFailRecord(TextView textViewWin, TextView textViewFail, int winTimes, int failTimes){
        String win = String.valueOf(winTimes);
        String fail = String.valueOf(failTimes);
        textViewWin.setText(win);
        textViewFail.setText(fail);
    }
}
