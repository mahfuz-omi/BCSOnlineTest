package com.example.omi.navigationdrawercommondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText password,confirm_password;
    TextView changePassword_error;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_change_password);
        super.onCreate(savedInstanceState);

        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);
        changePassword_error = (TextView) findViewById(R.id.changePassword_error);

    }


    public void change_password(View v)
    {
        String password_text = password.getText().toString();
        String confirm_password_text = confirm_password.getText().toString();
        if(!password_text.equalsIgnoreCase(confirm_password_text))
        {
            changePassword_error.setText("Both password fields must be same and non-empty");
            return;

        }
        if(password_text.length() == 0)
        {
            changePassword_error.setText("Both password fields must be same and non-empty");
            return;
        }

        JSONObject changePasswordJson = new JSONObject();
        try
        {
            changePasswordJson.put("password",password_text);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();


        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, WebService.changePasswordURL, changePasswordJson, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        try
                        {
                            System.out.println("changePassword response: "+response.toString());
                            if(response.has("success"))
                            {
                                Toast.makeText(ChangePasswordActivity.this, "You password has been changed!!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                JSONObject error = response.getJSONObject("error");
                                String text = error.getString("text");
                                Toast.makeText(ChangePasswordActivity.this, "Your Password Change Request is not successful", Toast.LENGTH_LONG).show();
                                changePassword_error.setText(text);
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
                        Toast.makeText(ChangePasswordActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
                )
        {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String access_token = ((BCSApplication)getApplication()).getAccessToken();
                Map<String,String> headers = new HashMap<>();
                System.out.println("get token from application:"+access_token);
                headers.put("access-token",access_token);
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonRequest);





    }
}
