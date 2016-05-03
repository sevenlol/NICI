package tw.gov.ey.nici.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Map;

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
}
