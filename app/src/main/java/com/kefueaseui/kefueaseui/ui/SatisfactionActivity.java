package com.kefueaseui.kefueaseui.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.kefueaseui.kefueaseui.R;

/**
 * Created by magic on 2017/4/17.
 */

public class SatisfactionActivity extends BaseActivity {

    private String msgId = "";
    private EditText ed_score;
    private EditText ed_evalua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satis);
        ed_score = $(R.id.ed_score);
        ed_evalua = $(R.id.ed_evalua);
        msgId = getIntent().getStringExtra("msgId");
        initView();
    }

    private void initView(){


        $(R.id.evaluation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String score = ed_score.getText().toString().trim();
                if(score.equals("1")||score.equals("2")||score.equals("3")||score.equals("4")||score.equals("5")){
                    MessageHelper.sendEvalMessage(msgId, score, ed_evalua.getText().toString().trim(), new Callback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "评价成功", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onError(final int i, final String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "评价失败："+i+"//"+s, Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

                }

            }
        });

    }
}
