package github.ivicel.zhihustory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import github.ivicel.zhihustory.db.DatabaseUtil;
import github.ivicel.zhihustory.db.Image;
import github.ivicel.zhihustory.db.MainStories;
import github.ivicel.zhihustory.db.StoryDetails;
import github.ivicel.zhihustory.db.Thumbnail;
import github.ivicel.zhihustory.db.TopStories;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.NetworkUtil;
import github.ivicel.zhihustory.http.ResponseParser;
import github.ivicel.zhihustory.responsejson.DayStoryJson;
import github.ivicel.zhihustory.responsejson.LastestStoryJson;
import github.ivicel.zhihustory.responsejson.StoryJson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private final static int TYPE_FAIL = 0;
    private final static int TYPE_DO_NOTHING = 1;
    private final static int TYPE_LOAD_LATEST_SUCCESS = 2;
    private final static int TYPE_LOAD_MORE_STORY = 3;
    
    private List<Article> storiesList;
    private List<Article> topStoriesList;
    private int mCurrentPos;
    private String mCurrentDate;
    private boolean isRefreshing = false;
    private SharedPreferences prefReadStatus;
    
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mRefreshLayout;
    private SharedPreferences mPref;
    private LastestStoryJson lastestStoryJson;
    
    private MainArticleAdapter mRecyclerViewAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private MainHeaderAdapter mViewPagerAdapter;
    
    private MainHeader mainHeader;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    private void initData() {
        storiesList = new ArrayList<>();
        topStoriesList = new ArrayList<>();
        
        mRecyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewAdapter = new MainArticleAdapter(storiesList);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        View mainHeaderView =
                getLayoutInflater().inflate(R.layout.top_pictures, mRecyclerView, false);
        mRecyclerViewAdapter.addMainHeaderView(mainHeaderView);
        
        mViewPagerAdapter = new MainHeaderAdapter(topStoriesList);
        ViewPager viewPager = (ViewPager)mainHeaderView.findViewById(R.id.viewpage);
        viewPager.setAdapter(mViewPagerAdapter);
        mainHeader = new MainHeader(mainHeaderView, viewPager, topStoriesList.size());
        prefReadStatus = getSharedPreferences("article_read", MODE_PRIVATE);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setToolBar();
        setDrawerAndRefresh();
        initData();
        
        mRecyclerViewAdapter.setOnItemClickListener(new MainArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Article article = storiesList.get(position);
                if (!isArticleRead(article)) {
                    mRecyclerViewAdapter.notifyItemChanged(++position);
                }
                loadArticleDetail(article);
            }
        });
        
        mViewPagerAdapter.setOnItemClickListener(new MainHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Article article = topStoriesList.get(position);
                if (!isArticleRead(article)) {
                    for (int i = 0; i < storiesList.size(); i++) {
                        if (article.getId().compareTo(storiesList.get(i).getId()) == 0) {
                            storiesList.get(i).setRead(true);
                            mRecyclerViewAdapter.notifyItemChanged(i);
                        }
                    }
                }
                loadArticleDetail(article);
            }
        });
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentDate = mPref.getString("date", null);
        mRefreshLayout.setRefreshing(true);
        if (NetworkUtil.isNetworkAvailabled(this)) {
            requestLatestStories(true);
        } else {
            if (mCurrentDate != null) {
                loadDataFromDatabase(mCurrentDate);
            }
            mRefreshLayout.setRefreshing(false);
        }
    }
    
    private boolean isArticleRead(Article article) {
        boolean status = prefReadStatus.getBoolean(article.getId(), false);
        if (!status) {
            SharedPreferences.Editor editor = prefReadStatus.edit();
            editor.putBoolean(article.getId(), true);
            editor.apply();
            article.setRead(true);
        }
        return status;
    }
    
    private void loadArticleDetail(Article article) {
        Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
        intent.putExtra("story_id", article.getId());
        intent.putExtra("story_date", article.getDate());
        startActivity(intent);
    }
    
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mRefreshLayout.setRefreshing(false);
            switch (msg.what) {
                case TYPE_LOAD_LATEST_SUCCESS:
                    mainHeader.notifyDataChange(topStoriesList.size());
                    mViewPagerAdapter.notifyDataSetChanged();
                    mRecyclerViewAdapter.notifyDataSetChanged();
                    mCurrentPos = storiesList.size();
                    mCurrentDate = storiesList.get(mCurrentPos - 1).getDate();
                    break;
                case TYPE_LOAD_MORE_STORY:
                    Log.d(TAG, "handleMessage: load more stories " + storiesList.size());
                    mRecyclerViewAdapter.notifyItemInserted(mCurrentPos + 1);
                    mCurrentPos = storiesList.size();
                    mCurrentDate = storiesList.get(mCurrentPos - 1).getDate();
                    isRefreshing = false;
                    break;
                case TYPE_DO_NOTHING:
                    break;
                case TYPE_FAIL:
                    Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT)
                            .show();
                    break;
                
            }
            return true;
        }
    });
    
    private void sendFail() {
        Message msg = handler.obtainMessage(TYPE_FAIL);
        msg.sendToTarget();
    }
    
    private void loadDataFromWeb(LastestStoryJson lastestStoryJson) {
        loadMainStoryFromWeb(lastestStoryJson.stories);
        for (StoryJson storyJson : lastestStoryJson.topStories) {
            Article article = new Article();
            article.setTitle(storyJson.storyTitle);
            article.setId(storyJson.storyId);
            article.setDate(lastestStoryJson.date);
            topStoriesList.add(article);
        }
    }
    
    private void loadMainStoryFromWeb(List<StoryJson> storyJsonList) {
        for (StoryJson storyJson : storyJsonList) {
            Article article = new Article();
            article.setTitle(storyJson.storyTitle);
            article.setId(storyJson.storyId);
            article.setDate(lastestStoryJson.date);
            article.setRead(prefReadStatus.getBoolean(storyJson.storyId, false));
            storiesList.add(article);
        }
    }
    
    private void loadDataFromDatabase(final String date) {
            // new Thread(new Runnable() {
            //     @Override
            //     public void run() {
        List<TopStories> topStories = DataSupport.findAll(TopStories.class);
        for (TopStories topStory : topStories) {
            Article article = new Article();
            article.setTitle(topStory.getTitle());
            article.setId(topStory.getStory_id());
            article.setDate(topStory.getDate());
            topStoriesList.add(article);
        }
        loadMoreMainStories(date);
        mCurrentPos = storiesList.size();
        mainHeader.notifyDataChange(topStoriesList.size());
        mRecyclerViewAdapter.notifyDataSetChanged();
        mViewPagerAdapter.notifyDataSetChanged();
                    
            //         Message msg = handler.obtainMessage(TYPE_LOAD_LATEST_SUCCESS);
            //         msg.sendToTarget();
            //     }
            // }).start();
    }
    
    private void loadMoreMainStories(String date) {
        List<MainStories> stories = DataSupport.where("date = ?", date).find(MainStories.class);
        for (MainStories story : stories) {
            Article article = new Article();
            article.setTitle(story.getTitle());
            article.setId(story.getStory_id());
            article.setDate(story.getDate());
            article.setRead(prefReadStatus.getBoolean(story.getStory_id(), false));
            storiesList.add(article);
        }
        
    }
    
    private void setToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(R.string.main_title);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
    }
    
    private void setDrawerAndRefresh() {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.main_swipe_refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshLayout.setRefreshing(true);
                if (NetworkUtil.isNetworkAvailabled(MainActivity.this)) {
                    requestLatestStories(false);
                } else {
                    sendFail();
                }
            }
        });
    }
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (storiesList.size() != 0 && newState == RecyclerView.SCROLL_STATE_IDLE &&
                    mLinearLayoutManager.findLastVisibleItemPosition() == storiesList.size() &&
                    !isRefreshing) {
                Log.d(TAG, "onScrollStateChanged: request more data from network");
                isRefreshing = true;
                if (NetworkUtil.isNetworkAvailabled(MainActivity.this)) {
                    requestMoreStories(mCurrentDate);
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentPos = storiesList.size();
                            loadMoreMainStories(getYesterday(mCurrentDate));
                            Message msg = handler.obtainMessage(TYPE_LOAD_MORE_STORY);
                            msg.sendToTarget();
                        }
                    }).start();
                    
                }
            }
        }
    };
    
    private void requestLatestStories(final boolean initial) {
        HttpRequest.getLatestStories(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendFail();
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    sendFail();
                }
                // SharedPreferences pref =
                //         PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                // final String date = pref.getString("date", null);
                
                final int count = mPref.getInt("count", 0);
                Message msg = handler.obtainMessage(TYPE_LOAD_LATEST_SUCCESS);
                lastestStoryJson = ResponseParser.parseLatestStory(body.string());
                if (lastestStoryJson != null) {
                    if (initial || mCurrentDate == null ||
                            mCurrentDate.compareTo(lastestStoryJson.date) < 0 ||
                            count < lastestStoryJson.stories.size()) {
                        /* 有更新 */
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putString("date", lastestStoryJson.date);
                        editor.putInt("count", lastestStoryJson.stories.size());
                        editor.apply();
                        
                        DatabaseUtil.saveMainStories(lastestStoryJson.stories,
                                lastestStoryJson.date);
                        DatabaseUtil.saveTopStories(lastestStoryJson.topStories,
                                lastestStoryJson.date);
                        topStoriesList.clear();
                        storiesList.clear();
                        loadDataFromWeb(lastestStoryJson);
                    
                    } else {
                        /* 当天无更新 */
                        Log.d(TAG, "onResponse: refresh but no update");
                        msg.what = TYPE_DO_NOTHING;
                    }
                }
                body.close();
                msg.sendToTarget();
            }
        });
    }
    
    private void requestMoreStories(String date) {
        HttpRequest.getStoriesBeforeDate(date, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFail();
            }
    
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                Message msg = handler.obtainMessage();
                if (body != null) {
                    DayStoryJson dayStoryJson = ResponseParser.parseDayStoryJson(body.string());
                    if (dayStoryJson != null) {
                        // load more
                        DatabaseUtil.saveMainStories(dayStoryJson.storyList, dayStoryJson.date);
                        loadMainStoryFromWeb(dayStoryJson.storyList);
                        mCurrentDate = dayStoryJson.date;
                        msg.what = TYPE_LOAD_MORE_STORY;
                    }
                    body.close();
                    msg.sendToTarget();
                }
            }
        });
    }
    
    private String getYesterday(String today) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        try {
            Integer yesterday = Integer.valueOf(today) - 1;
            return format.format(format.parse(yesterday.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected void onResume() {
        super.onResume();
        mainHeader.setTimer();
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mainHeader.cancelTimer();
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        Log.d(TAG, "onStop: ");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        Log.d(TAG, "onDestroy: ");
    }
}
