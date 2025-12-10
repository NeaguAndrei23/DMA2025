package ro.ase.ie.g1106_s04.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ro.ase.ie.g1106_s04.R;
import ro.ase.ie.g1106_s04.adapters.MovieAdapter;
import ro.ase.ie.g1106_s04.database.DatabaseManager;
import ro.ase.ie.g1106_s04.database.MovieDao;
import ro.ase.ie.g1106_s04.model.Movie;
import ro.ase.ie.g1106_s04.model.PersistenceMethodEnum;

public class MainActivity extends AppCompatActivity implements IMovieEventListener{

    private static final int ADD_MOVIE = 100;
    private static final int UPDATE_MOVIE = 200;
    private static final String MOVIES_JSON_FILE = "movies.json";
    private ActivityResultLauncher<Intent> launcher;
    private final ArrayList<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private DatabaseManager databaseManager;
    private MovieDao movieDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        movieAdapter=new MovieAdapter(this,movieList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(movieAdapter);

        // Initialize database
        databaseManager = DatabaseManager.getInstance(this);
        movieDao = databaseManager.getMovieDao();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == RESULT_OK)
                        {
                            Intent data = o.getData();
                            Movie movie = data.getParcelableExtra("movie");
                            if(!movieList.contains(movie)){
                                movieList.add(movie);
                            }
                            else{
                                int position=movieList.indexOf(movie);
                                movieList.set(position, movie);
                            }

                            Log.d("MainActivityTag", movie.toString());
                            movieAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_movie_menu_item)
        {
            //add a new movie instance
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra("action_code", ADD_MOVIE);
            launcher.launch(intent);
        }
        else if(item.getItemId() == R.id.export_json_menu_item)
        {
            exportMoviesToJson();
        }
        else if(item.getItemId() == R.id.import_json_menu_item)
        {
            importMoviesFromJson();
        }
        else if(item.getItemId() == R.id.save_database_menu_item)
        {
            saveMoviesToDatabase();
        }
        else if(item.getItemId() == R.id.load_database_menu_item)
        {
            loadMoviesFromDatabase();
        }
        else if(item.getItemId() == R.id.about_menu_item)
        {
            Toast.makeText(MainActivity.this,
                    "DMA2025 - G1106!",
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(int position) {
        Movie currentMovie = movieList.get(position);
        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        intent.putExtra("action_code", UPDATE_MOVIE);
        intent.putExtra("movie", currentMovie);
        launcher.launch(intent);
    }

    @Override
    public void onMovieDelete(int position) {
        movieList.remove(position);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExportMovies() {
        exportMoviesToJson();
    }

    @Override
    public void onImportMovies() {
        importMoviesFromJson();
    }

    private void exportMoviesToJson() {
        try {
            // Filter movies that have JSON persistence method selected
            ArrayList<Movie> jsonMovies = new ArrayList<>();
            for (Movie movie : movieList) {
                if (movie.getPersistenceMethod() == PersistenceMethodEnum.JSON) {
                    jsonMovies.add(movie);
                }
            }

            if (jsonMovies.isEmpty()) {
                Toast.makeText(this,
                        "No movies selected for JSON export. Use the 'Export' radio button on movies.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .setPrettyPrinting()
                    .create();

            String json = gson.toJson(jsonMovies);

            FileOutputStream fos = openFileOutput(MOVIES_JSON_FILE, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(json);
            osw.close();
            fos.close();

            Toast.makeText(this,
                    "Exported " + jsonMovies.size() + " movies to " + MOVIES_JSON_FILE,
                    Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "Movies exported successfully to " + MOVIES_JSON_FILE);
        } catch (Exception e) {
            Toast.makeText(this,
                    "Export failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "Error exporting movies", e);
        }
    }

    private void importMoviesFromJson() {
        try {
            FileInputStream fis = openFileInput(MOVIES_JSON_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            fis.close();

            String json = jsonBuilder.toString();
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();

            Type movieListType = new TypeToken<ArrayList<Movie>>(){}.getType();
            List<Movie> importedMovies = gson.fromJson(json, movieListType);

            if (importedMovies != null) {
                // Mark all imported movies as JSON persistence
                for (Movie movie : importedMovies) {
                    movie.setPersistenceMethod(PersistenceMethodEnum.JSON);
                }

                // Remove existing JSON movies and add imported ones
                movieList.removeIf(movie -> movie.getPersistenceMethod() == PersistenceMethodEnum.JSON);
                movieList.addAll(importedMovies);
                movieAdapter.notifyDataSetChanged();

                Toast.makeText(this,
                        "Imported " + importedMovies.size() + " movies from " + MOVIES_JSON_FILE,
                        Toast.LENGTH_LONG).show();
                Log.d("MainActivity", "Movies imported successfully from " + MOVIES_JSON_FILE);
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Import failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "Error importing movies", e);
        }
    }

    private void saveMoviesToDatabase() {
        try {
            // Filter movies that have SQLITE persistence method selected
            ArrayList<Movie> sqliteMovies = new ArrayList<>();
            for (Movie movie : movieList) {
                if (movie.getPersistenceMethod() == PersistenceMethodEnum.SQLITE) {
                    sqliteMovies.add(movie);
                }
            }

            if (sqliteMovies.isEmpty()) {
                Toast.makeText(this,
                        "No movies selected for database save. Use the 'Persist' radio button on movies.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Clear existing SQLite movies and insert new ones
            movieDao.deleteAllMovies();
            for (Movie movie : sqliteMovies) {
                long id = movieDao.insertMovie(movie);
                movie.setMovieId(id); // Update the ID in case it's used later
            }

            Toast.makeText(this,
                    "Saved " + sqliteMovies.size() + " movies to database",
                    Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "Movies saved successfully to database");
        } catch (Exception e) {
            Toast.makeText(this,
                    "Database save failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "Error saving movies to database", e);
        }
    }

    private void loadMoviesFromDatabase() {
        try {
            List<Movie> dbMovies = movieDao.getAllMovies();

            if (dbMovies == null || dbMovies.isEmpty()) {
                Toast.makeText(this,
                        "No movies found in database",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Mark all loaded movies as SQLITE persistence
            for (Movie movie : dbMovies) {
                movie.setPersistenceMethod(PersistenceMethodEnum.SQLITE);
            }

            // Remove existing SQLITE movies and add loaded ones
            movieList.removeIf(movie -> movie.getPersistenceMethod() == PersistenceMethodEnum.SQLITE);
            movieList.addAll(dbMovies);
            movieAdapter.notifyDataSetChanged();

            Toast.makeText(this,
                    "Loaded " + dbMovies.size() + " movies from database",
                    Toast.LENGTH_LONG).show();
            Log.d("MainActivity", "Movies loaded successfully from database");
        } catch (Exception e) {
            Toast.makeText(this,
                    "Database load failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "Error loading movies from database", e);
        }
    }

}