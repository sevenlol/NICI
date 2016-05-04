package tw.gov.ey.nici.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tw.gov.ey.nici.utils.JsonUtil;
import tw.gov.ey.nici.utils.NiciContentUtil;
import tw.gov.ey.nici.utils.NiciDateUtil;

public class NiciEventInfo {
    private String id;
    private String title;
    private Date date;
    private String location;
    private String description;
    private List<NiciContent> eventInfoContentList;
    private List<RelatedLink> relatedLinkList;
    private List<RelatedFile> relatedFileList;

    public NiciEventInfo() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public List<NiciContent> getEventInfoContentList() { return eventInfoContentList; }
    public List<RelatedLink> getRelatedLinkList() { return relatedLinkList; }
    public List<RelatedFile> getRelatedFileList() { return relatedFileList; }

    public NiciEventInfo setId(String id) {
        this.id = id; return this;
    }

    public NiciEventInfo setTitle(String title) {
        this.title = title; return this;
    }

    public NiciEventInfo setDate(Date date) {
        this.date = date; return this;
    }

    public NiciEventInfo setLocation(String location) {
        this.location = location; return this;
    }

    public NiciEventInfo setDescription(String description) {
        this.description = description; return this;
    }

    public NiciEventInfo setEventInfoContentList(List<NiciContent> eventInfoContentList) {
        this.eventInfoContentList = eventInfoContentList; return this;
    }

    public NiciEventInfo setRelatedLinkList(List<RelatedLink> relatedLinkList) {
        this.relatedLinkList = relatedLinkList; return this;
    }

    public NiciEventInfo setRelatedFileList(List<RelatedFile> relatedFileList) {
        this.relatedFileList = relatedFileList; return this;
    }

    public static class RelatedFile {
        public String fileTitle;
        public String fileUrl;
        public String fileLabel;
    }

    public static class RelatedLink {
        public String linkLabel;
        public String linkUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NiciEvent.").append(JsonUtil.LINE_SEPARATOR);
        builder.append("ID: " + (id == null ? "NULL" : id)).append(JsonUtil.LINE_SEPARATOR);
        builder.append("Title: " + (title == null ? "NULL" : title)).append(JsonUtil.LINE_SEPARATOR);
        builder.append("Date: " + (date == null ? "NULL" : date.toString())).append(JsonUtil.LINE_SEPARATOR);
        builder.append("Location: " + (location == null ? "NULL" : location)).append(JsonUtil.LINE_SEPARATOR);
        builder.append("ContentList: " + (eventInfoContentList == null ? "NULL" : eventInfoContentList.size())).append(JsonUtil.LINE_SEPARATOR);
        if (eventInfoContentList.size() > 0) {
            for (int i = 0; i < eventInfoContentList.size(); i++) {
                NiciContent content = eventInfoContentList.get(i);
                builder.append(String.format("Content %2d.", i + 1)).append(JsonUtil.LINE_SEPARATOR);
                builder.append(content == null ? "NULL" : content.toString());
                builder.append(System.getProperty("line.separator"));
            }
        }
        builder.append("RelatedLinks: " + (relatedLinkList == null ? "NULL" : relatedLinkList.size())).append(JsonUtil.LINE_SEPARATOR);
        // TODO print all related links
        builder.append("RelatedFiles: " + (relatedFileList == null ? "NULL" : relatedFileList.size())).append(JsonUtil.LINE_SEPARATOR);
        if (relatedFileList.size() > 0) {
            for (int i = 0; i < relatedFileList.size(); i++) {
                if (relatedFileList.get(i) == null) {
                    builder.append(String.format("RelatedFile %2d. NULL", i + 1)).append(JsonUtil.LINE_SEPARATOR);
                    continue;
                }

                RelatedFile file = relatedFileList.get(i);
                builder.append(String.format("RelatedFile %2d: Url: %s, Title: %s, Label: %s",
                        i+1, file.fileUrl == null ? "NULL" : file.fileUrl,
                        file.fileTitle == null ? "NULL" : file.fileTitle,
                        file.fileLabel == null ? "NULL" : file.fileLabel)).append(JsonUtil.LINE_SEPARATOR);
            }
        }
        return builder.toString();
    }

    public static class ListParser {
        private static final String JSON_KEY_ARTICLE_LIST = "ArticleList";
        private static final String JSON_KEY_ARTICLE_COUNT = "TotalArticle";
        public static Map.Entry<Integer, List<NiciEventInfo>> parse(JsonElement data) {
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
            List<NiciEventInfo> eventInfoList = new ArrayList<>();
            for (JsonElement itemElement : listElement.getAsJsonArray()) {
                // skip invalid item
                if (itemElement == null || !itemElement.isJsonObject()) {
                    continue;
                }

                JsonObject itemObj = itemElement.getAsJsonObject();
                try {
                    NiciEventInfo eventInfo = Parser.parse(itemObj);
                    if (eventInfo != null) {
                        eventInfoList.add(eventInfo);
                    }
                } catch (Exception e) {
                    // TODO add logs
                    // ignore invalid item
                }
            }

            Map.Entry<Integer, List<NiciEventInfo>> entry =
                    new AbstractMap.SimpleImmutableEntry<Integer, List<NiciEventInfo>>(total, eventInfoList);

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
            static final String PHOTO_LIST = "PhotoList";
        }

        public static NiciEventInfo parse(JsonElement item) {
            if (item == null || !item.isJsonObject()) {
                throw new IllegalArgumentException();
            }

            JsonObject obj = item.getAsJsonObject();

            // parse photo
            String photoUrl = JsonUtil.getStringFromObject(obj, JsonKey.PHOTO);
            if (photoUrl != null && !photoUrl.equals("")) {
                // TODO do something with it
            }

            // parse post date, do nothing atm
            String postDateStr = JsonUtil.getStringFromObject(obj, JsonKey.POST_DATE);

            // parse meeting date
            // TODO check date format
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
            if (photoElement != null && photoElement.isJsonObject()) {
                Map<String, String> photoMap = JsonUtil.getStringMapFromObject(
                        photoElement.getAsJsonObject());
                NiciContentUtil.addPhotos(contents, photoMap, true);
            }

            // parse attachment list
            JsonElement attachmentElement = obj.get(JsonKey.ATTACHMENT_LIST);
            List<NiciEventInfo.RelatedFile> relatedFiles = new ArrayList<>();
            if (attachmentElement != null && attachmentElement.isJsonObject()) {
                Map<String, String> attachmentMap = JsonUtil.getStringMapFromObject(
                        attachmentElement.getAsJsonObject());
                for (String key : attachmentMap.keySet()) {
                    if (key == null || key.equals("")) {
                        continue;
                    }
                    String url = attachmentMap.get(key);
                    if (url == null || url.equals("")) {
                        continue;
                    }
                    NiciEventInfo.RelatedFile file = new NiciEventInfo.RelatedFile();
                    file.fileUrl = url;
                    file.fileLabel = key;
                    file.fileTitle = key;
                    relatedFiles.add(file);
                }
            }

            // TODO parse related link list

            return new NiciEventInfo()
                    .setId(JsonUtil.getStringFromObject(obj, JsonKey.ID))
                    .setTitle(JsonUtil.getStringFromObject(obj, JsonKey.TITLE))
                    .setDate(NiciDateUtil.parseMeetingDateStr(meetingDateStr))
                    .setLocation(JsonUtil.getStringFromObject(obj, JsonKey.MEETING_LOCATION))
                    .setEventInfoContentList(contents)
                    .setRelatedFileList(relatedFiles);
        }
    }
}
