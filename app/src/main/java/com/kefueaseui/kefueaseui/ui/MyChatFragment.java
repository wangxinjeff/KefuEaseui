package com.kefueaseui.kefueaseui.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.chat.adapter.EMAChatClient;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.provider.CustomChatRowProvider;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.VisitorTrack;
import com.hyphenate.util.EasyUtils;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.chatrow.ChatRowEvaluation;
import com.kefueaseui.kefueaseui.chatrow.ChatRowOrder;
import com.kefueaseui.kefueaseui.chatrow.ChatRowTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by magic on 2017/4/14.
 */

public class MyChatFragment extends ChatFragment implements ChatFragment.EaseChatFragmentListener {


    private static final int ITEM_SHORTCUT = 11;
    private static final int ITEM_MAP = 12;
    private static final int ITEM_VIDEO = 13;

    public static final int MESSAGE_TYPE_SENT_ORDER = 1;
    public static final int MESSAGE_TYPE_RECV_ORDER = 2;
    public static final int MESSAGE_TYPE_SENT_TRACK = 3;
    public static final int MESSAGE_TYPE_RECV_TRACK = 4;
    public static final int MESSAGE_TYPE_RECV_EVAL = 5;
    public static final int MESSAGE_TYPE_SENT_EVAL = 6;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE_EVAL){
                messageList.refresh();
            }
        }
    }

    @Override
    protected void registerExtendMenuItem() {
        super.registerExtendMenuItem();
        inputMenu.registerExtendMenuItem(R.string.attach_location, R.drawable.hd_chat_location_selector, ITEM_MAP, R.id.chat_menu_map,extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_short_cut_message, R.drawable.em_chat_phrase_selector, ITEM_SHORTCUT, R.id.chat_menu_leave_msg,extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem(R.string.attach_call_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, R.id.chat_menu_video_call,extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem("插入", R.drawable.em_chat_video_selector, 14,R.id.chat_menu_test,extendMenuItemClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        super.setUpView();
        titleBar.setBackgroundColor(getResources().getColor(R.color.holo_red_light));
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (EasyUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(getActivity(), OptionsActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
            }
        });
    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(Message message) {

        return false;
    }

    @Override
    public void onMessageBubbleLongClick(Message message) {

    }


    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId){
            case ITEM_SHORTCUT:

                break;
            case ITEM_MAP:

                break;
            case ITEM_VIDEO:
                startVideoCall();
                break;
            case 14:
                ChatClient.getInstance().logout(false, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("MyChatFragment","logout-onsucess");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChatClient.getInstance().login("321", "111", new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.e("MyChatFragment",ChatClient.getInstance().currentUserName()+"//login-onsucess");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
//                                                            messageList.reSetData();
                                                        }
                                                    });
                                                }catch(InterruptedException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();

                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("MyChatFragment","login-onerror:"+i+"//"+s);
                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("MyChatFragment","logout-onerror:"+i+"//"+s);
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });

//                try {
//                    Message message = Message.createTxtSendMessage("这里传菜单项的内容", toChatUsername);
//                    JSONObject choice = new JSONObject();
//                    JSONObject menuid = new JSONObject();
//                    menuid.put("menuid","这里传菜单项对应的id");
//                    choice.put("choice",menuid);
//                    message.setAttribute("msgtype",choice);
//                    ChatClient.getInstance().chatManager().sendMessage(message);
//                }catch(Exception e){}
//                try {
//                    Message message = Message.createTxtSendMessage("消息", toChatUsername);
//                    JSONObject weichat = new JSONObject();
//                    JSONObject ctrlArgs = new JSONObject();
//                    ctrlArgs.put("inviteId", "34363219");
//                    ctrlArgs.put("serviceSessionId", "9b09ac87-19ff-419d-9751-03d039ed2614");
//                    ctrlArgs.put("detail", "非常好");
//                    ctrlArgs.put("summary", "5");
//                    ctrlArgs.put("evaluationDegreeId","4521");
//                    JSONArray appraiseTags = new JSONArray();
//                    appraiseTags.put(0,new JSONObject().put("id","221").put("name","问题解决了"));
//                    ctrlArgs.put("appraiseTags",appraiseTags);
//                    weichat.put("ctrlArgs",ctrlArgs);
//                    weichat.put("ctrlType","enquiry");
//                    message.setAttribute("weichat",weichat);
//
//                    ChatClient.getInstance().chatManager().sendMessage(message);
//                }catch (Exception e){}
//                try{
//                    Message message = Message.createSendMessage(Message.Type.CMD);
//                    String action = "action";
//                    message.addBody(new EMCmdMessageBody(action));
//                    message.setTo(toChatUsername);
//                    JSONObject cmd = new JSONObject();
//                    JSONObject updateVisitorInfoSrc = new JSONObject();
//                    JSONObject params = new JSONObject();
//                    params.put("phone","186");
//                    updateVisitorInfoSrc.put("params",params);
//                    cmd.put("updateVisitorInfoSrc",updateVisitorInfoSrc);
//                    message.setAttribute("cmd",cmd);
//                    ChatClient.getInstance().chatManager().sendMessage(message);
//                }catch(Exception e){}
                break;
            default:
                break;
        }
        return false;
    }


    private void startVideoCall(){

        if(!ChatClient.getInstance().isConnected()){
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
            return;
        }
        inputMenu.hideExtendMenuContainer();

        Message message = Message.createTxtSendMessage("邀请客服进行实时视频", toChatUsername);
        JSONObject jsonInvit = new JSONObject();
        try {
            JSONObject jsonMsg = new JSONObject();
            jsonMsg.put("msg", "邀请客服进行实时视频");
            String appKeyStr[] = ChatClient.getInstance().getAppKey().split("#");
            jsonMsg.put("orgName", appKeyStr[0]);
            jsonMsg.put("appName", appKeyStr[1]);
            jsonMsg.put("userName", ChatClient.getInstance().getCurrentUserName());
            jsonMsg.put("resource", "mobile");
            jsonInvit.put("liveStreamInvitation", jsonMsg);
            message.setAttribute("msgtype", jsonInvit);
            message.setAttribute("type", "rtcmedia/video");
            ChatClient.getInstance().chatManager().sendMessage(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CustomChatRowProvider onSetCustomChatRowProvider() {
        return new MyCustomChatRowProvider();
    }

    private class MyCustomChatRowProvider implements CustomChatRowProvider {
        @Override
        public ChatRow getCustomChatRow(Message message, int position, BaseAdapter adapter) {
            if (message.getType() == Message.Type.TXT) {
                if (MessageHelper.getOrderInfo(message) != null) {
                    return new ChatRowOrder(getActivity(), message, position, adapter);
                } else if (MessageHelper.getVisitorTrack(message) != null) {
                    return new ChatRowTrack(getActivity(), message, position, adapter);
                }else if (MessageHelper.getEvalRequest(message) != null) {
                    return new ChatRowEvaluation(getActivity(), message, position, adapter);
                }

            }
            return null;
        }

        @Override
        public int getCustomChatRowType(Message message) {
            if (message.getType() == Message.Type.TXT) {
                if (MessageHelper.getOrderInfo(message) != null) {
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ORDER : MESSAGE_TYPE_SENT_ORDER;
                } else if (MessageHelper.getVisitorTrack(message) != null) {
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRACK : MESSAGE_TYPE_SENT_TRACK;
                }else if (MessageHelper.getEvalRequest(message) != null){
                    return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EVAL : MESSAGE_TYPE_SENT_EVAL;
                }
            }
            return -1;
        }

        @Override
        public int getCustomChatRowTypeCount() {
            return 6;
        }

    }


}