package com.uth.conversormonedasapp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.DecimalFormat;

public class ResultadoActivity extends AppCompatActivity {

    private TextView tvMontoOriginal;
    private TextView tvResultado;
    private TextView tvTasa;
    private AppCompatButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Colores de barras del sistema, sin morado
        getWindow().setStatusBarColor(Color.parseColor("#0A3D73"));
        getWindow().setNavigationBarColor(Color.parseColor("#F3F6FA"));

        setContentView(R.layout.activity_resultado);

        tvMontoOriginal = findViewById(R.id.tvMontoOriginal);
        tvResultado = findViewById(R.id.tvResultado);
        tvTasa = findViewById(R.id.tvTasa);
        btnVolver = findViewById(R.id.btnVolver);

        mostrarResultado();

        btnVolver.setOnClickListener(v -> finish());
    }

    private void mostrarResultado() {
        double monto = getIntent().getDoubleExtra("monto", 0);
        double resultado = getIntent().getDoubleExtra("resultado", 0);
        double tasa = getIntent().getDoubleExtra("tasa", 0);

        String origen = getIntent().getStringExtra("origen");
        String destino = getIntent().getStringExtra("destino");

        if (origen == null) {
            origen = "";
        }

        if (destino == null) {
            destino = "";
        }

        DecimalFormat formatoMonto = new DecimalFormat("#,##0.00");
        DecimalFormat formatoTasa = new DecimalFormat("#,##0.0000");

        tvResultado.setText(
                formatoMonto.format(resultado) + " " + destino
        );

        tvMontoOriginal.setText(
                "Monto original: " + formatoMonto.format(monto) + " " + origen
        );

        tvTasa.setText(
                "Tasa aplicada: 1 " + origen + " = " + formatoTasa.format(tasa) + " " + destino
        );
    }
}