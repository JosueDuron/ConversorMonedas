package com.uth.conversormonedasapp;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.uth.conversormonedasapp.BD.DatabaseHelper;

public class HistorialActivity extends AppCompatActivity {

    private ListView listHistorial;
    private TextView tvSinHistorial;
    private ImageButton btnBackHistorial;

    private DatabaseHelper databaseHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#0A3D73"));
        getWindow().setNavigationBarColor(Color.parseColor("#F3F6FA"));

        setContentView(R.layout.activity_historial);

        listHistorial = findViewById(R.id.listHistorial);
        tvSinHistorial = findViewById(R.id.tvSinHistorial);
        btnBackHistorial = findViewById(R.id.btnBackHistorial);

        databaseHelper = new DatabaseHelper(this);

        btnBackHistorial.setOnClickListener(v -> finish());

        cargarHistorial();
    }

    private void cargarHistorial() {
        cursor = databaseHelper.obtenerHistorial();

        if (cursor == null || cursor.getCount() == 0) {
            tvSinHistorial.setText("No hay conversiones registradas.");
            tvSinHistorial.setVisibility(View.VISIBLE);
            listHistorial.setVisibility(View.GONE);
            return;
        }

        tvSinHistorial.setVisibility(View.GONE);
        listHistorial.setVisibility(View.VISIBLE);

        String[] columnas = {
                DatabaseHelper.COLUMN_CONV_FROM,
                DatabaseHelper.COLUMN_CONV_TO,
                DatabaseHelper.COLUMN_CONV_AMOUNT,
                DatabaseHelper.COLUMN_CONV_RESULT,
                DatabaseHelper.COLUMN_CONV_DATE
        };

        int[] vistas = {
                R.id.tvFromCode,
                R.id.tvToCode,
                R.id.tvAmount,
                R.id.tvResult,
                R.id.tvDate
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_historial,
                cursor,
                columnas,
                vistas,
                0
        );

        listHistorial.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cursor != null) {
            cursor.close();
        }
    }
}