package com.soamid.bowscore.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.soamid.bowscore.R;
import com.soamid.bowscore.persistance.SessionDAO;
import com.soamid.bowscore.slidinglayer.SlidingLayer;
import com.soamid.bowscore.swipedismiss.SwipeDismissListViewTouchListener;
import com.soamid.bowscore.util.FormatUtil;
import com.soamid.bowscore.util.FormattedDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Soamid on 31.12.13.
 */
public class SessionActivity extends Activity {

    private final FormatUtil formatUtil = new FormatUtil();
    private List<List<String>> sessionResults;

    private ArrayAdapter<String> sessionsResultsAdapter;
    private Date sessionDate;
    private SlidingLayer slidingLayer;

    private AddResultController addResultController;

    public void addResultsButtonOnClick(View v) {
        addResultController.startNewResult();
        slidingLayer.openLayer(true);
    }

    public void resultOnClick(View v) {
        addResultController.resultOnClick(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session);

        ListView listView = (ListView) findViewById(R.id.resultsListView);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    sessionsResultsAdapter.remove(sessionsResultsAdapter.getItem(position));
                                    sessionResults.remove(position);
                                }
                                sessionsResultsAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());

        slidingLayer = (SlidingLayer) findViewById(R.id.addResultLayer);
        addResultController = new AddResultController(slidingLayer);

        slidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener.Stub() {

            @Override
            public void onClose() {
                if (addResultController.isResultReady()) {
                    appendNewResult(addResultController.getCurrentResult());
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey(ActivityConstants.SESSION_DATE)) {
            sessionDate = (Date) bundle.get(ActivityConstants.SESSION_DATE);
            sessionResults = SessionDAO.getInstance().getSession(sessionDate);
        } else {
            sessionDate = new FormattedDate();
            sessionResults = new ArrayList<List<String>>();
            SessionDAO.getInstance().putSession(sessionDate, sessionResults);
        }

        loadSessionResults();
    }

    protected void appendNewResult(List<String> result) {
        sessionResults.add(result);


        sessionsResultsAdapter.add(FormatUtil.formatResult(result));
        sessionsResultsAdapter.notifyDataSetChanged();

        ListView resultsListView = (ListView) findViewById(R.id.resultsListView);
        resultsListView.setSelection(resultsListView.getCount() - 1);


        SessionDAO.getInstance().save();

    }

    private void loadSessionResults() {

        sessionsResultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        ((ListView) findViewById(R.id.resultsListView)).setAdapter(sessionsResultsAdapter);

        for (List<String> results : sessionResults) {
            sessionsResultsAdapter.add(FormatUtil.formatResult(results));
        }
    }
}
