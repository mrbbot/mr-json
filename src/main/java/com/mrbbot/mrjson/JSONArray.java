package com.mrbbot.mrjson;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"WeakerAccess", "unused"})
public class JSONArray extends ArrayList<Object> {
    public JSONArray(int initialCapacity) {
        super(initialCapacity);
    }

    public JSONArray() {

    }

    public JSONArray(Collection<?> c) {
        super(c);
    }

    //region Getters
    public Object getObject(int index) {
        return super.get(index);
    }

    public JSONObject get(int index) {
        return (JSONObject) super.get(index);
    }

    public String getString(int index) {
        return (String) super.get(index);
    }

    public int getInt(int index) {
        return (int) super.get(index);
    }

    public long getLong(int index) {
        return (long) super.get(index);
    }

    public double getDouble(int index) {
        return (double) super.get(index);
    }

    public boolean getBoolean(int index) {
        return (boolean) super.get(index);
    }

    public JSONArray getArray(int index) {
        return (JSONArray) super.get(index);
    }
    //endregion

    public String toString() {
        return JSON.getArrayText(this, 0);
    }

    public void toFile(File file) {
        JSON.saveToFile(file, toString());
    }

    public void toFile(String file) {
        toFile(new File(file));
    }
}
