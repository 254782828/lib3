package com.msxf.downloader;

import android.net.Uri;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 23)
public class OkHttpDownloaderTest {
  private Uri uri;
  private Uri mockUri;
  private MockWebServer mockWebServer;
  private OkHttpDownloader okHttpDownloader;

  @Before public void setUp() throws Exception {
    uri = Uri.parse(Constants.URL);
    mockWebServer = new MockWebServer();
    mockUri = Uri.parse(mockWebServer.url("/").toString());
    okHttpDownloader = OkHttpDownloader.create();
  }

  @After public void close() throws Exception {
    okHttpDownloader.close();
  }

  @Test public void testFullDownload() throws Exception {
    okHttpDownloader.start(Uri.parse(Constants.URL), 0);
    assertEquals(Constants.CONTENT_LENGTH, okHttpDownloader.contentLength());
    assertNotNull(okHttpDownloader.byteStream());
  }

  @Test public void testBreakpointDownload() throws Exception {
    okHttpDownloader.start(uri, 2000000);
    assertEquals(Constants.CONTENT_LENGTH - 2000000, okHttpDownloader.contentLength());
  }

  @Test public void testCustomClient() throws Exception {
    OkHttpClient client = new OkHttpClient.Builder().build();
    OkHttpDownloader downloader = OkHttpDownloader.create(client);
    downloader.start(uri, 0);
    assertEquals(Constants.CONTENT_LENGTH, downloader.contentLength());
    assertNotNull(downloader.byteStream());
  }

  @Test public void testRedirection() throws Exception {
    MockResponse response =
        new MockResponse().setResponseCode(301).addHeader("Location", Constants.URL);
    mockWebServer.enqueue(response);
    okHttpDownloader.start(mockUri, 0);
    System.out.print("len: " + okHttpDownloader.contentLength());
    assertEquals(Constants.CONTENT_LENGTH, okHttpDownloader.contentLength());
  }

  @Test public void testBadUrl() {
    try {
      okHttpDownloader.start(Uri.parse("www.baidu.com"), 0);
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        assertEquals("unexpected url: www.baidu.com", e.getMessage());
      }
    }
  }

  @Test public void testHttps() throws Exception {
    okHttpDownloader.start(Uri.parse(Constants.HTTPS_URL), 0);
    assertEquals(Constants.HTTPS_CONTENT_LENGTH, okHttpDownloader.contentLength());
  }
}
