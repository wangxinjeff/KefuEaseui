package com.kefueaseui.kefueaseui.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.adapter.EMACallRtcInterface;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.utils.SharePrefrencesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by magic on 2017/4/14.
 */

public class LoginActivity extends BaseActivity {

    private EditText mAccont;
    private EditText mPassword;
    private Button mLogin;
    private String token_url = "http://a1.easemob.com/%s/%s/token";
    private boolean progressShow;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        if(ChatClient.getInstance().isLoggedInBefore()){
            Intent intent = new Intent(LoginActivity.this,OptionsActivity.class);
            startActivity(intent);
            finish();
        }
        initview();
//        UIProvider.getInstance().addActivity(this);
    }

    private void initview(){
        mAccont = (EditText)findViewById(R.id.act);
        mAccont.setText("111");
        mPassword = (EditText)findViewById(R.id.pwd);
        mPassword.setText("111");
        mLogin = (Button)findViewById(R.id.login_btn);
//        Conversation conversation = ChatClient.getInstance().chatManager().getConversation("im服务号");
//        Message message = conversation.getLastMessage();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ChatClient.getInstance().isLoggedInBefore()){
                    Intent intent = new Intent(LoginActivity.this,OptionsActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    progressShow = true;
                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            progressShow = false;
                        }
                    });
                    pd.setMessage("正在登陆");
                    pd.show();
                    ChatClient.getInstance().loginWithToken(mAccont.getText().toString().trim(), mPassword.getText().toString().trim(), new Callback() {
                        @Override
                        public void onSuccess() {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Conversation conversation = ChatClient.getInstance().chatManager().getConversation(MyApplication.imService);
//                                    //获取此会话的所有消息
//                                    List<Message> messages = conversation.getAllMessages();
//                                    Log.e("LoginActivity",ChatClient.getInstance().currentUserName()+"=="+messages.size()+"///"+conversation.getAllMsgCount());
//                                }
//                            });

                            if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                                pd.dismiss();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "login-success:"+ChatClient.getInstance().currentUserName(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent(LoginActivity.this,OptionsActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(final int i, final String s) {
                            if (!progressShow) {
                                return;
                            }
                            pd.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "login-fail:code=" + i + "/error:" + s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                }


            }
        });

        $(R.id.regest_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                progressShow = true;
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        progressShow = false;
                    }
                });
                pd.setMessage("正在注册");
                pd.show();
                final String accont = getRandomAccount();
                ChatClient.getInstance().register(accont, "123456", new Callback() {
                    @Override
                    public void onSuccess() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "create-success", Toast.LENGTH_SHORT).show();
                                ChatClient.getInstance().login(accont, "123456", new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                                            pd.dismiss();
                                        }
                                        Intent intent = new Intent(LoginActivity.this,OptionsActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(final int i, final String s) {
                        if (!progressShow) {
                            return;
                        }
//                        pd.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "create-fail:code=" + i + "/error:" + s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    private String getToken(){
        HttpClient httpClient = new DefaultHttpClient();
        String remoteUrl = String.format(token_url,MyApplication.orgName,MyApplication.appName);
        Log.e("remoteUrl",remoteUrl);
        HttpPost httpPost = new HttpPost(remoteUrl);
        httpPost.addHeader("Content-Type","application/json");
        try {
            JSONObject body = new JSONObject();
            body.put("grant_type","password");
            body.put("username",MyApplication.imService);
            body.put("password","111");
            httpPost.setEntity(new StringEntity(body.toString()));
            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String rev = EntityUtils.toString(response.getEntity());
                JSONObject obj = new JSONObject(rev);
                final String token = obj.getString("access_token");
                SharePrefrencesUtil shareUtil = SharePrefrencesUtil.getInstance(this);
                shareUtil.saveToken(token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "token="+token, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (final Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"exception="+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        return null;
    }

    private String getRandomAccount(){
        String val = "";
        Random random = new Random();
        for(int i = 0; i < 15; i++){
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; //输出字母还是数字
            if("char".equalsIgnoreCase(charOrNum)){// 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            }else if("num".equalsIgnoreCase(charOrNum)){// 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase(Locale.getDefault());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        UIProvider.getInstance().removeActivity(this);
    }
}
