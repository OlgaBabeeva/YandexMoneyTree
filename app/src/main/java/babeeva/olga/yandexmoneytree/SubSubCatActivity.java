package babeeva.olga.yandexmoneytree;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class SubSubCatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_sub_category);

        final CategoriesDbHelper mOpenHelper;

        String album_id;

        Intent i = getIntent();
        album_id = i.getStringExtra("album_id");
        setTitle(album_id);

        mOpenHelper = CategoriesDbHelper.getInstance(this);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        List<CategoriesContract.CategoryEntry> list = mOpenHelper.getAllChildCategories(album_id);

        ArrayAdapter<CategoriesContract.CategoryEntry> adapter;
        adapter = new ArrayAdapter<CategoriesContract.CategoryEntry>(
                this,android.R.layout.simple_list_item_1,list);

        ListView lv = (ListView) findViewById(R.id.listView3);
        lv.setAdapter(adapter);

    }

}
