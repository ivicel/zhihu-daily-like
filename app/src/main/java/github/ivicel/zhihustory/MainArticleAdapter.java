package github.ivicel.zhihustory;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.List;

import github.ivicel.zhihustory.db.Thumbnail;


public class MainArticleAdapter extends RecyclerView.Adapter<MainArticleAdapter.ViewHolder> {
    private final static String TAG = "MainArticleAdapter";
    private List<Article> storiesList;
    private Context mContext;
    private final static int VIEW_HEADER = 1;
    private final static int VIEW_ITEM = 2;
    private final static int VIEW_DATE = 3;
    private View mainHeaderView;
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView articleTextView;
        private ImageView articleImageView;
        private ViewPager viewPager;
    
        private View view;
        
        public ViewHolder(View itemView) {
            super(itemView);
            /* item */
            articleTextView = (TextView)itemView.findViewById(R.id.article_title);
            articleImageView = (ImageView)itemView.findViewById(R.id.article_image);
            view = itemView;
        }
    }
    
    public MainArticleAdapter(List<Article> storiesList) {
        this.storiesList = storiesList;
    }
    
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    private OnItemClickListener onItemClickListener;
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    public void addMainHeaderView(View view) {
        this.mainHeaderView = view;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        // Log.d(TAG, "onCreateViewHolder: ");
        mContext = parent.getContext();
        View view;
        if (viewType == VIEW_HEADER) {
            view = mainHeaderView;
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.article_item, parent, false);
        }
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, holder.getAdapterPosition() - 1);
                // if (viewType == VIEW_HEADER) {
                    Log.d(TAG, "onClick: click view  -- " + holder.getAdapterPosition());
                // }
            }
        });
        return holder;
    }
    
    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_HEADER : VIEW_ITEM;
    }
    
    @Override
    public int getItemCount() {
        // Log.d(TAG, "getItemCount: " + storiesList.size());
        return storiesList.size() + 1;
    }
    
    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // if (holder.viewPager != null) {
        //     holder.viewPager.setAdapter(adapter);
        // }
        // if (holder.getItemViewType() == VIEW_HEADER) {
        //     mainHeader.init(holder.view, adapter);
        //     Log.d(TAG, "onViewAttachedToWindow: " + holder.view.toString());
        // }
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            
            // mainHeader.init(holder.view, adapter);
        } else {
            // position--;
            Article article = storiesList.get(--position);
            holder.articleTextView.setText(article.getTitle());
            if (article.isRead()) {
                holder.articleTextView.setTextColor(Color.GRAY);
            } else {
                holder.articleTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tuna));
            }
            Thumbnail thumbnail = DataSupport.where("story_id = ?", article.getId())
                    .findFirst(Thumbnail.class);
            byte[] byteMap = thumbnail.getImages();
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteMap, 0, byteMap.length);
            holder.articleImageView.setImageBitmap(bitmap);
            // Glide.with(mContext).load(bitmap).into(holder.articleImageView);
            // Log.d("onBindViewHolder", article.getTitle());
            // Log.d("onBindViewHolder", article.getId()+"");
        }
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position,
            List<Object> payloads) {
        onBindViewHolder(holder, position);
        // Log.d(TAG, "onBindViewHolder: payload parameters");
    }
        
    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        // Log.d(TAG, "onViewRecycled: " + holder.view.toString());
    }
    
    
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Log.d(TAG, "onViewDetachedFromWindow: " + holder.view.toString());
    }
}
