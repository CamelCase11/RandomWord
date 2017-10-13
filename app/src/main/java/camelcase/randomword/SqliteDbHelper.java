package camelcase.randomword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class SqliteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WordList.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WordEntry.TABLE_NAME + " (" +
                    WordEntry._ID + " INTEGER PRIMARY KEY," +
                    WordEntry.COLUMN_TABLE_WORD + " TEXT," +
                    WordEntry.COLUMN_TABLE_DEFINITION + " TEXT)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME;

    private Context mContext;
    private String TAG = SqliteDbHelper.class.getSimpleName();

    public SqliteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addWordProperties(String word, String definition) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WordEntry.COLUMN_TABLE_WORD, word);
        values.put(WordEntry.COLUMN_TABLE_DEFINITION, definition);
        return db.insert(WordEntry.TABLE_NAME, null, values);
    }

    public WordProperties getWordProperties(String word) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                WordEntry._ID,
                WordEntry.COLUMN_TABLE_WORD,
                WordEntry.COLUMN_TABLE_DEFINITION,
        };

        String selection = WordEntry.COLUMN_TABLE_WORD + " = ?";
        String[] selectionArgs = {word};

        Cursor cursor = db.query(WordEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String definition = cursor.getString(
                    cursor.getColumnIndex(
                            WordEntry.COLUMN_TABLE_DEFINITION)
            );

            return new WordProperties(word, definition);
        } else {
            return null;
        }
    }

    public boolean isWordExists(String word) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                WordEntry._ID,
                WordEntry.COLUMN_TABLE_WORD,
                WordEntry.COLUMN_TABLE_DEFINITION,
        };

        String selection = WordEntry.COLUMN_TABLE_WORD + " = ?";
        String[] selectionArgs = {word};

        Cursor cursor = db.query(WordEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String definition = cursor.getString(
                    cursor.getColumnIndex(
                            WordEntry.COLUMN_TABLE_DEFINITION)
            );
            return true;

        } else {
            return false;
        }
    }

    public int deleteWord(String word) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = WordEntry.COLUMN_TABLE_WORD + " LIKE ?";
        String[] selectionArgs = {word};
        int count = db.delete(WordEntry.TABLE_NAME, selection, selectionArgs);
        return count;
    }

    public int updateDatabase(WordProperties properties) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WordEntry.COLUMN_TABLE_WORD, properties.getWord());
        values.put(WordEntry.COLUMN_TABLE_DEFINITION, properties.getWordDefinition());
        String selection = WordEntry.COLUMN_TABLE_WORD + " LIKE ?";
        String[] selectionArgs = {properties.getWord()};
        return db.update(WordEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public ArrayList<WordProperties> getAllWords() {
        ArrayList<WordProperties> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + WordEntry.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                WordProperties properties = new WordProperties();
                properties.setWord(cursor.getString(1));
                properties.setWordDefinition(cursor.getString(2));
                list.add(properties);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean isEmpty() {
        String selectQuery = "SELECT * FROM " + WordEntry.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }

    public boolean deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + WordEntry.TABLE_NAME);
        return true;
    }

    public static class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "WORD_ENTRIES";
        public static final String COLUMN_TABLE_WORD = "WORD";
        public static final String COLUMN_TABLE_DEFINITION = "DEFINITION";
    }

}
