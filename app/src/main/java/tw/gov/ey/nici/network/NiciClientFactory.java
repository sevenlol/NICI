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
import tw.gov.ey.nici.models.NiciIntro;
import tw.gov.ey.nici.models.NiciList;
import tw.gov.ey.nici.models.NiciParagraph;
import tw.gov.ey.nici.models.NiciProject;

public class NiciClientFactory {
    private static final String SERVER_URL = "http://www.dcoffice.com.tw/";
//    private static final String SERVER_URL = "http://192.168.1.104:8080/";

    public enum ClientType {
        TESTING, SERVER
    }

    public static NiciClient getClient(ClientType type) {
        switch (type) {
            case TESTING:
                return getTestingClient();
            case SERVER:
                return getServerClient();
            default:
        }
        return null;
    }

    private static NiciClient getTestingClient() {
        return new FakeNiciClient();
    }

    private static NiciClient getServerClient() {
        return new NiciClientImpl(SERVER_URL);
    }

    private static class FakeNiciClient implements NiciClient {
        private static final int FAKE_DATA_COUNT = 15;

        private List<NiciEvent> eventList = new ArrayList<>();
        private List<NiciEventInfo> eventInfoList = new ArrayList<>();
        private List<NiciInfo> model = new ArrayList<NiciInfo>();

        {
            for (int i = 0; i < FAKE_DATA_COUNT; i++) {
//                model.add(new NiciInfo()
//                    .setTitle("Title " + i)
//                    .setPublishedBy("PublishedBy " + i)
//                    .setDate(new Date())
//                    .setDescription("Description " + i)
//                    .setLinkUrl("https://www.google.com?testId=" + i));

                eventList.add(new NiciEvent()
                        .setId("Id: " + i)
                        .setTitle(String.format("第%d次專家座談會", i + 1))
                        .setLocation("台經院208會議室")
                        .setDate(new Date())
                        .setDescription("Description " + i)
                        .setMinutesTaker("Person " + i)
                        .setCoverImageUrl("http://dev.iifun.com.tw/nici/WebTools/Thumbnail.ashx?" +
                                "SiteID=1&MmmID=40&fd=MessagessPic_Pics&Fixed=&Pname=06.jpg"));

                eventInfoList.add(new NiciEventInfo()
                        .setId("Id: " + i)
                        .setTitle(String.format("第%d次專家座談會", i + 1))
                        .setLocation("台經院208會議室")
                        .setDate(new Date())
                        .setDescription("Description " + i));
            }

            model.addAll(getTestingInfoData());

            // data that miss some fields
            /*eventList.add(new NiciEvent()
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
                    .setDate(new Date()));*/
        }

        @Override
        public void clearCache() {
            // do nothing
        }

        @Override
        public NiciIntro getNiciIntro() {
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
                    "images/ideataiwan-M.jpg", "創意台灣 政策白皮書");
            contentList.add(image1);

            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (contentList.size() > 0) {
//                throw new RuntimeException();
//            }

            return new NiciIntro()
                    .setContentList(contentList)
                    .setVideoId("Gx1emgAKkh0")
                    .setVideoLocationIndex(4);
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
//            NiciDocViewerLink viewerLink = new NiciDocViewerLink("http://www.nici.ey.gov.tw/Upload/" +
//                    "RelFile/2829/733138/73f7af44-4c6a-4a2f-8204-3c4e7c82bb62.pdf",
//                    "FILE LINK TITLE", "FILE LINK LABEL");
//            contentList.add(viewerLink);

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
                    "images/ideataiwan-M.jpg", "創意台灣 政策白皮書");
            contentList.add(image1);

            Random random = new Random();
            final int randomMax = 100;

            // test list
            List<String> items = new ArrayList<String>();
            for (int i = 0; i < 5; i++) {
                items.add("列表項目 " + random.nextInt(randomMax));
            }
            NiciList list1 = new NiciList(items);
            contentList.add(list1);
//            NiciList list2 = new NiciList(items, NiciList.ListType.NUMBER);
//            contentList.add(list2);

            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (items.size() > 0) {
//                throw new RuntimeException();
//            }

            return new NiciProject().setProjectFileUrl("http://dev.iifun.com.tw/nici/WebTools/File" +
                    "sDownload.ashx?Siteid=1&MmmID=31&fd=Messagess_Files&Pname=1.docx")
                    .setContentList(contentList);
        }

        @Override
        public int getNiciEventCount() {
            return model.size();
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

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (result.size() > 0) {
//                throw new RuntimeException();
//            }
            return result;
        }

        @Override
        public NiciEvent getNiciEventById(String eventId) {
            List<NiciContent> contentList = new ArrayList<NiciContent>();

            // heading 1
            NiciHeading heading1 = new NiciHeading("我國數位匯流發展現況與課題第一次專家座談會");
            contentList.add(heading1);

            // paragraphs for heading 1
            NiciParagraph paragraph1 = new NiciParagraph("日期：105-12-30 (三)<br>開會地點：台經院208會議室");
            NiciParagraph paragraph2 = new NiciParagraph("過去數位匯流專案辦公室由行政院科技彙報辦理，" +
                    "105年移交國家通訊傳播委員會辦理，並委託台經院執行，因此特別藉由第一次的會議，" +
                    "讓各部會、公協會及業者等，對我國數位匯流的發展的現況以及未來的課題進行交流語分享，" +
                    "亦在數位匯流發展方案2.0版於105年結束之際，提供國家通訊傳播委員會進行數位匯流發" +
                    "展方案3.0版研擬的參考。");
            contentList.add(paragraph1);
            contentList.add(paragraph2);

            // paragraphs for heading 2
            NiciParagraph paragraph3 = new NiciParagraph("為創造優質數位匯流生活，打造數位匯流產業，" +
                    "進而提升國家次世代競爭力，行政院於民國99年12月核定通過「數位匯流發展方案(2010-2015" +
                    "年)」，宣示推動我國的數位匯流產業發展環境。");
            NiciParagraph paragraph4 = new NiciParagraph("首先由台經院研究四所劉柏立所長進行20分鐘的引言，" +
                    "接著由大同大學許雲超教授、太穎國際法律事務所謝穎青律師、台北大學劉崇堅教授等，" +
                    "共5位專家，一同分享數位匯留的現況與相關應用發展的分享。最後，提供各部會, 協會, " +
                    "公會以及業者進行意見交流。");
            NiciParagraph paragraph5 = new NiciParagraph("為提昇效率、推動政府與產業合作發展數位匯流" +
                    "，數位匯流專案辦公室將參照部會執行「數位匯流發展方案（第二版）」辦理措施執行成效，" +
                    "隨時檢討推動策略，以期及早達成我國數位匯流相關指標，帶給民眾更好的數位匯流環境。");
            contentList.add(paragraph3);
            contentList.add(paragraph4);
            contentList.add(paragraph5);

            NiciImage image1 = new NiciImage("http://dev.iifun.com.tw/nici/WebTools/" +
                    "Thumbnail.ashx?SiteID=1&MmmID=40&fd=MessagessPic_Pics&Fixed=&Pname=06.jpg", null);
            NiciImage image2 = new NiciImage("http://dev.iifun.com.tw/nici/WebTools/Thumbnail.ashx?" +
                    "SiteID=1&MmmID=40&fd=MessagessPic_Pics&Fixed=&Pname=17.jpg", null);
            contentList.add(image1);
            contentList.add(image2);

            List<NiciEvent.RelatedFile> relatedFileList = new ArrayList<>();

            NiciEvent.RelatedFile file1 = new NiciEvent.RelatedFile();
            file1.fileUrl = "http://www.nici.ey.gov.tw/Upload/" +
                    "RelFile/2829/733138/73f7af44-4c6a-4a2f-8204-3c4e7c82bb62.pdf";
            file1.fileTitle = "第一次專家座談會會議記錄";
            file1.fileLabel = "會議記錄";
            relatedFileList.add(file1);

            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (contentList.size() > 0) {
//                throw new RuntimeException();
//            }

            return new NiciEvent()
                    .setId(eventId)
                    .setEventContentList(contentList)
                    .setRelatedFileList(relatedFileList);
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

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (result.size() > 0) {
//                throw new RuntimeException();
//            }
//            if (skip >= 9) {
//                throw new RuntimeException();
//            }
            return result;
        }

        @Override
        public NiciEventInfo getNiciEventInfoById(String eventInfoId) {
            List<NiciContent> contentList = new ArrayList<NiciContent>();

            // heading 1
            NiciHeading heading1 = new NiciHeading("我國數位匯流發展現況與課題第一次專家座談會");
            contentList.add(heading1);

            // paragraphs for heading 1
            NiciParagraph paragraph1 = new NiciParagraph("日期：105-12-30 (三)<br>開會地點：台經院208會議室");
            NiciParagraph paragraph2 = new NiciParagraph("103年7月行政院核定「數位匯流專案小組」併入" +
                    "NICI小組，於NICI小組下設置「數位匯流組」，由國家通訊傳播委員會擔任召集機關。");
            contentList.add(paragraph1);
            contentList.add(paragraph2);

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

            List<NiciEventInfo.RelatedFile> relatedFileList = new ArrayList<>();
            NiciEventInfo.RelatedFile file1 = new NiciEventInfo.RelatedFile();
            file1.fileUrl = "http://www.nici.ey.gov.tw/Upload/" +
                    "RelFile/2829/733138/73f7af44-4c6a-4a2f-8204-3c4e7c82bb62.pdf";
            file1.fileLabel = "會議說明〈線上觀看〉";
            file1.fileTitle = "專家說明會";
//            NiciEventInfo.RelatedFile file2 = new NiciEventInfo.RelatedFile();
//            file2.fileUrl = "http://www.nici.ey.gov.tw/Upload/" +
//                    "RelFile/2829/733138/73f7af44-4c6a-4a2f-8204-3c4e7c82bb62.pdf";
            relatedFileList.add(file1);
//            relatedFileList.add(file2);

            List<NiciEventInfo.RelatedLink> relatedLinkList = new ArrayList<>();
            NiciEventInfo.RelatedLink link1 = new NiciEventInfo.RelatedLink();
            link1.linkUrl = "http://www.nici.ey.gov.tw/";
            link1.linkLabel = "報名頁面";
            relatedLinkList.add(link1);

            // add delay for testing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (contentList.size() > 0) {
//                throw new RuntimeException();
//            }

            return new NiciEventInfo()
                    .setId(eventInfoId)
                    .setEventInfoContentList(contentList)
                    .setRelatedFileList(relatedFileList)
                    .setRelatedLinkList(relatedLinkList);
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

            // timeout test
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {}

            // failed
//            if (result.size() > 0) {
//                throw new RuntimeException();
//            }
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

        private List<NiciInfo> getTestingInfoData() {
            List<NiciInfo> infoList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                infoList.add(new NiciInfo()
                        .setTitle("NCC公布104年固網寬頻上網速率量測結果，我國固網寬頻服務速率已具穩定效能")
                        .setPublishedBy("NCC新聞稿")
                        .setDate(new Date())
                        .setDescription("Description ")
                        .setLinkUrl("http://www.ncc.gov.tw/chinese/news_detail.aspx?" +
                                "site_content_sn=8&is_history=0&pages=0&sn_f=35352"));
                infoList.add(new NiciInfo()
                        .setTitle("美將測試仿人腦電腦")
                        .setPublishedBy("聯合財經網")
                        .setDate(new Date())
                        .setDescription("Description ")
                        .setLinkUrl("http://money.udn.com/money/story/5599/" +
                                "1596579-%E7%BE%8E%E5%B0%87%E6%B8%AC%E8%A9%A6%E4%BB%BF%E4%BA%BA%E8%85%A6%E9%9B%BB%E8%85%A6"));
                infoList.add(new NiciInfo()
                        .setTitle("國會提案制定解密法規 科技主管拒配合 可刑判五年")
                        .setPublishedBy("聯合財經網")
                        .setDate(new Date())
                        .setDescription("Description ")
                        .setLinkUrl("http://money.udn.com/money/story/5602/" +
                                "1594012-%E5%9C%8B%E6%9C%83%E6%8F%90%E6%A1%88%E5%88%B6%E5%AE%9A%E8%A7%A3%E5%AF%86%E6%B3%95%E8%A6%8F-%E7%A7%91%E6%8A%80%E4%B8%BB%E7%AE%A1%E6%8B%92%E9%85%8D%E5%90%88-%E5%8F%AF%E5%88%91%E5%88%A4%E4%BA%94%E5%B9%B4"));
                infoList.add(new NiciInfo()
                        .setTitle("工研院開發3D智慧視覺感測技術，可提升機器人生產的效率！")
                        .setPublishedBy("科技新報")
                        .setDate(new Date())
                        .setDescription("Description ")
                        .setLinkUrl("http://technews.tw/2016/03/29/itri-20160328/"));
                infoList.add(new NiciInfo()
                        .setTitle("「台灣人真的很愛看電視」——中國串流影音龍頭愛奇藝在台灣正式開站")
                        .setPublishedBy("數位時代")
                        .setDate(new Date())
                        .setDescription("Description ")
                        .setLinkUrl("http://www.bnext.com.tw/article/view/id/39061"));
            }
            return infoList;
        }
    }
}
