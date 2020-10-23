package com.herewhite.demo.manager;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.herewhite.demo.BuildConfig;
import com.herewhite.demo.app.App;

public class AliOssSdkManager {
    private static AliOssSdkManager mApi;
    private OSS mOss;

    private AliOssSdkManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();

    }

    public static AliOssSdkManager get() {
        if (mApi == null) {
            synchronized (AliOssSdkManager.class) {
                if (mApi == null) {
                    mApi = new AliOssSdkManager();
                }
            }
        }
        return mApi;
    }

    private boolean hasKey () {
        if (TextUtils.isEmpty(BuildConfig.AK) || TextUtils.isEmpty(BuildConfig.SK)
            || TextUtils.isEmpty(BuildConfig.BUCKET)
            || TextUtils.isEmpty(BuildConfig.FOLDER)
            || TextUtils.isEmpty(BuildConfig.OSSREGION)
            || TextUtils.isEmpty(BuildConfig.PREFIX)
        ) {
            return false;
        }
        return true;
    }
    @WorkerThread
    private void init() {
        if (!hasKey()) {
            //没有配置key信息,无法使用.
            return;
        }
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
//        String endpoint = BuildConfig.PREFIX;
//        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("<StsToken.AccessKeyId>", "<StsToken.SecretKeyId>", "<StsToken.SecurityToken>");

//该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog(); //这个开启会支持写入手机sd卡中的一份日志文件位置在SDCard_path\OSSLog\logs.csv

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(BuildConfig.AK, BuildConfig.SK, "");

        mOss = new OSSClient(App.get(), endpoint, credentialProvider, conf);
    }

    public String getBasePath() {
        String urlpath = "http://" + BuildConfig.BUCKET + ".oss-cn-hangzhou.aliyuncs.com/" + BuildConfig.FOLDER + "/";
        return urlpath;
    }

    public void upLoad(String obKey, String uploadFilePath, OSSProgressCallback<PutObjectRequest> progressCallback,  OSSCompletedCallback<PutObjectRequest, PutObjectResult> completedCallback) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(BuildConfig.BUCKET, BuildConfig.FOLDER + "/" + obKey, uploadFilePath);

// 异步上传时可以设置进度回调
        put.setProgressCallback(progressCallback/*new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        }*/);

        OSSAsyncTask task = mOss.asyncPutObject(put,completedCallback /*new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        }*/);

// task.cancel(); // 可以取消任务

// task.waitUntilFinished(); // 可以等待直到任务完成
    }
}
