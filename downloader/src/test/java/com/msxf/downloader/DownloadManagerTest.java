package com.msxf.downloader;

import android.os.Environment;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowLog;

import static org.junit.Assert.assertEquals;

/**
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class DownloadManagerTest {
  private DownloadManager downloadManager;
  private DownloadRequest request;

  @Before public void setUp() throws Exception {
    ShadowLog.stream = System.out;
    downloadManager = new DownloadManager.Builder().context(
        ShadowApplication.getInstance().getApplicationContext()).build();
    String filePath =
        ShadowEnvironment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + File.separator
            + "download.apk";

    request =
        new DownloadRequest.Builder().url(Constants.URL).destinationFilePath(filePath).build();
  }

  @After public void close() throws Exception {
    downloadManager.release();
  }

  @Test public void testDownload() throws Exception {
    int downloadId = downloadManager.add(request);
    assertEquals(1, downloadId);
  }

  @Test public void testDuplicate() throws Exception {
    int downloadId = downloadManager.add(request);
    assertEquals(1, downloadId);
    int downloadId2 = downloadManager.add(request);
    assertEquals(-1, downloadId2);
  }
}
