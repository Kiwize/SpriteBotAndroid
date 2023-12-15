package fr.thomas.androiddevforbegginers;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "quizzgame";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_PLAYER = "player";
    private static final String PLAYER_ID = "idplayer";
    private static final String PLAYER_NAME = "name";
    private static final String PLAYER_PASSWORD = "password";

    private final SQLiteDatabase db;

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.

        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYER + " ("
                + PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PLAYER_NAME + " TEXT,"
                + PLAYER_PASSWORD + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER);
        onCreate(db);
    }

    public void clear(String table) {
        db.execSQL("truncate table " + table);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
}

