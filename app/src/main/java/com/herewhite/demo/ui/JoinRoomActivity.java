package com.herewhite.demo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.herewhite.demo.DemoAPIv5;
import com.herewhite.demo.R;
import com.herewhite.demo.RoomActivity;
import com.herewhite.demo.manager.SettingManager;
import com.herewhite.demo.utils.SelectUtil;

public class JoinRoomActivity extends Activity implements View.OnClickListener, TextWatcher {
    private static final String TAG = "JoinRoomActivity";
    public static final String EXTRA_MESSAGE = "com.example.whiteSDKDemo.UUID";
    public static final String EXTRA_MESSAGE_ROOMTOKEN = "com.example.whiteSDKDemo.roomToken";
    public static final String EXTRA_MESSAGE_M3U8 = "com.example.whiteSDKDemo.m3u8";
    public static final String EXTRA_MESSAGE_NAME = "com.example.whiteSDKDemo.name";
    private EditText mEditText;
    private EditText mSubEditText;
    private Button mBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);
        initView();
    }

    private void initView() {
        setListener(R.id.back, true);
        setListener(R.id.btn_deal, false);
        mBtn = findViewById(R.id.btn_deal);
        mEditText = findViewById(R.id.editText);
        mSubEditText = findViewById(R.id.editText_sub);
        mEditText.addTextChangedListener(this);
        mSubEditText.addTextChangedListener(this);

        String lastName = SettingManager.get().getName();
        if (!TextUtils.isEmpty(lastName)) {
            mEditText.setText(lastName);
        }
    }
    private void setListener(int id, boolean setClickEff) {
        View view = findViewById(id);
        if (view != null) {
            if (setClickEff) {
                SelectUtil.setSelect(view);
            }
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_deal:
                joinNewRoom();
                break;

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TAG, "");
        String name = mEditText.getText().toString();
        String roomNumber = mSubEditText.getText().toString();
        Log.d(TAG, "afterTextChanged:name:" + name + ",roomNumber:" + roomNumber);
        if (!TextUtils.isEmpty(mEditText.getText().toString()) && !TextUtils.isEmpty(mSubEditText.getText().toString())) {
            mBtn.setEnabled(true);
        } else {
            mBtn.setEnabled(false);
        }
    }

    private void joinNewRoom() {
        DemoAPIv5.get().getRoomToken(mSubEditText.getText().toString(), new DemoAPIv5.Result() {
            @Override
            public void success(String uuid, String roomToken) {
                joinRoom(uuid, roomToken, mEditText.getText().toString());
            }

            @Override
            public void fail(String message) {
                tokenAlert(message);
            }
        });
    }

    private void joinRoom(String uuid, String roomToken, String name) {
        if (!DemoAPIv5.get().validateToken()) {
            tokenAlert();
            return;
        }
        Intent intent = new Intent(this, WhiteRoomActivity.class);

        if (uuid.length() > 0) {
            intent.putExtra(EXTRA_MESSAGE, uuid);
            intent.putExtra(EXTRA_MESSAGE_ROOMTOKEN, roomToken);
            intent.putExtra(EXTRA_MESSAGE_NAME, name);
        }

        startActivity(intent);
        finish();
    }

    private void tokenAlert() {
        tokenAlert(getString(R.string.need_get_sdk_token));
    }

    private void tokenAlert(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JoinRoomActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
