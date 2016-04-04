package tw.gov.ey.nici.network;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tw.gov.ey.nici.models.NiciInfo;

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

        private List<NiciInfo> model = new ArrayList<NiciInfo>();

        {
            for (int i = 0; i < FAKE_DATA_COUNT; i++) {
                model.add(new NiciInfo()
                    .setTitle("Title " + i)
                    .setLocation("Location " + i)
                    .setDate(new Date())
                    .setDescription("Description " + i)
                    .setLinkUrl("https://www.google.com?testId=" + i));
            }
        }

        @Override
        public void clearCache() {
            // do nothing
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
            return result;
        }
    }
}
