package com.kefueaseui.kefueaseui.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.domain.NewTicketBody;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.widget.TitleBar;
import com.hyphenate.helpdesk.manager.TicketManager;
import com.kefueaseui.kefueaseui.MyApplication;
import com.kefueaseui.kefueaseui.R;
import com.google.gson.Gson;

/**
 * Created by magic on 2017/4/19.
 */

public class NewLeaveMessageActivity extends BaseActivity {

    private TitleBar titleBar;
    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextMail;
    private EditText editTextCon;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_leave_message);
        titleBar = $(R.id.title_bar);
        editTextName = $(R.id.lv_name);
        editTextPhone = $(R.id.lv_phone);
        editTextMail = $(R.id.lv_mail);
        editTextCon = $(R.id.lv_content);
        initView();
    }

    private void initView(){
        titleBar.setTitle("留言");
        titleBar.setBackgroundColor(getResources().getColor(R.color.holo_red_light));
        titleBar.setRightImageResource(R.mipmap.em_icon_comment);
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressDialog == null){
                    progressDialog = new ProgressDialog(NewLeaveMessageActivity.this);
                    progressDialog.setMessage(getString(R.string.leave_sending));
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.show();
                NewTicketBody ticketBody = new NewTicketBody();
                ticketBody.setContent(editTextCon.getText().toString());
                NewTicketBody.CreatorBean creatorBean = new NewTicketBody.CreatorBean();
                creatorBean.setEmail(editTextMail.getText().toString());
                creatorBean.setName(editTextName.getText().toString());
                creatorBean.setPhone(editTextPhone.getText().toString());
                ticketBody.setCreator(creatorBean);
                Gson gson = new Gson();
                TicketManager.getInstance().createLeaveMessage(gson.toJson(ticketBody).toString(), MyApplication.projectId, MyApplication.imService, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(final String s) {
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "发送成功"+s, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(final int i, final String s) {
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "发送失败："+i+"///"+s, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });

        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
