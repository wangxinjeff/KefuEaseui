package com.kefueaseui.kefueaseui.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.TicketEntity;
import com.hyphenate.helpdesk.domain.TicketListResponse;
import com.hyphenate.helpdesk.easeui.widget.TitleBar;
import com.hyphenate.helpdesk.manager.TicketManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.interfaces.IListener;
import com.kefueaseui.kefueaseui.ui.adapter.TicketListAdapter;
import com.kefueaseui.kefueaseui.utils.ListenerManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 留言列表界面
 * 此列表用了Easyrecyclerview (@link https://github.com/Jude95/EasyRecyclerView)
 */
public class TicketListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,IListener{
    private static final int MSG_LOAD_MORE_DATA = 0x01;
    private static final int MSG_REFRESH_DATA = 0x02;
    private static final int MSG_LOAD_ERROR = 0x03;
    private static final int PER_PAGE_COUNT = 15;
    private int mCurPageNo;
    private WeakHandler mWeakHandler;

    private List<TicketEntity> ticketEntityList = Collections.synchronizedList(new ArrayList<TicketEntity>());
    private EasyRecyclerView easyRecyclerView;
    private TicketListAdapter mAdapter;

    private TitleBar titleBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.em_fragment_tickets, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWeakHandler = new WeakHandler(this);
        mCurPageNo = 0;

        initView();
        loadFirstDatas();
    }



    private void initView(){
        titleBar = (TitleBar) getView().findViewById(R.id.title_bar);
        titleBar.setTitle("留言");
        titleBar.setBackgroundColor(getResources().getColor(R.color.holo_red_light));
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        easyRecyclerView = (EasyRecyclerView) getView().findViewById(R.id.recyclerview);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        easyRecyclerView.setLayoutManager(mLayoutManager);
        //设置分割线
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getActivity().getResources().getDisplayMetrics()));
        itemDecoration.setDrawLastItem(false);
        easyRecyclerView.addItemDecoration(itemDecoration);

        easyRecyclerView.setAdapterWithProgress(mAdapter = new TicketListAdapter(getActivity()));
        mAdapter.setMore(R.layout.em_view_more, new RecyclerArrayAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //加载更多
                loadMoreData();
            }
        });
        mAdapter.setError(R.layout.em_view_error);
        mAdapter.setNoMore(R.layout.em_view_nomore);
        easyRecyclerView.setRefreshListener(this);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TicketEntity ticketEntity = mAdapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), TicketDetailActivity.class);
                intent.putExtra("ticket", ticketEntity);
                startActivity(intent);
            }
        });

    }



    @Override
    public void notifyEvent(String str, Object obj) {
        if (str.equals("clearTicketEvent")){
            refreshView(new ArrayList<TicketEntity>());
        }else {
            onRefresh();
        }
    }


    private static class WeakHandler extends Handler {
        WeakReference<TicketListFragment> weakReference;
        public WeakHandler(TicketListFragment fragment){
            this.weakReference = new WeakReference<TicketListFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TicketListFragment fragment = weakReference.get();
            if (fragment != null){
                switch (msg.what){
                    case MSG_LOAD_MORE_DATA:
                        fragment.updateView((List<TicketEntity>) msg.obj);
                        break;
                    case MSG_REFRESH_DATA:
                        fragment.refreshView((List<TicketEntity>) msg.obj);
                        break;
                    case MSG_LOAD_ERROR:
                        fragment.loadDataError();
                        break;
                }


            }

        }
    }

    public void updateView(List<TicketEntity> entityList){
        if (entityList == null || entityList.size() == 0){
            easyRecyclerView.setRefreshing(false);
            mAdapter.stopMore();
            return;
        }
        ticketEntityList.addAll(entityList);
        mAdapter.addAll(entityList);
        mAdapter.notifyDataSetChanged();
        mAdapter.sort(new Comparator<TicketEntity>() {
            @Override
            public int compare(TicketEntity lhs, TicketEntity rhs) {
                return rhs.getUpdated_at().compareTo(lhs.getUpdated_at());
            }
        });
        mAdapter.pauseMore();
    }

    public void loadDataError(){
        easyRecyclerView.setRefreshing(false);
        easyRecyclerView.showError();
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    public void refreshView(List<TicketEntity> entityList){
        if (entityList == null){
            easyRecyclerView.setRefreshing(false);
            mAdapter.pauseMore();
            return;
        }
        ticketEntityList.clear();
        ticketEntityList.addAll(entityList);
        mAdapter.clear();
        mAdapter.addAll(ticketEntityList);
        mAdapter.notifyDataSetChanged();
        mAdapter.sort(new Comparator<TicketEntity>() {
            @Override
            public int compare(TicketEntity lhs, TicketEntity rhs) {
                return rhs.getUpdated_at().compareTo(lhs.getUpdated_at());
            }
        });
        if (entityList.size() < PER_PAGE_COUNT){
            mAdapter.stopMore();
        }
        mAdapter.pauseMore();
    }

    private void loadMoreData(){
        final int nextPage = mCurPageNo + 1;
        String target = MyApplication.imService;
        String tenantId = MyApplication.tenantId;
        String projectId = MyApplication.projectId;

        TicketManager.getInstance().getTickets(projectId, target, nextPage, PER_PAGE_COUNT, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                mCurPageNo = nextPage;
                Gson gson = new Gson();
                Message message = mWeakHandler.obtainMessage();
                TicketListResponse ticketListResponse = gson.fromJson(value, TicketListResponse.class);
                if (ticketListResponse != null){
                    message.obj = ticketListResponse.getEntities();
                }
                message.what = MSG_LOAD_MORE_DATA;
                mWeakHandler.sendMessage(message);
            }

            @Override
            public void onError(int error, String errorMsg) {
                mWeakHandler.sendEmptyMessage(MSG_LOAD_MORE_DATA);
            }
        });

    }


    private void loadFirstDatas() {
        String target = MyApplication.imService;
        String projectId = MyApplication.projectId;

        TicketManager.getInstance().getTickets(projectId, target, 0, PER_PAGE_COUNT, new ValueCallBack<String>() {
            @Override
            public void onSuccess(String value){
                Gson gson = new Gson();
                Message message = mWeakHandler.obtainMessage();
                TicketListResponse ticketListResponse = gson.fromJson(value,TicketListResponse.class);
                message.what = MSG_REFRESH_DATA;
                message.obj = ticketListResponse.getEntities();
                mCurPageNo = 0;
                mWeakHandler.sendMessage(message);

            }

            @Override
            public void onError(int error, String errorMsg) {
                mWeakHandler.sendEmptyMessage(MSG_LOAD_ERROR);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ListenerManager.getInstance().unRegisterListener(this);
    }

    @Override
    public void onRefresh() {
        loadFirstDatas();
    }





}
