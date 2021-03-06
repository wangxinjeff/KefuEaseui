package com.kefueaseui.kefueaseui.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.model.OrderInfo;
import com.kefueaseui.kefueaseui.R;

/**
 * Created by magic on 2017/4/14.
 */

public class ChatRowOrder extends ChatRow {


    ImageView mImageView;
    TextView mTextViewDes;
    TextView mTextViewprice;
    Button mBtnSend;
    TextView mChatTextView;
    @Override
    public void setUpView(Message message, int position, MessageList.MessageListItemClickListener itemClickListener) {
        super.setUpView(message, position, itemClickListener);
    }

    public ChatRowOrder(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void updateView() {
        super.updateView();
    }

    @Override
    protected void setMessageSendCallback() {
        super.setMessageSendCallback();
    }

    @Override
    protected void setMessageReceiveCallback() {
        super.setMessageReceiveCallback();
    }

    @Override
    protected void onFindViewById() {
        mTextViewDes = (TextView) findViewById(R.id.tv_description);
        mTextViewprice = (TextView) findViewById(R.id.tv_price);
        mImageView = (ImageView) findViewById(R.id.iv_picture);
        mChatTextView = (TextView) findViewById(R.id.tv_chatcontent);

    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        OrderInfo orderInfo = MessageHelper.getOrderInfo(message);
        if (orderInfo == null) {
            return;
        }
        mTextViewDes.setText(orderInfo.getDesc());
        mTextViewprice.setText(orderInfo.getPrice());
        String imageUrl = orderInfo.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)){
            Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(com.hyphenate.helpdesk.R.drawable.hd_default_image).into(mImageView);
        }

    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct()==Message.Direct.RECEIVE ? R.layout.hd_row_received_message : R.layout.chatrow_order,this);
    }

    @Override
    protected void onBubbleClick() {

    }

    @Override
    protected void onUpdateView() {

    }
}
