package github.ivicel.zhihustory.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.lang.ref.WeakReference;

import github.ivicel.zhihustory.model.Article;
import github.ivicel.zhihustory.R;
import github.ivicel.zhihustory.StoryController;
import github.ivicel.zhihustory.StoryListItemAdapter;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.NetworkUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static github.ivicel.zhihustory.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 11/10/2017.
 */

public class DailyStoryFragment extends Fragment {
    private static final String TAG = "DailyStoryFragment";
    private static final String CURRENT_ITEM_POSITION = "current_item_position";
    private static final int TYPE_RESPONSE_NOTHING = 1;
    private static final int TYPE_RESPONSE_NEW_LATEST = 2;
    private static final int TYPE_RESPONSE_NEW_STORY = 3;
    
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private RecyclerView mStoryRecyclerView;
    private StoryListItemAdapter mItemAdapter;
    private StoryController mStoryController;
    private SwipeRefreshLayout mRefreshLayout;
    private ResponseHandler mResponseHandler;
    private boolean mIsLoadingMoreStory = false;
    private LinearLayoutManager mItemLayoutManager;
    private RecyclerView.OnScrollListener mItemScrollerListener;
    private int mCurrentItemPosition;
    
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.navigation_items, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            mCurrentItemPosition = savedInstanceState.getInt(CURRENT_ITEM_POSITION);
        }
    
        mStoryController = StoryController.getInstance(getContext());
        mItemAdapter = new StoryListItemAdapter(mStoryController.getStories(),
                mStoryController.getTopStories(), getFragmentManager());
        mItemLayoutManager = new LinearLayoutManager(getContext());
        mResponseHandler = new ResponseHandler(getActivity());
        mItemScrollerListener = new ItemScrollListener();
        
        mResponseHandler.setMessageHandler(new ResponseHandler.ResponseHandleMessage() {
            @Override
            public void handleMessage(Message msg) {
                stopRefreshing();
                switch (msg.what) {
                    case TYPE_RESPONSE_NOTHING:
                        break;
                    case TYPE_RESPONSE_NEW_LATEST:
                        mItemAdapter.notifyNewLatestStories();
                        break;
                    case TYPE_RESPONSE_NEW_STORY:
                        mItemAdapter.notifyItemRangeInserted(msg.arg1, msg.arg2);
                        break;
                    default:
                        break;
                }
            }
        });
        mItemAdapter.setOnItemClickListener(new StoryListItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Article article, boolean isTopStory) {
                mStoryController.setArticleRead(article);
                if (isTopStory) {
                    int position = mStoryController.indexOfStoryList(article.getId());
                    if (position != -1) {
                        mItemAdapter.notifyItemChanged(++position);
                    }
                }
                
                Intent intent = StoryDetailsActivity.newIntent(getContext(), article.getId());
                startActivity(intent);
            }
        });
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_story, container, false);
        
        mToolbar = view.findViewById(R.id.story_list_toolbar);
        mStoryRecyclerView = view.findViewById(R.id.story_recycler_view);
        mRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mDrawerLayout = view.findViewById(R.id.main_drawer_layout);
    
        mStoryRecyclerView.setLayoutManager(mItemLayoutManager);
        mStoryRecyclerView.setAdapter(mItemAdapter);
        
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
        
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchLatestStory();
            }
        });

        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        mStoryRecyclerView.addOnScrollListener(mItemScrollerListener);
        
        if (mStoryController.getStories().isEmpty() ||
                mStoryController.getTopStories().isEmpty()) {
            startRefreshing();
            if (NetworkUtil.isNetworkAvailabled(getContext())) {
                fetchLatestStory();
            } else {
                loadLocalLatestStory();
            }
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mStoryRecyclerView.removeOnScrollListener(mItemScrollerListener);
    }
    
    public static DailyStoryFragment newInstance() {
        DailyStoryFragment fragment = new DailyStoryFragment();
        
        return fragment;
    }
    
    private void startRefreshing() {
        mRefreshLayout.setRefreshing(true);
    }
    
    private void stopRefreshing() {
        mIsLoadingMoreStory = false;
        mRefreshLayout.setRefreshing(false);
    }
    
    private void fetchLatestStory() {
        HttpRequest.fetchLatest(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (DEBUG) {
                    Log.e(TAG, "Can't get response from " + call.request().url(), e);
                }
                sendResponseNothing();
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    Message msg;
                    String s = body.string();
                    if (mStoryController.renewLatestStory(s)) {
                        msg = mResponseHandler.obtainMessage(TYPE_RESPONSE_NEW_LATEST);
                    } else {
                        msg = mResponseHandler.obtainMessage(TYPE_RESPONSE_NOTHING);
                    }
                    msg.sendToTarget();
                    return;
                }
                if (DEBUG) {
                    Log.e(TAG, "Get null response from " + call.request().url());
                }
                sendResponseNothing();
            }
        });
    }
    
    private void loadLocalLatestStory() {
        mStoryController.loadLocalLatestStories();
        mItemAdapter.notifyNewLatestStories();
        stopRefreshing();
    }
    
    private void loadMoreStory() {
        final int position = mStoryController.getLastItemPosition() + 1;
        if (NetworkUtil.isNetworkAvailabled(getContext())) {
            HttpRequest.fetchSpecifyDay(mStoryController.getCurrentDate(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (DEBUG) {
                        Log.e(TAG, "Load more story error from " + call.request().url(), e);
                    }
                    sendResponseNothing();
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody body = response.body();
                    if (body != null) {
                        int length = mStoryController.loadMoreStoryFromWeb(body.string());
                        Message msg = mResponseHandler.obtainMessage(TYPE_RESPONSE_NEW_STORY);
                        msg.arg1 = position;
                        msg.arg2 = length;
                        msg.sendToTarget();
                    } else {
                        sendResponseNothing();
                    }
                }
            });
        } else {
            int length = mStoryController.loadMoreStoryFromDB();
            if (length > 0) {
                mItemAdapter.notifyItemRangeInserted(position, length);
            }
            stopRefreshing();
        }
    }
    
    /*
     * @TODO this message handle may cause memory leak, need to fix
     */
    private static class ResponseHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public ResponseHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }
        
        public interface ResponseHandleMessage {
            void handleMessage(Message msg);
        }
        
        private ResponseHandleMessage mMessageHandler;
        
        public void setMessageHandler(ResponseHandleMessage handler) {
            mMessageHandler = handler;
        }
        
        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivity.get();
            if (activity != null) {
                mMessageHandler.handleMessage(msg);
            }
        }
    }
    
    private void sendResponseNothing() {
        mResponseHandler.obtainMessage(TYPE_RESPONSE_NOTHING).sendToTarget();
    }
    
    private class ItemScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mCurrentItemPosition = mItemLayoutManager.findFirstCompletelyVisibleItemPosition();
            
            if (newState == RecyclerView.SCROLL_STATE_IDLE && !mIsLoadingMoreStory &&
                    mItemLayoutManager.findLastVisibleItemPosition() ==
                            mStoryController.getLastItemPosition()) {
                mIsLoadingMoreStory = true;
                loadMoreStory();
            }
        }
    }
}
