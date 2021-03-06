package com.zxmark.videodownloader.util;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.imobapp.videodownloaderforinstagram.BuildConfig;
import com.zxmark.videodownloader.MainActivity;
import com.zxmark.videodownloader.MainApplication;
import com.imobapp.videodownloaderforinstagram.R;
import com.zxmark.videodownloader.bean.VideoBean;
import com.zxmark.videodownloader.widget.IToast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by fanlitao on 17/6/13.
 */

public class Utils {


    public static final String HOST_91 = "91porn.com";
    public static final String HOST_FACEBOOK = "facebook.com";
    public static final String HOST_XVIDEOS = "xvideos.com";
    public static final String HOST_YOUJI = "youjizz.com";
    public static final String HOST_YG = "365yg.com";
    public static final String EXPIRE_SUFFIX_ARRAY[] = new String[]{HOST_91, HOST_FACEBOOK, HOST_XVIDEOS, HOST_YOUJI, HOST_YG};

    public static void openInstagramByUrl(String url) {

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        intent.setPackage("com.instagram.android");

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApplication.getInstance().getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://instagram.com/xxx")));
        }
    }


    public static boolean openAppByPackageName(String packageName) {
        try {
            Intent intent = MainApplication.getInstance().getApplicationContext().getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApplication.getInstance().getApplicationContext().startActivity(intent);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void openInstagram() {

        boolean result = openAppByPackageName("com.instagram.android");
        if (!result) {
            goToGpByPackageName(MainApplication.getInstance().getApplicationContext(), "com.instagram.android");
        }
    }


    public static void openKuaiShouApp(String url) {

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        intent.setPackage("com.smile.gifmaker");

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApplication.getInstance().getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://instagram.com/xxx")));
        }
    }

    public static void launchMySelf() {
        Intent intent = new Intent(MainApplication.getInstance().getApplicationContext(), MainActivity.class);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            MainApplication.getInstance().getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://instagram.com/xxx")));
        }
    }

    public static void copyText2Clipboard(String content) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            IToast.makeText(MainApplication.getInstance().getApplicationContext(), R.string.clipboard_copy_text, Toast.LENGTH_SHORT).show();
        }
        final Context context = MainApplication.getInstance().getApplicationContext();
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    public static String getTextFromClipboard() {
        final Context context = MainApplication.getInstance().getApplicationContext();
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String pastContent = cmb.getText().toString();
        LogUtil.e("main", "getTextFromClipboard:" + pastContent);
        if (!TextUtils.isEmpty(pastContent)) {
            String handledUrl = URLMatcher.getHttpURL(pastContent);
            return handledUrl;
        }

        return "";
    }


    public static void sendMyApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, MainApplication.getInstance().getResources().getString(R.string.app_name) +
                " is very userful to download instagram video or pictures"
                + "\n https://play.google.com/store/apps/details?id=com.imobapp.videodownloaderforinstagram");
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent sendIntent = Intent.createChooser(sharingIntent, "Share using");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApplication.getInstance().getApplicationContext().startActivity(sendIntent);
    }

    public static void goToGpByPackageName(Context context, String packageName) {
        final String appPackageName = packageName;

        try {
            Intent launchIntent = new Intent();
            launchIntent.setPackage("com.android.vending");
            launchIntent.setData(Uri.parse("market://details?id=" + appPackageName));
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void rateUs5Star() {
        Context context = MainApplication.getInstance().getApplicationContext();
        goToGpByPackageName(context, context.getPackageName());
    }

    public static void sendMeEmail() {
        Uri uri = Uri.parse("mailto:litton.van@gmail.com");
        Intent intent_eamil = new Intent(Intent.ACTION_SENDTO, uri);
        intent_eamil.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApplication.getInstance().getApplicationContext().startActivity(intent_eamil);
    }

    public static void startShareIntent(VideoBean videoBean) {
        if (videoBean != null) {
            Intent shareIntent = new Intent(
                    android.content.Intent.ACTION_SEND);
            shareIntent.setType(MimeTypeUtil.getMimeTypeByFileName(videoBean.videoPath));
            shareIntent.putExtra(
                    android.content.Intent.EXTRA_SUBJECT, videoBean.pageTitle);
            shareIntent.putExtra(
                    android.content.Intent.EXTRA_TITLE, videoBean.pageTitle);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(videoBean.videoPath)));
            shareIntent
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);
            Context context = MainApplication.getInstance().getApplicationContext();
            Intent chooseIntent = Intent.createChooser(shareIntent,
                    context.getString(R.string.str_share_this_video));
            chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(chooseIntent);
        }
    }

    public static void originalShareImage(Context context, String path) {
        Intent share_intent = new Intent();
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri imageContentUri =
                    FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(path));
            imageUris.add(imageContentUri);
        } else {
            imageUris.add(Uri.fromFile(new File(path)));
        }
        share_intent.setAction(Intent.ACTION_SEND_MULTIPLE);//设置分享行为
        share_intent.setType(MimeTypeUtil.getMimeTypeByFileName(path));//设置分享内容的类型
        share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        share_intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        context.startActivity(Intent.createChooser(share_intent, context.getString(R.string.str_share_this_video)));
    }

    public static void startShareIntent(String filePath) {
        if (filePath != null) {

            if (Build.VERSION.SDK_INT >= 24) {
                try {

                    File requestFile = new File(filePath);
                    Uri fileUri = FileProvider.getUriForFile(
                            MainApplication.getInstance(),
                            "com.androidapp.tool.videodownloaderforinstagram.fileprovider",
                            requestFile);
                    LogUtil.e("fan", "fileUri=" + fileUri);
                    MainApplication.getInstance().grantUriPermission(BuildConfig.APPLICATION_ID, fileUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent shareIntent = new Intent(
                            android.content.Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    //  shareIntent.setType(MimeTypeUtil.getMimeTypeByFileName(filePath));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

                    shareIntent
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Context context = MainApplication.getInstance().getApplicationContext();
                    Intent chooseIntent = Intent.createChooser(shareIntent,
                            context.getString(R.string.str_share_this_video));
                    chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(chooseIntent);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            "The selected file can't be shared: ");
                }
            } else {
                Intent shareIntent = new Intent(
                        android.content.Intent.ACTION_SEND);
                shareIntent.setType(MimeTypeUtil.getMimeTypeByFileName(filePath));
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                shareIntent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);
                Context context = MainApplication.getInstance().getApplicationContext();
                Intent chooseIntent = Intent.createChooser(shareIntent,
                        context.getString(R.string.str_share_this_video));
                chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(chooseIntent);
            }


        }
    }

    public static void changeLocale(String ccd) {
        Context context = MainApplication.getInstance();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
// 应用用户选择语言
        String array[] = ccd.split("-");
        config.locale = array.length == 1 ? new Locale(ccd, "") : new Locale(array[0], array[1]);
        resources.updateConfiguration(config, dm);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Globals.ACTION_CHANGE_LOCALE);
        context.startActivity(intent);
    }


    public static long getMyAppInstallTime() {
        final Context context = MainApplication.getInstance().getApplicationContext();
        try {
            PackageInfo pif = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return pif.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis();
    }


    public static void writeFile(String content) {
        File writename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.txt"); // 相对路径，如果没有则要建立一个新的output。txt文件
        try {
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(content); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String replaceEscapteSequence(String rawUrl) {
        if (TextUtils.isEmpty(rawUrl)) {
            return rawUrl;
        }
        rawUrl = rawUrl.replace("&amp;", "&");
        rawUrl = rawUrl.replace("\\/", "/");
        return rawUrl;
    }
}
