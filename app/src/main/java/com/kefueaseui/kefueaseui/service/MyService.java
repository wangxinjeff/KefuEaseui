package com.kefueaseui.kefueaseui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Conversation;
import com.hyphenate.util.EMLog;
import com.kefueaseui.kefueaseui.MyAidl;

import java.util.Map;

/**
 * Created by magic on 2018/3/19.
 */

public class MyService extends Service {

    private IBinder mIBinder = new MyAidl.Stub(){
        @Override
        public void getConversations() throws RemoteException {
            Map<String, Conversation> list = ChatClient.getInstance().chatManager().getAllConversations();
            EMLog.e("conversations","size:"+list.size());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }
}
