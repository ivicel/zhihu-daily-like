package github.ivicel.zhihustory;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import org.litepal.LitePal;

/**
 * Created by Ivicel on 11/10/2017.
 */

public abstract class FragmentContainerActivity extends AppCompatActivity {
    
    public abstract Fragment createFragment();
    
    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
    
        LitePal.getDatabase();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
