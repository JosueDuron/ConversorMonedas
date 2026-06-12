package com.uth.conversormonedasapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.uth.conversormonedasapp.BD.DatabaseHelper;
import com.uth.conversormonedasapp.utils.DateUtils;

public class MainActivity extends AppCompatActivity {

    private EditText etMonto;
    private Spinner spinnerOrigen;
    private Spinner spinnerDestino;
    private AppCompatButton btnConvertir;
    private AppCompatButton btnHistorial;
    private AppCompatButton btnTasas;

    private DatabaseHelper databaseHelper;

    private final String[] monedasOrigen = {
            "HNL",
            "GTQ",
            "NIO",
            "CRC",
            "PAB",
            "USD"
    };

    private final String[] monedasDestino = {
            "USD"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#0A3D73"));
        getWindow().setNavigationBarColor(Color.parseColor("#F3F6FA"));

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        etMonto = findViewById(R.id.etMonto);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnHistorial = findViewById(R.id.btnHistorial);
        btnTasas = findViewById(R.id.btnTasas);

        configurarSpinners();
        configurarBotones();
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterOrigen = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                monedasOrigen
        );
        adapterOrigen.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerOrigen.setAdapter(adapterOrigen);

        ArrayAdapter<String> adapterDestino = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                monedasDestino
        );
        adapterDestino.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDestino.setAdapter(adapterDestino);
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

        String monedaOrigen = spinnerOrigen.getSelectedItem().toString();
        String monedaDestino = spinnerDestino.getSelectedItem().toString();

        double tasa = databaseHelper.obtenerTasa(monedaOrigen, monedaDestino);

        if (tasa == 0) {
            Toast.makeText(this, "No existe tasa para esta conversión", Toast.LENGTH_SHORT).show();
            return;
        }

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