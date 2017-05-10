package com.example.Wuziqi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.Wuziqi.helper.AchievementRecord;
import com.example.Wuziqi.helper.ThemeChangeUtil;

/**
 * Created by Administrator on 2017/2/12.
 */

public class MainActivityMultiPlayer extends Activity{

    public SharedPreferences mSharePreference;
    public SharedPreferences.Editor editor;
    private WuziqiPanel_Multi panel; // 棋盘VIew
    private AlertDialog.Builder builder; //Dialog构建
    private int fontSize;
    private Vibrator vibe;
    private int timesOfWin;
    private int timesOfFail;


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
        setContentView(R.layout.panel_multiplayer);

        Context mContext = getApplicationContext();
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mSharePreference.edit();
        //初始化mSharePreference，获取默认值
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(this);
        //设置第一次启动后加载默认值
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        /**
         * 初始化荣誉勋章
         */
        /*ImageView iv = (ImageView) findViewById(R.id.badge_multi);
        AchievementRecord.levelForCombat(setLevel(), iv);*/
        final TextView textView_win_times = (TextView) findViewById(R.id.multi_win_times);
        final TextView textView_fail_times = (TextView) findViewById(R.id.multi_fail_times);
        AchievementRecord.refreshWinOrFailRecord(textView_win_times, textView_fail_times, sharedPreferencesGet(0), sharedPreferencesGet(1));
        //Dialog弹窗字体大小
        int pref_font_size = Integer.parseInt(mSharePreference.getString("fontSize", "1"));
        if (pref_font_size == 1){
            fontSize = 20;
        }else if(pref_font_size == 2){
            fontSize = 22;
        }else{
            fontSize = 24;
        }

        //开启震动控制
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Button button_return = (Button) findViewById(R.id.wuziqi_panelmulti_buttons_return);
        Button button_replay = (Button) findViewById(R.id.wuziqi_panelmulti_buttons_replay);
        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(7);
                return_alert(0);
            }
        });
        button_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(7);
                return_alert(1);
            }
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
        builder = new AlertDialog.Builder(MainActivityMultiPlayer.this);
        builder.setTitle(R.string.alertOverTitle); // 设置Dialog的标题
        builder.setNegativeButton(R.string.wq_btn_return, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 设置退出按钮和点击事件
                Intent int1 = new Intent(MainActivityMultiPlayer.this, WelcomeActivity.class);
                startActivity(int1);
                /*MainActivityMultiPlayer.this.finish();*/
            }
        });
        builder.setPositiveButton(R.string.wq_btn_replay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // 设置再来一局的按钮和点击事件
                /*panel.reStartGame();*/
                reStart();
            }
        });


        panel = (WuziqiPanel_Multi) findViewById(R.id.panel_multi);
        panel.setOnGameListener(new WuziqiPanel_Multi.onGameListener() {
            @Override
            public void onGameOVer(int i) { // 设置监听器
                String text = "";
                if (i == WuziqiPanel_Multi.WHITE_WIN) {
                    //白子胜利
                    text = getResources().getString(R.string.alertWinnerUser);
                    timesOfFail = sharedPreferencesGet(1) + 1;
                    sharePreferencesPut(1, timesOfFail);
                    AchievementRecord.refreshWinOrFailRecord(textView_win_times,textView_fail_times,sharedPreferencesGet(0),timesOfFail);
                } else if (i == WuziqiPanel_Multi.BLACK_WIN) {
                    //黑子胜利
                    text = getResources().getString(R.string.alertWinnerAI);
                    timesOfWin = sharedPreferencesGet(0) + 1;
                    sharePreferencesPut(0, timesOfWin);
                    AchievementRecord.refreshWinOrFailRecord(textView_win_times,textView_fail_times,timesOfWin,sharedPreferencesGet(1));
                }
                builder.setMessage(text); // 设置Dialog内容
                builder.setCancelable(false); // 设置不可返回键取消
                AlertDialog dialog = builder.create(); // 构建Dialog
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.x = 0;//设置x坐标
                params.y = panel.getUnder();//设置y坐标
                dialogWindow.setAttributes(params); // 设置新的LayoutParams
                dialog.setCanceledOnTouchOutside(false); // 设置点击外部不取消
                dialog.show(); // 显示Dialog
            }
        });
    }

    private void reStart() {
        Intent int2 = getIntent();
        overridePendingTransition(0,0);
        finish();
        overridePendingTransition(0,0);
        startActivity(int2);
    }

    /**
     * 设置勋章等级
     * @return
     */
    private int setLevel() {
        SharedPreferences sharePreferences = getSharedPreferences("SP_wuziqi", Context.MODE_PRIVATE);
        int gameFreq = sharePreferences.getInt("MultiMode_wintimes",0);
        int badgeLevel;
        if (gameFreq<1){
            badgeLevel = 0;
        }else if (gameFreq>=1 && gameFreq<2){
            badgeLevel = 1;
        }else if (gameFreq>=3){
            badgeLevel = 3;
        }else {
            badgeLevel = 2;
        }
        return badgeLevel;
    }

    //关闭系统back键功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    public int sharedPreferencesGet(int num){
        SharedPreferences sharedPreferences = getSharedPreferences("SP_wuziqi", Context.MODE_PRIVATE);
        int times;
        //num为0，返回胜场次数，为1 则返回败场次数
        if (num == 0){
            times = sharedPreferences.getInt("MultiMode_wintimes", 0);
        }else{
            times = sharedPreferences.getInt("MultiMode_failtimes", 0);
        }
        return times;
    }

    public void sharePreferencesPut(int num, int times){
        SharedPreferences sharedPreferences = getSharedPreferences("SP_wuziqi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //num为0，记录胜场次数，为1 则记录败场次数
        if (num == 0){
            editor.putInt("MultiMode_wintimes", times);
            editor.commit();
        }else{
            editor.putInt("MultiMode_failtimes", times);
            editor.commit();
        }
    }

    public void return_alert(int judge){
        /**
         * 判断调用提示窗口接口的来源
         * “0”：退出游戏界面时
         * “1”：重新开始游戏时
         */
        int judgeNum = judge;

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setInverseBackgroundForced(true);
        if (judgeNum == 0) {
            builder.setTitle(R.string.alertTitle).setMessage(R.string.alertMessage);
        } else {
            builder.setTitle(R.string.alertTitle).setMessage(R.string.alertMessage_1);
        }
        setPositiveButton(builder, judgeNum);
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

    private AlertDialog.Builder setPositiveButton(final AlertDialog.Builder builder, final int judge) {
        return builder.setPositiveButton(R.string.alertQuit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if ( judge ==0 ) {
                    Intent int1 = new Intent(MainActivityMultiPlayer.this, WelcomeActivity.class);
                    startActivity(int1);
                    finish();
                } else {
                    /*panel.reStartGame();*/
                    reStart();
                }
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
}
