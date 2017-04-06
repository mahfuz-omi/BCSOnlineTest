package com.example.omi.navigationdrawercommondemo.services;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import com.example.omi.navigationdrawercommondemo.TestActivity;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by omi on 5/29/2016.
 */
public class PushNotificationRegistrationService extends Service {

    GoogleCloudMessaging gcm;
    String regid="";
    String PROJECT_NUMBER = "318878364407";
    String PREFERENCE_FIRST_RUN = "isFirstRun";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public void getRegId()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    System.out.println("reg key omi3: "+regid);
                    Log.i("GCM",  msg);

                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
                send_reg_id();
            }
        }.execute(null, null, null);

    }


    public void send_reg_id() {
        JSONObject regJson = new JSONObject();
        try
        {
            regJson.put("reg_id",regid);
        }
        catch(Exception e)
        {
            System.out.println("pn json creation error"+e.getMessage());
            e.printStackTrace();
        }


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, WebService.pnRegistrationURL, regJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            System.out.println("pn registration response: "+response.toString());

                            if(response.has("success"))
                            {
                                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(PushNotificationRegistrationService.this);
                                p.edit().putBoolean(PREFERENCE_FIRST_RUN, false).commit();
                                //Toast.makeText(PushNotificationRegistrationService.this, "pn registration succeeded", Toast.LENGTH_SHORT).show();
                                System.out.println("pn successful");
                            }
                            else
                            {
                                JSONObject error = response.getJSONObject("error");
                                String text = error.getString("text");
                                System.out.println("pn failed");
                                //loginErrorMsg.setText(text);
                                //System.out.println("pn reg failed: "+text);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("error in pn registration");
                        //Toast.makeText(PushNotificationRegistrationService.this, "Network error...faild to register Notification service", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("access-token",((BCSApplication)getApplication()).getAccessToken());
                return headers;

            }
        };

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(PushNotificationRegistrationService.this).add(jsonRequest);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        this.getRegId();
        return super.onStartCommand(intent, flags, startId);

    }
}
