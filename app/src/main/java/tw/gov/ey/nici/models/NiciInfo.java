package tw.gov.ey.nici.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tw.gov.ey.nici.utils.JsonUtil;
import tw.gov.ey.nici.utils.NiciDateUtil;

public class NiciInfo {
    private String title;
    private Date date;
    private String publishedBy;
    private String description;
    private String linkUrl;

    public NiciInfo() {}

    /* getter methods */

    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getPublishedBy() { return publishedBy; }
    public String getDescription() { return description; }
    public String getLinkUrl() { return linkUrl; }

    /* setter methods */

    public NiciInfo setTitle(String title) {
        this.title = title; return this;
    }

    public NiciInfo setDate(Date date) {
        this.date = date; return this;
    }

    public NiciInfo setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy; return this;
    }

    public NiciInfo setDescription(String description) {
        this.description = description; return this;
    }

    public NiciInfo setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl; return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Title: " + (title == null ? "NULL" : title)).append(JsonUtil.LINE_SEPARATOR);
        builder.append("Published By: " + (publishedBy == null ? "NULL" : publishedBy)).append(JsonUtil.LINE_SEPARATOR);
        builder.append("Url: " + (linkUrl == null ? "NULL" : linkUrl)).append(JsonUtil.LINE_SEPARATOR);
        return builder.toString();
    }

    public static class ListParser {
        private static final String JSON_KEY_INFO_LIST = "ArticleList";
        private static final String JSON_KEY_INFO_COUNT = "TotalArticle";
        public static Map.Entry<Integer, List<NiciInfo>> parse(JsonElement data) {
            if (data == null || !data.isJsonObject()) {
                throw new IllegalArgumentException();
            }

            JsonObject obj = data.getAsJsonObject();
            JsonElement listElement = obj.get(JSON_KEY_INFO_LIST);
            JsonElement countElement = obj.get(JSON_KEY_INFO_COUNT);
            if (listElement == null || !listElement.isJsonArray()) {
                throw new IllegalArgumentException();
            }
            if (countElement == null || !countElement.isJsonPrimitive()) {
                throw new IllegalArgumentException();
            }

            // parse total count
            Integer total = countElement.getAsInt();

            // // parse info list
            List<NiciInfo> infoList = new ArrayList<>();
            for (JsonElement itemElement : listElement.getAsJsonArray()) {
                // skip invalid item
                if (itemElement == null || !itemElement.isJsonObject()) {
                    continue;
                }

                JsonObject itemObj = itemElement.getAsJsonObject();
                try {
                    NiciInfo info = Parser.parse(itemObj);
                    if (info != null) {
                        infoList.add(info);
                    }
                } catch (Exception e) {
                    // TODO add logs
                    // ignore invalid item
                }
            }

            Map.Entry<Integer, List<NiciInfo>> entry =
                    new AbstractMap.SimpleImmutableEntry<Integer, List<NiciInfo>>(total, infoList);

            return entry;
        }
    }

    public static class Parser {
        static class JsonKey {
            static final String TITLE = "Title";
            static final String URL = "Url";
            static final String POST_DATE = "PostDate";
        }

        public static NiciInfo parse(JsonElement item) {
            if (item == null || !item.isJsonObject()) {
                throw new IllegalArgumentException();
            }

            JsonObject obj = item.getAsJsonObject();

            // parse post date, do nothing atm
            String postDateStr = JsonUtil.getStringFromObject(obj, JsonKey.POST_DATE);

            return new NiciInfo()
                    .setTitle(JsonUtil.getStringFromObject(obj, JsonKey.TITLE))
                    .setDate(NiciDateUtil.parsePostDateStr(postDateStr))
                    .setLinkUrl(JsonUtil.getStringFromObject(obj, JsonKey.URL));
        }
    }
}
