package com.example.omi.navigationdrawercommondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by omi on 5/15/2016.
 */
public class LoginActivity extends AppCompatActivity {
    ImageButton btnLogin;
    Button btnLinkToRegister;
    EditText inputUserName;
    EditText inputPassword;
    TextView loginErrorMsg;
    private ProgressDialog pDialog;
    String user_name;
    Button forgot_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setHomeButtonEnabled(true);

        inputUserName = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (ImageButton) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        forgot_pass = (Button) findViewById(R.id.forgot_pass);
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String userName  = inputUserName.getText().toString();
                final String password = inputPassword.getText().toString();
                if(userName.length() != 0 && password.length() != 0)
                {
                    JSONObject loginJson = new JSONObject();
                    try
                    {
                        loginJson.put("user_name",userName);
                        loginJson.put("password",password);

                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    System.out.println("login json: "+loginJson.toString());

                    pDialog = new ProgressDialog(LoginActivity.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    JsonObjectRequest jsonRequest = new JsonObjectRequest
                            (Request.Method.POST, WebService.loginURL, loginJson, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                    // the response is already constructed as a JSONObject!
                                    try {
                                        System.out.println("login response: "+response.toString());

                                        if(response.has("success"))
                                        {
                                            user_name = response.getString("user_name");
                                            ((BCSApplication)getApplication()).setUserName(user_name);
                                            Intent intent = new Intent(LoginActivity.this,TestActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            JSONObject error = response.getJSONObject("error");
                                            String text = error.getString("text");
                                            loginErrorMsg.setText(text);

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
                                    Toast.makeText(LoginActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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
                    Volley.newRequestQueue(LoginActivity.this).add(jsonRequest);

                }
                else
                {
                    loginErrorMsg.setText("Please fill up all input");
                    //Toast.makeText(LoginActivity.this,"plz fill up the form completely", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                //finish();
            }
        });
    }

}
