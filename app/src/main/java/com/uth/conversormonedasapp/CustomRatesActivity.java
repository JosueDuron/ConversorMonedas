package com.uth.conversormonedasapp;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.uth.conversormonedasapp.BD.DatabaseHelper;

public class CustomRatesActivity extends AppCompatActivity {

    private EditText etFromCode;
    private EditText etToCode;
    private EditText etRate;
    private AppCompatButton btnGuardarTasa;
    private ImageButton btnBackCustomRates;
    private ListView listCustomRates;

    private DatabaseHelper databaseHelper;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#0A3D73"));
        getWindow().setNavigationBarColor(Color.parseColor("#F3F6FA"));

        setContentView(R.layout.activity_custom_rates);

        databaseHelper = new DatabaseHelper(this);

        etFromCode = findViewById(R.id.etFromCode);
        etToCode = findViewById(R.id.etToCode);
        etRate = findViewById(R.id.etRate);
        btnGuardarTasa = findViewById(R.id.btnGuardarTasa);
        btnBackCustomRates = findViewById(R.id.btnBackCustomRates);
        listCustomRates = findViewById(R.id.listCustomRates);

        btnBackCustomRates.setOnClickListener(v -> finish());
        btnGuardarTasa.setOnClickListener(v -> guardarTasa());

        cargarTasas();
    }

    private void guardarTasa() {
        String from = etFromCode.getText().toString().trim().toUpperCase();
        String to = etToCode.getText().toString().trim().toUpperCase();
        String rateTexto = etRate.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty() || rateTexto.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (from.length() != 3 || to.length() != 3) {
            Toast.makeText(this, "Use códigos de 3 letras. Ejemplo: HNL, USD", Toast.LENGTH_SHORT).show();
            return;
        }

        double rate;

        try {
            rate = Double.parseDouble(rateTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una tasa válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rate <= 0) {
            Toast.makeText(this, "La tasa debe ser mayor que cero", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean guardado = databaseHelper.agregarTasaPersonalizada(from, to, rate);

        if (guardado) {
            Toast.makeText(this, "Tasa personalizada guardada", Toast.LENGTH_SHORT).show();

            etFromCode.setText("");
            etToCode.setText("");
            etRate.setText("");

            cargarTasas();
        } else {
            Toast.makeText(this, "No se pudo guardar la tasa", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarTasas() {
        if (cursor != null) {
            cursor.close();
        }

        cursor = databaseHelper.obtenerTasasPersonalizadas();

        String[] columnas = {
                DatabaseHelper.COLUMN_CUSTOM_FROM,
                DatabaseHelper.COLUMN_CUSTOM_TO,
                DatabaseHelper.COLUMN_CUSTOM_RATE
        };

        int[] vistas = {
                R.id.tvCustomFrom,
                R.id.tvCustomTo,
                R.id.tvCustomRate
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_custom_rate,
                cursor,
                columnas,
                vistas,
                0
        );

        listCustomRates.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cursor != null) {
            cursor.close();
        }
    }
}