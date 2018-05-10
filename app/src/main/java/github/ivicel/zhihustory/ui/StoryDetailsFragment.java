package github.ivicel.zhihustory.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.IOException;

import github.ivicel.zhihustory.model.ArticleDetail;
import github.ivicel.zhihustory.R;
import github.ivicel.zhihustory.db.DatabaseUtil;
import github.ivicel.zhihustory.gson.StoryDetailGson;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.ResponseParser;

import static github.ivicel.zhihustory.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 11/10/2017.
 */

public class StoryDetailsFragment extends Fragment {
    private static final String TAG = "StoryDetailsFragment";
    private static final String ARTICLE_ID = "article_id";
    
    private WebView mWebView;
    private String mArticleId;
    private ArticleDetail mArticleDetail;
    private Toolbar mToolbar;
    
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        mArticleId = getArguments().getString(ARTICLE_ID);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragemnt_story_details, container, false);
    
        mWebView = v.findViewById(R.id.answer_webview);
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        mToolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    
        return v;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        /* try to load story detail from local first */
        mArticleDetail = DatabaseUtil.queryStoryDetail(mArticleId);
        if (mArticleDetail == null) {
            // mArticleDetail = new ArticleDetail();
            new FetchStoryDetail().execute();
        } else {
            mWebView.loadData(mArticleDetail.getContentUri(), "text/html", "utf-8");
        }
    }
    
    public static StoryDetailsFragment newInstance(String articleId) {
        Bundle args = new Bundle();
        args.putString(ARTICLE_ID, articleId);
        StoryDetailsFragment fragment = new StoryDetailsFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    public void fetchStoryDetailFromWeb() {
        try {
            String response = HttpRequest.fetchSpecifyStory(mArticleId);
            StoryDetailGson storyGson = ResponseParser.parseSpecifyStoryDetail(response);
            if (storyGson != null) {
                mArticleDetail = new ArticleDetail();
                mArticleDetail.setShareUrl(storyGson.shareUrl);
                mArticleDetail.setImageSource(storyGson.storyImageSource);
                mArticleDetail.setArticleId(storyGson.storyId);
                mArticleDetail.setTitle(storyGson.storyTitle);
                /* should recode this */
                mArticleDetail.setContentUri(storyGson.buildStoryContent());
            }
        } catch (IOException ioe) {
            if (DEBUG) {
                Log.e(TAG, "load story detail error", ioe);
            }
        }
    }
    
    private class FetchStoryDetail extends AsyncTask<Void, Void, Void> {
    
        @Override
        protected Void doInBackground(Void... params) {
            fetchStoryDetailFromWeb();
            
            return null;
        }
    
        @Override
        protected void onPostExecute(Void aVoid) {
            if (mArticleDetail != null) {
                // mToolbar.setTitle(mArticleDetail.getTitle());
                // mWebView.loadData(mArticleDetail.getContentUri(), "text/html", "utf-8");
                mWebView.loadDataWithBaseURL("http", mArticleDetail.getContentUri(),
                        "text/html", "utf-8", null);
            }
        }
    }
}
