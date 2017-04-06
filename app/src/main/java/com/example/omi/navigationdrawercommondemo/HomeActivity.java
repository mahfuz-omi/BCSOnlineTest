package com.example.omi.navigationdrawercommondemo;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class HomeActivity extends DrawerActivity {
    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7912682669795193~7592020668");
        this.selectedNavigation = 0;
        TextView text = (TextView) findViewById(R.id.text);
        String data = "<font color=#3eadeb>Bangladesh Civil Service (BCS)</font> Examination is a nationwide competitive examination in Bangladesh conducted by the Bangladesh Public Service Commission (BPSC) for recruitment to the various Bangladesh Civil Service cadres, including BCS (Admin), BCS (Taxation), BCS (Foreign Affairs), and BCS (Police) among others.[1] The examination is conducted in three phases - the preliminary examination, the written examination and the viva voce (interview). The entire process from the notification of the preliminary examination to declaration of the final results takes 1.5 to 2 years.";
        text.setText(Html.fromHtml(data));





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
