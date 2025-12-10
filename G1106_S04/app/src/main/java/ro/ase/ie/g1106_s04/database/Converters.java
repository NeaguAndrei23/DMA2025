package ro.ase.ie.g1106_s04.database;

import androidx.room.TypeConverter;

import java.util.Date;

import ro.ase.ie.g1106_s04.model.GenreEnum;
import ro.ase.ie.g1106_s04.model.ParentalGuidanceEnum;
import ro.ase.ie.g1106_s04.model.PersistenceMethodEnum;

public class Converters {

    // Date converters
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // GenreEnum converters
    @TypeConverter
    public static GenreEnum fromGenreString(String value) {
        return value == null ? null : GenreEnum.valueOf(value);
    }

    @TypeConverter
    public static String genreToString(GenreEnum genre) {
        return genre == null ? null : genre.name();
    }

    // ParentalGuidanceEnum converters
    @TypeConverter
    public static ParentalGuidanceEnum fromParentalGuidanceString(String value) {
        return value == null ? null : ParentalGuidanceEnum.valueOf(value);
    }

    @TypeConverter
    public static String parentalGuidanceToString(ParentalGuidanceEnum pGuidance) {
        return pGuidance == null ? null : pGuidance.name();
    }

    // PersistenceMethodEnum converters
    @TypeConverter
    public static PersistenceMethodEnum fromPersistenceMethodString(String value) {
        return value == null ? PersistenceMethodEnum.NONE : PersistenceMethodEnum.valueOf(value);
    }

    @TypeConverter
    public static String persistenceMethodToString(PersistenceMethodEnum persistenceMethod) {
        return persistenceMethod == null ? null : persistenceMethod.name();
    }
}
