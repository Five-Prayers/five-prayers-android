package com.hbouzidi.fiveprayers.di.factory.worker;

import android.content.Context;

import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public interface ChildWorkerFactory {

    ListenableWorker create(Context appContext, WorkerParameters workerParameters);
}
