package com.soamid.bowscore.activity;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
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

    private PointF touchPosition;

    private SlidingLayer slidingLayer;

    public AddResultController(final SlidingLayer slidingLayer) {
        this.slidingLayer = slidingLayer;
        final ImageView boardImageView = (ImageView) slidingLayer.findViewById(R.id.boardImageView);
        boardImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    boardImageView.setImageResource(R.drawable.board);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    touchPosition = new PointF(motionEvent.getRawX(), motionEvent.getRawY());
                    int result = computeResult();
                    if (result > 0) {
                        int imageId = slidingLayer.getResources().getIdentifier("board" + result, "drawable", "com.soamid.bowscore");
                        boardImageView.setImageResource(imageId);
                    }
                }

                return false;
            }
        });

    }


    protected void startNewResult() {

        resultCounter = 1;
        currentResult.clear();
        setResultLabel("");
    }

    public boolean isResultReady() {
        return currentResult.size() == MAX_RESULTS;
    }

    public void resultOnClick(View v) {

        Integer result = computeResult();

        currentResult.add(result.toString());

        setResultLabel(FormatUtil.formatResult(currentResult));


        resultCounter++;
        if (resultCounter > MAX_RESULTS) {
            slidingLayer.closeLayer(true);
        }
    }

    public ArrayList<String> getCurrentResult() {
        return currentResult;
    }

    private int computeResult() {
        ImageView boardImageView = (ImageView) slidingLayer.findViewById(R.id.boardImageView);

        float radius = boardImageView.getWidth() / 2.0f;

        float distance = getDistance(getBoardCenterLocation(), touchPosition);
        float fragment = radius / 6;

        int result = 10;

        PointF center = getBoardCenterLocation();

        Log.i("Strzelanie", "center: " + center.x + ", " + center.y);
        Log.i("Strzelanie", "touch: " + touchPosition.x + ", " + touchPosition.y);
        Log.i("Strzelanie", "radius: " + radius);
        Log.i("Strzelanie", "distance: " + distance);

        for (float x = fragment; x < radius; x += fragment) {
            if (distance <= x) {
                break;
            }
            result--;
        }

        result = (distance <= radius) ? result : 0;

        return result;
    }

    private void setResultLabel(String label) {
        ((TextView) slidingLayer.findViewById(R.id.currentResultLabel)).setText(label);
    }

    private PointF getBoardCenterLocation() {

        ImageView boardImageView = (ImageView) slidingLayer.findViewById(R.id.boardImageView);
        int location[] = new int[2];
        boardImageView.getLocationOnScreen(location);
        float viewCenterX = location[0] + boardImageView.getWidth() / 2.0f;
        float viewCenterY = location[1] + boardImageView.getHeight() / 2.0f;
        return new PointF(viewCenterX, viewCenterY);
    }

    private static float getDistance(PointF point1, PointF point2) {
        float x = point2.x - point1.x;
        float y = point2.y - point1.y;

        return (float) Math.sqrt(x * x + y * y);
    }
}
