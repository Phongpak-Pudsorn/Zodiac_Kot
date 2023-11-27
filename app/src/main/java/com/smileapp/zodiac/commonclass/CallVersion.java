package com.smileapp.zodiac.commonclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;

import com.smileapp.zodiac.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Muey on 11/8/2017.
 */

public class CallVersion {

    Activity activity;
    Context mContext;
    CallServer callServer;
    private CallVersionListener mCallVersionListener = null;
    public interface CallVersionListener{
        public void onSuccess();
        public void onGotUpdate();
        public void onFailed();
    }
    public CallVersion(Activity activity){
        this.activity = activity;
        this.mContext = activity;
        callServer = new CallServer();
        callServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setCallListener(CallVersionListener callListener){
        mCallVersionListener = callListener;
    }

    private class CallServer extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;
        JSONArray jsonArray;

        @Override
        protected String doInBackground(String... strings) {
            return getHttpUrlConnection("https://www.starvision.in.th/mobileweb/appsmartdirect/zodiac/serverweb/services/datajson/status_and_version_Android.json");
        }
        @Override
        protected void onPostExecute(String s) {
//            Log.e("s","hello test"+s);
            if (!s.equals("")) {
                try {
                    double current_version = 0;
                    double ver = Double.parseDouble(getVersionApp(mContext).replace(".","" ));
//                    Log.e("ver",""+ver);
                    current_version = ver;
                    String message = "", link = "";
                    boolean priority = false;

                    jsonObject = new JSONObject(s);
                    String status = jsonObject.getString("Status").trim();
                    if (status.equals("True")) {
                        jsonArray = jsonObject.getJSONArray("Version");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            message = jsonData.getString("message_alert_update_version").trim();
                            current_version = Double.parseDouble(jsonData.getString("current_version").replace(".","" ));
                            priority = jsonData.getBoolean("priority");
                            link = jsonData.getString("link").trim();
                            Utils.INSTANCE.setNameMonth(jsonData.getString("monthly"));
                            Utils.INSTANCE.setNameYear(jsonData.getString("yearly"));
                        }

                        if (ver >= current_version) {
                            //Go
//                            Log.e("if",""+ver+" "+current_version);
                            mCallVersionListener.onSuccess();
                        } else if (priority == true) {
                            //Not
//                            Log.e("else if",""+ver);
                            mCallVersionListener.onFailed();
                            alertDialogActive(message, link);
//							booData = false;
                        } else {
                            //Go
//                            Log.e("else",""+ver);
                            mCallVersionListener.onGotUpdate();
                            alertDialogActiveOrNot(message, link);
                        }
                    }else{
//                        Log.e("if out",""+ver);
                        mCallVersionListener.onSuccess();
                    }
                } catch (JSONException e) {
                    mCallVersionListener.onSuccess();
                    e.printStackTrace();
                }
            }
            else{
                mCallVersionListener.onSuccess();
            }

            super.onPostExecute(s);
        }
    }

    private void alertDialogActive(String message, final String link) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, com.starvision.bannersdk.R.style.AppTheme));
            builder.setMessage(message);
            // Button OK
            builder.setPositiveButton("อัพเดท", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (appInstalledOrNot(mContext, "com.android.vending")) {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
                            sendIntent.setClassName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity");
                            mContext.startActivity(sendIntent);
                        } else {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                        }
                    } catch (android.content.ActivityNotFoundException anfe) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        anfe.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activity.finish();
                }
            });
            builder.setCancelable(false);
            builder.create();
            AlertDialog dialog = builder.show();
            dialog.show();
        } catch (Exception e) {

        }

    }

    private void alertDialogActiveOrNot(String message, final String link) {
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, com.starvision.bannersdk.R.style.AppTheme));
            builder.setMessage(message);
            // Button OK
            builder.setNegativeButton("อัพเดต", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (appInstalledOrNot(mContext, "com.android.vending")) {
                            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                            sendIntent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
                            sendIntent.setClassName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity");
                            mContext.startActivity(sendIntent);
                        } else {
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
                        }
                    } catch (android.content.ActivityNotFoundException anfe) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                        anfe.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activity.finish();
                }
            });
            builder.setPositiveButton("อัพเดทภายหลัง", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallVersionListener.onSuccess();
                    dialog.dismiss();
                }
            });
            builder.setCancelable(true);
            builder.create();
            AlertDialog dialog = builder.show();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getHttpUrlConnection(String strUrl){
        String string = "";
        BufferedReader reader;
//        Log.e("getHttpUrlConnection", ""+string);
        try {
            StringBuilder stringBuilder;
            URL url = new URL(strUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(2000);
            urlConnection.setConnectTimeout(2000);
            urlConnection.setRequestMethod("GET");
//            urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
//            urlConnection.setRequestProperty("Accept","*/*");
            urlConnection.setDoOutput(false);
            urlConnection.connect();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = null;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            string = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            string = "";
        }
        return string;
    }

    public String getVersionApp(Context context){
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    private boolean appInstalledOrNot(Context mContext, String uri){
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


}
