package com.example.omi.navigationdrawercommondemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.omi.navigationdrawercommondemo.Adapters.ErrorsShowDataAdapter;
import com.example.omi.navigationdrawercommondemo.Model.Error;
import com.example.omi.navigationdrawercommondemo.SQLite.ErrorsSavingSQLiteOpenhelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class ShowErrorsActivity extends AppCompatActivity implements View.OnClickListener {
    Button save,saved_errors;
    ArrayList<Error> errors;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_errors);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7912682669795193~7592020668");
        getSupportActionBar().setHomeButtonEnabled(true);
        save = (Button) findViewById(R.id.save);
        saved_errors = (Button) findViewById(R.id.saved_errors);
        save.setOnClickListener(this);
        saved_errors.setOnClickListener(this);
        this.errors = (ArrayList<Error>) getIntent().getSerializableExtra("error");
        ErrorsShowDataAdapter errorsShowDataAdapter = new ErrorsShowDataAdapter(errors);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.errorsView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(errorsShowDataAdapter);
    }
    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.save)
        {
            ErrorsSavingSQLiteOpenhelper helper = new ErrorsSavingSQLiteOpenhelper(this);
            helper.saveListOfError(this.errors);
            view.setEnabled(false);
        }
        else if(view.getId() == R.id.saved_errors)
        {
            Intent intent = new Intent(this,ShowSavedQuestionsActivity.class);
            startActivity(intent);
            this.finish();
        }
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
