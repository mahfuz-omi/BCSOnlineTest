package com.example.omi.navigationdrawercommondemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.util.Map;

public class FacebookORNormalLoginActivity extends Activity {
    LoginButton facebook_login;
    Button normal_login;
    CallbackManager callbackManager;
    private ProgressDialog pDialog;
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_ornormal_login);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7912682669795193~7592020668");


        facebook_login = (LoginButton) findViewById(R.id.facebook_login);
        normal_login = (Button) findViewById(R.id.normal_login);
        normal_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacebookORNormalLoginActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        callbackManager = CallbackManager.Factory.create();
        facebook_login.setReadPermissions("email");
        facebook_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                Toast.makeText(FacebookORNormalLoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                System.out.println(loginResult.getAccessToken());


                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                Log.v("LoginActivity", response.toString());
                                String email;
                                String id;
                                String name;
                                try
                                {
                                    email = object.getString("email");
                                    id = object.getString("id");
                                    name = object.getString("name");
                                    JSONObject fbLoginJson = new JSONObject();
                                    fbLoginJson.put("email",email);
                                    fbLoginJson.put("id",id);
                                    fbLoginJson.put("name",name);
                                    System.out.println("fb login JSON "+fbLoginJson.toString());
                                    //System.out.println(email+":"+id+":"+name);
                                    pDialog = new ProgressDialog(FacebookORNormalLoginActivity.this);
                                    pDialog.setMessage("Please wait...");
                                    pDialog.setCancelable(false);
                                    pDialog.show();

                                    JsonObjectRequest jsonRequest = new JsonObjectRequest
                                            (Request.Method.POST, WebService.fbLoginURL, fbLoginJson, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    if (pDialog.isShowing())
                                                        pDialog.dismiss();
                                                    // the response is already constructed as a JSONObject!
                                                    try {
                                                        System.out.println("login response: "+response.toString());

                                                        if(response.has("success"))
                                                        {
                                                            String user_name = response.getString("user_name");
                                                            ((BCSApplication)getApplication()).setUserName(user_name);
                                                            ((BCSApplication)getApplication()).setLoginWithFB();
                                                            Intent intent = new Intent(FacebookORNormalLoginActivity.this,TestActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else
                                                        {
                                                            JSONObject error = response.getJSONObject("error");
                                                            String text = error.getString("text");
                                                            Toast.makeText(FacebookORNormalLoginActivity.this, text, Toast.LENGTH_LONG).show();

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
                                                    Toast.makeText(FacebookORNormalLoginActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                                                    error.printStackTrace();
                                                }
                                            }
                                            )
                                    {
                                        @Override
                                        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                            Map<String, String> responseHeaders = response.headers;
                                            for(String key:responseHeaders.keySet())
                                            {
                                                if("access-token".equalsIgnoreCase(key))
                                                    ((BCSApplication)getApplication()).setAccess_token(responseHeaders.get(key));
                                                System.out.println(key+":"+responseHeaders.get(key));

                                            }
                                            System.out.println("omi token"+responseHeaders.get("access-token"));
                                            return super.parseNetworkResponse(response);
                                        }
                                    };

                                    {
                                    };
                                    jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                                            10000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    Volley.newRequestQueue(FacebookORNormalLoginActivity.this).add(jsonRequest);

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(FacebookORNormalLoginActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(FacebookORNormalLoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
