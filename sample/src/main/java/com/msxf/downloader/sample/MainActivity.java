package com.msxf.downloader.sample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.msxf.downloader.DownloadCallback;
import com.msxf.downloader.DownloadManager;
import com.msxf.downloader.DownloadRequest;
import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private static final String TAG = "Downloader";
  private DownloadManager downloadManager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.btn_start).setOnClickListener(this);
    downloadManager = new DownloadManager.Builder().context(this).build();
  }

  @Override public void onClick(View v) {
    String url =
        "http://git.msxf.local/client/apps/raw/master/msjr/android/1.4.3/test/msjr_v1.4.3.2_internal_release.apk";
    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + File.separator
        + "test.apk";
    DownloadRequest request = new DownloadRequest.Builder().url(url)
        .destinationFilePath(filePath)
        .downloadCallback(callback)
        .build();
    downloadManager.add(request);
  }

  private DownloadCallback callback = new DownloadCallback() {
    @Override public void onProgress(int downloadId, long bytesWritten, long totalBytes) {
      Log.d(TAG, "progress: " + (bytesWritten * 100f / totalBytes));
    }

    @Override public void onSuccess(int downloadId, String filePath) {
      Log.d(TAG, "sucess: " + downloadId);
    }

    @Override public void onFailure(int downloadId, int statusCode, String errMsg) {
      Log.d(TAG, "fail: " + errMsg + " code: " + statusCode);
    }
  };
}
