package github.ivicel.zhihustory;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import github.ivicel.zhihustory.model.Article;
import github.ivicel.zhihustory.ui.TopStoryItemFragment;


public class StoryListItemAdapter extends
        RecyclerView.Adapter<StoryListItemAdapter.ArticleViewHolder> {
    private final static String TAG = "StoryListItemAdapter";
    private final static int TYPE_VIEW_HEADER = 1;
    private final static int TYPE_VIEW_ITEM = 2;
    private final static int TYPE_VIEW_DATE = 3;
    
    private List<Article> mStories;
    private List<Article> mTopStories;
    private Context mContext;
    private TopStoryAdapter mTopStoryAdapter;
    
    class ArticleViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView mTitleTextView;
        private ImageView mImageView;
        private Article mArticle;
        private boolean mPagerAutoPlay = false;

        
        public ArticleViewHolder(View itemView) {
            super(itemView);
    
            mTitleTextView = itemView.findViewById(R.id.article_title);
            mImageView = itemView.findViewById(R.id.article_image);
        }
        
        public void bindArticleView(Article article) {
            mArticle = article;
            
            if (article.isRead()) {
                mTitleTextView.setTextColor(Color.GRAY);
            }
            
            mTitleTextView.setText(article.getTitle());
            mImageView.setImageBitmap(article.getStoryImage());
            
            itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(mArticle, false);
        }
    }
    
    public StoryListItemAdapter(List<Article> stories, List<Article> topStories,
            FragmentManager fm) {
        mStories = stories;
        mTopStories = topStories;
        mTopStoryAdapter = new TopStoryAdapter(fm);
    }
    
    public interface OnItemClickListener {
        void onItemClick(Article article, boolean isTopStory);
    }
    
    private OnItemClickListener onItemClickListener;
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v;
        if (viewType == TYPE_VIEW_HEADER) {
            v = inflater.inflate(R.layout.fragment_top_story_placeholder, parent, false);
            ((ViewPager)v).setAdapter(mTopStoryAdapter);
        } else {
            v = inflater.inflate(R.layout.story_list_item, parent, false);
        }

        return new ArticleViewHolder(v);
    }
    
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_VIEW_HEADER : TYPE_VIEW_ITEM;
    }
    
    @Override
    public int getItemCount() {
        return mStories.size() + 1;
    }
    
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_VIEW_ITEM) {
            Article article = mStories.get(--position);
            holder.bindArticleView(article);
        }
    }
    
    public void notifyNewLatestStories() {
        notifyDataSetChanged();
        mTopStoryAdapter.notifyDataSetChanged();
    }
    
    private class TopStoryAdapter extends FragmentStatePagerAdapter {
        
        TopStoryAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public Fragment getItem(int position) {
            TopStoryItemFragment itemFragment = TopStoryItemFragment.newInstance(position);
            itemFragment.setOnItemClickListener(new TopStoryItemFragment.OnItemClickListener() {
                @Override
                public void onItemClick(View view, Article article) {
                    StoryListItemAdapter.this.onItemClickListener.onItemClick(article, true);
                }
            });
            
            return itemFragment;
        }
        
        @Override
        public int getCount() {
            return mTopStories.size();
        }
    
    }
}
