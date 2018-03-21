package com.kefueaseui.kefueaseui.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.hyphenate.helpdesk.easeui.widget.EaseChatPrimaryMenu;

/**
 * Created by magic on 2017/8/4.
 */

public class CustomMenu extends EaseChatPrimaryMenu {

    public CustomMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public CustomMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMenu(Context context) {
        super(context);
    }

    @Override
    public boolean isRecording() {
        return super.isRecording();
    }

    @Override
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        super.onEmojiconInputEvent(emojiContent);
    }

    @Override
    public void onEmojiconDeleteEvent() {
        super.onEmojiconDeleteEvent();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    protected void setModeVoice() {
        super.setModeVoice();
    }

    @Override
    protected void setModeKeyboard() {
        super.setModeKeyboard();
    }

    @Override
    protected void toggleFaceImage() {
        super.toggleFaceImage();
    }

    @Override
    public void onExtendAllContainerHide() {
        super.onExtendAllContainerHide();
    }

    @Override
    public void setInputMessage(CharSequence txtContent) {
        super.setInputMessage(txtContent);
    }

    @Override
    public void setEmojiSendBtn(Button btn) {
        super.setEmojiSendBtn(btn);
    }
}
