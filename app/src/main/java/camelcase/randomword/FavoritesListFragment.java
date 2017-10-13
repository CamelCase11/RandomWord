package camelcase.randomword;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

public class FavoritesListFragment extends Fragment {

    private static final String TAG = FavoritesListFragment.class.getSimpleName();
    private ListView lv;
    private CustomListAdapter customListAdapter;
    private SqliteDbHelper dbHelper;
    private TextView tv;
    private FloatingActionButton mFab;
    private Context mContext;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.favorites_list, container, false);
        lv = v.findViewById(R.id.favorites_list_view);
        tv = v.findViewById(R.id.empty_message);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        tv.setTypeface(tf);
        mContext = getContext();
        dbHelper = new SqliteDbHelper(mContext);
        initList();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    /**
     * initialize the list
     */
    public void initList() {
        if (!dbHelper.isEmpty()) {
            tv.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            final ArrayList<WordProperties> favoriteList = dbHelper.getAllWords();

            customListAdapter = new CustomListAdapter(getActivity(), favoriteList);
            customListAdapter.notifyDataSetChanged();

            customListAdapter.sort(new Comparator<WordProperties>() {
                @Override
                public int compare(WordProperties o1, WordProperties o2) {
                    return o1.getWord().compareTo(o2.getWord());
                }
            });

            lv.setAdapter(customListAdapter);

            customListAdapter.setOnItemDeleteListener(new CustomListAdapter.ListItemDeletedListener() {
                @Override
                public void OnItemDelete(WordProperties properties) {
                    String word = properties.getWord();
                    dbHelper.deleteWord(word);
                    customListAdapter.remove(properties);
                    customListAdapter.notifyDataSetChanged();
                    if (customListAdapter.isEmpty()) {
                        tv.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            tv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }
    }

    public void deleteAllWords() {
        if (dbHelper != null && !dbHelper.isEmpty() && !customListAdapter.isEmpty()) {
            dbHelper.deleteAll();
            customListAdapter.clear();
            customListAdapter.notifyDataSetChanged();
            tv.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            Toast.makeText(mContext, "Deleted All Words", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "List is empty", Toast.LENGTH_SHORT).show();
        }
    }
}
