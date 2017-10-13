package camelcase.randomword;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class WordFragment extends Fragment {

    private static final String randomWordUrl = "https://randomword.com/";
    private static final String TAG = MainActivity.class.getSimpleName();
    OnFavoriteSateChange onFavoriteSateChange;
    private Utils mUtils;
    private WordProperties mWordProperties;
    private SwipeRefreshLayout mRefreshLayout;
    private TextView mWord;
    private TextView mWordDefinition;
    private FloatingActionButton mAddToFav;
    private Context mContext;
    private SqliteDbHelper dbHelper;
    private static final int REMOVE_FAV_ICON_ID = R.drawable.ic_remove_favorite;
    private static final int ADD_FAV_ICON_ID = R.drawable.ic_add_favorite;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;

    // add or remove the word from favoirites.
    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            WordProperties props = dbHelper.getWordProperties(mWordProperties.getWord());
            if (props != null) {
                dbHelper.deleteWord(mWordProperties.getWord());
                mAddToFav.setImageResource(REMOVE_FAV_ICON_ID);
                Snackbar.make(coordinatorLayout, "Word is removed from favorties", Snackbar.LENGTH_SHORT).show();
            } else {
                dbHelper.addWordProperties(
                        mWordProperties.getWord(),
                        mWordProperties.getWordDefinition());
                mAddToFav.setImageResource(ADD_FAV_ICON_ID);
                Snackbar.make(coordinatorLayout, "Word is added to favorties", Snackbar.LENGTH_SHORT).show();
            }
            if (onFavoriteSateChange != null) onFavoriteSateChange.favoriteStateChanged();
        }
    };

    // Swipe down to refresh listener
    private SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new GetWordPropertiesTask().execute();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.word_fragment,container,false);
        coordinatorLayout = v.findViewById(R.id.wordfrag_coordinatorlayout);
        mWord = v.findViewById(R.id.word);
        mWordDefinition = v.findViewById(R.id.definition);
        mRefreshLayout = v.findViewById(R.id.swipeRefresh);
        mAddToFav =  v.findViewById(R.id.addToFav);

        dbHelper = new SqliteDbHelper(getActivity());
        mUtils = new Utils(getActivity());
        mWordProperties = new WordProperties("Hello", "used as a greeting or to begin a telephone conversation.");

        snackbar = Snackbar.make(coordinatorLayout, "Swipe down to get new words.", Snackbar.LENGTH_SHORT);
        snackbar.show();

        if (dbHelper.isWordExists(mWordProperties.getWord())) {
            mAddToFav.setImageResource(ADD_FAV_ICON_ID);
        } else {
            mAddToFav.setImageResource(REMOVE_FAV_ICON_ID);
        }

        initView();
        if (!mUtils.isNetConnected()) {
            Toast.makeText(getContext(), "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public interface OnFavoriteSateChange {
        void favoriteStateChanged();
    }

    // Gets random word from url
    private class GetWordPropertiesTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRefreshLayout.setRefreshing(false);
            mWord.setText(mWordProperties.getWord());
            mWordDefinition.setText(mWordProperties.getWordDefinition());
            if (dbHelper.isWordExists(mWordProperties.getWord())) {
                mAddToFav.setImageResource(ADD_FAV_ICON_ID);
            } else {
                mAddToFav.setImageResource(REMOVE_FAV_ICON_ID);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            InputStream is = mUtils.openUrl(randomWordUrl);
            if (is != null){
                mWordProperties = mUtils.getWordProperties(is);
            }
            return null;
        }
    }

    public void initView() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        mWord.setTypeface(typeface);
        mWordDefinition.setTypeface(typeface);

        mWord.setText(mWordProperties.getWord());
        mWordDefinition.setText(mWordProperties.getWordDefinition());

        mRefreshLayout.setOnRefreshListener(swipeListener);

        mAddToFav.setOnClickListener(fabListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHelper.isWordExists(mWordProperties.getWord())) {
            mAddToFav.setImageResource(ADD_FAV_ICON_ID);
        } else {
            mAddToFav.setImageResource(REMOVE_FAV_ICON_ID);
        }
        initView();
    }
}
