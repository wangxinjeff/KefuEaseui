package com.kefueaseui.kefueaseui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Looper;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.Notifier;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.easeui.util.UserUtil;
import com.hyphenate.helpdesk.model.AgentInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.kefueaseui.kefueaseui.ui.CallReceiver;
import com.kefueaseui.kefueaseui.ui.ChatActvity;
import com.kefueaseui.kefueaseui.ui.LoginActivity;
import com.kefueaseui.kefueaseui.utils.GlideCircleTransform;
import com.kefueaseui.kefueaseui.utils.SharePrefrencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by magic on 2017/4/14.
 */

public class MyApplication extends Application {

    private static Context mContext;
    private UIProvider mUIProvider;
    private CallReceiver mCallReceiver;
    public static String appkey = "0019#testtwo";
    public static String tenantId = "7038";
    public static String imService = "test";
    public static String orgName = appkey.split("#")[0];
    public static String appName = appkey.split("#")[1];
    public static String projectId = "56107";
    private String TAG = "MyApplication";

    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ChatClient.Options option = new ChatClient.Options();

        option.setAppkey(appkey);
        option.setTenantId(tenantId);
        option.showAgentInputState();
        option.showVisitorWaitCount();

        option.setConsoleLog(true);
//        option.setKefuServerAddress("118.192.134.212");
//        option.setRestServer("124.207.9.210");
//        option.setIMServer("124.207.9.210");
//        option.setIMPort(443);
        if (ChatClient.getInstance().init(this, option)) {
//                ChatClient.getInstance().logout(false,null);
                ChatClient.getInstance().setDebugMode(true);
                mUIProvider = UIProvider.getInstance();
                mUIProvider.init(mContext);

//            ChatClient.getInstance().appKey();
            EMClient.getInstance().groupManager().addGroupChangeListener(new EMGroupChangeListener() {
                @Override
                public void onInvitationReceived(String s, String s1, String s2, String s3) {

                }

                @Override
                public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {

                }

                @Override
                public void onRequestToJoinAccepted(String s, String s1, String s2) {

                }

                @Override
                public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {

                }

                @Override
                public void onInvitationAccepted(String s, String s1, String s2) {

                }

                @Override
                public void onInvitationDeclined(String s, String s1, String s2) {

                }

                @Override
                public void onUserRemoved(String s, String s1) {

                }

                @Override
                public void onGroupDestroyed(String s, String s1) {

                }

                @Override
                public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

                }

                @Override
                public void onMuteListAdded(String s, List<String> list, long l) {

                }

                @Override
                public void onMuteListRemoved(String s, List<String> list) {

                }

                @Override
                public void onAdminAdded(String s, String s1) {

                }

                @Override
                public void onAdminRemoved(String s, String s1) {

                }

                @Override
                public void onOwnerChanged(String s, String s1, String s2) {

                }

                @Override
                public void onMemberJoined(String s, String s1) {

                }

                @Override
                public void onMemberExited(String s, String s1) {

                }

                @Override
                public void onAnnouncementChanged(String s, String s1) {

                }

                @Override
                public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

                }

                @Override
                public void onSharedFileDeleted(String s, String s1) {

                }
            });
                EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
                    @Override
                    public void onConnected() {

                    }

                    @Override
                    public void onDisconnected(int i) {
                        switch (i) {
                            case EMError.USER_KICKED_BY_CHANGE_PASSWORD:
                                Log.e(TAG, "onDisconnected:"+i );
                        }

                    }
                });
                    ChatClient.getInstance().addConnectionListener(new ChatClient.ConnectionListener() {
                    @Override
                    public void onConnected() {
                        Log.e(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int i) {
                        Log.e(TAG, "onDisconnected:"+i );
                        switch (i){
                            case Error.USER_LOGIN_ANOTHER_DEVICE:
//                                if(ChatClient.getInstance().isLoggedInBefore()){
                                    ChatClient.getInstance().logout(false, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Intent intent = new Intent(mContext,LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                            mContext.startActivity(intent);
                                        }

                                        @Override
                                        public void onError(int i, String s) {

                                        }

                                        @Override
                                        public void onProgress(int i, String s) {

                                        }
                                    });
//                                }
                                break;
                        }

                    }
                });

                mUIProvider.setUserProfileProvider(new UIProvider.UserProfileProvider() {
                    @Override
                    public void setNickAndAvatar(final Context context, Message message, final ImageView userAvatarView, TextView usernickView) {
                        if (message.direct() == Message.Direct.RECEIVE) {
                            //设置接收方的昵称和头像
//                    UserUtil.setAgentNickAndAvatar(context, message, userAvatarView, usernickView);
                            AgentInfo agentInfo = com.hyphenate.helpdesk.model.MessageHelper.getAgentInfo(message);
                            if (usernickView != null){
                                usernickView.setText(message.getFrom());
                                if (agentInfo != null){
                                    if (!TextUtils.isEmpty(agentInfo.getNickname())) {
                                        usernickView.setText(agentInfo.getNickname());
                                    }
                                }
                            }
                            if (userAvatarView != null){
                                userAvatarView.setImageResource(com.hyphenate.helpdesk.R.drawable.hd_default_avatar);
                                if (agentInfo != null){
                                    if (!TextUtils.isEmpty(agentInfo.getAvatar())) {
                                        String strUrl = agentInfo.getAvatar();
                                        com.hyphenate.helpdesk.util.Log.e("application","strUrl:"+strUrl);
                                        // 设置客服头像
                                        if (!TextUtils.isEmpty(strUrl)) {
                                            if (!strUrl.startsWith("http")) {
                                                strUrl = "http:" + strUrl;
                                                com.hyphenate.helpdesk.util.Log.e("application","strUrl//"+strUrl);
                                            }
                                            //正常的string路径
                                            Glide.with(context).load(strUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(com.hyphenate.helpdesk.R.drawable.hd_default_avatar).transform(new GlideCircleTransform(context)).into(userAvatarView);
                                        }
                                    }
                                }

                            }
                        } else {
                            //此处设置当前登录用户的头像，
                            if (userAvatarView != null) {
                                userAvatarView.setBackgroundResource(R.mipmap.ic_launcher);
//                            Glide.with(context).load(R.mipmap.ic_launcher).asBitmap().centerCrop().into(new BitmapImageViewTarget(userAvatarView) {
//                                @Override
//                                protected void setResource(Bitmap resource) {
//                                    RoundedBitmapDrawable circularBitmapDrawable =
//                                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                                    circularBitmapDrawable.setCircular(true);
//                                    userAvatarView.setImageDrawable(circularBitmapDrawable);
//                                }
//                            });
                            }
                        }
                    }
                });

            mUIProvider.getNotifier().setNotificationInfoProvider(new Notifier.NotificationInfoProvider() {
                @Override
                public String getDisplayedText(Message message) {
                    return null;
                }

                @Override
                public String getLatestText(Message message, int fromUsersNum, int messageNum) {
                    return null;
                }

                @Override
                public String getTitle(Message message) {
                    return null;
                }

                @Override
                public int getSmallIcon(Message message) {
                    return R.drawable.e_e_1;
                }

                @Override
                public Intent getLaunchIntent(Message message) {



                    return null;
                }
            });


//        Log.e(TAG,"filter:"+ChatClient.getInstance().callManager().getIncomingCallBroadcastAction());
//
//            IntentFilter callFilter = new IntentFilter(ChatClient.getInstance().callManager().getIncomingCallBroadcastAction());
//            if (mCallReceiver == null) {
//                mCallReceiver = new CallReceiver();
//            }
//            // register incoming call receiver
//            mContext.registerReceiver(mCallReceiver, callFilter);

            EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
                @Override
                public void onMessageReceived(List<EMMessage> list) {
                    Conversation conversation = ChatClient.getInstance().chatManager().getConversation(imService);
                    Log.e(TAG,"emmessagecount:"+conversation.unreadMessagesCount());
                    for (EMMessage message : list) {
                        Log.e(TAG,"emmessage:"+message.getMsgTime());
                    }
                }

                @Override
                public void onCmdMessageReceived(List<EMMessage> list) {

                }

                @Override
                public void onMessageRead(List<EMMessage> list) {

                }

                @Override
                public void onMessageDelivered(List<EMMessage> list) {

                }

                @Override
                public void onMessageRecalled(List<EMMessage> list) {

                }

                @Override
                public void onMessageChanged(EMMessage emMessage, Object o) {

                }
            });


            ChatClient.getInstance().chatManager().addMessageListener(new ChatManager.MessageListener() {
                @Override
                public void onMessage(List<Message> list) {

                    for (Message message : list) {
//                        Conversation conversation = ChatClient.getInstance().chatManager().getConversation(imService);
//                        Log.e(TAG,"messagecount:"+conversation.unreadMessagesCount());
//                        try {
//                            JSONObject weichat = message.getJSONObjectAttribute("weichat");
//                            JSONObject ctrlArgs = weichat.getJSONObject("ctrlArgs");
//                            JSONArray evaluationDegree = ctrlArgs.getJSONArray("evaluationDegree");
//                            JSONObject no1 = evaluationDegree.getJSONObject(1);
//                            JSONArray appraiseTags = no1.getJSONArray("appraiseTags");
//                            JSONObject name = appraiseTags.getJSONObject(1);
//                            int id = name.getInt("id");
//                            String na = name.getString("name");
//                            int tagSeqId = name.getInt("tagSeqId");
//                            int eid = no1.getInt("id");
//                            Log.e(TAG,weichat.toString());
//                            Log.e(TAG,evaluationDegree.toString());
//                            Log.e(TAG,appraiseTags.toString());
//                            Log.e(TAG,id+"-"+na+"-"+tagSeqId+"-"+eid);
//                        }catch (Exception e){
//
//                        }mes
                        Log.e(TAG,message.messageId()+"==="+message.ext());
                        UIProvider.getInstance().getNotifier().onNewMsg(message);
                        if (message.getType() == Message.Type.TXT) {
//                            Log.e(TAG,message.from()+"///"+message.ext());
                            EMTextMessageBody textBody = (EMTextMessageBody) message.getBody();
                            String content = textBody.getMessage();
                            if (content.equals("会话已结束。")) {
                                SharePrefrencesUtil.getInstance(getApplicationContext()).saveIsSaveCompanies(false);
                                SharePrefrencesUtil.getInstance(getApplicationContext()).saveIsSaveRobot(false);
                            }
                        }
                    }
                }

                @Override
                public void onCmdMessage(List<Message> list) {
                    for (Message message : list) {
                        EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
                        Log.e("action",message.ext().toString());
                        String action = body.action();

                        Log.e("action",body.action());
                    }

                }

                @Override
                public void onMessageStatusUpdate() {

                }

                @Override
                public void onMessageSent() {

                }
            });
        ChatClient.getInstance().chatManager().addVisitorWaitListener(new ChatManager.VisitorWaitListener() {
            @Override
            public void waitCount(int i) {
                Log.e("application","排队人数："+i);
            }
        });
        }
    }
}
