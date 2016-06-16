package tw.gov.ey.nici.models;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import tw.gov.ey.nici.utils.JsonUtil;
import tw.gov.ey.nici.utils.NiciContentUtil;
import tw.gov.ey.nici.utils.NiciDateUtil;

public class NiciEvent {
    private String id;
    private String title;
    private Date date;
    private String location;
    private String description;
    private String minutesTaker;
    private String coverImageUrl;
    private String eventMinutesUrl;
    private List<NiciContent> eventContentList;
    private List<RelatedFile> relatedFileList;


    public NiciEvent() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getMinutesTaker() { return minutesTaker; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getEventMinutesUrl() { return eventMinutesUrl; }
    public List<NiciContent> getEventContentList() { return eventContentList; }
    public List<RelatedFile> getRelatedFileList() { return relatedFileList; }

    public NiciEvent setId(String id) {
        this.id = id; return this;
    }

    public NiciEvent setTitle(String title) {
        this.title = title; return this;
    }

    public NiciEvent setDate(Date date) {
        this.date = date; return this;
    }

    public NiciEvent setLocation(String location) {
        this.location = location; return this;
    }

    public NiciEvent setDescription(String description) {
        this.description = description; return this;
    }

    public NiciEvent setMinutesTaker(String minutesTaker) {
        this.minutesTaker = minutesTaker; return this;
    }

    public NiciEvent setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl; return this;
    }

    public NiciEvent setEventMinutesUrl(String eventMinutesUrl) {
        this.eventMinutesUrl = eventMinutesUrl; return this;
    }

    public NiciEvent setEventContentList(List<NiciContent> eventContentList) {
        this.eventContentList = eventContentList; return this;
    }

    public NiciEvent setRelatedFileList(List<RelatedFile> relatedFileList) {
        this.relatedFileList = relatedFileList; return this;
    }

    public static class RelatedFile {
        public String fileTitle;
        public String fileUrl;
        public String fileLabel;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NiciEvent.").append(System.lineSeparator());
        builder.append("ID: " + (id == null ? "NULL" : id)).append(System.lineSeparator());
        builder.append("Title: " + (title == null ? "NULL" : title)).append(System.lineSeparator());
        builder.append("Date: " + (date == null ? "NULL" : date.toString())).append(System.lineSeparator());
        builder.append("Location: " + (location == null ? "NULL" : location)).append(System.lineSeparator());
        builder.append("Image: " + (coverImageUrl == null ? "NULL" : coverImageUrl)).append(System.lineSeparator());
        builder.append("ContentList: " + (eventContentList == null ? "NULL" : eventContentList.size())).append(System.lineSeparator());
        if (eventContentList.size() > 0) {
            for (int i = 0; i < eventContentList.size(); i++) {
                NiciContent content = eventContentList.get(i);
                builder.append(String.format("Content %2d.", i + 1)).append(System.lineSeparator());
                builder.append(content == null ? "NULL" : content.toString());
                builder.append(System.lineSeparator());
            }
        }
        builder.append("RelatedFiles: " + (relatedFileList == null ? "NULL" : relatedFileList.size())).append(System.lineSeparator());
        if (relatedFileList.size() > 0) {
            for (int i = 0; i < relatedFileList.size(); i++) {
                if (relatedFileList.get(i) == null) {
                    builder.append(String.format("RelatedFile %2d. NULL", i + 1)).append(System.lineSeparator());
                    continue;
                }

                RelatedFile file = relatedFileList.get(i);
                builder.append(String.format("RelatedFile %2d: Url: %s, Title: %s, Label: %s",
                        i+1, file.fileUrl == null ? "NULL" : file.fileUrl,
                        file.fileTitle == null ? "NULL" : file.fileTitle,
                        file.fileLabel == null ? "NULL" : file.fileLabel)).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public static class ListParser {
        private static final String JSON_KEY_ARTICLE_LIST = "ArticleList";
        private static final String JSON_KEY_ARTICLE_COUNT = "TotalArticle";
        public static Map.Entry<Integer, List<NiciEvent>> parse(JsonElement data) {
            if (data == null || !data.isJsonObject()) {
                throw new IllegalArgumentException();
            }

            JsonObject obj = data.getAsJsonObject();
            JsonElement listElement = obj.get(JSON_KEY_ARTICLE_LIST);
            JsonElement countElement = obj.get(JSON_KEY_ARTICLE_COUNT);
            if (listElement == null || !listElement.isJsonArray()) {
                throw new IllegalArgumentException();
            }
            if (countElement == null || !countElement.isJsonPrimitive()) {
                throw new IllegalArgumentException();
            }

            // parse total count
            Integer total = countElement.getAsInt();

            // // parse event list
            List<NiciEvent> eventList = new ArrayList<>();
            for (JsonElement itemElement : listElement.getAsJsonArray()) {
                // skip invalid item
                if (itemElement == null || !itemElement.isJsonObject()) {
                    continue;
                }

                JsonObject itemObj = itemElement.getAsJsonObject();
                try {
                    NiciEvent event = Parser.parse(itemObj);
                    if (event != null) {
                        eventList.add(event);
                    }
                } catch (Exception e) {
                    // TODO add logs
                    // ignore invalid item
                }
            }

            Map.Entry<Integer, List<NiciEvent>> entry =
                    new AbstractMap.SimpleImmutableEntry<Integer, List<NiciEvent>>(total, eventList);

            return entry;
        }
    }

    public static class Parser {
        private static final String ATTACHMENT_HEADING = "相關檔案";
        private static final String DEFAULT_ATTACHMENT_TITLE = "";
        private static final String DEFAULT_ATTACHMENT_LABEL = "";

        static class JsonKey {
            static final String ID = "Id";
            static final String PHOTO = "Photo";
            static final String TITLE = "Title";
            static final String POST_DATE = "PostDate";
            static final String MEETING_DATE = "MeetingDate";
            static final String MEETING_LOCATION = "MeetingLocat";

            static final String CONTENT = "Content";
            static final String ATTACHMENT_LIST = "AttachmentList";
            static final String ATTACHMENT_NAME = "name";
            static final String ATTACHMENT_URL = "url";
            static final String PHOTO_LIST = "PhotoList";
        }

        public static NiciEvent parse(JsonElement item) {
            if (item == null || !item.isJsonObject()) {
                throw new IllegalArgumentException();
            }

            JsonObject obj = item.getAsJsonObject();

            // parse post date, do nothing atm
            String postDateStr = JsonUtil.getStringFromObject(obj, JsonKey.POST_DATE);

            // parse meeting date
            String meetingDateStr = JsonUtil.getStringFromObject(obj, JsonKey.MEETING_DATE);

            // generate contentList
            List<NiciContent> contents = new ArrayList<>();

            // parse content
            String content = JsonUtil.getStringFromObject(obj, JsonKey.CONTENT);
            if (content != null && !content.equals("")) {
                contents.addAll(NiciContentUtil.Parser.parse(content));
            }

            // parse photo list
            JsonElement photoElement = obj.get(JsonKey.PHOTO_LIST);
            if (photoElement != null && photoElement.isJsonArray()) {
                // cuz idiots changing API on their own
//                Map<String, String> photoMap = JsonUtil.getStringMapFromObject(
//                        photoElement.getAsJsonObject());
                Set<String> photoSet = JsonUtil.getStringSetFromArray(photoElement.getAsJsonArray());
                Map<String, String> photoMap = new TreeMap<>();
                for (String url : photoSet) {
                    photoMap.put(url, url);
                }
                NiciContentUtil.addPhotos(contents, photoMap, false);
            }

            // parse attachment list
            JsonElement attachmentElement = obj.get(JsonKey.ATTACHMENT_LIST);
            List<NiciEvent.RelatedFile> relatedFiles = new ArrayList<>();
            if (attachmentElement != null && attachmentElement.isJsonArray()) {
                /*Map<String, String> attachmentMap = JsonUtil.getStringMapFromObject(
                        attachmentElement.getAsJsonObject());*/
                // cuz idiots changing API on their own
                Map<String, String> attachmentMap = JsonUtil.getStringMapFromArray(
                        attachmentElement.getAsJsonArray(),
                        JsonKey.ATTACHMENT_NAME,
                        JsonKey.ATTACHMENT_URL);
                for (String key : attachmentMap.keySet()) {
                    if (key == null || key.equals("")) {
                        continue;
                    }
                    String url = attachmentMap.get(key);
                    if (url == null || url.equals("")) {
                        continue;
                    }
                    NiciEvent.RelatedFile file = new NiciEvent.RelatedFile();
                    file.fileUrl = url;
                    file.fileLabel = key;
                    file.fileTitle = key;
                    relatedFiles.add(file);
                }
            }

            return new NiciEvent()
                    .setId(JsonUtil.getStringFromObject(obj, JsonKey.ID))
                    .setCoverImageUrl(JsonUtil.getStringFromObject(obj, JsonKey.PHOTO))
                    .setTitle(JsonUtil.getStringFromObject(obj, JsonKey.TITLE))
                    .setDate(NiciDateUtil.parseMeetingDateStr(meetingDateStr))
                    .setLocation(JsonUtil.getStringFromObject(obj, JsonKey.MEETING_LOCATION))
                    .setEventContentList(contents)
                    .setRelatedFileList(relatedFiles);
        }
    }
}
