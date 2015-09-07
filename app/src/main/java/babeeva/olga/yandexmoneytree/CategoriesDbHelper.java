package babeeva.olga.yandexmoneytree;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import babeeva.olga.yandexmoneytree.CategoriesContract.CategoryEntry;
public class CategoriesDbHelper extends SQLiteOpenHelper {

    private final Context myContext;
    private static CategoriesDbHelper sInstance;

    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "categories_2.db";

    public static synchronized CategoriesDbHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new CategoriesDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private CategoriesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +

                CategoryEntry.COLUMN_KEY + " INTEGER PRIMARY KEY, "  +
                CategoryEntry.COLUMN_PARENT_KEY + " INTEGER NOT NULL, "  +
                CategoryEntry.COLUMN_TITLE + " TEXT NOT NULL );" ;

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //returns all categories with COLUMN_PARENT_KEY = parent
    public ArrayList<CategoryEntry> getAllCategories(int parent) {

        ArrayList<CategoryEntry> categoriesList = new ArrayList<CategoryEntry>();

        String query = "SELECT  * FROM " + CategoryEntry.TABLE_NAME +
                " WHERE " + CategoryEntry.COLUMN_PARENT_KEY + " = " + parent + ";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        CategoryEntry ce = null;
        if (cursor.moveToFirst()) {
            do {
                ce = new CategoryEntry();

                ce.setTitle(cursor.getString(2));
                ce.setAuthor(Integer.parseInt(cursor.getString(1)));
                ce.setKey(Integer.parseInt(cursor.getString(0)));

                if (ce.getAuthor() == parent) {
                    categoriesList.add(ce);
                }

            } while (cursor.moveToNext());
        }

        Log.d("getAllCategories", categoriesList.toString());

        return categoriesList;
    }

    //returns all child categories based on its title
    public ArrayList<CategoryEntry> getAllChildCategories(String title) {

        ArrayList<CategoryEntry> categoriesList;

        String query = "SELECT  * FROM " + CategoryEntry.TABLE_NAME +
                " WHERE " + CategoryEntry.COLUMN_TITLE + " = '" + title + "' ;";
        int parent;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        parent = -1;

        if (cursor.moveToFirst()) {
            do {
                parent = Integer.parseInt(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        categoriesList = getAllCategories(parent);

        return categoriesList;
    }
}