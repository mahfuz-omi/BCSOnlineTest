package com.example.omi.navigationdrawercommondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.omi.navigationdrawercommondemo.Adapters.ShowSavedQuestionsAdapter;
import com.example.omi.navigationdrawercommondemo.Model.Error;
import com.example.omi.navigationdrawercommondemo.Model.QuestionErrorPair;
import com.example.omi.navigationdrawercommondemo.SQLite.ErrorsSavingSQLiteOpenhelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

public class ShowSavedQuestionsActivity extends DrawerActivity {
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_saved_questions);
        super.onCreate(savedInstanceState);
        this.selectedNavigation = 5;
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7912682669795193~7592020668");
        //getSupportActionBar().setHomeButtonEnabled(true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.errorsView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ErrorsSavingSQLiteOpenhelper helper = new ErrorsSavingSQLiteOpenhelper(this);
        List<QuestionErrorPair> pairList = helper.getQuestionErrorPairList();
        ShowSavedQuestionsAdapter adapter = new ShowSavedQuestionsAdapter(pairList);
        recyclerView.setAdapter(adapter);
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
