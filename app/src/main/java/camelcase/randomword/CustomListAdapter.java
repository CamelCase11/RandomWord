package camelcase.randomword;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<WordProperties> {

    private static final String TAG = CustomListAdapter.class.getSimpleName();
    private ListItemDeletedListener mListItemDeletedListener;

    public CustomListAdapter(Context context, ArrayList<WordProperties> items) {
        super(context, R.layout.custom_list_adapter, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final WordProperties singleItem = getItem(position);
        ViewHolder viewHolder = null;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.custom_list_adapter, parent, false);
            viewHolder.word = convertView.findViewById(R.id.list_word);
            viewHolder.definition = convertView.findViewById(R.id.list_definition);
            viewHolder.deleteButton = convertView.findViewById(R.id.delete_word);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Typeface tfWord = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface tfDefinition = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");

        viewHolder.word.setTypeface(tfWord);
        viewHolder.definition.setTypeface(tfDefinition);

        if (singleItem.getWord() != null) {
            viewHolder.word.setText(singleItem.getWord());
            viewHolder.definition.setText(singleItem.getWordDefinition());
        }

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListItemDeletedListener.OnItemDelete(singleItem);
            }
        });
        return convertView;
    }

    public void setOnItemDeleteListener(ListItemDeletedListener listItemDeletedListener) {
        mListItemDeletedListener = listItemDeletedListener;
    }

    public interface ListItemDeletedListener {
        void OnItemDelete(WordProperties wordProperties);
    }

    private static class ViewHolder {
        TextView word;
        TextView definition;
        ImageButton deleteButton;
    }
}
