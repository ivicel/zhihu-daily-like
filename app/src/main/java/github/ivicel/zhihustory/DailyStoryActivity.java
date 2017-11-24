package github.ivicel.zhihustory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
import github.ivicel.zhihustory.db.Story;
import github.ivicel.zhihustory.db.TopStory;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.NetworkUtil;
import github.ivicel.zhihustory.http.ResponseParser;
import github.ivicel.zhihustory.gson.SpecifyDayStoryGson;
import github.ivicel.zhihustory.gson.LatestStoriesGson;
import github.ivicel.zhihustory.gson.StoryGson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DailyStoryActivity extends FragmentContainerActivity {
    private final static String TAG = "DailyStoryActivity";
    
    @Override
    public Fragment createFragment() {
        return DailyStoryFragment.newInstance();
    }
}
