package com.example.omi.navigationdrawercommondemo;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import com.robotium.solo.Solo;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<HomeActivity>
{
    private Solo solo;
    public ApplicationTest()
    {
        super(HomeActivity.class);
    }
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        getActivity();
    }
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


    /// function er naam "test" diye start hote hobe....

    public void testDoLoginActivity() throws Exception
    {


    }


}