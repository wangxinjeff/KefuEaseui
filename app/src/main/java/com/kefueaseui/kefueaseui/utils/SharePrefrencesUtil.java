package com.kefueaseui.kefueaseui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.chat.ChatClient;

/**
 * Created by magic on 2017/4/17.
 */


public class SharePrefrencesUtil {

    static private SharedPreferences sharedPreferences;
    static private SharedPreferences.Editor editor;
    static private SharePrefrencesUtil sharePrefrencesUtil = null;

    public synchronized static SharePrefrencesUtil getInstance(Context context){
        if(sharePrefrencesUtil == null){
            sharePrefrencesUtil = new SharePrefrencesUtil();
            sharedPreferences = context.getSharedPreferences(ChatClient.getInstance().getCurrentUserName()+"_setting",Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
        return sharePrefrencesUtil;
    }

    public void saveCompanies(String str){
        editor.putString("com_welcome",str);
        editor.commit();
    }

    public void saveRobot(String str){
        editor.putString("rob_welcome",str);
        editor.commit();
    }
    


    public void saveToken(String str){
        editor.putString("token",str);
        editor.commit();
    }

    public String getToken(){
        String token = sharedPreferences.getString("token","");
        return token;
    }

    public String getCompanies(){
        String companies = sharedPreferences.getString("com_welcome","");
        return companies;
    }

    public String getRobot(){
        String robot = sharedPreferences.getString("rob_welcome","");
        return robot;
    }


    public void saveIsSaveCompanies(boolean b){
        editor.putBoolean("isSaveCompanies",b);
        editor.commit();
    }

    public void saveIsSaveRobot(boolean b){
        editor.putBoolean("isSaveRobot",b);
        editor.commit();
    }

    public boolean isSaveRobot(){
        boolean bean = sharedPreferences.getBoolean("isSaveRobot",false);
        return bean;
    }

    public boolean isSaveCompanies(){
        boolean bean = sharedPreferences.getBoolean("isSaveCompanies",false);
        return bean;
    }

}
