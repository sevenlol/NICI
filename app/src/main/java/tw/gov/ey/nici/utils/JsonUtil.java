package tw.gov.ey.nici.utils;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class JsonUtil {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /*
     * return string from a specified key, return null if the type is not primitive or
     * the value not exist
     *
     * Note: all primitive values will be transformed into string
     */
    public static String getStringFromObject(JsonObject obj, String key) {
        if (obj == null || key == null) {
            throw new IllegalArgumentException();
        }
        JsonElement element = obj.get(key);
        if (element == null || !(element instanceof JsonPrimitive)) {
            return null;
        }
        JsonPrimitive primitive = (JsonPrimitive) element;
        if (primitive.isJsonNull()) {
            return null;
        }
        return primitive.getAsString();
    }

    public static Set<String> getStringSetFromArray(JsonArray array) {
        if (array == null) {
            throw new IllegalArgumentException();
        }

        Set<String> stringSet = new TreeSet<>();
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            if (element == null || !element.isJsonPrimitive() ||
                    element.isJsonNull()) {
                continue;
            }
            String str = element.getAsString();
            if (str == null || TextUtils.isEmpty(str)) {
                continue;
            }
            stringSet.add(str);
        }
        return stringSet;
    }

    public static Map<String, String> getStringMapFromObject(JsonObject obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        Map<String, String> map = new HashMap<>();
        if (obj.entrySet() == null) {
            return map;
        }
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            if (entry.getKey().equals("")) {
                continue;
            }

            JsonElement value = entry.getValue();
            if (!value.isJsonPrimitive()) {
                continue;
            }
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isJsonNull()) {
                continue;
            }
            if (primitive.getAsString() == null ||
                    primitive.getAsString().equals("")) {
                continue;
            }
            map.put(entry.getKey(), primitive.getAsString());
        }
        return map;
    }

    public static Map<String, String> getStringMapFromArray(
            JsonArray array, String nameKey, String valueKey) {
        if (array == null) {
            throw new IllegalArgumentException();
        }
        if (nameKey == null || TextUtils.isEmpty(nameKey)) {
            throw new IllegalArgumentException();
        }
        if (valueKey == null || TextUtils.isEmpty(valueKey)) {
            throw new IllegalArgumentException();
        }

        Map<String, String> stringMap = new TreeMap<>();
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            if (element == null || !element.isJsonObject() ||
                element.isJsonNull()) {
                continue;
            }

            JsonObject obj = element.getAsJsonObject();
            JsonElement keyElement = obj.get(nameKey);
            JsonElement valueElement = obj.get(valueKey);
            if (keyElement == null || !keyElement.isJsonPrimitive() ||
                keyElement.isJsonNull()) {
                continue;
            }
            if (valueElement == null || !valueElement.isJsonPrimitive() ||
                    valueElement.isJsonNull()) {
                continue;
            }

            String key = keyElement.getAsString();
            String value = valueElement.getAsString();
            if (key == null || TextUtils.isEmpty(key)) {
                continue;
            }
            if (value == null || TextUtils.isEmpty(value)) {
                continue;
            }

            stringMap.put(key, value);
        }
        return stringMap;
    }
}
