package com.example.omi.navigationdrawercommondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email_address;
    TextView getPassword_error;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email_address = (EditText) findViewById(R.id.email_address);
        getPassword_error = (TextView) findViewById(R.id.getPassword_error);
    }


    public void get_password(View v)
    {
        String email = email_address.getText().toString();

        if(email.length() != 0)
        {
            JSONObject getPasswordJson = new JSONObject();
            try
            {
                getPasswordJson.put("email_address",email);

            }catch(Exception e)
            {
                e.printStackTrace();
            }

            System.out.println("getPassword json: "+getPasswordJson.toString());

            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.POST, WebService.getPasswordURL, getPasswordJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            // the response is already constructed as a JSONObject!
                            try {
                                System.out.println("getPassword response: "+response.toString());

                                if(response.has("success"))
                                {
                                    Toast.makeText(ForgotPasswordActivity.this, "Go to the inbox or Spam folder of your mail to get your password", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else
                                {
                                    JSONObject error = response.getJSONObject("error");
                                    String text = error.getString("text");
                                    getPassword_error.setText(text);

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
                            Toast.makeText(ForgotPasswordActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                    );

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(ForgotPasswordActivity.this).add(jsonRequest);

        }
        else
        {
            getPassword_error.setText("Please input email");
            //Toast.makeText(LoginActivity.this,"plz fill up the form completely", Toast.LENGTH_LONG).show();
        }

    }
}
