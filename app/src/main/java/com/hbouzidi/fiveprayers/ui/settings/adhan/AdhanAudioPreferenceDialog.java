package com.hbouzidi.fiveprayers.ui.settings.adhan;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.app.Activity.RESULT_OK;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AdhanAudioPreferenceDialog extends PreferenceDialogFragmentCompat {

    private static final String TAG = "AdhanAudioPreference";

    private static final String SAVE_STATE_VALUE = "AdhanAudioPreferenceDialog.value";
    private final CharSequence[] mExtraRingtones;
    private final CharSequence[] mExtraRingtoneTitles;
    private final MediaPlayer mMediaPlayer;
    private String mValue;

    public AdhanAudioPreferenceDialog(AdhanAudioPreference preference) {
        final Bundle b = new Bundle();
        b.putString(ARG_KEY, preference.getKey());
        mExtraRingtones = preference.getmExtraRingtones();
        mExtraRingtoneTitles = preference.getmExtraRingtoneTitles();
        mMediaPlayer = new MediaPlayer();

        setArguments(b);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mValue = savedInstanceState.getString(SAVE_STATE_VALUE, null);
        } else {
            mValue = getExtraRingtonePreference().getValue();
        }

        // Nexus 7 needs the keyboard hiding explicitly.
        // A flag on the activity in the manifest doesn't
        // apply to the dialog, so needs to be in code:
        Window window = requireActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);

        ListView listView = dialog.getListView();

        View addRingtoneView = LayoutInflater.from(listView.getContext()).inflate(R.layout.add_ringtone_item, listView, false);
        listView.addFooterView(addRingtoneView);

        return dialog;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        final Map<String, Uri> sounds = getAllSoundsMap();

        final String[] titleArray = sounds.keySet().toArray(new String[0]);
        final Uri[] uriArray = sounds.values().toArray(new Uri[0]);

        int index = mValue != null ? Arrays.asList(uriArray).indexOf(Uri.parse(mValue)) : -1;

        builder.setSingleChoiceItems(titleArray, index, (dialog, which) -> {
            final Map<String, Uri> sounds1 = getAllSoundsMap();
            final Uri[] uriArray1 = sounds1.values().toArray(new Uri[0]);

            if (which < uriArray1.length) {
                Uri uri = uriArray1[which];

                if (uri != null) {
                    if (uri.toString().length() > 0) {
                        initializeAndPlayAdhan(uri);
                    }
                    mValue = uri.toString();
                } else {
                    mValue = null;
                }
            } else {
                newRingtone();
            }
        });
    }

    @NotNull
    private Map<String, Uri> getAllSoundsMap() {
        final Map<String, Uri> sounds = new LinkedHashMap<>();

        if (mExtraRingtones != null) {
            for (CharSequence extraRingtone : mExtraRingtones) {
                Uri uri = UiUtils.uriFromRaw(extraRingtone.toString(), requireContext());
                String title = getExtraRingtoneTitle(extraRingtone);

                sounds.put(title, uri);
            }
        }

        sounds.putAll(getSounds());
        return sounds;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getExtraRingtonePreference().getCustomRingtoneRequestCode()) {
            if (resultCode == RESULT_OK) {
                final Uri fileUri = data.getData();
                final Context context = getContext();

                try {
                    File newFile = addCustomExternalRingtone(context, fileUri);

                    if (newFile != null) {
                        final Map<String, Uri> sounds = getAllSoundsMap();
                        final String[] titleArray = sounds.keySet().toArray(new String[0]);
                        final Uri[] uriArray = sounds.values().toArray(new Uri[0]);

                        int index = ArrayUtils.indexOf(titleArray, newFile.getName());

                        final ListView listView = ((AlertDialog) getDialog()).getListView();
                        listView.setAdapter(buildAdapter(context));

                        listView.setItemChecked(index, true);
                        listView.setSelection(index);
                        listView.clearFocus();

                        mValue = uriArray[index].toString();
                    } else {
                        Toast.makeText(context, getString(R.string.preference_unable_add_new_adhan), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | IllegalArgumentException e) {
                    Log.e(TAG, "Unable to add new ringtone: ", e);
                }

            } else {
                Toast.makeText(requireContext(), getString(R.string.preference_unable_add_new_adhan), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_STATE_VALUE, mValue);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        stopAdhanPreview();

        AdhanAudioPreference preference = getExtraRingtonePreference();

        if (positiveResult && preference.callChangeListener(mValue)) {
            preference.setValue(mValue);
        }
    }

    public String getValue() {
        return mValue;
    }

    private Map<String, Uri> getSounds() {
        Map<String, Uri> list = new TreeMap<>();
        File[] soundFiles = null;
        File customAdhanFolder = new File(requireContext().getFilesDir().getAbsolutePath() + "/" + getExtraRingtonePreference().getCustomAdhanFolderName());

        if (customAdhanFolder.exists()) {
            soundFiles = customAdhanFolder.listFiles();
        }

        if (soundFiles != null) {
            for (File file : soundFiles) {
                list.put(file.getName(), Uri.fromFile(file));
            }
        }
        return list;
    }

    private String getExtraRingtoneTitle(CharSequence name) {
        if (mExtraRingtones != null && mExtraRingtoneTitles != null) {
            int index = Arrays.asList(mExtraRingtones).indexOf(name);
            return mExtraRingtoneTitles[index].toString();
        }

        return null;
    }

    private void initializeAndPlayAdhan(Uri uri) {
        if (uri != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getContext(), uri);
                setAudioAttribute();
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAudioAttribute() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_ALARM);
            builder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
            builder.setLegacyStreamType(AudioManager.STREAM_ALARM);

            mMediaPlayer.setAudioAttributes(builder.build());
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
    }

    private void stopAdhanPreview() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    private AdhanAudioPreference getExtraRingtonePreference() {
        return (AdhanAudioPreference) getPreference();
    }

    private void newRingtone() {
        boolean hasPerm = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (hasPerm) {
            final Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("audio/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"audio/*", "application/ogg"});
            }
            startActivityForResult(chooseFile, getExtraRingtonePreference().getCustomRingtoneRequestCode());
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, getExtraRingtonePreference().getPermissionRequestCode());
        }
    }

    @WorkerThread
    public static File addCustomExternalRingtone(Context context, @NonNull Uri fileUri)
            throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new IOException("External storage is not mounted. Unable to install ringtones.");
        }

        if (ContentResolver.SCHEME_FILE.equals(fileUri.getScheme())) {
            fileUri = Uri.fromFile(new File(fileUri.getPath()));
        }

        String mimeType = context.getContentResolver().getType(fileUri);

        if (mimeType == null) {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }

        if (mimeType == null || !(mimeType.startsWith("audio/") || mimeType.equals("application/ogg"))) {
            throw new IllegalArgumentException("Ringtone file must have MIME type \"audio/*\"."
                    + " Given file has MIME type \"" + mimeType + "\"");
        }

        final File outFile = getUniqueExternalFile(context, getFileDisplayNameFromUri(context, fileUri), mimeType);

        if (outFile != null) {
            final InputStream input = context.getContentResolver().openInputStream(fileUri);
            final OutputStream output = new FileOutputStream(outFile);

            if (input != null) {
                byte[] buffer = new byte[10240];

                for (int len; (len = input.read(buffer)) != -1; ) {
                    output.write(buffer, 0, len);
                }

                input.close();
            }

            output.close();

            return outFile;
        } else {
            return null;
        }
    }

    @Nullable
    private static File getUniqueExternalFile(Context context, String fileName, String mimeType) {
        File externalStorage = new File(context.getFilesDir().getAbsolutePath() + "/" + BuildConfig.CUSTOM_ADHAN_SOUNDS_FOLDER_NAME);
        // Make sure the storage subdirectory exists
        //noinspection ResultOfMethodCallIgnored
        externalStorage.mkdirs();

        // Ensure the file has a unique name, as to not override any existing file
        return buildUniqueFile(externalStorage, mimeType, fileName);
    }

    @NonNull
    private static File buildUniqueFile(File externalStorage, String mimeType, String fileName) {
        final String[] parts = splitFileName(mimeType, fileName);

        String name = parts[0];
        String ext = (parts[1] != null) ? "." + parts[1] : "";

        File file = new File(externalStorage, name + ext);
        SecureRandom random = new SecureRandom();

        int n = 0;
        while (file.exists()) {
            if (n++ >= 32) {
                n = random.nextInt();
            }
            file = new File(externalStorage, name + " (" + n + ")" + ext);
        }

        return file;
    }

    @NonNull
    public static String[] splitFileName(String mimeType, String displayName) {
        String name;
        String ext;

        String mimeTypeFromExt;

        // Extract requested extension from display name
        final int lastDot = displayName.lastIndexOf('.');
        if (lastDot >= 0) {
            name = displayName.substring(0, lastDot);
            ext = displayName.substring(lastDot + 1);
            mimeTypeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    ext.toLowerCase());
        } else {
            name = displayName;
            ext = null;
            mimeTypeFromExt = null;
        }

        if (mimeTypeFromExt == null) {
            mimeTypeFromExt = "application/octet-stream";
        }

        final String extFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(
                mimeType);
        //noinspection StatementWithEmptyBody
        if (TextUtils.equals(mimeType, mimeTypeFromExt) || TextUtils.equals(ext, extFromMimeType)) {
            // Extension maps back to requested MIME type; allow it
        } else {
            // No match; insist that create file matches requested MIME
            name = displayName;
            ext = extFromMimeType;
        }


        if (ext == null) {
            ext = "";
        }

        return new String[]{name, ext};
    }

    private static String getFileDisplayNameFromUri(Context context, Uri uri) {
        String scheme = uri.getScheme();

        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getLastPathSegment();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            String[] projection = {OpenableColumns.DISPLAY_NAME};

            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }

        // This will only happen if the Uri isn't either SCHEME_CONTENT or SCHEME_FILE, so we assume
        // it already represents the file's name.
        return uri.toString();
    }

    private AdhanAudioPreferenceDialog.CheckedItemAdapter buildAdapter(Context context) {
        final Map<String, Uri> sounds = getAllSoundsMap();

        final String[] titleArray = sounds.keySet().toArray(new String[0]);

        final TypedArray a = context.obtainStyledAttributes(null, R.styleable.AlertDialog,
                R.attr.alertDialogStyle, 0);
        int layout = a.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
        a.recycle();
        return new AdhanAudioPreferenceDialog.CheckedItemAdapter(context, layout, android.R.id.text1, titleArray);
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId,
                                  CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
