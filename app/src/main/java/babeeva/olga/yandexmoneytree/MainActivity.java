package babeeva.olga.yandexmoneytree;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import babeeva.olga.yandexmoneytree.CategoriesContract.CategoryEntry;
import static babeeva.olga.yandexmoneytree.CategoriesContract.CategoryEntry.*;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://money.yandex.ru/api/categories-list";
    // JSON Node names
    private static final String TAG_TITLE = "title";
    private static final String TAG_SUBS = "subs";

    public static CategoriesDbHelper mOpenHelper;
    public SQLiteDatabase db;
    private ProgressDialog pDialog;
    public int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (doesDatabaseExist(this, CategoriesDbHelper.DATABASE_NAME)) {

            setLayout();

        }   else {
            if (this.isOnline()) {
                new GetCategories().execute();
            } else {
                noInternet();
            }
        }
    }

    private class GetCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Подождите...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            num = 1; //Sets initial value of database key

            mOpenHelper = CategoriesDbHelper.getInstance(MainActivity.this);
            db = mOpenHelper.getWritableDatabase();

            if (jsonStr != null) {
                try {
                    JSONArray categories = new JSONArray(jsonStr);
                    writeCategory(categories, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                runOnUiThread(new Runnable(){

                    @Override
                    public void run(){
                        noInternet();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            setLayout();
        }

        public void writeCategory(JSONArray jsonArray, int parent) {

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject category = null;
                try {
                    category = jsonArray.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    String categoryTitle = category.getString(TAG_TITLE);

                    values.put(CategoriesContract.CategoryEntry.COLUMN_PARENT_KEY, parent);
                    values.put(CategoriesContract.CategoryEntry.COLUMN_TITLE, categoryTitle);
                    values.put(CategoriesContract.CategoryEntry.COLUMN_KEY, num);
                    num ++;

                    long newRowId;
                    newRowId =
                            db.replace(
                                    CategoriesContract.CategoryEntry.TABLE_NAME,
                                    null,
                                    values);

                    //writes all subcategories

                    if (category.has(TAG_SUBS)) {
                        writeCategory(category.getJSONArray(TAG_SUBS), (Integer) values.get(COLUMN_KEY));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            if (this.isOnline()) {
                mOpenHelper = CategoriesDbHelper.getInstance(MainActivity.this);
                SQLiteDatabase db1 = mOpenHelper.getWritableDatabase();
                db1.delete(CategoryEntry.TABLE_NAME, null, null);

                mOpenHelper = CategoriesDbHelper.getInstance(MainActivity.this);
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();

                new GetCategories().execute();
            } else {
                noInternet();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //checks if network is available
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void noInternet() {
        Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
    }

    public void setLayout() {

        mOpenHelper = CategoriesDbHelper.getInstance(MainActivity.this);

        List<CategoriesContract.CategoryEntry> list = mOpenHelper.getAllCategories(0);

        ArrayAdapter<CategoriesContract.CategoryEntry> adapter =
                new ArrayAdapter<CategoriesContract.CategoryEntry>(MainActivity.this,
                android.R.layout.simple_list_item_1,list);

        ListView lv= (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String album_id = ((TextView) view).getText().toString();
                Intent i = new Intent(getApplicationContext(), SubCatActivity.class);
                i.putExtra("album_id", album_id);
                startActivity(i);
            }
        });
    }

    private static boolean doesDatabaseExist(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

}
