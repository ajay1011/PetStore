package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AJAY OMKAR on 20-Dec-16.
 */
// class created to store database related constants
public final class PetContract {

    private PetContract(){};
    // Content authority
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    // base content uri for the opeartions
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_PETS = "pets";
    public static abstract class PetEntry implements BaseColumns{
        // content uri for each class of the application
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        // constant for name of the table
        public static final String TABLE_NAME="pets";
        // constants for columns of the table
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BREED = "breed";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_WEIGHT = "weight";

        // constants for different genders
        // o for unknown,1 for male,2 for female
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        public static boolean isValidGender(Integer gender) {
            if(gender==GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE)
                return true;
            return false;
        }
    }
}
