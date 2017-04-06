package com.example.omi.navigationdrawercommondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.example.omi.navigationdrawercommondemo.services.PushNotificationRegistrationService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.vision.face.Face;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class TestActivity extends DrawerActivity {
    LinearLayout mainLayout;
    ProgressDialog pDialog;
    JsonArrayRequest jsonRequest;
    String PREFERENCE_FIRST_RUN = "isFirstRun";
    InterstitialAd mInterstitialAd;
    String clicked_setId;
    TextView user_name_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_test);
        super.onCreate(savedInstanceState);

        this.user_name_view = (TextView) findViewById(R.id.user_name);
        String user_name = ((BCSApplication)getApplication()).getUserName();
        this.user_name_view.setText("Hi "+user_name);


        this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        this.selectedNavigation = 1;

        if(!((BCSApplication)getApplication()).isUserLoggedIn())
        {
            Intent intent = new Intent(this,FacebookORNormalLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }



        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                beginRegularExecution();
            }
        });

        requestNewInterstitial();
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = p.getBoolean(PREFERENCE_FIRST_RUN, true);
        System.out.println("isFirstRun: "+isFirstRun);
        if(isFirstRun)
        {
            Intent startPush = new Intent(this, PushNotificationRegistrationService.class);
            startService(startPush);
        }
        this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        jsonRequest = new JsonArrayRequest
                (Request.Method.GET, WebService.questionSetsURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        // the response is already constructed as a JSONObject!
                        try {
                            System.out.println("test response: "+response.toString());

                            if(response.length() == 0)
                            {
                                Toast.makeText(TestActivity.this, "no data available", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                parseTestData(response);
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
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Toast.makeText(TestActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
                )
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


    }




    public void parseTestData(JSONArray response) throws JSONException
    {
        System.out.println(response.toString());

        for(int i=0;i<response.length();i++)
        {

            JSONObject jobj = response.getJSONObject(i);
            FButton setButton = new FButton(this);
            setButton.setBackgroundColor(getResources().getColor(R.color.fbutton_color_pumpkin));
            final String value = jobj.getString("set_id");
            setButton.setText("set id: "+value);
            setButton.setShadowEnabled(true);
            setButton.setShadowHeight(5);
            setButton.setCornerRadius(5);
            setButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clicked_setId = value;
                    if (mInterstitialAd.isLoaded())
                    {
                        mInterstitialAd.show();
                    }
                    else
                    {
                        beginRegularExecution();
                    }

                }
            });
           // setButton.setButtonM
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(setButton.getWidth(), setButton.getHeight());
//            params.leftMargin = 5;
//            params.rightMargin = 5;
//            params.topMargin = 5;
//            params.bottomMargin = 5;
            //setButton.setLayoutParams(params);
            //setButton.getLayoutParams().
            setButton.setFButtonPadding(5,5,5,5);
            mainLayout.addView(setButton);

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mainLayout.removeAllViews();
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        Volley.newRequestQueue(this).add(jsonRequest);

    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void beginRegularExecution()
    {
        Intent intent = new Intent(TestActivity.this,ShowQuestionsActivity.class);
        intent.putExtra("set_id",Integer.parseInt(clicked_setId));
        startActivity(intent);
    }
}
