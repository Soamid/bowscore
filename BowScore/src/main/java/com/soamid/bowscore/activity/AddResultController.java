package com.soamid.bowscore.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.soamid.bowscore.R;
import com.soamid.bowscore.slidinglayer.SlidingLayer;
import com.soamid.bowscore.util.FormatUtil;

import java.util.ArrayList;

/**
 * Created by Soamid on 31.12.13.
 */
public class AddResultController {

    public static int MAX_RESULTS = 4;

    private int resultCounter;
    private ArrayList<String> currentResult = new ArrayList<String>();

    private SlidingLayer slidingLayer;

    public AddResultController(SlidingLayer slidingLayer) {
        this.slidingLayer = slidingLayer;
    }


    protected void startNewResult() {

        resultCounter = 1;
        currentResult.clear();
    }

    public void resultOnClick(View v) {

        currentResult.add(((Button) v).getText().toString());
        ((TextView) slidingLayer.findViewById(R.id.currentResultLabel)).setText(FormatUtil.formatResult(currentResult));


        resultCounter++;
        if (resultCounter > MAX_RESULTS) {
            slidingLayer.closeLayer(true);
        }
    }

    public ArrayList<String> getCurrentResult() {
        return currentResult;
    }
}
