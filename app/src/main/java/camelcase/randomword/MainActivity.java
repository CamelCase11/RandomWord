package camelcase.randomword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity{

    private final String TAG = MainActivity.class.getSimpleName();
    private ViewPagerFragment mViewPagerFragment;
    private Utils mUtils;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS_NAME = "THEME_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main);
        mUtils = new Utils(this);

        if (!mUtils.isNetConnected()) {
            Snackbar.make(coordinatorLayout,"Please connect to the internet", BaseTransientBottomBar.LENGTH_SHORT).show();
        }

        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME,MODE_PRIVATE);
        if (sharedPreferences.getBoolean("DARK_THEME_SET",false)){
            getTheme().applyStyle(R.style.DarkTheme,true);
        } else {
            setTheme(R.style.AppTheme);
        }

        mViewPagerFragment = new ViewPagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mViewPagerFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_words:
                mViewPagerFragment.requestClearFavoritesList();
                return true;

            case R.id.rate_and_review:
                String appPackageName = "camelcase.randomword";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;

            case R.id.dark_theme:
                editor = sharedPreferences.edit();
                editor.putBoolean("DARK_THEME_SET",true);
                editor.apply();
                finish();
                startActivity(getIntent());
                return true;

            case R.id.light_theme:
                editor = sharedPreferences.edit();
                editor.putBoolean("DARK_THEME_SET",false);
                editor.apply();
                finish();
                startActivity(getIntent());
                return true;

            default:
                return false;
        }
    }
}
