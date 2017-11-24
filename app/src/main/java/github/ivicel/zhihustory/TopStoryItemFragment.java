package github.ivicel.zhihustory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ivicel on 11/10/2017.
 */

public class TopStoryItemFragment extends Fragment implements
        View.OnClickListener {
    private static final String TAG = "TopStoryItemFragment";
    private static final String ARTICLE_POSITION = "article_position";
    
    private Article mArticle;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        int position = getArguments().getInt(ARTICLE_POSITION);
        mArticle = StoryController.getInstance(getContext()).getTopStory(position);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_story_list_item, container, false);
    
        ImageView topImageView = view.findViewById(R.id.top_story_image);
        TextView topTextView = view.findViewById(R.id.top_story_title);
        topTextView.setText(mArticle.getTitle());
        topImageView.setImageBitmap(mArticle.getTopStoryImage());
        
        view.setOnClickListener(this);
        
        return view;
    }
    
    
    public static TopStoryItemFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARTICLE_POSITION, position);
        TopStoryItemFragment fragment = new TopStoryItemFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    private OnItemClickListener onItemClickListener;
    
    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(v, mArticle);
    }
    
    public interface OnItemClickListener {
        void onItemClick(View view, Article article);
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
}
