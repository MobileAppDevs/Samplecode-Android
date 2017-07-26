package com.gofym.linkedin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.gofym.utils.YTTDebug;
import com.youtuktuk.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class LinkedInAuthActivity extends Activity {
    private View waitSpinner;
    private WebView webView;
    private GetProfileRequestAsyncTask getProfileTask;
    private PostRequestAsyncTask postResuest;
    private String accessToken;

    /**
     * Method that generates the url for get the access token from the Service
     *
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken) {
        String URL = LinkedinConstants.ACCESS_TOKEN_URL + LinkedinConstants.QUESTION_MARK
                + LinkedinConstants.GRANT_TYPE_PARAM + LinkedinConstants.EQUALS + LinkedinConstants.GRANT_TYPE
                + LinkedinConstants.AMPERSAND + LinkedinConstants.RESPONSE_TYPE_VALUE + LinkedinConstants.EQUALS
                + authorizationToken + LinkedinConstants.AMPERSAND + LinkedinConstants.CLIENT_ID_PARAM
                + LinkedinConstants.EQUALS + LinkedinConstants.API_KEY + LinkedinConstants.AMPERSAND
                + LinkedinConstants.REDIRECT_URI_PARAM + LinkedinConstants.EQUALS + LinkedinConstants.REDIRECT_URI
                + LinkedinConstants.AMPERSAND + LinkedinConstants.SECRET_KEY_PARAM + LinkedinConstants.EQUALS
                + LinkedinConstants.SECRET_KEY;
        Log.i("accessToken URL", "" + URL);
        return URL;
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    private static String getAuthorizationUrl() {
        String URL = LinkedinConstants.AUTHORIZATION_URL + LinkedinConstants.QUESTION_MARK
                + LinkedinConstants.RESPONSE_TYPE_PARAM + LinkedinConstants.EQUALS
                + LinkedinConstants.RESPONSE_TYPE_VALUE + LinkedinConstants.AMPERSAND
                + LinkedinConstants.CLIENT_ID_PARAM + LinkedinConstants.EQUALS + LinkedinConstants.API_KEY
                + LinkedinConstants.AMPERSAND + LinkedinConstants.SCOPE_PARAM + LinkedinConstants.EQUALS
                + LinkedinConstants.SCOPES + LinkedinConstants.AMPERSAND + LinkedinConstants.STATE_PARAM
                + LinkedinConstants.EQUALS + LinkedinConstants.STATE + LinkedinConstants.AMPERSAND
                + LinkedinConstants.REDIRECT_URI_PARAM + LinkedinConstants.EQUALS + LinkedinConstants.REDIRECT_URI;
        Log.i("authorization URL", "" + URL);
        return URL;
    }

    private static final String getProfileUrl(String accessToken) {
        return LinkedinConstants.PROFILE_URL + LinkedinConstants.QUESTION_MARK
                + LinkedinConstants.OAUTH_ACCESS_TOKEN_PARAM + LinkedinConstants.EQUALS + accessToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_auth_screen);

        webView = (WebView) findViewById(R.id.webView);
        waitSpinner = findViewById(R.id.notification_spinner_linkedin);
        waitSpinner.setVisibility(View.VISIBLE);
        webView.requestFocus(View.FOCUS_DOWN);
        // Set a custom web view client
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // This method will be executed each time a page
                // finished loading.
                // The only we do is dismiss the progressDialog, in case
                // we are showing any.

                waitSpinner.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                // This method will be called when the Auth proccess
                // redirect to our RedirectUri.
                // We will check the url looking for our RedirectUri.
                if (authorizationUrl.startsWith(LinkedinConstants.REDIRECT_URI)) {
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    // We take from the url the authorizationToken
                    // and the state token. We have to check that
                    // the state token returned by the Service is
                    // the same we sent.
                    // If not, that means the request may be a
                    // result of CSRF and must be rejected.
                    String stateToken = uri.getQueryParameter(LinkedinConstants.STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(LinkedinConstants.STATE)) {
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    // If the user doesn't allow authorization to
                    // our application, the authorizationToken Will
                    // be null.
                    String authorizationToken = uri.getQueryParameter(LinkedinConstants.RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        setResult(RESULT_CANCELED);
                        finish();
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);
                    waitSpinner.setVisibility(View.INVISIBLE);

                    // Generate URL for requesting Access Token
                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                    YTTDebug.isDebugAble("accessTokenUrl" + accessTokenUrl);
                    // We make the request in a AsyncTask
                    postResuest = new PostRequestAsyncTask();
                    postResuest.execute(accessTokenUrl);

                } else {
                    // Default behaviour
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        // Get the authorization Url
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        // Load the authorization URL into the webView
        webView.loadUrl(authUrl);
    }

    ;

    @Override
    protected void onDestroy() {
        if (getProfileTask != null) {
            getProfileTask.cancel(true);
        }
        if (postResuest != null) {
            postResuest.cancel(true);
        }
        super.onDestroy();
    }

    private void fetchdataFromLinkedin() {
        SharedPreferences preferences = this.getSharedPreferences(LinkedinConstants.LINKEDIN_USER_INFO, 0);
        String accessToken = preferences.getString("accessToken", null);
        if (accessToken != null) {
            String profileUrl = getProfileUrl(accessToken);
            getProfileTask = new GetProfileRequestAsyncTask();
            getProfileTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, profileUrl);
        }
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            if (!isCancelled()) {
            }
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (!isCancelled()) {
                if (urls.length > 0) {
                    String url = urls[0];
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpost = new HttpPost(url);
                    try {
                        HttpResponse response = httpClient.execute(httpost);
                        if (response != null) {
                            // If status is OK 200
                            if (response.getStatusLine().getStatusCode() == 200) {
                                String result = EntityUtils.toString(response.getEntity());
                                // Convert the
                                // string result
                                // to a JSON Object
                                JSONObject resultJson = new JSONObject(result);

                                // Extract data from
                                // JSON
                                // Response
                                int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

                                accessToken = resultJson.has("access_token") ? resultJson
                                        .getString("access_token") : null;
                                Log.e("Token", "" + accessToken);
                                if (expiresIn > 0 && accessToken != null) {
                                    Log.i("Authorize", "This is the access Token: " + accessToken
                                            + ". It will expires in " + expiresIn + " secs");

                                    // Calculate
                                    // date of
                                    // expiration
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.add(Calendar.SECOND, expiresIn);
                                    long expireDate = calendar.getTimeInMillis();

                                    // //Store
                                    // both
                                    // expires
                                    // in and
                                    // access
                                    // token in
                                    // shared
                                    // preferences
                                    SharedPreferences preferences = LinkedInAuthActivity.this.getSharedPreferences(
                                            LinkedinConstants.LINKEDIN_USER_INFO, 0);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("expires", expireDate);
                                    editor.putString("accessToken", accessToken);
                                    editor.commit();

                                    return true;
                                }
                            }
                        }
                    } catch (IOException e) {
                        Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                    } catch (ParseException e) {
                        Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                    } catch (JSONException e) {
                        Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (!isCancelled()) {
                if (status) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(LinkedinConstants.linkedInData, accessToken);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(LinkedInAuthActivity.this, "unable to fetch data from linkeding please try again..",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, String> {

        String result = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(LinkedinConstants.LINKEDIN_UPDATE_DATA_COUNT);

        @Override
        protected void onPreExecute() {
            waitSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            if (!isCancelled()) {
                if (urls.length > 0) {
                    String url = urls[0];
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(url);
                    httpget.setHeader("x-li-format", "json");
                    try {
                        HttpResponse response = httpClient.execute(httpget);
                        if (response != null) {
                            // If status is OK 200
                            if (response.getStatusLine().getStatusCode() == 200) {
                                String result = EntityUtils.toString(response.getEntity());
                                // Convert the
                                // string result
                                // to a JSON Object
                                return result;
                            }
                        }
                    } catch (IOException e) {
                        Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                    } catch (Exception e) {
                        Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String linkedInData) {
            waitSpinner.setVisibility(View.GONE);
            if (linkedInData != null) {
                YTTDebug.isDebugAble("@@@ linkedInData" + linkedInData);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(LinkedinConstants.linkedInData, linkedInData);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }

    }

}
