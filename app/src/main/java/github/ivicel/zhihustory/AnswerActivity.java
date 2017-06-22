package github.ivicel.zhihustory;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import github.ivicel.zhihustory.db.Image;
import github.ivicel.zhihustory.db.StoryDetails;
import github.ivicel.zhihustory.http.HttpRequest;
import github.ivicel.zhihustory.http.ResponseParser;
import github.ivicel.zhihustory.responsejson.StoryContentJson;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnswerActivity extends AppCompatActivity {
    private final static String TAG = "AnswerActivity";
    private final static int LOAD_FROM_DATABASE = 1;
    private final static int LOAD_FROM_WEB = 2;
    private WebView webView;
    private StoryDetails storyDetails;
    private ImageView mImageView;
    
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_FROM_DATABASE:
                    break;
                case LOAD_FROM_WEB:
                    webView.loadDataWithBaseURL(null, storyDetails.getContent(), "text/html", "utf-8", null);
                    // byte[] byteImage = storyDetails.getImage().getImage();
                    // mImageView.setImageBitmap(BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length));
                    break;
            }
            return false;
        }
    });
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // mImageView = (ImageView)findViewById(R.id.answer_banner_image_view);
        Intent intent = getIntent();
        String storyId = intent.getStringExtra("story_id");
        String storyDate = intent.getStringExtra("story_date");
        Log.d(TAG, "story id = " + storyId);
        storyDetails = DataSupport.where("story_id = ?", storyId)
                .findFirst(StoryDetails.class, true);
        List<Image> imageList = DataSupport.where("story_id = ?", storyId).find(Image.class);
        webView = (WebView)findViewById(R.id.answer_webview);
        if (storyDetails != null) {
            webView.loadDataWithBaseURL(null, storyDetails.getContent(), "text/html", "utf-8",
                    null);
            // byte[] byteImage = storyDetails.getImage().getImage();
            // mImageView.setImageBitmap(BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length));

        } else {
            loadDataFromWeb(storyId, storyDate);
        }
    }
    
    private void loadDataFromWeb(final String storyId, final String date) {
        HttpRequest.getSpefifyStoryById(storyId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    // Matcher matcher = Pattern.matches("\\s?");
                    String bodyString = body.string().replaceAll("\r\n", "\n");
                    // File file = File.createTempFile("TEMPFILE", ".tmp", getExternalCacheDir());
                    // BufferedWriter writer =
                    //         new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    // writer.write(bodyString);
                    // writer.close();
                    storyDetails = ResponseParser.getStoryContent(bodyString, date);
                    if (storyDetails != null) {
                        Message msg = handler.obtainMessage(LOAD_FROM_WEB);
                        msg.sendToTarget();
                    }
                }

            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
            
        }
        return true;
    }
}
