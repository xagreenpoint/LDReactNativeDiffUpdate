package com.updatademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.yyh.lib.bsdiff.downloader.ModuleManager;
import com.yyh.lib.bsdiff.downloader.RnModuleDiffUpdateService;


/**
 * Created by Lynn on 2017/8/16.
 */

public class StartPage extends Activity implements View.OnClickListener {


    private Button action_bt0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startpage_layout);
        initView();
        Intent intent = new Intent(this, RnModuleDiffUpdateService.class);
        startService(intent);
    }


    private void initView() {
        action_bt0 = (Button) findViewById(R.id.action_bt0);
        action_bt0.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bt0:
                action();
                break;
        }
    }


    private void action() {
        String moduleName = "updataDemo";
        String path = ModuleManager.getMoudleJsBundleFile(this, moduleName);
        Intent intent = new Intent(this, MyReactActivity.class);
        intent.putExtra("jsbundlePath", path);
        intent.putExtra("moduleName", moduleName);
        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RnModuleDiffUpdateService.class);
        stopService(intent);
        super.onBackPressed();
    }
}
