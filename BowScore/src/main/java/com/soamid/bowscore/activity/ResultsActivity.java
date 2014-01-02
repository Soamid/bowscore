package com.soamid.bowscore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.soamid.bowscore.R;
import com.soamid.bowscore.persistance.SessionDAO;
import com.soamid.bowscore.swipedismiss.SwipeDismissListViewTouchListener;

import java.util.Date;
import java.util.List;

/**
 * Created by Soamid on 31.12.13.
 */
public class ResultsActivity extends Activity {

    private AdapterView.OnItemClickListener onSessionClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Date date = (Date) adapterView.getItemAtPosition(i);

            Intent intent = new Intent(ResultsActivity.this, SessionActivity.class);
            intent.putExtra(ActivityConstants.SESSION_DATE, date);
            startActivity(intent);
        }
    };
    private ArrayAdapter<Date> sessionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        ListView listView = (ListView) findViewById(R.id.sessionsListView);
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
                                    Date sessionDate = sessionsAdapter.getItem(position);
                                    sessionsAdapter.remove(sessionDate);
                                    SessionDAO.getInstance().removeSession(sessionDate);
                                }
                                sessionsAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSessions();
    }

    private void loadSessions() {
        List<Date> sessionsList = SessionDAO.getInstance().getSessionDates();
        sessionsAdapter = new ArrayAdapter<Date>(this, android.R.layout.simple_list_item_1, sessionsList);

        ListView sessionsView = (ListView) findViewById(R.id.sessionsListView);
        sessionsView.setAdapter(sessionsAdapter);
        sessionsView.setOnItemClickListener(onSessionClickListener);
    }
}
