package com.hbouzidi.fiveprayers.di.factory.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class WorkerProviderFactory extends WorkerFactory {

    private final Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> workersFactories;

    @Inject
    public WorkerProviderFactory(Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> workersFactories) {
        this.workersFactories = workersFactories;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName, @NonNull WorkerParameters workerParameters) {
        Provider<ChildWorkerFactory> factoryProvider = getWorkerFactoryProviderByKey(workersFactories, workerClassName);
        return factoryProvider.get().create(appContext, workerParameters);
    }

    private Provider<ChildWorkerFactory> getWorkerFactoryProviderByKey(Map<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> map, String key) {
        for (Map.Entry<Class<? extends ListenableWorker>, Provider<ChildWorkerFactory>> entry : map.entrySet()) {
            if (Objects.equals(key, entry.getKey().getName())) {
                return entry.getValue();
            }
        }
        return null;
    }
}

