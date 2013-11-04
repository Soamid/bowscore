package com.soamid.bowscore.persistance;

import android.content.Context;
import android.util.JsonReader;

import com.soamid.bowscore.FormattedDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Soamid on 04.11.13.
 */
public class ScoreDataManager {

    private static final String DATA_FILE = "scores.sav";

    private static final String DATE = "date";

    private static final String RESULTS = "results";

    private Context context;

    public ScoreDataManager(Context context) {
        this.context = context;
    }

    public Map<Date, List<List<String>>> loadData() {
        FileReader reader = null;
        try {
             reader = new FileReader(getDataFile());
            JsonReader jsonReader = new JsonReader(reader);

            Map<Date, List<List<String>>> data = new TreeMap<Date, List<List<String>>>();

            jsonReader.beginArray();
            while(jsonReader.hasNext()) {
                jsonReader.beginObject();

                Date date = null;
                List<List<String>> results = null;
                while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                    if(name.equals(DATE)) {
                        date = new FormattedDate(jsonReader.nextLong());
                    } else if(name.equals(RESULTS)) {
                        results = parseResults(jsonReader);
                    }
                }
                jsonReader.endObject();

                if(date != null && results != null) {
                    data.put(date, results);
                }
            }
            jsonReader.endArray();

            return data;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if(reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }

            return null;
    }

    public void saveData(Map<Date, List<List<String>>> sessions) {
        try {
        JSONArray mainArray = new JSONArray();
        for(Date date : sessions.keySet()) {
                mainArray.put(createSessionData(date, sessions.get(date)));
            }
            saveToFile(mainArray);
        }
        catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<List<String>> parseResults(JsonReader reader) throws IOException {
        List<List<String>> results = new ArrayList<List<String>>();

        reader.beginArray();
        while(reader.hasNext()) {
            List<String> singleResult = new ArrayList<String>();
            reader.beginArray();

            while(reader.hasNext()) {
                singleResult.add(reader.nextString());
            }
            reader.endArray();

            results.add(singleResult);
        }

        reader.endArray();

        return results;
    }

    private JSONObject createSessionData(Date sessionDate,List<List<String>> results ) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put(DATE, sessionDate.getTime());

        JSONArray resultsArray = new JSONArray();
        for(List<String> result :results) {
            JSONArray singleResultArray = new JSONArray();
            for(String singleResult : result) {
                singleResultArray.put(singleResult);
            }
            resultsArray.put(singleResultArray);
        }

        obj.put(RESULTS, resultsArray);

        return obj;
    }

    private void saveToFile(JSONArray obj) throws IOException {
        File file = getDataFile();
        if(!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file);

        writer.write(obj.toString());

        writer.flush();
        writer.close();
        System.out.println(obj);
    }

    private File getDataFile() {
        return new File(context.getFilesDir(), DATA_FILE);
    }


}
