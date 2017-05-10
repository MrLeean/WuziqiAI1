package com.example.Wuziqi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/7/24.
 */
public class ScoreRecord extends Activity{

    private static final String KEY = "score_list_data";
    Context mContext;

    public ScoreRecord(Context mContext){
        this.mContext = mContext;
    }

    //保存数组
    public static void saveArray(Context context,String[] StringArray) {
        SharedPreferences prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        for (String b : StringArray) {
            jsonArray.put(b);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY,jsonArray.toString());
        editor.commit();
    }

    //读取数组
    public static String[] getArray(Context context,int arrayLength)
    {
        SharedPreferences prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        String[] resArray=new String[arrayLength];
        Arrays.fill(resArray, "---");
        try {
            JSONArray jsonArray = new JSONArray(prefs.getString(KEY, "---"));
            for (int i = 0; i < jsonArray.length(); i++) {
                resArray[i] = jsonArray.getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resArray;
    }


}
