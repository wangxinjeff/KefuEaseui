package com.kefueaseui.kefueaseui.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.Config;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.OrderInfo;
import com.hyphenate.helpdesk.model.VisitorTrack;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.utils.SharePrefrencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;


/**
 * Created by magic on 2017/4/14.
 */

public class ChatActvity extends BaseActivity {

    public static ChatActvity instance = null;
    private String toUsername;
    private ChatFragment mChatFragment;
    private SharePrefrencesUtil shareUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        UIProvider.getInstance().addActivity(this);
        instance = this;
        shareUtil = SharePrefrencesUtil.getInstance(this);
        toUsername = getIntent().getExtras().getString(Config.EXTRA_SERVICE_IM_NUMBER);
        mChatFragment = new MyChatFragment();
        mChatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.frame,mChatFragment).commit();

//        Conversation conversation = ChatClient.getInstance().chatManager().getConversation("传IM服务号");
//        if(conversation.getAllMsgCount() != 0){
//            Message message = conversation.latestMessage();
//            if(message.getType() == Message.Type.TXT){
//                EMTextMessageBody txtBody = (EMTextMessageBody) message.body();
//                if(txtBody.getMessage().equals("这里传设置的企业欢迎语")){
//                    return ;
//                }
//            }
//        }

        Bundle bundle = getIntent().getBundleExtra(Config.EXTRA_BUNDLE);
//        try {
//            String tenantId = bundle.getString("tenantId");
//            Log.e("bundle","tenantId="+tenantId);
//            ChatClient.getInstance().setTenantId(tenantId);
//        }catch(Exception e){
//            Log.e("bundle","tenantId=null");
//        }
        if(!shareUtil.isSaveCompanies()){
            Message message = Message.createReceiveMessage(Message.Type.TXT);
            EMTextMessageBody body = new EMTextMessageBody(shareUtil.getCompanies());
            message.setFrom(MyApplication.imService);
            message.setBody(body);
            message.setStatus(Message.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
            System.currentTimeMillis();
//            ChatClient.getInstance().chatManager().saveMessage(message);
            try{
                JSONObject weichat = new JSONObject();
                JSONObject agent = new JSONObject();
                agent.put("userNickname","机器人");
                agent.put("avatar","");
                weichat.put("agent",agent);
                message.setAttribute("weichat",weichat);
//                ChatClient.getInstance().chatManager().saveMessage(message);
                shareUtil.saveIsSaveCompanies(true);
            }catch(Exception e){
            }

        }
        if(!shareUtil.isSaveRobot()){
            //创建消息
            Message message = Message.createReceiveMessage(Message.Type.TXT);
            //从本地获取保存的string
            String str = shareUtil.getRobot();
            EMTextMessageBody body = null;
            //判断是否是菜单消息的string，这是自己实现的一个方法
            if(!isRobotMenu(str)){
                //文字消息直接去设置给消息
            body = new EMTextMessageBody(str);
            }else{
                //菜单消息需要设置给消息扩展
                try{
                    body = new EMTextMessageBody("");
                    JSONObject msgtype = new JSONObject(str);
                    message.setAttribute("msgtype",msgtype);
                }catch (Exception e){
                    Toast.makeText(this,"exception="+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            message.setFrom(MyApplication.imService);//这里传IM服务号
            message.addBody(body);
            message.setMsgTime(System.currentTimeMillis());
            message.setStatus(Message.Status.SUCCESS);
            message.setMsgId(UUID.randomUUID().toString());
//            ChatClient.getInstance().chatManager().saveMessage(message);//插入消息到本地
            shareUtil.saveIsSaveRobot(true);

        }
        if(bundle!=null){
            try{
                String message = bundle.getString("message","");
                Log.e("bundle","message="+message);
                if(message.equals("order")){
                    sendOrderMessage();
                }else if(message.equals("track")){
                    sendTrackMessage();
                }
            }catch(Exception e){
                Log.e("bundle","message=null");
            }

        }

//        ChatClient.getInstance().chatManager().addAgentInputListener(agentInputListener);
//        ChatClient.getInstance().chatManager().addVisitorWaitListener(visitorWaitListener);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//            if(resultCode == 2){
//                sendTrackMessage();
//            }
//
//    }

    private boolean isRobotMenu(String str){
        try {
            JSONObject json = new JSONObject(str);
            JSONObject obj = json.getJSONObject("choice");
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /***
     * 发送轨迹消息
     */
    public void sendTrackMessage(){
        VisitorTrack track = ContentFactory.createVisitorTrack(null);
        track.title("轨迹消息")  //显示标题
                .price("￥235") //显示价格
                .desc("假两件衬衣+V领毛衣上衣") //描述
                .imageUrl("http://o8ugkv090.bkt.clouddn.com/em_three.png")//显示图片
                .itemUrl("http://www.baidu.com"); //点击会跳转到哪
        Message message = Message.createTxtSendMessage("", toUsername);
        message.addContent(track);
        ChatClient.getInstance().getChat().sendMessage(message, new Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "send-success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "send-fail:code"+i+"/error:"+s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**
     * 发送订单消息
     */
    private void sendOrderMessage(){
        OrderInfo info = ContentFactory.createOrderInfo(null);
        info.title("订单消息")
                .orderTitle("订单号：7890")
                .price("￥128")
                .desc("2015早春新款高腰复古牛仔裙")
                .imageUrl("http://o8ugkv090.bkt.clouddn.com/em_three.png")
                .itemUrl("http://www.baidu.com");
        Message message = Message.createTxtSendMessage("订单消息", toUsername);
        message.addContent(info);
        ChatClient.getInstance().getChat().sendMessage(message, new Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "send-success", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "send-fail:code"+i+"/error:"+s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
//        UIProvider.getInstance().removeActivity(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(Config.EXTRA_SERVICE_IM_NUMBER);
        if (toUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        mChatFragment.onBackPressed();
        if (CommonUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



}
