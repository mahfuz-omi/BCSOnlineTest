package com.example.omi.navigationdrawercommondemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
public class RegisterActivity extends AppCompatActivity {
    ImageButton btnRegister;
    Button btnLinkToLogin;
    EditText inputUserName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setHomeButtonEnabled(true);


        inputUserName = (EditText) findViewById(R.id.user_name);
        inputEmail = (EditText) findViewById(R.id.email_address);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (ImageButton) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);


        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String userName  = inputUserName.getText().toString();
                final String password = inputPassword.getText().toString();
                String email  = inputEmail.getText().toString();
                if(!isValidEmail(email))
                {
                    registerErrorMsg.setText("Please input a valid email ");
                    return;
                }
                if(userName.length() != 0 && password.length() != 0 && email.length() != 0)
                {
                    JSONObject registerJson = new JSONObject();
                    try
                    {
                        registerJson.put("user_name",userName);
                        registerJson.put("password",password);
                        registerJson.put("email_address",email);

                    }catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    System.out.println("register json: "+registerJson.toString());

                    pDialog = new ProgressDialog(RegisterActivity.this);
                    pDialog.setMessage("Please wait...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    JsonObjectRequest jsonRequest = new JsonObjectRequest
                            (Request.Method.POST, WebService.registerURL, registerJson, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (pDialog.isShowing())
                                        pDialog.dismiss();
                                    // the response is already constructed as a JSONObject!
                                    try {
                                        System.out.println("response: "+response.toString());

                                        if(response.has("success"))
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                                    RegisterActivity.this);
                                            builder.setTitle("Success");
                                            String message = "Your Registration has been successful. You have to activate your account through mail.Please go to the inbox or <p><font color='#e30000'>Spam</font> folder of your mail account to check a mail from <p><font color=#3eadeb>omiserve</font>@server.hostingbangladesh.com.Please open this mail and click the link from the mail. After completing activation,you can login.";
                                            builder.setMessage(Html.fromHtml(message));
                                            builder.setPositiveButton("Login now",
                                                    new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // TODO Auto-generated method stub
                                                            Intent i = new Intent(getApplicationContext(),
                                                                    LoginActivity.class);
                                                            startActivity(i);
                                                            finish();


                                                        }
                                                    });

                                            builder.setNegativeButton("Open mail",
                                                    new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // TODO Auto-generated method stub
                                                            Intent intent = new Intent(Intent.ACTION_MAIN);
                                                            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                                startActivity(Intent.createChooser(intent,"Choose Mail App"));
                                                                finish();
                                                            }
                                                        }
                                                    });

                                            AlertDialog alertdialog = builder.create();
                                            alertdialog.show();
                                        }
                                        else
                                        {
                                            JSONObject error = response.getJSONObject("error");
                                            String text = error.getString("text");
                                            registerErrorMsg.setText(text);

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
                                    Toast.makeText(RegisterActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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
                                System.out.println(key+":"+responseHeaders.get(key));

                            }
                            return super.parseNetworkResponse(response);
                        }
                    };

                    {
                    };
                    jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(RegisterActivity.this).add(jsonRequest);

                }
                else
                {
                    registerErrorMsg.setText("please fill up the all input");
                    //Toast.makeText(RegisterActivity.this,"plz fill up the form completely", Toast.LENGTH_LONG).show();
                }
            }

        });

        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });


    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
