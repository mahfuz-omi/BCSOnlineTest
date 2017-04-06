package com.example.omi.navigationdrawercommondemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.omi.navigationdrawercommondemo.Model.Error;
import com.example.omi.navigationdrawercommondemo.Model.Question;
import com.example.omi.navigationdrawercommondemo.WebServiceConstants.WebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowQuestionsActivity extends AppCompatActivity {
    int set_id;
    MenuItem time;
    CountDownTimer timer;
    LinearLayout mainLayout;
    RadioGroup[] radioGroup;
    Question[] questions;
    int numOfQuestions = 10;
    Button submitButton;
    int totalTimes;
    ProgressDialog pDialog;
    ArrayList<Error> errors;
    boolean isOptionCreated,isNetworkRequestDone;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_time, menu);
        this.time = menu.findItem(R.id.time);
        this.totalTimes = this.numOfQuestions*30000;
        this.timer = new CountDownTimer(totalTimes,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time.setTitle("time: "+millisUntilFinished/1000+"");
                System.out.println(millisUntilFinished/1000);

            }

            @Override
            public void onFinish() {
                time.setTitle("time:"+0+"");
                submitButton.performClick();
                Toast.makeText(ShowQuestionsActivity.this, "finish", Toast.LENGTH_SHORT).show();

            }
        };
        timer.start();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.timer.cancel();
        super.onBackPressed();
    }

    public void checkResult()
    {
        this.timer.cancel();
        int correctAnswered = 0;
        for(int i=0;i<numOfQuestions;i++)
        {
            int checkedId = radioGroup[i].getCheckedRadioButtonId();
            String correctAnswer = questions[i].e;
            if(checkedId != -1)
            {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                String givenAnswer = radioButton.getText().toString();
                if(givenAnswer.equalsIgnoreCase(correctAnswer))
                    correctAnswered++;
                else
                {
                    Error error = new Error();
                    error.setQuestion(questions[i].question);
                    error.setGiven(givenAnswer);
                    error.setAnswer(questions[i].e);
                    errors.add(error);

                }
            }
            else
            {
                Error error = new Error();
                error.setQuestion(questions[i].question);
                error.setGiven(null);
                error.setAnswer(questions[i].e);
                errors.add(error);
            }
        }
        System.out.println("count: "+correctAnswered);
        JSONObject jobj = null;
        try{
            jobj = new JSONObject();
            jobj.put("set_id",this.set_id);
            jobj.put("got_marks",correctAnswered);

        }
        catch(Exception e)
        {
            Toast.makeText(ShowQuestionsActivity.this, "errors", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println(jobj.toString());
        final int correct = correctAnswered;


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,WebService.submitURL,jobj,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                System.out.println("ranking response: "+response);
                if (pDialog.isShowing())
                    pDialog.dismiss();
                String rank="";
                String count="";
                if(response.has("rank"))
                {
                    try
                    {
                        rank = response.getString("rank");
                        count = response.getString("count");

                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowQuestionsActivity.this);
                        builder.setTitle("Test Marks");
                        builder.setMessage("Your marks: " + correct+"\n"+"Your ranking for set "+set_id+" is : "+rank+"\n"+"Total examinee :"+count);
                        builder.setPositiveButton("Test another set",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });

//                        builder.setNeutralButton("Share", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                Intent sendIntent = new Intent();
//                                sendIntent.setAction(Intent.ACTION_SEND);
//                                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
//                                sendIntent.setType("text/plain");
//                                startActivity(Intent.createChooser(sendIntent, "Share to:"));
//                                finish();
//
//                            }
//                        });

                        if (correct < 10)
                        {

                            builder.setNegativeButton("show errors",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ShowQuestionsActivity.this,
                                                    ShowErrorsActivity.class);
                                            intent.putExtra("error", errors);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                        }

                        final AlertDialog alertdialog = builder.create();
                        //alertdialog.setCancelable(false);
                        alertdialog.setCanceledOnTouchOutside(false);
                        alertdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                alertdialog.dismiss();
                                onBackPressed();
                            }
                        });
                        alertdialog.show();

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    System.out.println("no rank");
                }
            }
        },
        new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error)
            {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Toast.makeText(ShowQuestionsActivity.this, "errors", Toast.LENGTH_SHORT).show();
                error.printStackTrace();

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_questions);


        getSupportActionBar().setHomeButtonEnabled(true);
        this.submitButton = (Button) findViewById(R.id.submit);
        this.errors = new ArrayList<>();
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkResult();
            }
        });
        this.mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        this.set_id = getIntent().getIntExtra("set_id",0);
        setTitle("Question Set: "+this.set_id);
        String questionsURL = WebService.questionsURL+"/"+set_id;
        System.out.println("questions url:"+questionsURL);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,questionsURL,null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response)
                    {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Log.d("questions response: ", response.toString());
                        System.out.println("questions: "+response.toString());
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        questions = gson.fromJson(response.toString(),Question[].class);
                        numOfQuestions = questions.length;
                        System.out.println("size: "+questions.length);
                        radioGroup = new RadioGroup[numOfQuestions];
                        for(int i=0;i<numOfQuestions;i++)
                        {
                            Question question = questions[i];
                            RadioGroup rg = new RadioGroup(ShowQuestionsActivity.this);

                            TextView tv = new TextView(ShowQuestionsActivity.this);
                            tv.setText("(" + (i + 1) + ")" + question.question);
                            tv.setTextSize(20);
                            mainLayout.addView(tv);

                            RadioButton rb1 = new RadioButton(ShowQuestionsActivity.this);
                            RadioButton rb2 = new RadioButton(ShowQuestionsActivity.this);
                            RadioButton rb3 = new RadioButton(ShowQuestionsActivity.this);
                            RadioButton rb4 = new RadioButton(ShowQuestionsActivity.this);

                            rb1.setText(question.a);
                            rb2.setText(question.b);
                            rb3.setText(question.c);
                            rb4.setText(question.d);
                            rg.addView(rb1);
                            rg.addView(rb2);
                            rg.addView(rb3);
                            rg.addView(rb4);
                            mainLayout.addView(rg);
                            radioGroup[i] = rg;

                        }

                    }
                },
                new Response.ErrorListener()
                {
                @Override
                public void onErrorResponse(VolleyError error)
                    {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        System.out.println("omi errors: "+error.getMessage());
                        VolleyLog.d("omi", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                        "Network error...please try again later", Toast.LENGTH_LONG).show();

                    }
                })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String,String> headers = new HashMap<>();
                    headers.put("access-token",((BCSApplication)getApplication()).getAccessToken());
                    return headers;
                }
            };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }
}
