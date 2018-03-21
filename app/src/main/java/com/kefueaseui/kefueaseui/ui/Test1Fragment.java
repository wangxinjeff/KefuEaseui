package com.kefueaseui.kefueaseui.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.kefueaseui.kefueaseui.R;

/**
 * Created by magic on 2017/7/31.
 */

public class Test1Fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test1, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Button btn = (Button)getView().findViewById(R.id.btn_test);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new IntentBuilder(getActivity())
                        .setServiceIMNumber("test")
                        .setTitleName("测试客服001")
                        .setShowUserNick(false)
                        .build();

                startActivity(intent);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
