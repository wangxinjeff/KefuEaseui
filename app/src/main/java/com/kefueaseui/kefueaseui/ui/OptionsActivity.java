package com.kefueaseui.kefueaseui.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.manager.TicketManager;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.util.PathUtil;
import com.kefueaseui.kefueaseui.MyAidl;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.service.MyService;
import com.kefueaseui.kefueaseui.utils.SharePrefrencesUtil;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by magic on 2017/4/14.
 */

public class OptionsActivity extends BaseActivity {

    private String orgName = MyApplication.appkey.split("#")[0];
    private String appName = MyApplication.appkey.split("#")[1];

    private SharePrefrencesUtil shareUtil;
    private String com_url = "http://kefu.easemob.com/v1/tenantapi/welcome?tenantId=%s&orgName=%s&appName=%s&userName=%s&token=%s";

    private MyAidl mAidl;

    private ServiceConnection mConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAidl = MyAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAidl = null;
        }
    };


    Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.e("handlerMessage","handler");
            Message message = Message.createTxtSendMessage("handler", MyApplication.imService);
                ChatClient.getInstance().getChat().sendMessage(message);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        ChatClient.getInstance().chatManager().bindChat(MyApplication.imService);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        ChatClient.getInstance().chatManager().unbindChat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        mConnection=null;
//        UIProvider.getInstance().removeActivity(this);
//        ChatClient.getInstance().chatManager().unbindChat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.kefueaseui.kefueaseui.MyApplication", "com.kefueaseui.kefueaseui.service.MyService"));
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        Intent intent1 = new Intent(getApplicationContext(), MyService.class);
        bindService(intent1, mConnection, BIND_AUTO_CREATE);

//        UIProvider.getInstance().addActivity(this);

        PathUtil.getInstance().getVoicePath().getPath();



        shareUtil = SharePrefrencesUtil.getInstance(this);


        //联系客服
        $(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!MyApplication.isSaveCompanies){
//                    Message message = Message.createReceiveMessage(Message.Type.TXT);
//                    EMTextMessageBody body = new EMTextMessageBody(shareUtil.getCompanies());
//                    message.setFrom(imService);
//                    message.addBody(body);
//                    message.setMsgTime(System.currentTimeMillis());
//                    message.setStatus(Message.Status.SUCCESS);
//                    message.setMsgId(UUID.randomUUID().toString());
//                    ChatClient.getInstance().chatManager().saveMessage(message);
//                    MyApplication.isSaveCompanies = true;
//                }
//                if(!MyApplication.isSaveRobot){
//                    Message message = Message.createReceiveMessage(Message.Type.TXT);
//                    EMTextMessageBody body = new EMTextMessageBody(shareUtil.getRobot());
//                    message.setFrom(imService);
//                    message.addBody(body);
//                    message.setMsgTime(System.currentTimeMillis());
//                    message.setStatus(Message.Status.SUCCESS);
//                    message.setMsgId(UUID.randomUUID().toString());
//                    ChatClient.getInstance().chatManager().saveMessage(message);
//                    MyApplication.isSaveRobot = true;
//                }
//                Message message = Message.createTxtSendMessage("",MyApplication.imService);
//                try {
//                    JSONObject weichat = new JSONObject();
//                    JSONObject ctrlArgs = new JSONObject();
//                    ctrlArgs.put("inviteId", "33948778");
//                    ctrlArgs.put("serviceSessionId", "9eeab92d-178c-440a-bbba-b0ad4c676778");
//                    ctrlArgs.put("detail", "非常好");
//                    ctrlArgs.put("summary", "5");
//                    weichat.put("ctrlArgs",ctrlArgs);
//                    weichat.put("ctrlType","enquiry");
//                    message.setAttribute("weichat",weichat);
//                }catch (Exception e){
//
//                }


                Conversation conversation = ChatClient.getInstance().chatManager().getConversation(MyApplication.imService);
                String titleName = null;
                if (conversation.getOfficialAccount() != null){
                    titleName = conversation.getOfficialAccount().getName();
                }

                Bundle bundle = new Bundle();
                bundle.putString("tenantId","7038");
                // 进入聊天页面
                Intent intent = new IntentBuilder(OptionsActivity.this)
                        .setTargetClass(ChatActvity.class)
                        .setVisitorInfo(ContentFactory.createVisitorInfo(null)
                                .nickName("昵称")
                                .name("jeff")
                                .qq("22222")
                                .phone("138")
                                .companyName("公司")
                                .description("介绍")
                                .email("qq@163.com"))
                        .setServiceIMNumber(MyApplication.imService)
                        .setTitleName(titleName)
                        //指定技能组
                      .setScheduleQueue(ContentFactory.createQueueIdentityInfo("售前测试"))
                        //指定客服
//						.setScheduleAgent(ContentFactory.createAgentIdentityInfo(" "))
                        .setShowUserNick(true)
                        .setBundle(bundle)
                        .build();
//                Intent intent = new Intent(OptionsActivity.this,Test1Activity.class);
                startActivity(intent);
            }
        });
        //退出登录
        $(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(OptionsActivity.this);
                pd.setMessage("正在退出登录");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                    ChatClient.getInstance().logout(false, new Callback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"logout-success",Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity(new Intent(OptionsActivity.this,LoginActivity.class));

                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(),"logout-fail",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
        //获取企业欢迎语
        $(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String token = ChatClient.getInstance().accessToken();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpClient = new DefaultHttpClient();
                        String remote_com = String.format(com_url,MyApplication.tenantId,orgName,appName,MyApplication.imService,token);
                        Log.e("remote_com",remote_com);
                        HttpGet httpGet = new HttpGet(remote_com);
                        try {
                            HttpResponse response = httpClient.execute(httpGet);
                            int code = response.getStatusLine().getStatusCode();
                            if (code == 200) {
                                final String com_welcome = EntityUtils.toString(response.getEntity());
                                //这里是做的本地保存，使用的时候就从本地获取创建消息去插入本地
                                shareUtil.saveCompanies(com_welcome);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"com_welcome="+com_welcome,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }catch(final Exception e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"exception="+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();



            }
        });
        //获取机器人欢迎语
        $(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet("http://kefu.easemob.com/v1/Tenants/"+MyApplication.tenantId+"/robots/visitor/greetings/app");
                        try {
                            HttpResponse response = httpClient.execute(httpGet);
                            int code = response.getStatusLine().getStatusCode();
                            if (code == 200) {
                                final String rev  = EntityUtils.toString(response.getEntity());

                                JSONObject obj = new JSONObject(rev);
                                int type = obj.getInt("greetingTextType");
                                final String rob_welcome = obj.getString("greetingText");
                                //type为0代表是文字消息的机器人欢迎语
                                //type为1代表是菜单消息的机器人欢迎语
                                if(type == 0){
                                    //把解析拿到的string保存在本地
                                    shareUtil.saveRobot(rob_welcome);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"rob_welcome="+rob_welcome,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else if(type == 1){
                                    final String str = rob_welcome.replaceAll("&quot;","\"");
                                    JSONObject json = new JSONObject(str);
                                    JSONObject ext = json.getJSONObject("ext");
                                    final JSONObject msgtype = ext.getJSONObject("msgtype");
                                    //把解析拿到的string保存在本地
                                    shareUtil.saveRobot(msgtype.toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),"rob_welcome="+msgtype,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        }catch(final Exception e){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"exception="+e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
        //创建留言
        $(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Message message = Message.createReceiveMessage(Message.Type.TXT);
//                message.setBody(new EMTextMessageBody("测试"));
//                message.setFrom("test");
//                message.setMessageTime(1513823400000L);
//                message.setMsgId(System.currentTimeMillis()+"");
//                message.setStatus(Message.Status.SUCCESS);
//                ChatClient.getInstance().chatManager().saveMessage(message);
                startActivity(new Intent(OptionsActivity.this,WebviewActivity.class));
            }
        });
        //获取留言
        $(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(OptionsActivity.this,TicketListActivity.class));
//                ChatClient.getInstance().leaveMsgManager().getWorkStatus(MyApplication.imService,new ValueCallBack<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        Log.e("OptionsActivity",s+"");
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//
//                    }
//                });
                try {
                    mAidl.getConversations();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        });
        //发送订单消息
        $(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("message","order");
                Intent intent = new IntentBuilder(OptionsActivity.this)
                        .setTargetClass(ChatActvity.class)
                        .setVisitorInfo(ContentFactory.createVisitorInfo(null).nickName("jeff").name("jeff").qq("23456").phone("151").companyName("环信").description("vip").email("aiup@163.com"))
                        .setServiceIMNumber(MyApplication.imService)
                        //指定技能组
//                      .setScheduleQueue(ContentFactory.createQueueIdentityInfo("shouqian"))
                        //指定客服
//						.setScheduleAgent(MessageHelper.createAgentIdentity("ceshiok1@qq.com"))
                        .setShowUserNick(true)
                        .setBundle(bundle)
                        .build();
//                startActivity(intent);
            }
        });
        //发送轨迹消息
        $(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("message","track");
                Intent intent = new IntentBuilder(OptionsActivity.this)
                        .setTargetClass(ChatActvity.class)
                        .setVisitorInfo(ContentFactory.createVisitorInfo(null).nickName("jeff").name("jeff").qq("23456").phone("151").companyName("环信").description("vip").email("aiup@163.com"))
                        .setServiceIMNumber(MyApplication.imService)
                        //指定技能组
//                      .setScheduleQueue(ContentFactory.createQueueIdentityInfo("shouqian"))
                        //指定客服
//						.setScheduleAgent(MessageHelper.createAgentIdentity("ceshiok1@qq.com"))
                        .setShowUserNick(true)
                        .setBundle(bundle)
                        .build();
                startActivity(intent);
                finish();
//            try {
//                Message message = Message.createSendMessage(Message.Type.CMD);
//                String action = "TransferToKf";
//                message.setBody(new EMCmdMessageBody(action));
//                message.setTo("test");
//                JSONObject weichat = new JSONObject();
//                JSONObject ctrlArgs = new JSONObject();
//                ctrlArgs.put("id", "");
//                ctrlArgs.put("serviceSessionId", "");
//                weichat.put("ctrlArgs",ctrlArgs);
//                message.setAttribute("weichat",weichat);
//                ChatClient.getInstance().chatManager().sendMessage(message);
//            }catch(JSONException e){
//
//            }
//                Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
//
//                for (EMConversation conversation : conversations.values()) {
//                    Log.e("Option",conversation.conversationId());
//                }
            }
        });
        $(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this,GetLogActivity.class));
            }
        });
    }



    private boolean isRobotMenu(JSONObject json){
        try {
            JSONObject obj = json.getJSONObject("ext");
        }catch (Exception e){
            return false;
        }
        return true;
    }

    //记录用户首次点击返回键的时间
    private long firstTime=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(OptionsActivity.this,"再按一次退出程序--->onKeyUp",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    ChatClient.getInstance().logout(false, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("OptionsActivity","success");
//                            System.exit(0);
//                            List<Activity> list = UIProvider.getInstance().getActivity();
//                            for (Activity activity : list){
//                                activity.finish();
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(OptionsActivity.this,"Logout onsuccess",Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("OptionsActivity","error");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(OptionsActivity.this,"Logout onerror",Toast.LENGTH_SHORT).show();
//                                }
//                            });

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
