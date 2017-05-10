package com.example.Wuziqi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.Wuziqi.helper.ThemeChangeUtil;

/**
 * Created by Administrator on 2016/7/22.
 */
public class ScoreList extends Activity {

    WuziqiPanel_AI wuziqiPanel_ai;
    MainActivity mainActivity;
    WelcomeActivity welcomeActivity;
    ScoreList scoreList;
    private Vibrator vibe;

    String[] a = {"---",
            "---",
            "---",
            "---",
            "---",
            "---",
            "---",
            "---",
            "---",
            "---"
    };

    //关闭系统back键功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
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
        setContentView(R.layout.score_list);
        vibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        final ListView scoreList = (ListView) findViewById(R.id.score_list);

        //获取数据
        Intent int2 = getIntent();
        Bundle bd = int2.getExtras();
        String[] high_score_list = bd.getStringArray("SCORE_LIST_DATA");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sl_array_item, high_score_list);
        scoreList.setAdapter(adapter);

        Button btn_L = (Button) findViewById(R.id.SL_btn_L);
        Button btn_R = (Button) findViewById(R.id.SL_btn_R);
        Button btn_M = (Button) findViewById(R.id.SL_btn_M);

        //返回主页
        btn_L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(7);
                Intent intent = new Intent(ScoreList.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //重置得分榜
        btn_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(7);
            }
        });

        //分享成绩
        btn_M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(7);
            }
        });
    }

}
