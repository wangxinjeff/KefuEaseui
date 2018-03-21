package com.kefueaseui.kefueaseui.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.kefueaseui.kefueaseui.R;


/**
 * Created by magic on 2017/7/31.
 */

public class Test1Activity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
//        Test1Fragment fragment = new Test1Fragment();
//        getSupportFragmentManager().beginTransaction().add(R.id.frame1,fragment).commit();
    }
}
