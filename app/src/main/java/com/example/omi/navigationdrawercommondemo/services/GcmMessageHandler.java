package com.example.omi.navigationdrawercommondemo.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.navigationdrawercommondemo.BCSApplication;
import com.example.omi.navigationdrawercommondemo.R;
import com.example.omi.navigationdrawercommondemo.ShowQuestionsActivity;
import com.example.omi.navigationdrawercommondemo.TestActivity;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.example.omi.navigationdrawercommondemo.receivers.GCMBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by omi on 5/27/2016.
 */
public class GcmMessageHandler extends IntentService {
    int notification_id = 0;
    String title;
    String text;
    String set_id_text;
    private Handler handler;
    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        title = extras.getString("title");
        text = extras.getString("text");
        set_id_text = extras.getString("extra_data");
        showNotification();
        Log.i("GCM", "Received : (" +messageType+")  "+extras.getString("title"));

        GCMBroadcastReceiver.completeWakefulIntent(intent);

    }

    public void showNotification(){

        if(((BCSApplication)getApplication()).isUserLoggedIn())
        {
            JSONObject jobj = null;
            try{
                jobj = new JSONObject();
                jobj.put("set_id",set_id_text);

            }catch(Exception e)
            {
                e.printStackTrace();
            }
            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, WebService.isUserSubmittedSetURL,jobj , new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try
                            {
                                System.out.println("push response: "+response.toString());

                                if(response.has("value"))
                                {
                                    String isUserSubmittedSetURL = response.getString("value");
                                    if(isUserSubmittedSetURL.equalsIgnoreCase("false"))
                                    {
                                        Intent intent = new Intent(GcmMessageHandler.this, ShowQuestionsActivity.class);
                                        int set_id = Integer.parseInt(set_id_text);
                                        intent.putExtra("set_id",set_id);
                                        PendingIntent pIntent = PendingIntent.getActivity(GcmMessageHandler.this, (int) System.currentTimeMillis(), intent, 0);
                                        Notification n  = new Notification.Builder(GcmMessageHandler.this)
                                                .setContentTitle(title)
                                                .setContentText(text)
                                                .setSubText("Set No: "+set_id_text)
                                                .setSmallIcon(R.drawable.ic_main_icon)
                                                .setContentIntent(pIntent)
                                                .setAutoCancel(true)
                                                .build();
                                        NotificationManager notificationManager =
                                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        n.flags |= Notification.FLAG_AUTO_CANCEL;
                                        notificationManager.notify(notification_id, n);
                                        notification_id++;

                                    }

                                }
                                else
                                {
                                    System.out.println("no value obtained");
                                }

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener()
                    {

                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            System.out.println("error in push");

                        }
                    }
                    )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String,String> headers = new HashMap<>();
                    headers.put("access-token",((BCSApplication)getApplication()).getAccessToken());
                    return headers;
                }
            };

            {
            };
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(GcmMessageHandler.this).add(jsonRequest);
        }
    }
}
