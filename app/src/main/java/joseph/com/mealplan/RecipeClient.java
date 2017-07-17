package joseph.com.mealplan;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by kcguo on 7/17/17.
 */

public class RecipeClient {
    private static final String API_BASE_URL = "http://food2fork.com/api/";
    private AsyncHttpClient client;

    public RecipeClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void getRecipes(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("search");
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
