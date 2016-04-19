package tw.gov.ey.nici.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import tw.gov.ey.nici.models.NiciContent;
import tw.gov.ey.nici.models.NiciDocViewerLink;
import tw.gov.ey.nici.models.NiciEvent;
import tw.gov.ey.nici.models.NiciEventInfo;
import tw.gov.ey.nici.models.NiciFileUtilBar;
import tw.gov.ey.nici.models.NiciHeading;
import tw.gov.ey.nici.models.NiciImage;
import tw.gov.ey.nici.models.NiciInfo;
import tw.gov.ey.nici.models.NiciList;
import tw.gov.ey.nici.models.NiciParagraph;
import tw.gov.ey.nici.models.NiciProject;

public class NiciClientFactory {
    public enum ClientType {
        TESTING
    }

    public static NiciClient getClient(ClientType type) {
        switch (type) {
            case TESTING:
                return getTestingClient();
            default:
        }
        return null;
    }

    private static NiciClient getTestingClient() {
        return new FakeNiciClient();
    }

    private static class FakeNiciClient implements NiciClient {
        private static final int FAKE_DATA_COUNT = 15;

        private List<NiciEvent> eventList = new ArrayList<>();
        private List<NiciEventInfo> eventInfoList = new ArrayList<>();
        private List<NiciInfo> model = new ArrayList<NiciInfo>();

        {
            for (int i = 0; i < FAKE_DATA_COUNT; i++) {
                model.add(new NiciInfo()
                    .setTitle("Title " + i)
                    .setPublishedBy("PublishedBy " + i)
                    .setDate(new Date())
                    .setDescription("Description " + i)
                    .setLinkUrl("https://www.google.com?testId=" + i));

                eventList.add(new NiciEvent()
                        .setId("Id: " + i)
                        .setTitle("Title " + i)
                        .setLocation("Location " + i)
                        .setDate(new Date())
                        .setDescription("Description " + i)
                        .setMinutesTaker("Person " + i)
                        .setCoverImageUrl("http://dev.iifun.com.tw/nici/WebTools/Thumbnail.ashx?" +
                                "SiteID=1&MmmID=40&fd=MessagessPic_Pics&Fixed=&Pname=06.jpg"));

                eventInfoList.add(new NiciEventInfo()
                        .setId("Id: " + i)
                        .setTitle("Title " + i)
                        .setLocation("Location " + i)
                        .setDate(new Date())
                        .setDescription("Description " + i));
            }

            // data that miss some fields
            eventList.add(new NiciEvent()
                    .setId("Id")
                    .setLocation("Location ")
                    .setDate(new Date())
                    .setDescription("Description "));

            eventList.add(new NiciEvent()
                    .setId("Id")
                    .setTitle("Title ")
                    .setDate(new Date())
                    .setDescription("Description "));

            eventList.add(new NiciEvent()
                    .setId("Id")
                    .setTitle("Title ")
                    .setLocation("Location ")
                    .setDescription("Description "));

            eventList.add(new NiciEvent()
                    .setId("Id")
                    .setTitle("VERY LONG TITLE AHHAAHAHAHAAHAHAHAHAHAHAHAHAHAHAHAHAHAHA" +
                            "AAHAHAHAHAHAHHAAHAHHAHAAHHAHAHAAHAHAHHAHAAHAHAHAHAHAHAHAH ")
                    .setLocation("Location ")
                    .setDate(new Date()));
        }

        @Override
        public void clearCache() {
            // do nothing
        }

        @Override
        public NiciProject getNiciProject() {
            List<NiciContent> contentList = new ArrayList<NiciContent>();

            // heading 1
            NiciHeading heading1 = new NiciHeading("數位匯流發展方案推廣歷史");
            contentList.add(heading1);

            // paragraphs for heading 1
            NiciParagraph paragraph1 = new NiciParagraph("為創造優質數位匯流生活，" +
                    "打造數位匯流產業，進而提升國家次世代競爭力，我國於民國99年12月核定通過「數位匯" +
                    "流發展方案(2010-2015年)」，民國101年5月核定通過「數位匯流發展方案(2010-2015年)" +
                    "」（第二版）改版修訂，行政院同時成立「數位匯流專案小組」，負責督導、協調與推動我國的" +
                    "數位匯流工作。");
            NiciParagraph paragraph2 = new NiciParagraph("103年7月行政院核定「數位匯流專案小組」併入" +
                    "NICI小組，於NICI小組下設置「數位匯流組」，由國家通訊傳播委員會擔任召集機關。");
            contentList.add(paragraph1);
            contentList.add(paragraph2);

            // heading 2
            NiciHeading heading2 = new NiciHeading("數位匯流發展方案第二版");
            contentList.add(heading2);

            // file util bar
            NiciFileUtilBar fileUtilBar = new NiciFileUtilBar(true, true,
                    "http://www.nici.ey.gov.tw/Upload/RelFile/2829/733138/73f7af44-4c6a-4a2f-8204-3c4e7c82bb62.pdf",
                    "數位匯流發展方案第二版");
            contentList.add(fileUtilBar);

            // doc viewer link test
            NiciDocViewerLink viewerLink = new NiciDocViewerLink("http://www.nici.ey.gov.tw/Upload/" +
                    "RelFile/2829/733138/73f7af44-4c6a-4a2f-8204-3c4e7c82bb62.pdf",
                    "FILE LINK TITLE", "FILE LINK LABEL");
            contentList.add(viewerLink);

            // paragraphs for heading 2
            NiciParagraph paragraph3 = new NiciParagraph("為創造優質數位匯流生活，打造數位匯流產業，" +
                    "進而提升國家次世代競爭力，行政院於民國99年12月核定通過「數位匯流發展方案(2010-2015" +
                    "年)」，宣示推動我國的數位匯流產業發展環境。");
            NiciParagraph paragraph4 = new NiciParagraph("「數位匯流發展方案」自民國99年核定實施以來" +
                    "，在各界引起普遍的共鳴與迴響，惟伴隨資通訊科技的日行千里，各界對匯流發展的期待已遠高於" +
                    "方案設定當時。為精進我國數位化進程，帶給民眾速度更快、品質更好的數位生活，民國101年5" +
                    "月行政院核定通過「<b>數位匯流發展方案(2010-2015年)</b>」（第二版）修正，規劃七大推動主軸（" +
                    "增訂豐富電視節目內容主軸）、26項推動策略、 107個辦理措施，並重新設定數位匯流推動主要" +
                    "指標。");
            NiciParagraph paragraph5 = new NiciParagraph("為提昇效率、推動政府與產業合作發展數位匯流" +
                    "，數位匯流專案辦公室將參照部會執行「數位匯流發展方案（第二版）」辦理措施執行成效，" +
                    "隨時檢討推動策略，以期及早達成我國數位匯流相關指標，帶給民眾更好的數位匯流環境。");
            contentList.add(paragraph3);
            contentList.add(paragraph4);
            contentList.add(paragraph5);

            // test image
            NiciImage image1 = new NiciImage("http://www.nici.ey.gov.tw/Upload/UserFiles/" +
                    "images/ideataiwan-M.jpg", "DESCRIPTION");
            contentList.add(image1);

            Random random = new Random();
            final int randomMax = 100;

            // test list
            List<String> items = new ArrayList<String>();
            for (int i = 0; i < 5; i++) {
                items.add("Item " + random.nextInt(randomMax));
            }
            NiciList list1 = new NiciList(items);
            contentList.add(list1);
            NiciList list2 = new NiciList(items, NiciList.ListType.NUMBER);
            contentList.add(list2);

            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            return new NiciProject().setProjectFileUrl("http://dev.iifun.com.tw/nici/WebTools/File" +
                    "sDownload.ashx?Siteid=1&MmmID=31&fd=Messagess_Files&Pname=1.docx")
                    .setContentList(contentList);
        }

        @Override
        public int getNiciEventCount() {
            return model.size() + 4;
        }

        @Override
        public List<NiciEvent> getNiciEvent(int skip, int limit) {
            List<NiciEvent> result = new ArrayList<NiciEvent>();
            for (int i = 0; i < eventList.size(); i++) {
                if (i < skip) {
                    continue;
                }
                if (result.size() == limit) {
                    break;
                }
                result.add(eventList.get(i));
            }
            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            return result;
        }

        @Override
        public NiciEvent getNiciEventById(String eventId) {
            List<NiciContent> contentList = new ArrayList<NiciContent>();

            // heading 1
            NiciHeading heading1 = new NiciHeading("數位匯流發展方案推廣歷史");
            contentList.add(heading1);

            // paragraphs for heading 1
            NiciParagraph paragraph1 = new NiciParagraph("為創造優質數位匯流生活，" +
                    "打造數位匯流產業，進而提升國家次世代競爭力，我國於民國99年12月核定通過「數位匯" +
                    "流發展方案(2010-2015年)」，民國101年5月核定通過「數位匯流發展方案(2010-2015年)" +
                    "」（第二版）改版修訂，行政院同時成立「數位匯流專案小組」，負責督導、協調與推動我國的" +
                    "數位匯流工作。");
            NiciParagraph paragraph2 = new NiciParagraph("103年7月行政院核定「數位匯流專案小組」併入" +
                    "NICI小組，於NICI小組下設置「數位匯流組」，由國家通訊傳播委員會擔任召集機關。");
            contentList.add(paragraph1);
            contentList.add(paragraph2);
            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            return new NiciEvent().setId(eventId).setEventContentList(contentList);
        }

        @Override
        public int getNiciEventInfoCount() {
            return eventInfoList.size();
        }

        @Override
        public List<NiciEventInfo> getNiciEventInfo(int skip, int limit) {
            List<NiciEventInfo> result = new ArrayList<>();
            for (int i = 0; i < eventInfoList.size(); i++) {
                if (i < skip) {
                    continue;
                }
                if (result.size() == limit) {
                    break;
                }
                result.add(eventInfoList.get(i));
            }
            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
//            if (skip >= 9) {
//                throw new RuntimeException();
//            }
            return result;
        }

        @Override
        public NiciEventInfo getNiciEventInfoById(String eventInfoId) {
            List<NiciContent> contentList = new ArrayList<NiciContent>();

            // heading 1
            NiciHeading heading1 = new NiciHeading("數位匯流發展方案推廣歷史");
            contentList.add(heading1);

            // paragraphs for heading 1
            NiciParagraph paragraph1 = new NiciParagraph("為創造優質數位匯流生活，" +
                    "打造數位匯流產業，進而提升國家次世代競爭力，我國於民國99年12月核定通過「數位匯" +
                    "流發展方案(2010-2015年)」，民國101年5月核定通過「數位匯流發展方案(2010-2015年)" +
                    "」（第二版）改版修訂，行政院同時成立「數位匯流專案小組」，負責督導、協調與推動我國的" +
                    "數位匯流工作。");
            NiciParagraph paragraph2 = new NiciParagraph("103年7月行政院核定「數位匯流專案小組」併入" +
                    "NICI小組，於NICI小組下設置「數位匯流組」，由國家通訊傳播委員會擔任召集機關。");
            contentList.add(paragraph1);
            contentList.add(paragraph2);
            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            return new NiciEventInfo().setId(eventInfoId).setEventInfoContentList(contentList);
        }

        @Override
        public int getNiciInfoCount() {
            return model.size();
        }

        @Override
        public List<NiciInfo> getNiciInfo(int skip, int limit) {
            List<NiciInfo> result = new ArrayList<NiciInfo>();
            for (int i = 0; i < model.size(); i++) {
                if (i < skip) {
                    continue;
                }
                if (result.size() == limit) {
                    break;
                }
                result.add(model.get(i));
            }
            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}
            // add exception for testing
//            if (skip >= 10) {
//                throw new RuntimeException();
//            }
            // fail the first request for testing
//            if (skip == 0) {
//                throw new RuntimeException();
//            }
            return result;
        }
    }
}
