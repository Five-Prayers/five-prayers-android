package com.hbouzidi.fiveprayers.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class FileDownloader {

    private static final String TAG = "FileDownloader";

    private final PostDownload callback;
    private File file;
    private final String downloadLocation;

    public FileDownloader(String downloadLocation, PostDownload callback) {
        this.callback = callback;
        this.downloadLocation = downloadLocation;
    }

    public void download(String aurl, MutableLiveData<Integer> mPercentage) {
        int count;

        try {
            URL url = new URL(aurl);
            URLConnection connection = url.openConnection();
            connection.connect();

            int lenghtOfFile = connection.getContentLength();
            Log.d(TAG, "Length of the file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            file = new File(downloadLocation);
            FileOutputStream output = new FileOutputStream(file);
            Log.d(TAG, "file saved at " + file.getAbsolutePath());

            byte[] data = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                mPercentage.postValue((int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            onPostExecute();
        } catch (Exception e) {
            Log.e(TAG, "Enable to download file from " + aurl, e);
        }
    }

    protected void onPostExecute() {
        if (callback != null) callback.downloadDone(file);
    }

    public interface PostDownload {
        void downloadDone(File fd);
    }
}
