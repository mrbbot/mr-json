package com.mrbbot.mrjson;

import java.io.*;
import java.net.URL;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "unused"})
public class JSON {
    private enum ParseState {
        KEY, SEPARATOR, VALUE
    }

    private final static Character[] numberChars = new Character[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', 'e', 'E', '+', '-'};
    private static List<Character> numberCharList;

    private static String getIndent(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append(" ");
        }
        return indent.toString();
    }

    private static String getContainerText(String json, char start, char end) {
        int i, brackets = 0, startIndex = json.indexOf(start);
        if(startIndex == -1)
            return "";
        boolean broken = false;
        for(i = startIndex; i < json.length(); i++) {
            if(json.charAt(i) == start) {
                brackets++;
            } else if(json.charAt(i) == end) {
                brackets--;
            } if(brackets == 0) {
                broken = true;
                break;
            }
        }
        if(!broken) {
            throw new JSONException("Invalid JSON: Missing brackets in container declaration!");
        }
        return json.substring(startIndex + 1, i + 1);
    }

    private static JSONArray parseToArray(String arrayText, int level) {
        int startIndex = -1;
        List<Object> list = new ArrayList<>();
        boolean findingNumber = false, findingString = false;
        for(int i = 0; i < arrayText.length(); i++) {
            if(!findingString && arrayText.charAt(i) == '{') {
                String nestedObjectText = getContainerText(arrayText.substring(i), '{', '}');
                i += nestedObjectText.length() + 1;
                list.add(parseToObject(nestedObjectText, level + 4));
            } else if(!findingString && arrayText.charAt(i) == '[') {
                String nestedArrayText = getContainerText(arrayText.substring(i), '[', ']');
                i += nestedArrayText.length() + 1;
                list.add(parseToArray(nestedArrayText, level + 4));
            } else if(arrayText.charAt(i) == '\"') {
                if(startIndex == -1) {
                    startIndex = i;
                    findingString = true;
                } else {
                    String value = arrayText.substring(startIndex + 1, i);
                    list.add(value);
                    findingString = false;
                    startIndex = -1;
                }
            } else if(!findingString && numberCharList.contains(arrayText.charAt(i))) {
                if(startIndex == -1) {
                    startIndex = i;
                    findingNumber = true;
                }
            } else if(findingNumber) {
                String numberText = arrayText.substring(startIndex, i);
                if(numberText.contains(".") || numberText.contains("e") || numberText.contains("E")) {
                    list.add(Double.parseDouble(numberText));
                } else {
                    list.add(Long.parseLong(numberText));
                }
                findingNumber = false;
                startIndex = -1;
            } else if(!findingString && (arrayText.charAt(i) == 't' || arrayText.charAt(i) == 'f')) {
                String booleanText = (arrayText.charAt(i) == 't' ? "true" : "false");
                i += booleanText.length();
                list.add(Boolean.parseBoolean(booleanText));
            } else if(!findingString && arrayText.charAt(i) == 'n') {
                i += "null".length();
                list.add(null);
            }
        }

        return new JSONArray(list);
    }

    private static JSONObject parseToObject(String objectText, int level) {
        JSONObject object = new JSONObject();
        ParseState state = ParseState.KEY;
        int startIndex = -1;
        String currentKey = "";
        boolean findingNumber = false, findingString = false, reset = false;
        for(int i = 0; i < objectText.length(); i++) {
            switch (state) {
                case KEY:
                    if(objectText.charAt(i) == '\"') {
                        if(startIndex == -1) {
                            startIndex = i;
                        } else {
                            currentKey = objectText.substring(startIndex + 1, i);
                            startIndex = -1;
                            state = ParseState.SEPARATOR;
                        }
                    }
                    break;
                case SEPARATOR:
                    if(objectText.charAt(i) == ':') {
                        state = ParseState.VALUE;
                    }
                    break;
                case VALUE:
                    if(!findingString && objectText.charAt(i) == '{') {
                        String nestedObjectText = getContainerText(objectText.substring(i), '{', '}');
                        object.put(currentKey, parseToObject(nestedObjectText, level + 4));
                        i += nestedObjectText.length() + 1;
                        reset = true;
                    } else if(!findingString && objectText.charAt(i) == '[') {
                        String nestedArrayText = getContainerText(objectText.substring(i), '[', ']');
                        object.put(currentKey, parseToArray(nestedArrayText, level + 4));
                        i += nestedArrayText.length() + 1;
                        reset = true;
                    } else if(objectText.charAt(i) == '\"') {
                        if(startIndex == -1) {
                            startIndex = i;
                            findingString = true;
                        } else {
                            String value = objectText.substring(startIndex + 1, i);
                            object.put(currentKey, value);
                            findingString = false;
                            reset = true;
                        }
                    } else if(!findingString && numberCharList.contains(objectText.charAt(i))) {
                        if(startIndex == -1) {
                            startIndex = i;
                            findingNumber = true;
                        }
                    } else if(findingNumber) {
                        String numberText = objectText.substring(startIndex, i);
                        if(numberText.contains(".") || numberText.contains("e") || numberText.contains("E")) {
                            object.put(currentKey, Double.parseDouble(numberText));
                        } else {
                            object.put(currentKey, Long.parseLong(numberText));
                        }
                        findingNumber = false;
                        reset = true;
                    } else if(!findingString && (objectText.charAt(i) == 't' || objectText.charAt(i) == 'f')) {
                        String booleanText = (objectText.charAt(i) == 't' ? "true" : "false");
                        i += booleanText.length();
                        boolean booleanValue = Boolean.parseBoolean(booleanText);
                        object.put(currentKey, booleanValue);
                        reset = true;
                    } else if(!findingString && objectText.charAt(i) == 'n') {
                        i += "null".length();
                        object.put(currentKey, null);
                        reset = true;
                    }
                    break;
            }

            if(reset) {
                reset = false;
                startIndex = -1;
                currentKey = "";
                state = ParseState.KEY;
            }
        }
        return object;
    }

    public static String readReader(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line;StringBuilder text = new StringBuilder();
            while((line = bufferedReader.readLine()) != null)
                text.append(line).append("\n");
            return text.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile(File file) {
        try (FileReader reader = new FileReader(file)) {
            return readReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readURL(URL url) {
        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            return readReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean parserInitialized = false;
    private static void initParser() {
        Collections.addAll(numberCharList = new ArrayList<>(), numberChars);
    }

    public static JSONObject parseObject(String json) {
        if(!parserInitialized)
            initParser();
        return parseToObject(getContainerText(json, '{', '}'), 0);
    }

    public static JSONObject parseObject(File file) {
        return parseObject(readFile(file));
    }

    public static JSONObject parseObject(URL url) {
        return parseObject(readURL(url));
    }

    public static JSONArray parseArray(String json) {
        if(!parserInitialized)
            initParser();
        return parseToArray(getContainerText(json, '[', ']'), 0);
    }

    public static JSONArray parseArray(File file) {
        return parseArray(readFile(file));
    }

    public static JSONArray parseArray(URL url) {
        return parseArray(readURL(url));
    }

    private static String getValueText(Object value, int level) {
        if(value instanceof String) {
            return "\"" + value + "\"";
        } else if(value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if(value instanceof Long) {
            return Long.toString((Long) value);
        } else if(value instanceof Double) {
            return Double.toString((Double) value);
        } else if(value instanceof JSONObject) {
            return getObjectText((JSONObject) value, level);
        } else if(value instanceof JSONArray) {
            return getArrayText((JSONArray) value, level);
        } else if(value instanceof Boolean) {
            return Boolean.toString((Boolean) value);
        } else if(value == null) {
            return "null";
        } else {
            throw new JSONException("Unknown data type!");
        }
    }

    static String getArrayText(JSONArray array, int level) {
        if(array.size() == 0)
            return "[]";
        StringBuilder result = new StringBuilder("[\n");
        String initialIndent = getIndent(level);
        level += 4;
        String indent = getIndent(level);
        int index = 0;
        for(Object value : array) {
            result.append(indent).append(getValueText(value, level)).append(index < array.size() - 1 ? ',' : ' ').append("\n");
            index++;
        }
        result.append(initialIndent).append("]");
        return result.toString();
    }

    static String getObjectText(JSONObject object, int level) {
        SortedSet<String> keys = object.getSortedKeySet();
        if(keys.size() == 0)
            return "{}";
        StringBuilder result = new StringBuilder("{\n");
        String initialIndent = getIndent(level);
        level += 4;
        String indent = getIndent(level);
        int index = 0;
        for(String key : keys) {
            result.append(indent).append("\"").append(key).append("\": ").append(getValueText(object.getObject(key), level)).append(index < keys.size() - 1 ? ',' : ' ').append("\n");
            index++;
        }
        result.append(initialIndent).append("}");
        return result.toString();
    }

    static void saveToFile(File file, String json) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file); PrintWriter printWriter = new PrintWriter(fileOutputStream)){
            printWriter.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static Object getObjectFromObject(Object object) {
        if(object instanceof Map) {
            return fromMap((Map<String, Object>) object);
        } else if(object instanceof List) {
            return fromList((List) object);
        }
        return object;
    }

    public static JSONObject fromMap(Map<String, Object> map) {
        JSONObject result = new JSONObject();

        for(Map.Entry<String, Object> entry : map.entrySet())
            result.put(entry.getKey(), getObjectFromObject(entry.getValue()));

        return result;
    }

    public static JSONArray fromList(List list) {
        JSONArray result = new JSONArray();

        for(Object object : list)
            result.add(getObjectFromObject(object));

        return result;
    }
}
