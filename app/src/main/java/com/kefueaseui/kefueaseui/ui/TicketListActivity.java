package com.kefueaseui.kefueaseui.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.TicketEntity;
import com.hyphenate.helpdesk.domain.TicketListResponse;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.TitleBar;
import com.hyphenate.helpdesk.manager.TicketManager;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;

import java.util.List;

/**
 * Created by magic on 2017/4/19.
 */

public class TicketListActivity extends BaseActivity {


    private ListView listView;
    private TitleBar titleBar;
    private List<TicketEntity> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        getSupportFragmentManager().beginTransaction().add(R.id.frame,new TicketListFragment()).commit();
    }


}
