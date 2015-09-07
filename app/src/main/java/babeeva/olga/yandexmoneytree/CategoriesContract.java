package babeeva.olga.yandexmoneytree;

/**
 * Created by Olga on 19.08.2015.
 */
import android.provider.BaseColumns;

/**
 * Defines table and column names for the categories database.
 */
public class CategoriesContract {

    public static final class CategoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "categories";

        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_PARENT_KEY = "parent";
        public static final String COLUMN_TITLE = "title";

        public String title;
        public int parent;
        public int key;

        public CategoryEntry() {
        }

        public CategoryEntry(int key, int parent, String title) {
            super();
            this.title = title;
            this.parent = parent;
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getAuthor() {
            return parent;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public void setAuthor(int parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return  title;
        }

    }
}
