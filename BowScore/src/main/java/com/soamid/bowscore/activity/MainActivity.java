package com.soamid.bowscore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.soamid.bowscore.persistance.SessionDAO;
import com.soamid.bowscore.R;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        SessionDAO.getInstance().initialize(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_all:
                SessionDAO.getInstance().clearAllData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void newSessionButtonOnClick(View v) {

        Intent intent = new Intent(this, SessionActivity.class);
        startActivity(intent);
    }

    public void resultsButtonOnClick(View v) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }


}
