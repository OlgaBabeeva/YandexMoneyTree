package babeeva.olga.yandexmoneytree;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import babeeva.olga.yandexmoneytree.CategoriesContract.CategoryEntry;

public class SubCatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);

        final CategoriesDbHelper mOpenHelper;

        String album_id;

        Intent i = getIntent();
        album_id = i.getStringExtra("album_id");
        setTitle(album_id);

        mOpenHelper = CategoriesDbHelper.getInstance(this);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        List<CategoryEntry> list = mOpenHelper.getAllChildCategories(album_id);

        ArrayAdapter<CategoriesContract.CategoryEntry> adapter;

        adapter = new ArrayAdapter<CategoryEntry>(this,android.R.layout.simple_list_item_1,list);

        ListView lv= (ListView) findViewById(R.id.listView2);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String album_id = ((TextView) view).getText().toString();

                List<CategoryEntry> list = mOpenHelper.getAllChildCategories(album_id);
                if (list.size() > 0) { //check if category has any subcategories
                    Intent i = new Intent(getApplicationContext(), SubSubCatActivity.class);

                    i.putExtra("album_id", album_id);

                    startActivity(i);
                }
            }
        });

    }


}
