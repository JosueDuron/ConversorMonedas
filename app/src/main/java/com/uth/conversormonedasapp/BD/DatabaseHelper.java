package com.uth.conversormonedasapp.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "conversiones_monedas.db";
    private static final int DATABASE_VERSION = 3;

    // Tabla rates
    public static final String TABLE_RATES = "rates";
    public static final String COLUMN_RATES_ID = "id";
    public static final String COLUMN_RATES_FROM = "from_code";
    public static final String COLUMN_RATES_TO = "to_code";
    public static final String COLUMN_RATES_RATE = "rate";
    public static final String COLUMN_RATES_IS_FAVORITE = "is_favorite";

    // Tabla conversions
    public static final String TABLE_CONVERSIONS = "conversions";
    public static final String COLUMN_CONV_ID = "id";
    public static final String COLUMN_CONV_FROM = "from_code";
    public static final String COLUMN_CONV_TO = "to_code";
    public static final String COLUMN_CONV_AMOUNT = "amount";
    public static final String COLUMN_CONV_RESULT = "result";
    public static final String COLUMN_CONV_DATE = "date";
    public static final String COLUMN_CONV_IS_FAVORITE = "is_favorite";

    // Tabla custom_rates
    public static final String TABLE_CUSTOM_RATES = "custom_rates";
    public static final String COLUMN_CUSTOM_ID = "id";
    public static final String COLUMN_CUSTOM_FROM = "from_code";
    public static final String COLUMN_CUSTOM_TO = "to_code";
    public static final String COLUMN_CUSTOM_RATE = "rate";
    public static final String COLUMN_CUSTOM_IS_FAVORITE = "is_favorite";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_RATES =
            "CREATE TABLE " + TABLE_RATES + "(" +
                    COLUMN_RATES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RATES_FROM + " TEXT, " +
                    COLUMN_RATES_TO + " TEXT, " +
                    COLUMN_RATES_RATE + " REAL, " +
                    COLUMN_RATES_IS_FAVORITE + " INTEGER DEFAULT 0" +
                    ")";

    private static final String CREATE_TABLE_CONVERSIONS =
            "CREATE TABLE " + TABLE_CONVERSIONS + "(" +
                    COLUMN_CONV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CONV_FROM + " TEXT, " +
                    COLUMN_CONV_TO + " TEXT, " +
                    COLUMN_CONV_AMOUNT + " REAL, " +
                    COLUMN_CONV_RESULT + " REAL, " +
                    COLUMN_CONV_DATE + " TEXT, " +
                    COLUMN_CONV_IS_FAVORITE + " INTEGER DEFAULT 0" +
                    ")";

    private static final String CREATE_TABLE_CUSTOM_RATES =
            "CREATE TABLE " + TABLE_CUSTOM_RATES + "(" +
                    COLUMN_CUSTOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CUSTOM_FROM + " TEXT, " +
                    COLUMN_CUSTOM_TO + " TEXT, " +
                    COLUMN_CUSTOM_RATE + " REAL, " +
                    COLUMN_CUSTOM_IS_FAVORITE + " INTEGER DEFAULT 0" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RATES);
        db.execSQL(CREATE_TABLE_CONVERSIONS);
        db.execSQL(CREATE_TABLE_CUSTOM_RATES);

        insertarTasasIniciales(db);
    }

    private void insertarTasasIniciales(SQLiteDatabase db) {
        insertarTasaInicial(db, "HNL", "USD", 0.040);
        insertarTasaInicial(db, "GTQ", "USD", 0.13);
        insertarTasaInicial(db, "NIO", "USD", 0.027);
        insertarTasaInicial(db, "CRC", "USD", 0.0019);
        insertarTasaInicial(db, "PAB", "USD", 1.00);
        insertarTasaInicial(db, "USD", "USD", 1.00);
    }

    private void insertarTasaInicial(SQLiteDatabase db, String from, String to, double rate) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATES_FROM, from);
        values.put(COLUMN_RATES_TO, to);
        values.put(COLUMN_RATES_RATE, rate);

        db.insert(TABLE_RATES, null, values);
    }

    public Cursor obtenerTodasLasTasas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_RATES_ID + " AS _id, " +
                COLUMN_RATES_FROM + ", " +
                COLUMN_RATES_TO + ", " +
                COLUMN_RATES_RATE + ", " +
                COLUMN_RATES_IS_FAVORITE +
                " FROM " + TABLE_RATES +
                " ORDER BY " + COLUMN_RATES_IS_FAVORITE + " DESC", null);
    }

    public boolean agregarTasa(String from, String to, double rate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATES_FROM, from);
        values.put(COLUMN_RATES_TO, to);
        values.put(COLUMN_RATES_RATE, rate);

        long result = db.insert(TABLE_RATES, null, values);
        db.close();
        return result != -1;
    }

    public boolean actualizarTasa(int id, String from, String to, double rate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RATES_FROM, from);
        values.put(COLUMN_RATES_TO, to);
        values.put(COLUMN_RATES_RATE, rate);

        int result = db.update(TABLE_RATES, values, COLUMN_RATES_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public double obtenerTasa(String from, String to) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_RATES,
                new String[]{COLUMN_RATES_RATE},
                COLUMN_RATES_FROM + "=? AND " + COLUMN_RATES_TO + "=?",
                new String[]{from, to},
                null,
                null,
                null
        );

        double tasa = 0;

        if (cursor != null && cursor.moveToFirst()) {
            tasa = cursor.getDouble(0);
            cursor.close();
        }

        db.close();
        return tasa;
    }

    public boolean guardarConversion(String from, String to, double amount, double result, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONV_FROM, from);
        values.put(COLUMN_CONV_TO, to);
        values.put(COLUMN_CONV_AMOUNT, amount);
        values.put(COLUMN_CONV_RESULT, result);
        values.put(COLUMN_CONV_DATE, date);
        values.put(COLUMN_CONV_IS_FAVORITE, 0);

        long respuesta = db.insert(TABLE_CONVERSIONS, null, values);

        db.close();

        return respuesta != -1;
    }

    public Cursor obtenerHistorial() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        COLUMN_CONV_ID + " AS _id, " +
                        COLUMN_CONV_FROM + ", " +
                        COLUMN_CONV_TO + ", " +
                        COLUMN_CONV_AMOUNT + ", " +
                        COLUMN_CONV_RESULT + ", " +
                        COLUMN_CONV_DATE + ", " +
                        COLUMN_CONV_IS_FAVORITE +
                        " FROM " + TABLE_CONVERSIONS +
                        " ORDER BY " + COLUMN_CONV_ID + " DESC",
                null
        );
    }

    public boolean agregarTasaPersonalizada(String from, String to, double rate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOM_FROM, from);
        values.put(COLUMN_CUSTOM_TO, to);
        values.put(COLUMN_CUSTOM_RATE, rate);
        values.put(COLUMN_CUSTOM_IS_FAVORITE, 0);

        long respuesta = db.insert(TABLE_CUSTOM_RATES, null, values);

        db.close();

        return respuesta != -1;
    }

    public Cursor obtenerTasasPersonalizadas() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        COLUMN_CUSTOM_ID + " AS _id, " +
                        COLUMN_CUSTOM_FROM + ", " +
                        COLUMN_CUSTOM_TO + ", " +
                        COLUMN_CUSTOM_RATE + ", " +
                        COLUMN_CUSTOM_IS_FAVORITE +
                        " FROM " + TABLE_CUSTOM_RATES +
                        " ORDER BY " + COLUMN_CUSTOM_ID + " DESC",
                null
        );
    }

    public boolean marcarConversionFavorita(int id, int favorito) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONV_IS_FAVORITE, favorito);

        int filas = db.update(
                TABLE_CONVERSIONS,
                values,
                COLUMN_CONV_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas > 0;
    }

    public boolean marcarTasaFavorita(int id, int favorito) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_RATES_IS_FAVORITE, favorito);

        int filas = db.update(
                TABLE_RATES,
                values,
                COLUMN_RATES_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas > 0;
    }

    public Cursor obtenerConversionesFavoritas() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        COLUMN_CONV_ID + " AS _id, " +
                        COLUMN_CONV_FROM + ", " +
                        COLUMN_CONV_TO + ", " +
                        COLUMN_CONV_AMOUNT + ", " +
                        COLUMN_CONV_RESULT + ", " +
                        COLUMN_CONV_DATE + ", " +
                        COLUMN_CONV_IS_FAVORITE +
                        " FROM " + TABLE_CONVERSIONS +
                        " WHERE " + COLUMN_CONV_IS_FAVORITE + " = 1" +
                        " ORDER BY " + COLUMN_CONV_ID + " DESC",
                null
        );
    }

    public Cursor obtenerTasasFavoritas() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        COLUMN_CUSTOM_ID + " AS _id, " +
                        COLUMN_CUSTOM_FROM + ", " +
                        COLUMN_CUSTOM_TO + ", " +
                        COLUMN_CUSTOM_RATE + ", " +
                        COLUMN_CUSTOM_IS_FAVORITE +
                        " FROM " + TABLE_CUSTOM_RATES +
                        " WHERE " + COLUMN_CUSTOM_IS_FAVORITE + " = 1" +
                        " ORDER BY " + COLUMN_CUSTOM_ID + " DESC",
                null
        );
    }

    public boolean eliminarConversion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int filas = db.delete(
                TABLE_CONVERSIONS,
                COLUMN_CONV_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas > 0;
    }

    public boolean eliminarTasa(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_RATES, COLUMN_RATES_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean eliminarTasaPersonalizada(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int filas = db.delete(
                TABLE_CUSTOM_RATES,
                COLUMN_CUSTOM_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas > 0;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_RATES + " ADD COLUMN " + COLUMN_RATES_IS_FAVORITE + " INTEGER DEFAULT 0");
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOM_RATES);
            onCreate(db);
        }
    }
}