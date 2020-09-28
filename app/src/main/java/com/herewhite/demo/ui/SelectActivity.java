package com.herewhite.demo.ui;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.herewhite.demo.DemoAPIv5;
import com.herewhite.demo.R;
import com.herewhite.demo.RoomActivity;
import com.herewhite.demo.utils.SelectUtil;

public class SelectActivity extends BaseActivity implements View.OnClickListener {
    DemoAPIv5 demoAPI = DemoAPIv5.get();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initView();
        demoAPI.downloadZip("https://convertcdn.netless.link/dynamicConvert/e1ee27fdb0fc4b7c8f649291010c4882.zip", getCacheDir().getAbsolutePath());

        checkPermission(null, R.string.ask_again,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE);
    }

    private void initView() {
        setListener(R.id.select_join);
        setListener(R.id.select_create);
        setListener(R.id.select_text_open_source);
    }
    private void setListener(int id) {
        View view = findViewById(id);
        if (view != null) {
            SelectUtil.setSelect(view);
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_join:
                joinRoom();
                break;
            case R.id.select_create:
                joinNewRoom();
                break;
            case R.id.select_text_open_source:
                jumpUrl();
                break;
        }
    }

    private void jumpUrl() {
        try {
            Uri uri = Uri.parse("https://baike.baidu.com/item/MIT/10772952?fr=aladdin");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void joinRoom() {
        if (!demoAPI.validateToken()) {
            tokenAlert();
            return;
        }
        Intent intent = new Intent(this, JoinRoomActivity.class);

        startActivity(intent);
    }

    private void joinNewRoom() {
        if (!demoAPI.validateToken()) {
            tokenAlert();
            return;
        }
        Intent intent = new Intent(this, CreateRoomActivity.class);
        startActivity(intent);
    }

    private void tokenAlert() {
        tokenAlert(getString(R.string.need_get_sdk_token));
    }

    private void tokenAlert(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SelectActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
