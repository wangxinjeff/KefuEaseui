package com.kefueaseui.kefueaseui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.util.EMLog;

/**
 * Created by magic on 2017/4/14.
 */

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ChatClient.getInstance().isLoggedInBefore()){
            return;
        }
        //username
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");
        //call to
        String to = intent.getStringExtra("to");
        if (!to.equals(ChatClient.getInstance().getCurrentUserName())){
            return;
        }

        if ("video".equals(type)){// video call
//            context.startActivity(new Intent(context, VideoCallActivity.class)
//                    .putExtra("username", from).putExtra("isComingCall", true)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        EMLog.d("callreceiver", "app receiver");
    }

}
