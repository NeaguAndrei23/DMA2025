package ro.ase.ie.g1106_s04.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import ro.ase.ie.g1106_s04.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DatabaseManager extends RoomDatabase {

    private static final String DB_NAME = "movies_db";
    private static DatabaseManager instance;

    public abstract MovieDao getMovieDao();

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseManager.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // For simplicity - in production use background threads
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Database created - you could seed data here if needed
                        }
                    })
                    .build();
        }
        return instance;
    }
}
