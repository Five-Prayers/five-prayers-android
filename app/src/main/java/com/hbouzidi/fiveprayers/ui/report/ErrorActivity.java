package com.hbouzidi.fiveprayers.ui.report;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.grack.nanojson.JsonWriter;
import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.BaseActivity;
import com.hbouzidi.fiveprayers.utils.TimingUtils;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * inspired from NewPipe : https://github.com/TeamNewPipe/NewPipe
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class ErrorActivity extends BaseActivity {

    private static final String TAG = "ErrorActivity";
    private static final String ERROR_EMAIL_ADDRESS = BuildConfig.ERROR_EMAIL_ADDRESS;
    private static final String ERROR_EMAIL_SUBJECT
            = "Exception in Five Prayer Android " + BuildConfig.VERSION_NAME;

    public static final String ERROR_GITHUB_ISSUE_URL = BuildConfig.ERROR_GITHUB_ISSUE_URL;
    public static final String PRIVACY_POLICY_URL = BuildConfig.PRIVACY_POLICY_URL;

    private EditText userCommentBox;
    private String stackTrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        TextView errorView = findViewById(R.id.errorView);
        final Button reportEmailButton = findViewById(R.id.errorReportEmailButton);
        final Button reportGithubButton = findViewById(R.id.errorReportGitHubButton);
        final Button copyButton = findViewById(R.id.errorReportCopyButton);
        userCommentBox = findViewById(R.id.errorCommentBox);

        buildInfo();

        stackTrace = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());

        errorView.setText(decorateStackTrace(stackTrace));

        copyButton.setOnClickListener((View v) -> {
            copyToClipboard(this, buildMarkdown());
            Toast.makeText(this, R.string.msg_copied, Toast.LENGTH_SHORT).show();
        });

        reportEmailButton.setOnClickListener((View v) -> openPrivacyPolicyDialog(this, "EMAIL"));
        reportGithubButton.setOnClickListener(this::onGithubButtonClick);
    }

    private String decorateStackTrace(final String stackTrace) {
        return "-------------------------------------\n" + stackTrace +
                "-------------------------------------";
    }

    private void openPrivacyPolicyDialog(final Context context, final String action) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.mainStyles);
        int topColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundEndColor, ContextCompat.getColor(context, R.color.colorAccent));
        int iconTintColor = typedArray.getColor(R.styleable.mainStyles_iconsMainColor, ContextCompat.getColor(context, R.color.colorAccent));
        int topTitleColor = typedArray.getColor(R.styleable.mainStyles_textColorPrimary, ContextCompat.getColor(context, R.color.textColorPrimary));
        int colorAccent = typedArray.getColor(R.styleable.mainStyles_colorAccent, ContextCompat.getColor(context, R.color.colorAccent));

        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setCancelable(false)
                .setTopColor(topColor)
                .setButtonsColor(colorAccent)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIconTintColor(iconTintColor)
                .setTitle(R.string.privacy_policy_title)
                .setTopTitleColor(topTitleColor)
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setMessageGravity(Gravity.FILL)
                .setMessage(R.string.start_accept_privacy_policy)
                .setNeutralButton(R.string.read_privacy_policy, v -> openUrlInBrowser(context, PRIVACY_POLICY_URL))
                .setPositiveButton(R.string.common_accept, v -> {
                    if (action.equals("EMAIL")) {
                        String urlString = "mailto:" + Uri.encode(ERROR_EMAIL_ADDRESS) + "?subject=" + Uri.encode(ERROR_EMAIL_SUBJECT) + "&body=" + Uri.encode(buildJson());
                        final Intent i = new Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse(urlString))
                                .putExtra(Intent.EXTRA_EMAIL, new String[]{ERROR_EMAIL_ADDRESS})
                                .putExtra(Intent.EXTRA_SUBJECT, ERROR_EMAIL_SUBJECT)
                                .putExtra(Intent.EXTRA_TEXT, buildJson());
                        try {
                            startActivity(i);
                        } catch (ActivityNotFoundException e) {
                            Log.e(TAG, "Cannot open mail app", e);
                            Toast.makeText(context, "Cannot open mail app", Toast.LENGTH_LONG).show();
                        }
                    } else if (action.equals("GITHUB")) {
                        openUrlInBrowser(this, ERROR_GITHUB_ISSUE_URL);
                    }
                })
                .setNegativeButton(R.string.common_decline, v -> {
                })
                .show();
    }

    private String buildJson() {
        try {
            return JsonWriter.string()
                    .object()
                    .value("app_language", getAppLanguage())
                    .value("package", getPackageName())
                    .value("version", BuildConfig.VERSION_NAME)
                    .value("os", getOsString())
                    .value("time", getUTCDateTime())
                    .value("exceptions", stackTrace)
                    .value("user_comment", userCommentBox.getText().toString())
                    .end()
                    .done();
        } catch (Throwable e) {
            Log.e(TAG, "Error while erroring: Could not build json", e);
        }

        return "";
    }

    private String getAppLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    private String getOsString() {
        final String osBase = Build.VERSION.SDK_INT >= 23 ? Build.VERSION.BASE_OS : "Android";
        return System.getProperty("os.name")
                + " " + (osBase.isEmpty() ? "Android" : osBase)
                + " " + Build.VERSION.RELEASE
                + " - " + Build.VERSION.SDK_INT;
    }

    public String getUTCDateTime() {
        return TimingUtils.getUTCDateTime();
    }

    private void buildInfo() {
        TextView infoLabelView = findViewById(R.id.errorInfoLabelsView);
        TextView infoView = findViewById(R.id.errorInfosView);
        String text = "";

        infoLabelView.setText(getString(R.string.info_labels).replace("\\n", "\n"));

        text += StringUtils.capitalize(getAppLanguage()) + "\n"
                + getUTCDateTime() + "\n"
                + getPackageName() + "\n"
                + BuildConfig.VERSION_NAME + "\n"
                + getOsString();

        infoView.setText(text);
    }

    private String buildMarkdown() {
        try {
            final StringBuilder htmlErrorReport = new StringBuilder();

            final String userComment = userCommentBox.getText().toString();
            if (!userComment.isEmpty()) {
                htmlErrorReport.append(userComment).append("\n");
            }

            htmlErrorReport
                    .append("## Exception")
                    .append("\n* __App Language:__ ").append(getAppLanguage())
                    .append("\n* __Version:__ ").append(BuildConfig.VERSION_NAME)
                    .append("\n* __OS:__ ").append(getOsString()).append("\n");

            htmlErrorReport
                    .append("<details><summary><b>Exceptions")
                    .append("</b></summary><p>\n");

            htmlErrorReport.append("<b>Crash log ");
            htmlErrorReport.append("</b>");
            htmlErrorReport.append("\n```\n").append(stackTrace).append("\n```\n");

            htmlErrorReport.append("</p></details>\n");

            htmlErrorReport.append("<hr>\n");
            return htmlErrorReport.toString();
        } catch (Throwable e) {
            Log.e(TAG, "Error while erroring: Could not build markdown", e);
            return "";
        }
    }

    public static void openUrlInBrowser(final Context context, final String url) {
        final String defaultBrowserPackageName = getDefaultBrowserPackageName(context);

        try {
            if (defaultBrowserPackageName.equals("android")) {
                openInDefaultApp(context, url);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Cannot open browser app", e);
            Toast.makeText(context, "Cannot open browser app", Toast.LENGTH_LONG).show();
        }
    }

    private static String getDefaultBrowserPackageName(final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return Objects.requireNonNull(resolveInfo).activityInfo.packageName;
    }

    private static void openInDefaultApp(final Context context, final String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(Intent.createChooser(
                intent, context.getString(R.string.share_dialog_title))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void copyToClipboard(final Context context, final String text) {
        final ClipboardManager clipboardManager =
                ContextCompat.getSystemService(context, ClipboardManager.class);

        if (clipboardManager == null) {
            Toast.makeText(context,
                    R.string.permission_denied,
                    Toast.LENGTH_LONG).show();
            return;
        }

        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
        Toast.makeText(context, R.string.msg_copied, Toast.LENGTH_SHORT)
                .show();
    }

    private void onGithubButtonClick(View v) {
        openPrivacyPolicyDialog(this, "GITHUB");
    }
}