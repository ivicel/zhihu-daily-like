package github.ivicel.zhihustory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class StoryDetailsActivity extends FragmentContainerActivity {
    private static final String TAG = "StoryDetailsActivity";
    private static final String ARTICLE_ID = "article_id";
    
    private String mArticleId;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mArticleId = getIntent().getStringExtra(ARTICLE_ID);
                
        super.onCreate(savedInstanceState);
        
    }
    
    @Override
    public Fragment createFragment() {
        return StoryDetailsFragment.newInstance(mArticleId);
    }
    
    public static Intent newIntent(Context context, String articleId) {
        Intent intent = new Intent(context, StoryDetailsActivity.class);
        intent.putExtra(ARTICLE_ID, articleId);
        
        return intent;
    }
}
