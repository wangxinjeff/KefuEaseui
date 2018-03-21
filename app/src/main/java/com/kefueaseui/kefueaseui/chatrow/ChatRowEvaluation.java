package com.kefueaseui.kefueaseui.chatrow;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.hyphenate.chat.Message;

import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.kefueaseui.kefueaseui.R;
import com.kefueaseui.kefueaseui.ui.MyChatFragment;
import com.kefueaseui.kefueaseui.ui.SatisfactionActivity;

public class ChatRowEvaluation extends ChatRow {

    Button btnEval;
    private RatingBar ratingBar = null;

    public ChatRowEvaluation(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        if (MessageHelper.getEvalRequest(message) != null) {
            inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.em_row_received_satisfaction
                    : R.layout.em_row_sent_satisfaction, this);
        }
    }

    @Override
    protected void onFindViewById() {
        btnEval = (Button) findViewById(R.id.btn_eval);

    }

    @Override
    protected void onUpdateView() {
    }

    @Override
    protected void onSetUpView() {
        try {
            if(MessageHelper.getEvalRequest(message) != null){
                btnEval.setEnabled(true);
                btnEval.setText("立即评价");
                btnEval.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((Activity)context).startActivityForResult(new Intent(context, SatisfactionActivity.class)
                                .putExtra("msgId", message.getMsgId()), MyChatFragment.REQUEST_CODE_EVAL);

                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBubbleClick() {


    }

}
