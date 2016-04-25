package com.msxf.downloader;

import android.net.Uri;

import static org.junit.Assert.*;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Vincent Cheung (coolingfall@gmail.com)
 */
@RunWith(RobolectricGradleTestRunner.class) @Config(constants = BuildConfig.class, sdk = 21)
public class URLDownloaderTest {
  private Uri uri;
  private Uri mockUri;
  private MockWebServer mockWebServer;
  private URLDownloader urlDownloader;

  @Before public void setUp() throws Exception {
    uri = Uri.parse(Constants.URL);
    mockWebServer = new MockWebServer();
    mockUri = Uri.parse(mockWebServer.url("/").toString());
    urlDownloader = URLDownloader.create();
  }

  @After public void close() throws Exception {
    urlDownloader.close();
  }

  @Test public void testFullDownload() throws Exception {
    urlDownloader.start(uri, 0);
    assertEquals(Constants.CONTENT_LENGTH, urlDownloader.contentLength());
    assertNotNull(urlDownloader.byteStream());
  }

  @Test public void testBreakpointDownload() throws Exception {
    urlDownloader.start(uri, 10000);
    assertEquals(Constants.CONTENT_LENGTH - 10000, urlDownloader.contentLength());
  }

  @Test public void testRedirection() throws Exception {
    MockResponse response =
        new MockResponse().setResponseCode(307).addHeader("Location", Constants.URL);
    mockWebServer.enqueue(response);
    urlDownloader.start(mockUri, 0);
    assertEquals(Constants.CONTENT_LENGTH, urlDownloader.contentLength());
  }

  @Test public void testBadUrl() {
    try {
      urlDownloader.start(Uri.parse("www.baidu.com"), 0);
    } catch (IOException e) {
      if (e instanceof DownloadException) {
        assertSame("url should start with http or https", e.getMessage());
      }
    }
  }

  @Test public void testHttps() throws Exception {
    urlDownloader.start(Uri.parse(Constants.HTTPS_URL), 0);
    assertEquals(Constants.HTTPS_CONTENT_LENGTH, urlDownloader.contentLength());
  }
}
