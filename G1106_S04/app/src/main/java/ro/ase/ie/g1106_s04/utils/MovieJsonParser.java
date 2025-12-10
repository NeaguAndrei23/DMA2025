package ro.ase.ie.g1106_s04.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ro.ase.ie.g1106_s04.model.GenreEnum;
import ro.ase.ie.g1106_s04.model.Movie;
import ro.ase.ie.g1106_s04.model.ParentalGuidanceEnum;

public class MovieJsonParser {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static List<Movie> fromJson(String movieJsonArray) {
        List<Movie> movies = null;
        if (movieJsonArray != null) {
            movies = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(movieJsonArray);
                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    Movie movie = readMovie(jsonObject);
                    if (movie != null) {
                        movies.add(movie);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movies;
    }

    private static Movie readMovie(JSONObject jsonObject) {
        try {
            String title = jsonObject.optString("title", "Unknown");
            Double budget = jsonObject.optDouble("budget", 0.0);
            String releaseStr = jsonObject.optString("release", null);
            Integer duration = jsonObject.optInt("duration", 0);
            String genreStr = jsonObject.optString("genre", "Drama");
            String pGuidanceStr = jsonObject.optString("pGuidance", "G");
            Float rating = (float) jsonObject.optDouble("rating", 0.0);
            Boolean watched = jsonObject.optBoolean("watched", false);
            String posterUrl = jsonObject.optString("posterUrl", "");

            // Parse date
            Date release = null;
            if (releaseStr != null && !releaseStr.isEmpty()) {
                try {
                    release = DATE_FORMAT.parse(releaseStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    release = new Date(); // Default to current date if parsing fails
                }
            } else {
                release = new Date();
            }

            // Parse enum values with fallback
            GenreEnum genre;
            try {
                genre = GenreEnum.valueOf(genreStr);
            } catch (IllegalArgumentException e) {
                genre = GenreEnum.Drama; // Default
            }

            ParentalGuidanceEnum pGuidance;
            try {
                pGuidance = ParentalGuidanceEnum.valueOf(pGuidanceStr);
            } catch (IllegalArgumentException e) {
                pGuidance = ParentalGuidanceEnum.G; // Default
            }

            return new Movie(title, budget, release, duration, genre, pGuidance, rating, watched, posterUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toJson(List<Movie> movies) {
        JSONArray array = new JSONArray();
        try {
            for (Movie movie : movies) {
                JSONObject jsonObject = writeMovie(movie);
                array.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    protected static JSONObject writeMovie(Movie movie) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", movie.getTitle());
        jsonObject.put("budget", movie.getBudget());
        jsonObject.put("release", DATE_FORMAT.format(movie.getRelease()));
        jsonObject.put("duration", movie.getDuration());
        jsonObject.put("genre", movie.getGenre().toString());
        jsonObject.put("pGuidance", movie.getpGuidance().toString());
        jsonObject.put("rating", movie.getRating());
        jsonObject.put("watched", movie.getWatched());
        jsonObject.put("posterUrl", movie.getPosterUrl());
        return jsonObject;
    }
}
