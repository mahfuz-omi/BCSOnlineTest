package com.example.omi.navigationdrawercommondemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AboutActivity extends DrawerActivity {
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        this.selectedNavigation = 2;
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7912682669795193~7592020668");

        TextView text = (TextView) findViewById(R.id.text);
        String data = "<p><font color=#303F9F>BCS Online Test</font> is a dynamic MCQ question bank software which is continuously updating it's question Bank.<font color=#3eadeb>Test Online</font> option facilitates you with showing all currently available question sets which is not tested(submitted) by you before. Every set has exactly 10 MCQ questions prepared for BCS preliminary exam.Total time allocated for every set is 300 seconds(5 mins). Marks count is 1 for each question.There are having no negative marking.For attending online test, everyone should login to their respective account.Those who doesn't has account must register.An email verification is performed after registration. After submitting a set, you will be provided with your marks for that particular set, ranking for this set among all other candidates etc. You can save all your mistakes during exam.  </p>" +
                "    <p> <font color=#303F9F>History</font> options shows all past submissions made by you.<font color=#303F9F>Push Notification Service</font> is enabled in this software.So,when new questions arrive, you will get notification if you are logged in. </p>";

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
