package tw.gov.ey.nici.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tw.gov.ey.nici.utils.JsonUtil;
import tw.gov.ey.nici.utils.NiciContentUtil;

public class NiciProject {
    private List<NiciContent> contentList;
    private String projectFileUrl;

    public NiciProject() {}

    /* getter methods */

    public List<NiciContent> getContentList() { return contentList; }
    public String getProjectFileUrl() { return projectFileUrl; }

    /* setter methods */

    public NiciProject setContentList(List<NiciContent> contentList) {
        this.contentList = contentList; return this;
    }

    public NiciProject setProjectFileUrl(String projectFileUrl) {
        this.projectFileUrl = projectFileUrl; return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NiciProject.").append(JsonUtil.LINE_SEPARATOR);
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
        private static final String ATTACHMENT_HEADING = "計畫相關檔案";
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
        }

        public static NiciProject parse(JsonElement data) {
            if (data == null || !(data instanceof JsonObject)) {
                throw new IllegalArgumentException();
            }

            List<NiciContent> contents = new ArrayList<>();
            JsonObject obj = (JsonObject) data;

            // parse mode, do nothing atm
            String mode = JsonUtil.getStringFromObject(obj, NiciProject.Parser.JsonKey.MODE);

            // parse title
            String title = JsonUtil.getStringFromObject(obj, NiciProject.Parser.JsonKey.TITLE);
            if (title != null && !title.equals("")) {
                contents.add(new NiciHeading(title));
            }

            // parse content
            String content = JsonUtil.getStringFromObject(obj, NiciProject.Parser.JsonKey.CONTENT);
            if (content != null && !content.equals("")) {
                contents.addAll(NiciContentUtil.Parser.parse(content));
            }

            // parse post date, do nothing atm
            String postDate = JsonUtil.getStringFromObject(obj, NiciProject.Parser.JsonKey.POST_DATE);

            // parse attachment list, do nothing atm
            JsonElement attachmentElement = obj.get(NiciProject.Parser.JsonKey.ATTACHMENT_LIST);
            if (attachmentElement != null && attachmentElement.isJsonArray()) {
                // cuz idiots changing API on their own
                /*Map<String, String> attachmentMap = JsonUtil.getStringMapFromObject(
                        attachmentElement.getAsJsonObject());
                */
                Map<String, String> attachmentMap = JsonUtil.getStringMapFromArray(
                        attachmentElement.getAsJsonArray(),
                        JsonKey.ATTACHMENT_NAME,
                        JsonKey.ATTACHMENT_URL);
                NiciContentUtil.addFileActionBars(
                        contents, attachmentMap,
                        true, // show heading when no attachment
                        ATTACHMENT_HEADING); // default heading, title, label
            }
            return new NiciProject().setContentList(contents);
        }
    }
}
