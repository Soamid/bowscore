package com.soamid.bowscore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends ActionBarActivity {

    private int currentViewId = -1;

    private static int MAX_RESULTS = 4;

    private static String BOWSCORE_DATA = "bowscore_data";

    private static String BOWSCORE_DATA_DATES = BOWSCORE_DATA + "_dates";

    private static String BOWSCORE_DATA_RESULTS = BOWSCORE_DATA + "_results";

    private int resultCounter;

    private List<String> currentResult = new ArrayList<String>();

    private List<List<String>> sessionResults;
    private Map<Date, List<List<String>>> sessions = new TreeMap<Date, List<List<String>>>();


    private ArrayAdapter<String> sessionsResultsAdapter;
    private ArrayAdapter<Date> sessionsAdapter;

    private AdapterView.OnItemClickListener onSessionClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Date date = (Date) adapterView.getItemAtPosition(i);

            sessionResults = sessions.get(date);

            loadSessionResults();
            for (List<String> results : sessionResults) {
                sessionsResultsAdapter.add(formatResult(results));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        loadSavedData();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadSessions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getCurrentViewId() == R.layout.fragment_main) {
            saveData();
            super.onBackPressed();

        } else {
            setContentView(R.layout.fragment_main);

            ListView sessionsView = (ListView) findViewById(R.id.sessionsListView);
            sessionsView.setAdapter(sessionsAdapter);
            sessionsView.setOnItemClickListener(onSessionClickListener);
        }
    }


    public void saveData() {


        ArrayList<Date> dates = new ArrayList<Date>();
        ArrayList<List<List<String>>> results = new ArrayList<List<List<String>>>();

        for (Date date : sessions.keySet()) {
            dates.add(date);
            results.add(sessions.get(date));
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("bow_score_save.bin")));
            oos.writeObject(dates);
            oos.writeObject(results);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadSavedData() {

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("bow_score_save.bin")));
            ArrayList<List<List<String>>> results = (ArrayList<List<List<String>>>) ois.readObject();
            ArrayList<Date> dates = (ArrayList<Date>) ois.readObject();


            for (int i = 0; i < dates.size(); i++) {
                sessions.put(dates.get(i), results.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.currentViewId = layoutResID;
    }

    public int getCurrentViewId() {
        return currentViewId;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void newSessionButtonOnClick(View v) {

        sessionResults = new ArrayList<List<String>>();

        Date sessionDate = new Date();
        sessions.put(sessionDate, sessionResults);
        sessionsAdapter.add(sessionDate);

        loadSessionResults();
    }

    public void addResultsButtonOnClick(View v) {
        resultCounter = 1;
        currentResult.clear();
        setContentView(R.layout.fragment_result);
    }

    public void resultOnClick(View v) {

        currentResult.add(((Button) v).getText().toString());
        ((TextView) findViewById(R.id.currentResultLabel)).setText(formatResult(currentResult));

        resultCounter++;
        if (resultCounter > MAX_RESULTS) {
            setContentView(R.layout.fragment_session);
            addResult(new ArrayList<String>(currentResult));

        }
    }

    private void loadSessionResults() {
        setContentView(R.layout.fragment_session);


        sessionsResultsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        ((ListView) findViewById(R.id.resultsListView)).setAdapter(sessionsResultsAdapter);
    }

    private void loadSessions() {
        List<Date> sessionsList = new ArrayList<Date>();
        sessionsList.addAll(sessions.keySet());
        sessionsAdapter = new ArrayAdapter<Date>(this, android.R.layout.simple_list_item_1, sessionsList);

        ListView sessionsView = (ListView) findViewById(R.id.sessionsListView);
        sessionsView.setAdapter(sessionsAdapter);
        sessionsView.setOnItemClickListener(onSessionClickListener);
    }

    private void addResult(List<String> result) {
        sessionResults.add(result);

        ((ListView) findViewById(R.id.resultsListView)).setAdapter(sessionsResultsAdapter);
        sessionsResultsAdapter.add(formatResult(result));
    }

    private String formatResult(List<String> results) {
        StringBuilder sb = new StringBuilder();

        for (String result : results) {
            sb.append(result + " ");
        }

        sb.trimToSize();
        return sb.toString();
    }
}
