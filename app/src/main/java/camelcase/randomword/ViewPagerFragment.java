package camelcase.randomword;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ViewPagerFragment extends Fragment {

    private static final int NUMPAGES = 2;
    private ViewPager mViewPager;
    private TabLayout mTablayout;
    private PagerAdapter mPagerAdapter;
    private WordFragment mWordFragment;
    private FavoritesListFragment mFavoritesListFragment;
    private String TAG = ViewPagerFragment.class.getSimpleName();
    private Fragment currentFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.viewpager_fragment, container, false);
        mViewPager = v.findViewById(R.id.view_pager);
        mTablayout = v.findViewById(R.id.tab_layout);
        mTablayout.setupWithViewPager(mViewPager);
        mFavoritesListFragment = new FavoritesListFragment();
        mWordFragment = new WordFragment();
        mPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentFragment = ((FragmentStatePagerAdapter) mViewPager.getAdapter()).getItem(position);
                if (currentFragment != null) {
                    currentFragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return v;
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private String[] mTabTitles = {"Random Word", "Favorites List"};

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mWordFragment;
                case 1:
                    return mFavoritesListFragment;
                default:
                    return mWordFragment;
            }
        }

        @Override
        public int getCount() {
            return NUMPAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

    }

    public void requestClearFavoritesList() {
        if (currentFragment != null && currentFragment instanceof FavoritesListFragment) {
            showAlert().show();
        } else {
            Toast.makeText(getContext(), "Please go to favorites list first", Toast.LENGTH_SHORT).show();
        }
    }

    public AlertDialog.Builder showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to delete all words?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((FavoritesListFragment) currentFragment).deleteAllWords();
                currentFragment.onResume();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }

}
