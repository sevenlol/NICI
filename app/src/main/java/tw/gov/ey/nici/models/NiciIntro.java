package tw.gov.ey.nici.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tw.gov.ey.nici.utils.JsonUtil;
import tw.gov.ey.nici.utils.NiciContentUtil;

public class NiciIntro {
    private List<NiciContent> contentList;
    private String videoId;
    private Integer videoLocationIndex;

    public NiciIntro() {}

    public List<NiciContent> getContentList() { return contentList; }
    public String getVideoId() { return videoId; }
    public Integer getVideoLocationIndex() { return videoLocationIndex; }

    public NiciIntro setContentList(List<NiciContent> contentList) {
        this.contentList = contentList; return this;
    }

    public NiciIntro setVideoId(String videoId) {
        this.videoId = videoId; return this;
    }

    public NiciIntro setVideoLocationIndex(Integer videoLocationIndex) {
        this.videoLocationIndex = videoLocationIndex; return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NiciIntro.").append(JsonUtil.LINE_SEPARATOR);
        builder.append("Video ID: " + (videoId == null ? "NULL" : videoId)).append(JsonUtil.LINE_SEPARATOR);
        builder.append("Video Location Index: " + (videoLocationIndex == null ? "NULL" : videoLocationIndex));
        builder.append(JsonUtil.LINE_SEPARATOR);
        builder.append("ContentList: " + (contentList == null ? "NULL" : contentList.size())).append(JsonUtil.LINE_SEPARATOR);
        if (contentList.size() > 0) {
            for (int i = 0; i < contentList.size(); i++) {
                NiciContent content = contentList.get(i);
                builder.append(String.format("Content %2d.", i + 1)).append(JsonUtil.LINE_SEPARATOR);
                builder.append(content == null ? "NULL" : content.toString());
                builder.append(JsonUtil.LINE_SEPARATOR);
            }
        }
        return builder.toString();
    }

    public static class Parser {
        private static final String ATTACHMENT_HEADING = "相關檔案";
        private static final String DEFAULT_ATTACHMENT_TITLE = "";
        private static final String DEFAULT_ATTACHMENT_LABEL = "";

        static class JsonKey {
            static final String MODE = "Mode";
            static final String TITLE = "Title";
            static final String CONTENT = "Content";
            static final String POST_DATE = "PostDate";
            static final String ATTACHMENT_LIST = "AttachmentList";
            static final String ATTACHMENT_NAME = "name";
            static final String ATTACHMENT_URL = "url";
            static final String VIDEO = "Video";
        }

        public static NiciIntro parse(JsonElement data) {
            if (data == null || !(data instanceof JsonObject)) {
                throw new IllegalArgumentException();
            }

            List<NiciContent> contents = new ArrayList<>();
            JsonObject obj = (JsonObject) data;

            // parse mode, do nothing atm
            String mode = JsonUtil.getStringFromObject(obj, JsonKey.MODE);

            // parse title
            String title = JsonUtil.getStringFromObject(obj, JsonKey.TITLE);
            if (title != null && !title.equals("")) {
                contents.add(new NiciHeading(title));
            }

            // parse content
            String content = JsonUtil.getStringFromObject(obj, JsonKey.CONTENT);
            if (content != null && !content.equals("")) {
                contents.addAll(NiciContentUtil.Parser.parse(content));
            }

            // parse post date, do nothing atm
            String postDate = JsonUtil.getStringFromObject(obj, JsonKey.POST_DATE);

            // parse video id
            String videoUrl = JsonUtil.getStringFromObject(obj, JsonKey.VIDEO);
            String videoId = NiciContentUtil.getVideoIdFromUrl(videoUrl);

            // parse attachment list, do nothing atm
            JsonElement attachmentElement = obj.get(JsonKey.ATTACHMENT_LIST);
            if (attachmentElement != null && attachmentElement.isJsonArray()) {
                // cuz idiots changing API on their own
                /*Map<String, String> attachmentMap = JsonUtil.getStringMapFromObject(
                        attachmentElement.getAsJsonObject());
                */
                Map<String, String> attachmentMap = JsonUtil.getStringMapFromArray(
                        attachmentElement.getAsJsonArray(),
                        JsonKey.ATTACHMENT_NAME,
                        JsonKey.ATTACHMENT_URL);
                NiciContentUtil.addAttachments(
                        contents, attachmentMap,
                        false, // show heading when no attachment
                        true, true, // show key as title, label
                        ATTACHMENT_HEADING, null, null); // default heading, title, label
            }

            return new NiciIntro().setContentList(contents).setVideoId(videoId);
        }
    }
}
