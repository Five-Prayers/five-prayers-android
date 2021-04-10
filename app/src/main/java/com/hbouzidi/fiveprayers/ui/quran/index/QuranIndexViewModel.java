package com.hbouzidi.fiveprayers.ui.quran.index;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.database.QuranBookmarkRegistry;
import com.hbouzidi.fiveprayers.quran.dto.QuranBookmark;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;
import com.hbouzidi.fiveprayers.utils.Decompressor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranIndexViewModel extends AndroidViewModel {

    private static final String TAG = "QuranViewModel";

    private final MutableLiveData<List<Surah>> mSurahs;
    private final MutableLiveData<List<QuranPage>> mQuranPages;
    private final MutableLiveData<List<QuranBookmark>> mQuranBookmarks;
    private final MutableLiveData<Integer> mPercentage;
    private final MutableLiveData<Integer> mUnzipPercentage;
    private final MutableLiveData<Long> mDownloadID;
    private final MutableLiveData<Boolean> mDownloadAndUnzipFinished;
    private final MutableLiveData<Boolean> mDownloadError;
    private final MutableLiveData<Boolean> mUnzipError;
    private final QuranBookmarkRegistry quranBookmarkRegistry;

    @Inject
    public QuranIndexViewModel(@NonNull Application application, QuranBookmarkRegistry quranBookmarkRegistry) {
        super(application);
        mSurahs = new MutableLiveData<>();
        mQuranPages = new MutableLiveData<>();
        mQuranBookmarks = new MutableLiveData<>();
        mPercentage = new MutableLiveData<>();
        mDownloadID = new MutableLiveData<>();
        mDownloadError = new MutableLiveData<>();
        mUnzipError = new MutableLiveData<>();
        mUnzipPercentage = new MutableLiveData<>();

        mDownloadAndUnzipFinished = new MutableLiveData<>();

        this.quranBookmarkRegistry = quranBookmarkRegistry;

        updateLiveData(application.getApplicationContext());
    }

    public void updateLiveData(Context context) {
        List<QuranPage> quranPages = QuranParser.getInstance().getQuranPages(context);
        mQuranPages.postValue(quranPages);

        List<Surah> surahs = QuranParser.getInstance().getSurahs(context);
        mSurahs.postValue(surahs);

        mQuranBookmarks.postValue(quranBookmarkRegistry.getAllBookmarks());
    }

    public void downloadAnsUnzipQuranImages(Context applicationContext) {
        String url = BuildConfig.QURAN_IMAGES_ZIP_URL;

        File unzipDestinationFolder = new File(applicationContext.getFilesDir().getAbsolutePath() + "/" + BuildConfig.QURAN_IMAGES_FOLDER_NAME);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(applicationContext, applicationContext.getFilesDir().getAbsolutePath(), BuildConfig.QURAN_IMAGES_FOLDER_NAME + ".zip")
                .setTitle("Quran Images files")
                .setDescription("Downloading")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        DownloadManager downloadManager = (DownloadManager) applicationContext.getSystemService(DOWNLOAD_SERVICE);
        long downloadID = downloadManager.enqueue(request);

        mDownloadID.postValue(downloadID);
        mPercentage.postValue(0);
        mUnzipPercentage.postValue(0);

        new Thread(() -> {
            boolean finishDownload = false;
            int progress;

            while (!finishDownload) {
                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    switch (status) {
                        case DownloadManager.STATUS_FAILED: {
                            finishDownload = true;
                            mDownloadError.postValue(true);
                            break;
                        }
                        case DownloadManager.STATUS_PAUSED:
                            break;
                        case DownloadManager.STATUS_PENDING:
                            break;
                        case DownloadManager.STATUS_RUNNING: {
                            final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            if (total >= 0) {
                                final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                                mPercentage.postValue((int) ((downloaded * 100L) / total));
                            }
                            break;
                        }
                        case DownloadManager.STATUS_SUCCESSFUL: {
                            progress = 100;
                            mPercentage.postValue(progress);

                            finishDownload = true;

                            String fullPath;
                            File downloadedFile;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                fullPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                downloadedFile = new File(Uri.parse(fullPath).getPath());
                            } else {
                                fullPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                downloadedFile = new File(fullPath);
                            }

                            Decompressor unzip;
                            try {
                                unzip = new Decompressor(downloadedFile, unzipDestinationFolder.getCanonicalPath());
                                mDownloadAndUnzipFinished.postValue(unzip.unzip(mUnzipPercentage));
                            } catch (IOException e) {
                                mDownloadError.postValue(true);
                                Log.e(TAG, "Error while Unzipping Quran files : Cannot getCanonicalPath", e);
                            }

                            if (downloadedFile.exists() && downloadedFile.delete()) {
                                Log.i(TAG, "Temporary Quran zip file deleted");
                            } else {
                                Log.e(TAG, "Cannot delete temporary Quran zip file deleted");
                            }

                            break;
                        }
                    }
                }
                cursor.close();
            }
        }).start();
    }

    public MutableLiveData<List<Surah>> getSurahs() {
        return mSurahs;
    }

    public MutableLiveData<List<QuranPage>> getQuranPages() {
        return mQuranPages;
    }

    public MutableLiveData<List<QuranBookmark>> getQuranBookmarks() {
        return mQuranBookmarks;
    }

    public MutableLiveData<Long> getmDownloadID() {
        return mDownloadID;
    }

    public MutableLiveData<Integer> getmPercentage() {
        return mPercentage;
    }

    public MutableLiveData<Boolean> getmDownloadAndUnzipFinished() {
        return mDownloadAndUnzipFinished;
    }

    public MutableLiveData<Boolean> getmDownloadError() {
        return mDownloadError;
    }

    public MutableLiveData<Boolean> getmUnzipError() {
        return mUnzipError;
    }

    public MutableLiveData<Integer> getmUnzipPercentage() {
        return mUnzipPercentage;
    }
}