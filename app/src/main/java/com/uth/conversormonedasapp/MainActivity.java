package com.uth.conversormonedasapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.uth.conversormonedasapp.BD.DatabaseHelper;
import com.uth.conversormonedasapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etMonto;
    private Spinner spinnerTasas;
    private TextView tvMensajeVacio;
    private AppCompatButton btnConvertir;
    private AppCompatButton btnHistorial;
    private AppCompatButton btnTasas;

    private DatabaseHelper databaseHelper;
    private List<RateInfo> listaTasas;

    private static class RateInfo {
        String from;
        String to;
        double rate;
        boolean isFavorite;

        RateInfo(String from, String to, double rate, boolean isFavorite) {
            this.from = from;
            this.to = to;
            this.rate = rate;
            this.isFavorite = isFavorite;
        }

        @Override
        public String toString() {
            String star = isFavorite ? "⭐ " : "";
            return star + from + " a " + to + " (Tasa: " + rate + ")";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#0A3D73"));
        getWindow().setNavigationBarColor(Color.parseColor("#F3F6FA"));

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        etMonto = findViewById(R.id.etMonto);
        spinnerTasas = findViewById(R.id.spinnerTasas);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnTasas = findViewById(R.id.btnTasas);

        cargarTasas();
        configurarBotones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTasas();
    }

    private void cargarTasas() {
        listaTasas = new ArrayList<>();
        Cursor cursor = databaseHelper.obtenerTodasLasTasas();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String from = cursor.getString(cursor.getColumnIndexOrThrow("from_code"));
                String to = cursor.getString(cursor.getColumnIndexOrThrow("to_code"));
                double rate = cursor.getDouble(cursor.getColumnIndexOrThrow("rate"));
                int isFav = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite"));
                listaTasas.add(new RateInfo(from, to, rate, isFav == 1));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (listaTasas.isEmpty()) {
            tvMensajeVacio.setVisibility(View.VISIBLE);
            spinnerTasas.setVisibility(View.GONE);
            btnConvertir.setEnabled(false);
        } else {
            tvMensajeVacio.setVisibility(View.GONE);
            spinnerTasas.setVisibility(View.VISIBLE);
            btnConvertir.setEnabled(true);

            ArrayAdapter<RateInfo> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.spinner_item,
                    listaTasas
            );
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerTasas.setAdapter(adapter);
        }
    }

    private void configurarBotones() {
        btnConvertir.setOnClickListener(v -> convertirMoneda());

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });

        btnTasas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomRatesActivity.class);
            startActivity(intent);
        });
    }

    private void convertirMoneda() {
        String montoTexto = etMonto.getText().toString().trim();

        if (montoTexto.isEmpty()) {
            Toast.makeText(this, "Ingrese un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;

        try {
            monto = Double.parseDouble(montoTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (monto <= 0) {
            Toast.makeText(this, "El monto debe ser mayor que cero", Toast.LENGTH_SHORT).show();
            return;
        }

        RateInfo selectedRate = (RateInfo) spinnerTasas.getSelectedItem();

        if (selectedRate == null) {
            Toast.makeText(this, "Seleccione una tasa de conversión", Toast.LENGTH_SHORT).show();
            return;
        }

        String monedaOrigen = selectedRate.from;
        String monedaDestino = selectedRate.to;
        double tasa = selectedRate.rate;

        double resultado = monto * tasa;
        String fecha = DateUtils.obtenerFechaActual();

        databaseHelper.guardarConversion(
                monedaOrigen,
                monedaDestino,
                monto,
                resultado,
                fecha
        );

        Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
        intent.putExtra("monto", monto);
        intent.putExtra("resultado", resultado);
        intent.putExtra("origen", monedaOrigen);
        intent.putExtra("destino", monedaDestino);
        intent.putExtra("tasa", tasa);
        startActivity(intent);
    }
}