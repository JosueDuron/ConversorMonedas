package com.uth.conversormonedasapp.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "conversiones_monedas.db";
    private static final int DATABASE_VERSION = 1;

    // Table rates
    public static final String TABLE_RATES = "rates";
    public static final String COLUMN_RATES_ID = "id";
    public static final String COLUMN_RATES_FROM = "from_code";
    public static final String COLUMN_RATES_TO = "to_code";
    public static final String COLUMN_RATES_RATE = "rate";

    // Table conversions
    public static final String TABLE_CONVERSIONS = "conversions";
    public static final String COLUMN_CONV_ID = "id";
    public static final String COLUMN_CONV_FROM = "from_code";
    public static final String COLUMN_CONV_TO = "to_code";
    public static final String COLUMN_CONV_AMOUNT = "amount";
    public static final String COLUMN_CONV_RESULT = "result";
    public static final String COLUMN_CONV_DATE = "date";

    // Create table rates query
    private static final String CREATE_TABLE_RATES = "CREATE TABLE " + TABLE_RATES + "("
            + COLUMN_RATES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_RATES_FROM + " TEXT, "
            + COLUMN_RATES_TO + " TEXT, "
            + COLUMN_RATES_RATE + " REAL"
            + ")";

    // Create table conversions query
    private static final String CREATE_TABLE_CONVERSIONS = "CREATE TABLE " + TABLE_CONVERSIONS + "("
            + COLUMN_CONV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_CONV_FROM + " TEXT, "
            + COLUMN_CONV_TO + " TEXT, "
            + COLUMN_CONV_AMOUNT + " REAL, "
            + COLUMN_CONV_RESULT + " REAL, "
            + COLUMN_CONV_DATE + " TEXT"
            + ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RATES);
        db.execSQL(CREATE_TABLE_CONVERSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSIONS);
        onCreate(db);
    }
}
