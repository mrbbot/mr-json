package com.mrbbot.mrjson;

import java.io.File;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "unused"})
public class JSONObject {
    private Map<String, Object> map;

    public JSONObject() {
        map = new HashMap<>();
    }

    //region Getters
    public Object getObject(String key) {
        return map.get(key);
    }

    public JSONObject get(String key) {
        return (JSONObject) map.get(key);
    }

    public String getString(String key) {
        return (String) map.get(key);
    }

    public int getInt(String key) {
        return (int) map.get(key);
    }

    public long getLong(String key) {
        return (long) map.get(key);
    }

    public double getDouble(String key) {
        return (double) map.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) map.get(key);
    }

    public JSONArray getArray(String key) {
        return (JSONArray) map.get(key);
    }

    public SortedSet<String> getSortedKeySet() {
        return new TreeSet<>(map.keySet());
    }

    public Set<Map.Entry<String, Object>> getEntrySet() {
        return map.entrySet();
    }
    //endregion

    //region Putters
    public void put(String key, Object value) {
        map.put(key, value);
    }
    //endregion

    public String toString() {
        return JSON.getObjectText(this, 0);
    }

    public void toFile(File file) {
        JSON.saveToFile(file, toString());
    }

    public void toFile(String file) {
        toFile(new File(file));
    }
}
