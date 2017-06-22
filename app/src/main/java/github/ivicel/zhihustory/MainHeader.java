package github.ivicel.zhihustory;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sedny on 16/06/2017.
 */

public class MainHeader {
    private final static String TAG = "MainHeader";
    private final static int UPDATE_VIEWPAGER_ITME = 1;
    private int listSize;
    private boolean autoPlay = true;
    private int currentPosition = 0;
    private Timer timer;
    
    private ViewPager viewPager;
    private LinearLayout indicatorsLayout;
    
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        
        }
    
        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < listSize; i++) {
                getImageView(i).setImageResource(R.drawable.indicator_white);
            }
            getImageView(position).setImageResource(R.drawable.indicator_red);
            currentPosition = position;
        }
    
        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    autoPlay = false;
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    autoPlay = true;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    autoPlay = false;
                    break;
            }
        }
    };
    
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == UPDATE_VIEWPAGER_ITME) {
                viewPager.setCurrentItem(msg.arg1);
            }
            return false;
        }
    });
    
    public MainHeader(View view, ViewPager viewPager, int listSize) {
        this.viewPager = viewPager;
        
        indicatorsLayout = (LinearLayout)view.findViewById(R.id.indicators);
        viewPager.addOnPageChangeListener(listener);
        notifyDataChange(listSize);
    }
    
    private ImageView getImageView(int position) {
        return (ImageView)indicatorsLayout.getChildAt(position);
    }
    
    public void notifyDataChange(int listSize) {
        this.listSize = listSize;
        if (listSize > 0) {
            setIndicators();
            cancelTimer();
            setTimer();
        }
    }
    
    
    private void setIndicators() {
        if (listSize > indicatorsLayout.getChildCount()) {
            int size = listSize - indicatorsLayout.getChildCount();
            for (int i = 0; i < size; i++) {
                ImageView imageView = new ImageView(indicatorsLayout.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                params.gravity = Gravity.CENTER;
                imageView.setImageResource(R.drawable.indicator_white);
                imageView.setLayoutParams(params);
                indicatorsLayout.addView(imageView);
            }
        } else if (listSize < indicatorsLayout.getChildCount()){
            int size = indicatorsLayout.getChildCount() - listSize;
            indicatorsLayout.removeViews(listSize, size);
            currentPosition = currentPosition > listSize ? 0 : currentPosition;
        }
        getImageView(currentPosition).setImageResource(R.drawable.indicator_red);
    }
    
    public void setTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage(UPDATE_VIEWPAGER_ITME);
                if (autoPlay) {
                    if (currentPosition == listSize - 1) {
                        currentPosition = 0;
                    } else {
                        currentPosition += 1;
                    }
                    msg.arg1 = currentPosition;
                    msg.sendToTarget();
                }
            }
        }, 5000, 5000);
    }
    
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
