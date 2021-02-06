package com.hbouzidi.fiveprayers.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompressor {
    private static final String TAG = "Decompressor";

    private final File _zipFile;
    private final String destinationPath;

    public Decompressor(File zipFile, String destinationPath) {
        _zipFile = zipFile;
        this.destinationPath = destinationPath;

        _dirChecker(destinationPath);
    }

    public boolean unzip(MutableLiveData<Integer> mUnzipPercentage) {
        try {
            Log.i(TAG, "Starting to unzip");
            InputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            int unzipIndex = 0;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v(TAG, "Unzipping " + ze.getName());
                unzipIndex++;

                if (ze.isDirectory()) {
                    _dirChecker(destinationPath + "/" + ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(new File(destinationPath, ze.getName()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;

                    while ((count = zin.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                        byte[] bytes = baos.toByteArray();
                        fout.write(bytes);
                        baos.reset();
                    }

                    mUnzipPercentage.postValue(unzipIndex);
                    fout.close();
                    zin.closeEntry();
                }

            }
            zin.close();
            Log.i(TAG, "Finished unzip");

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Unzip Error", e);
        }
        return false;
    }

    private void _dirChecker(String dir) {
        File f = new File(dir);
        Log.i(TAG, "creating dir " + dir);

        if (!f.exists() && dir.length() >= 0 && !f.isDirectory()) {
            f.mkdirs();
        }
    }
}
