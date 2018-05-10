package github.ivicel.zhihustory.ui;

import android.support.v4.app.Fragment;

public class DailyStoryActivity extends FragmentContainerActivity {
    private final static String TAG = "DailyStoryActivity";
    
    @Override
    public Fragment createFragment() {
        return DailyStoryFragment.newInstance();
    }
}
