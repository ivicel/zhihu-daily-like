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
import java.util.ArrayList;
import java.util.List;

import github.ivicel.zhihustory.db.DatabaseUtil;
import github.ivicel.zhihustory.db.Image;
import github.ivicel.zhihustory.db.MainStories;
import github.ivicel.zhihustory.db.StoryDetails;
import github.ivicel.zhihustory.db.Thumbnail;
import github.ivicel.zhihustory.db.TopStories;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.NetworkUtil;
import github.ivicel.zhihustory.http.ResponseParser;
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
    private final static int TYPE_SUCCESS = 2;
    
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mRefreshLayout;
    // private SharedPreferences pref;
    private LastestStoryJson lastestStoryJson;
    private List<Article> storiesList;
    private List<Article> topStoriesList;
    private MainArticleAdapter recyclerViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private MainHeaderAdapter viewPagerAdapter;
    private SharedPreferences prefReadStatus;
    private MainHeader mainHeader;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void initData() {
        storiesList = new ArrayList<>();
        topStoriesList = new ArrayList<>();
    
        recyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new MainArticleAdapter(storiesList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        View mainHeaderView = getLayoutInflater().inflate(R.layout.top_pictures, recyclerView, false);
        recyclerViewAdapter.addMainHeaderView(mainHeaderView);
    
        viewPagerAdapter = new MainHeaderAdapter(topStoriesList);
        ViewPager viewPager = (ViewPager)mainHeaderView.findViewById(R.id.viewpage);
        viewPager.setAdapter(viewPagerAdapter);
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
    
        recyclerViewAdapter.setOnItemClickListener(new MainArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Article article = storiesList.get(position);
                if (!checkIsRead(article)) {
                    recyclerViewAdapter.notifyItemChanged(++position);
                }
                loadArticleDetail(article);
            }
        });
    
        viewPagerAdapter.setOnItemClickListener(new MainHeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Article article = topStoriesList.get(position);
                if (!checkIsRead(article)) {
                    for (int i = 0; i < storiesList.size(); i++) {
                        if (article.getId().compareTo(storiesList.get(i).getId()) == 0) {
                            storiesList.get(i).setRead(true);
                            recyclerViewAdapter.notifyItemChanged(i);
                        }
                    }
                }
                loadArticleDetail(article);
            }
        });
        
        if (NetworkUtil.checkNetwork(this)) {
            requestLatestStories(false);
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String date = pref.getString("date", null);
            loadDataFromDatabase(date);
        }
    }
    
    private boolean checkIsRead(Article article) {
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
                case TYPE_SUCCESS:
                    mainHeader.notifyDataChange(topStoriesList.size());
                    viewPagerAdapter.notifyDataSetChanged();
                    recyclerViewAdapter.notifyDataSetChanged();
                    break;
                case TYPE_FAIL:
                    Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_SHORT)
                        .show();
                    break;
                case TYPE_DO_NOTHING:
                    break;
            }
            return true;
        }
    });
    
    private void requestLatestStories(final boolean isRefresh) {
        HttpRequest.getLatestStories(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendFail();
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    SharedPreferences pref =
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    final String date = pref.getString("date", null);
                    final int count = pref.getInt("count", 0);
                    lastestStoryJson = ResponseParser.parseLatestStory(body.string());
                    Message msg = handler.obtainMessage(TYPE_SUCCESS);

                    if (lastestStoryJson != null ) {
                        if (date == null || date.compareTo(lastestStoryJson.date) < 0 ||
                                count < lastestStoryJson.stories.size()) {
                            /* 有更新 */
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("date", lastestStoryJson.date);
                            editor.putInt("count", lastestStoryJson.stories.size());
                            editor.apply();
                            DatabaseUtil.saveMainStories(lastestStoryJson.stories,
                                    lastestStoryJson.date);
                            DatabaseUtil.saveTopStories(lastestStoryJson.topStories,
                                    lastestStoryJson.date);
                            loadDataFromWeb(lastestStoryJson);
                            msg.what = TYPE_SUCCESS;
                        } else if (isRefresh) {
                            Log.d(TAG, "onResponse: refresh but no update");
                            msg.what = TYPE_DO_NOTHING;
                        } else {
                            Log.d(TAG, "onResponse: load from web not refresh");
                            loadDataFromWeb(lastestStoryJson);
                        }
                    } else if (!isRefresh) {
                        loadDataFromDatabase(date);
                    }
                    msg.sendToTarget();
                } else {
                    sendFail();
                }
            }
        });
    }
    
    private void sendFail() {
        Message msg = handler.obtainMessage(TYPE_FAIL);
        msg.sendToTarget();
    }
    
    private void deleteTopStories() {
        List<TopStories> topStories = DataSupport.findAll(TopStories.class);
        for (TopStories story : topStories) {
            DataSupport.deleteAll(Thumbnail.class, "story_id = ?", story.getStory_id());
            DataSupport.deleteAll(Image.class, "story_id = ?", story.getStory_id());
            DataSupport.deleteAll(StoryDetails.class, "story_id = ?", story.getStory_id());
        }
        DataSupport.deleteAll(TopStories.class);
    }

    
    private void loadDataFromWeb(LastestStoryJson lastestStoryJson) {
        for (StoryJson storyJson : lastestStoryJson.stories) {
            Article article = new Article();
            article.setTitle(storyJson.storyTitle);
            article.setId(storyJson.storyId);
            article.setDate(lastestStoryJson.date);
            article.setRead(prefReadStatus.getBoolean(storyJson.storyId, false));
            storiesList.add(article);
        }
        for (StoryJson storyJson : lastestStoryJson.topStories) {
            Article article = new Article();
            article.setTitle(storyJson.storyTitle);
            article.setId(storyJson.storyId);
            article.setDate(lastestStoryJson.date);
            topStoriesList.add(article);
        }
    }
    
    private void loadDataFromDatabase(String date) {
        
        if (date != null) {
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
            // Message msg = handler.obtainMessage(LOAD_STORY_OK);
            // msg.sendToTarget();
            //     }
            // }).start();
        }
    }
    
    private void loadMoreMainStories(String date) {
        if (date != null) {
            
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
                requestLatestStories(true);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mainHeader.setTimer();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mainHeader.cancelTimer();
        Log.d(TAG, "onStop: ");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        Log.d(TAG, "onDestroy: ");
    }
}
