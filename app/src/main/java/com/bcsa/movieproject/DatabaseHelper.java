package com.bcsa.movieproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.bcsa.movieproject.LoginFragment;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MovieProject";

    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_USERS = "users";
    public static final String KEY_ID = "id";
    public static final String KEY_USER_NAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String SQL_TABLE_USERS = " CREATE TABLE " + TABLE_USERS
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_USER_NAME + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_PASSWORD + " TEXT"
            + " ) ";

    public Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sqLiteDatabase.execSQL(SQL_TABLE_USERS);
        sqLiteDatabase.execSQL(Image.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + Image.TABLE_NAME);
    }

    public void addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USER_NAME, user.userName);
        values.put(KEY_EMAIL, user.email);
        values.put(KEY_PASSWORD, user.password);

        long todo_id = db.insert(TABLE_USERS, null, values);
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,// Selecting Table
                new String[]{KEY_ID, KEY_USER_NAME, KEY_EMAIL, KEY_PASSWORD},//Selecting columns want to query
                KEY_EMAIL + "=?",
                new String[]{email},//Where clause
                null, null, null);
        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            User user1 = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            return user1;
        }
        return null;
    }

    public User Authenticate(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,// Selecting Table
                new String[]{KEY_ID, KEY_USER_NAME, KEY_EMAIL, KEY_PASSWORD},//Selecting columns want to query
                KEY_EMAIL + "=?",
                new String[]{user.email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email
            User user1 = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

            //Match both passwords check they are same or not
            if (user.password.equalsIgnoreCase(user1.password)) {
                return user1;
            }
        }

        //if user password does not matches or there is no record with that email then return @false
        return null;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(COLUMN_ID, user.getId());
        values.put(KEY_PASSWORD, user.getPassword());
        // values.put(User.COLUMN_USERNAME, user.getUsername());

        return db.update(TABLE_USERS, values, KEY_ID + " = ?",  new String[]{String.valueOf(user.getId())});
        //return 1;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,// Selecting Table
                new String[]{KEY_ID, KEY_USER_NAME, KEY_EMAIL, KEY_PASSWORD},//Selecting columns want to query
                KEY_EMAIL + "=?",
                new String[]{email},//Where clause
                null, null, null);

        if (cursor != null && cursor.moveToFirst()&& cursor.getCount()>0) {
            //if cursor has value then in user database there is user associated with this given email so return true
            return true;
        }

        //if email does not exist return false
        return false;
    }

    public long InsertImage(String name, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Image.COLUMN_NAME, name);
        cv.put(Image.COLUMN_USER_ID, User.LOGGEDIN.getId());
        cv.put(Image.COLUMN_IMAGE, image);
        long id = db.insert(Image.TABLE_NAME, null, cv);
        db.close();
        return id;
    }

    public int UpdateImage(Image image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Image.COLUMN_ID, image.getId());
        values.put(Image.COLUMN_NAME, image.getName());
        values.put(Image.COLUMN_USER_ID, image.getUserId());
        values.put(Image.COLUMN_IMAGE, image.getImage());

        return db.update(Image.TABLE_NAME, values, Image.COLUMN_ID + " = ?",
                new String[]{String.valueOf(image.getId())});
    }

    public Image getImage(String idS) {
        int id = Integer.parseInt(idS);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Image.TABLE_NAME,
                new String[]{Image.COLUMN_ID, Image.COLUMN_NAME, Image.COLUMN_USER_ID, Image.COLUMN_IMAGE},
                Image.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() <= 0)
            return null;

        byte[] image1 = null;//cursor.getBlob(1);
        long lId = cursor.getLong(cursor.getColumnIndex(Image.COLUMN_ID));
        String sName = cursor.getString(cursor.getColumnIndex(Image.COLUMN_NAME));
        int iUserId = cursor.getInt(cursor.getColumnIndex(Image.COLUMN_USER_ID));
        Image image = new Image(lId, sName, iUserId, image1);
        cursor.close();
        return image;
    }

    /*public List<Result> getFavourites(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        List<Result> favourites = new ArrayList<Result>();
        Cursor res = db.rawQuery("select m." + MOVIES_COLUMN_ID +
                ", m." + MOVIES_COLUMN_TITLE +
                ", m." + MOVIES_COLUMN_DATE +
                ", m." + MOVIES_COLUMN_DESCRIPTION +
                ", m." + MOVIES_COLUMN_POSTER +
                " from " + TABLE_FAVOURITES + " f JOIN " + TABLE_MOVIES + " m" +
                " on f." + FAVOURITES_COLUMN_MOVIE_ID + " = m." + MOVIES_COLUMN_ID +
                " where f." + FAVOURITES_COLUMN_USERNAME + " = \"" + username + "\"",null);
        while(res.moveToNext()){
            Result movie = new Result();
            movie.setId(res.getInt(0));
            movie.setTitle(res.getString(1));
            movie.setReleaseDate(res.getString(2));
            movie.setOverview(res.getString(3));
            movie.setBackdropPath(res.getString(4));
            favourites.add(movie);
        }
        return favourites;
    }*/
}
