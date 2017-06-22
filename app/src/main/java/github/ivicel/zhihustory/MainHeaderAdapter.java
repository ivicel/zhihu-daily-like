package github.ivicel.zhihustory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import github.ivicel.zhihustory.db.Image;
import github.ivicel.zhihustory.db.Thumbnail;

/**
 * Created by sedny on 15/06/2017.
 */

public class MainHeaderAdapter extends PagerAdapter {
    private List<Article> topStoriesList;
    private Context mContext;
    private final static String TAG = "MainHeaderAdapter";
    
    public MainHeaderAdapter(List<Article> topStoriesList) {
        this.topStoriesList = topStoriesList;
        Log.d(TAG, "MainHeaderAdapter: " + topStoriesList.size());
    }
    
    private OnItemClickListener onItemClickListener;
    
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        mContext = container.getContext();
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.banner_article, container, false);
        ImageView bannerImageView = (ImageView)view.findViewById(R.id.main_banner_image);
        TextView bannerTextView = (TextView)view.findViewById(R.id.main_banner_title);
        Image image = DataSupport.where("story_id = ?", topStoriesList.get(position).getId())
                .findFirst(Image.class);
        Bitmap bitmap = BitmapFactory.decodeByteArray(image.getImage(), 0, image.getImage().length);
        bannerImageView.setImageBitmap(bitmap);
        // Glide.with(mContext).load(bitmap).into(bannerImageView);
        bannerTextView.setText(topStoriesList.get(position).getTitle());
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });
        return view;
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    
    @Override
    public int getCount() {
        return topStoriesList.size();
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
