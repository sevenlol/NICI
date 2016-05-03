package tw.gov.ey.nici.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import okhttp3.ResponseBody;
import retrofit2.Response;
import tw.gov.ey.nici.network.NiciRequestFailedException;

public class NiciResponse {
    private boolean isSuccess = false;
    private String message = null;
    private JsonElement data = null;

    public NiciResponse() {}

    public boolean getIsSuccess() {return isSuccess; }
    public String getMessage() { return message; }
    public JsonElement getData() { return data; }

    public NiciResponse setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess; return this;
    }

    public NiciResponse setMessage(String message) {
        this.message = message; return this;
    }

    public NiciResponse setData(JsonElement data) {
        this.data = data; return this;
    }

    public static class Parser {
        private static final String JSON_KEY_IS_SUCCESS = "IsSuccess";
        private static final String JSON_KEY_MESSAGE = "Message";
        private static final String JSON_KEY_DATA = "Data";

        public static NiciResponse parse(Response<ResponseBody> response) {
            if (response == null) {
                throw new IllegalArgumentException();
            }

            ResponseBody body = null;
            if (response.isSuccessful()) {
                body = response.body();
            } else {
                // TODO request failed, add log for status
                throw new NiciRequestFailedException();
            }
            if (body == null) {
                throw new IllegalArgumentException();
            }

            boolean isSuccess = false;
            String message = null;
            JsonElement data = null;
            try {
                Gson gson = new Gson();
                JsonObject jsonBody = gson.fromJson(body.string(), JsonObject.class);

                JsonElement isSuccessElement = jsonBody.get(JSON_KEY_IS_SUCCESS);
                if (isSuccessElement != null) {
                    isSuccess = isSuccessElement.getAsBoolean();
                }

                // allow message field to have the wrong type
                JsonElement messageElement = jsonBody.get(JSON_KEY_MESSAGE);
                if (messageElement != null && messageElement instanceof JsonPrimitive) {
                    message = messageElement.getAsString();
                }

                data = jsonBody.get(JSON_KEY_DATA);
            } catch (Exception e) {
                // TODO parse error, invalid body, add log
                // FIXME change this to android log
                System.out.println("INVALID BODY: " + body.toString());
                throw new IllegalArgumentException();
            }

            // when the request succeeded, data should not be null
            if (isSuccess && data == null) {
                throw new IllegalArgumentException();
            }

            return new NiciResponse()
                    .setIsSuccess(isSuccess)
                    .setMessage(message)
                    .setData(data);
        }
    }
}
