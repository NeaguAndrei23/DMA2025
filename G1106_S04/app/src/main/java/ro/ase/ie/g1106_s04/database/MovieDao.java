package ro.ase.ie.g1106_s04.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ro.ase.ie.g1106_s04.model.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    List<Movie> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :movieId")
    Movie getMovieById(long movieId);

    @Insert
    long insertMovie(Movie movie);

    @Insert
    void insertMovies(List<Movie> movies);

    @Update
    void updateMovie(Movie movie);

    @Update
    void updateMovies(List<Movie> movies);

    @Delete
    void deleteMovie(Movie movie);

    @Query("DELETE FROM movies WHERE id = :movieId")
    void deleteMovieById(long movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies();
}
