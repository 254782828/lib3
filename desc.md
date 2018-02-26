Downloader
==========

A http/https downloader used to download files from server.

Usage
=====
* If you don't set the destination file path, the download manager will use `Environment.DIRECTORY_DOWNLOADS` in SDCard as default directory, and it will detect filename automatically from header or url if destinationFilePath not set:
```java
DownloadManager manager = downloadManager =
			new DownloadManager.Builder().context(this)
				.downloader(OkHttpDownloader.create())
				.threadPoolSize(2)
				.build();
String destPath = Environment.getExternalStorageDirectory() + File.separator + "test.apk";
DownloadRequest request = new DownloadRequest.Builder()
				.url("http://something.to.download")
				.retryTime(5)
				.retryInterval(2, TimeUnit.SECONDS)
				.progressInterval(1, TimeUnit.SECONDS)
				.priority(Priority.HIGH)
				.allowedNetworkTypes(DownloadRequest.NETWORK_WIFI)
				.destinationFilePath(destPath)
				.downloadCallback(new DownloadCallback() {
					@Override public void onStart(int downloadId, long totalBytes) {

					}

					@Override public void onRetry(int downloadId) {

					}

					@Override
					public void onProgress(int downloadId, long bytesWritten, long totalBytes) {

					}

					@Override public void onSuccess(int downloadId, String filePath) {

					}

					@Override public void onFailure(int downloadId, int statusCode, String errMsg) {

					}
				})
				.build();
int downloadId = manager.add(request);
```

It's easy to stop:
```java
	/* stop single */
	manager.cancel(downloadId);
	/* stop all */
	manager.cancelAll();
```

* If you don't want to set the filename but want to set the download directory, then you can use `destinationDirectory(String directory)`, but this method will be ignored if `destinationFilePath((String filePath)` was used.
* You can also set retry time with method `retryTime(int retryTime)` if necessary, default retry time is 1. You can set retry interval to decide how long to retry with method `retryInterval(long interval, TimeUnit unit)`.
* This manager support downloading in different network type with method `allowedNetworkTypes(int types)`, the types can be `DownloadRequest.NETWORK_MOBILE` and `DownloadRequest.NETWORK_WIFI`. This method need *android.permission.ACCESS_NETWORK_STATE* permission.
* The thread pool size of download manager is 3 by default. If you need a larger pool, then you can try the method `threadPoolSize(int poolSize)` in `DownloadManager#Builder`.
* You need *android.permission.WRITE_EXTERNAL_STORAGE* permission if you don't use public directory in SDCard as download destination file path. Don't forget to add *android.permission.INTERNET* permission.
* This download manager support breakpoint downloading, so you can restart the downloading after pause.
* If you don't want DownloadDispatcher invoke `onProgress(int downloadId, long bytesWritten, long totalBytes)` frequently, then you can use `progressInterval(long interval, TimeUnit unit)`.
* If you want one download request get high priority, then you can use `priority(Priority priority)`.
* The download manager provides two kinds of `Downloader`(`URLDownloader` and `OkHttpDownloader`), and the it will detect which downloader to use. You can also implement your own `Downloader` just like what `URLDownloader` and `OkHttpDownloader` do.

Download
========
	compile 'com.msxf.android.downloader:downloader:1.0.1'

Note
====
If you're using `OkHttpDownloader` with custom `OkHttpClient` as `Downloader` in `DownloadManager`, then you should not add [HttpLoggingInterceptor][2] in your custom `OkHttpClient`. It may be crashed(OOM) as `HttpLoggingInterceptor ` use `okio` to reqeust the whole body in memory.