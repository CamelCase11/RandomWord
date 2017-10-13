package camelcase.randomword;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * General purpose class
 * collection of some utilities.
 * <p>
 * 1. openUrl(String) : Inputstream
 * 2. inputstreamToString(inputStream) : String
 * 3. getWordProperties(inputStream) : WordProperties // basic line splitting and storing in model
 * 4. isnetConnected() : boolean
 * 5. writeToSharedPrefs(key,value) : boolean
 * 6. readFromSharedPrefs(key) : value
 */

public class Utils {

    private final String TAG = Utils.class.getSimpleName();
    private Context mContext;
    private final static String SHARED_PREF_NAME = "appData";

    public Utils(Context context) {
        mContext = context;
    }

    public InputStream openUrl(String url){
        InputStream ip = null;
        try {
            URL myUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(8000);
            ip = connection.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "openUrl: unable to open connection", e);
        }
        return ip;
    }

    public String inputStreamToString(InputStream is){
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "inputStreamToString: error occurred while reading inputstream", e);
            sb.append("");
            return sb.toString();
        }
    }

    public WordProperties getWordProperties(InputStream is){

        String word = "";
        String wordDefinition = "";
        String line;
        String regex = "<.+?>";
        if (is != null) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null){
                    if (line.contains("random_word") && !line.contains("definition") && !line.contains("next")){
                        word = line.replaceAll(regex,"").trim();
                    }

                    if (line.contains("random_word") && line.contains("definition")) {
                        wordDefinition = line.replaceAll(regex, "").trim();
                    }
                }
            return new WordProperties(word,wordDefinition);

            } catch (IOException e) {
                return new WordProperties("null","null");
            }

        } else{
            Toast.makeText(mContext, "input stream empty", Toast.LENGTH_SHORT).show();
            return new WordProperties("null","null");
        }
    }

    public boolean isNetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public boolean writeToSharedPrefs(String key, String value) {
        if (mContext != null) {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        } else {
            return false;
        }
    }

    public boolean readFromSharedPrefs(String key) {
        if (mContext != null) {
            SharedPreferences preferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return preferences.getString(key, "").equals("ff4df7709dede26b811da383161d4729999d2ac272f51a632b862ba8fa3d1522");
        } else {
            return false;
        }
    }

}
