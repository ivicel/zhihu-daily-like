package github.ivicel.zhihustory;

/**
 * Created by Ivicel on 13/10/2017.
 */

public class ArticleDetail {
    private String mImageSource;
    private String mShareUrl;
    private String mContentUri;
    private String mArticleId;
    private String mTitle;
    
    public String getTitle() {
        return mTitle;
    }
    
    public void setTitle(String title) {
        mTitle = title;
    }
    
    public String getArticleId() {
        return mArticleId;
    }
    
    public void setArticleId(String articleId) {
        mArticleId = articleId;
    }
    
    public String getImageSource() {
        return mImageSource;
    }
    
    public void setImageSource(String imageSource) {
        mImageSource = imageSource;
    }
    
    public String getShareUrl() {
        return mShareUrl;
    }
    
    public void setShareUrl(String shareUrl) {
        mShareUrl = shareUrl;
    }
    
    public String getContentUri() {
        return mContentUri;
    }
    
    public void setContentUri(String contentUri) {
        mContentUri = contentUri;
    }
    
}
