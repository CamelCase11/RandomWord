package camelcase.randomword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity{

    private final String TAG = MainActivity.class.getSimpleName();
    private ViewPagerFragment mViewPagerFragment;
    private Utils mUtils;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS_NAME = "THEME_PREF";
    private boolean darkTheme;
    private CustomTabsIntent.Builder builder;
    private CustomTabsIntent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check for theme settings
        sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME,MODE_PRIVATE);
        darkTheme = sharedPreferences.getBoolean("DARK_THEME_SET",false);
        if (darkTheme){
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_main);

        CoordinatorLayout coordinatorLayout = findViewById(R.id.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mUtils = new Utils(this);
        if (!mUtils.isNetConnected()) {
            Snackbar.make(coordinatorLayout,"Please connect to the internet", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
        mViewPagerFragment = new ViewPagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mViewPagerFragment)
                .commit();

        builder = new CustomTabsIntent.Builder();
        intent = builder.build();
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

            case R.id.change_theme:
                editor = sharedPreferences.edit();
                if (darkTheme) {
                    editor.putBoolean("DARK_THEME_SET", false);
                } else {
                    editor.putBoolean("DARK_THEME_SET",true);
                }
                editor.apply();
                finish();
                startActivity(getIntent());
                return true;

            case R.id.donate:
                intent.launchUrl(this,Uri.parse("https://paypal.me/MohitTank"));

            default:
                return false;
        }
    }
}
