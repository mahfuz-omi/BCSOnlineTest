package com.example.omi.navigationdrawercommondemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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
import com.facebook.login.LoginManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DrawerActivity extends AppCompatActivity {
    public int selectedNavigation;
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        final FrameLayout content = (FrameLayout) findViewById(R.id.content);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch(menuItem.getItemId())
                {
                    case R.id.drawer_home:
                    {
                        Intent intent = new Intent(DrawerActivity.this,HomeActivity.class);
                        startActivity(intent);
                        break;

                    }

                    case R.id.drawer_test:
                    {
                        Intent intent = new Intent(DrawerActivity.this,TestActivity.class);
                        startActivity(intent);
                        break;

                    }

                    case R.id.drawer_history:
                    {
                        Intent intent = new Intent(DrawerActivity.this,HistoryActivity.class);
                        startActivity(intent);
                        break;

                    }

                    case R.id.drawer_about:
                    {
                        Intent intent = new Intent(DrawerActivity.this,AboutActivity.class);
                        startActivity(intent);
                        break;

                    }
                    case R.id.drawer_saved_questions:
                    {
                        Intent intent = new Intent(DrawerActivity.this,ShowSavedQuestionsActivity.class);
                        startActivity(intent);
                        break;

                    }

                    case R.id.drawer_change_password:
                    {
                        Intent intent = new Intent(DrawerActivity.this,ChangePasswordActivity.class);
                        startActivity(intent);
                        break;

                    }

                    case R.id.drawer_logout:
                    {
                        pDialog = new ProgressDialog(DrawerActivity.this);
                        pDialog.setMessage("Please wait...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        LoginManager.getInstance().logOut();
                        ((BCSApplication)getApplication()).unsetLoginWithFB();
                        JsonObjectRequest jsonRequest = new JsonObjectRequest
                                (Request.Method.GET, WebService.logoutURL, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (pDialog.isShowing())
                                            pDialog.dismiss();
                                        // the response is already constructed as a JSONObject!
                                        try {
                                            System.out.println("response: "+response.toString());
                                            navigationView.getMenu().getItem(4).setVisible(false);
                                            ((BCSApplication)getApplication()).logoutUser();

                                            if(response.has("success"))
                                            {
                                                Toast.makeText(DrawerActivity.this, "You are logged out now!!", Toast.LENGTH_LONG).show();
                                            }
                                            else
                                            {
                                                //JSONObject error = response.getJSONObject("error");
                                                //String text = error.getString("text");
                                                //Toast.makeText(DrawerActivity.this, text, Toast.LENGTH_LONG).show();
                                                Toast.makeText(DrawerActivity.this, "you are logged out from this application", Toast.LENGTH_LONG).show();
                                            }
                                            Intent intent = new Intent(DrawerActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;

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
                                        Toast.makeText(DrawerActivity.this, "Network error...please try again later", Toast.LENGTH_SHORT).show();
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

                        Volley.newRequestQueue(DrawerActivity.this).add(jsonRequest);

                    }
                }
                return true;
            }
        });


        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolbar.setLogo(R.drawable.main_banner);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer,null);
        frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.content);
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(drawerLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.navigationView.getMenu().getItem(selectedNavigation).setChecked(true);
        this.checkLogin();
    }

    public void checkLogin()
    {
        if(((BCSApplication)getApplication()).isUserLoggedIn())
        {
            this.navigationView.getMenu().getItem(3).setVisible(true);
            this.navigationView.getMenu().getItem(4).setVisible(true);
            this.navigationView.getMenu().getItem(5).setVisible(true);
            if(!((BCSApplication)getApplication()).isUserLogidWithFB())
                this.navigationView.getMenu().getItem(6).setVisible(true);
        }
        else
        {
            this.navigationView.getMenu().getItem(3).setVisible(false);
            this.navigationView.getMenu().getItem(4).setVisible(false);
            this.navigationView.getMenu().getItem(5).setVisible(false);
            this.navigationView.getMenu().getItem(6).setVisible(false);
        }
    }
}
