package com.example.omi.navigationdrawercommondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.omi.navigationdrawercommondemo.Adapters.ErrorsShowDataAdapter;
import com.example.omi.navigationdrawercommondemo.Adapters.HistoryShowAdapter;
import com.example.omi.navigationdrawercommondemo.Adapters.ShowSavedQuestionsAdapter;
import com.example.omi.navigationdrawercommondemo.Model.History;
import com.example.omi.navigationdrawercommondemo.Model.Question;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.hoang8f.widget.FButton;

public class HistoryActivity extends DrawerActivity {
    LinearLayout mainLayout;
    ProgressDialog pDialog;
    JsonArrayRequest jsonRequest;
    History []histories;
    AdView mAdView;
    TextView user_name_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_history);
        super.onCreate(savedInstanceState);
        this.selectedNavigation = 3;

        this.user_name_view = (TextView) findViewById(R.id.user_name);
        String user_name = ((BCSApplication)getApplication()).getUserName();
        this.user_name_view.setText("History of "+user_name+":");

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7912682669795193~7592020668");

        this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        jsonRequest = new JsonArrayRequest
                (Request.Method.GET, WebService.historyURL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        try {
                            System.out.println("test response: "+response.toString());

                            if(response.length() == 0)
                            {
                                Toast.makeText(HistoryActivity.this, "no dataavailable", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(HistoryActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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


        Volley.newRequestQueue(this).add(jsonRequest);

    }


    public void parseTestData(JSONArray response) throws JSONException
    {
        System.out.println(response.toString());
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        histories = gson.fromJson(response.toString(),History[].class);
        System.out.println("histories size: "+histories.length);
        HistoryShowAdapter historyShowDataAdapter = new HistoryShowAdapter(histories);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.historyView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(historyShowDataAdapter);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
