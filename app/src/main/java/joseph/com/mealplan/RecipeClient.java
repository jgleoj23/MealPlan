package joseph.com.mealplan;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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

    public void getRecipes(final String query, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("search");
        RequestParams params = new RequestParams();
        params.put("key", "0baf87954f134397696ae1c2da1ce965");
        params.put("q", query);
        client.get(url, params, handler);
    }
}
