package com.example.app_notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notas.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTAS = "notas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTA1 = "nota1";
    public static final String COLUMN_NOTA2 = "nota2";
    public static final String COLUMN_NOTA3 = "nota3";
    public static final String COLUMN_IMAGEN_URI = "imagen_uri"; // Nueva columna para la URI de la imagen

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTAS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NOTA1
            + " text not null, " + COLUMN_NOTA2
            + " text not null, " + COLUMN_NOTA3
            + " text not null, " + COLUMN_IMAGEN_URI // Nueva columna para la URI de la imagen
            + " text not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Maneja la actualización de la base de datos si es necesario.
    }

    public Cursor getData(String query) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(query, null);
    }

    // Método para insertar una nueva nota en la base de datos
    public long insertNota(String nota1, String nota2, String nota3, String imagenUri) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTA1, nota1);
        values.put(COLUMN_NOTA2, nota2);
        values.put(COLUMN_NOTA3, nota3);
        values.put(COLUMN_IMAGEN_URI, imagenUri); // Almacena la URI de la imagen como una cadena

        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NOTAS, null, values);


    }
}