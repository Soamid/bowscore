package com.soamid.bowscore.persistance;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Soamid on 31.12.13.
 */
public class SessionDAO {

    private Map<Date, List<List<String>>> sessions;
    private ScoreDataManager scoreDataManager;

    private SessionDAO() {
    }


    private static class SessionDataProviderHolder {
        private final static SessionDAO instance = new SessionDAO();
    }

    public static SessionDAO getInstance() {
        return SessionDataProviderHolder.instance;
    }

    public void initialize(Context context) {
        scoreDataManager = new ScoreDataManager(context);
        Map<Date, List<List<String>>> loadedData = scoreDataManager.loadData();

        if(loadedData != null) {
            sessions = loadedData;
        } else {
            sessions = new TreeMap<Date, List<List<String>>>();
        }
    }

    public void save() {
        scoreDataManager.saveData(sessions);
    }

    public List<Date> getSessionDates() {
        return new ArrayList<Date>(sessions.keySet());
    }

    public List<List<String>> getSession(Date date) {
        return sessions.get(date);
    }

    public void putSession(Date date, List<List<String>> sessionData) {
        sessions.put(date, sessionData);
    }

    public void removeSession(Date date) {
        sessions.remove(date);
    }

    public void clearAllData() {
        sessions.clear();
    }


}
