package com.gDyejeekis.aliencompanion.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gDyejeekis.aliencompanion.R;
import com.gDyejeekis.aliencompanion.utils.GeneralUtils;

/**
 * Created by sound on 10/23/2015.
 */
public class OAuthActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        GeneralUtils.clearCookies(this);
        setContentView(R.layout.activity_oauth);
    }

    //@Override
    //public void onDestroy() {
    //    super.onDestroy();
    //}

}