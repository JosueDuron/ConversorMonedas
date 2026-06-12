package com.uth.conversormonedasapp;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private AppCompatButton btnActualizarTasa;
    private ImageButton btnBackCustomRates;
    private ListView listCustomRates;

    private DatabaseHelper databaseHelper;
    private Cursor cursor;
    private int selectedId = -1;

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
        btnActualizarTasa = findViewById(R.id.btnActualizarTasa);
        btnBackCustomRates = findViewById(R.id.btnBackCustomRates);
        listCustomRates = findViewById(R.id.listCustomRates);

        btnBackCustomRates.setOnClickListener(v -> finish());
        btnGuardarTasa.setOnClickListener(v -> guardarTasa());
        btnActualizarTasa.setOnClickListener(v -> actualizarTasa());

        listCustomRates.setOnItemClickListener((parent, view, position, id) -> {
            cursor.moveToPosition(position);
            selectedId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String from = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATES_FROM));
            String to = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATES_TO));
            double rate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATES_RATE));

            etFromCode.setText(from);
            etToCode.setText(to);
            etRate.setText(String.valueOf(rate));

            btnActualizarTasa.setVisibility(android.view.View.VISIBLE);
            btnGuardarTasa.setVisibility(android.view.View.GONE);
        });

        listCustomRates.setOnItemLongClickListener((parent, view, position, id) -> {
            mostrarDialogoEliminar(position);
            return true;
        });

        cargarTasas();
    }

    private void mostrarDialogoEliminar(int position) {
        cursor.moveToPosition(position);
        int idEliminar = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String from = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATES_FROM));
        String to = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATES_TO));

        new AlertDialog.Builder(this)
                .setTitle("Eliminar tasa")
                .setMessage("¿Estás seguro de que deseas eliminar la tasa " + from + " a " + to + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (databaseHelper.eliminarTasa(idEliminar)) {
                        Toast.makeText(this, "Tasa eliminada", Toast.LENGTH_SHORT).show();
                        if (selectedId == idEliminar) {
                            resetForm();
                        }
                        cargarTasas();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarTasa() {
        String from = etFromCode.getText().toString().trim().toUpperCase();
        String to = etToCode.getText().toString().trim().toUpperCase();
        String rateTexto = etRate.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty() || rateTexto.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double rate;
        try {
            rate = Double.parseDouble(rateTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese una tasa válida", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean actualizado = databaseHelper.actualizarTasa(selectedId, from, to, rate);

        if (actualizado) {
            Toast.makeText(this, "Tasa actualizada correctamente", Toast.LENGTH_SHORT).show();
            resetForm();
            cargarTasas();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetForm() {
        etFromCode.setText("");
        etToCode.setText("");
        etRate.setText("");
        selectedId = -1;
        btnActualizarTasa.setVisibility(android.view.View.GONE);
        btnGuardarTasa.setVisibility(android.view.View.VISIBLE);
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

        boolean guardado = databaseHelper.agregarTasa(from, to, rate);

        if (guardado) {
            Toast.makeText(this, "Tasa guardada", Toast.LENGTH_SHORT).show();
            resetForm();
            cargarTasas();
        } else {
            Toast.makeText(this, "No se pudo guardar la tasa", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarTasas() {
        if (cursor != null) {
            cursor.close();
        }

        cursor = databaseHelper.obtenerTodasLasTasas();

        String[] columnas = {
                DatabaseHelper.COLUMN_RATES_FROM,
                DatabaseHelper.COLUMN_RATES_TO,
                DatabaseHelper.COLUMN_RATES_RATE
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
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView ivFavorite = view.findViewById(R.id.ivFavorite);

                cursor.moveToPosition(position);
                final int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                final int isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RATES_IS_FAVORITE));

                if (isFavorite == 1) {
                    ivFavorite.setImageResource(R.drawable.ic_star_on);
                } else {
                    ivFavorite.setImageResource(R.drawable.ic_star_off);
                }

                ivFavorite.setOnClickListener(v -> {
                    int nuevoEstado = (isFavorite == 1) ? 0 : 1;
                    if (databaseHelper.marcarTasaFavorita(id, nuevoEstado)) {
                        // Animación de rebote
                        v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100).withEndAction(() -> 
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).withEndAction(() -> {
                                // Recargar datos después de la animación
                                cargarTasas();
                            })
                        );
                    }
                });

                return view;
            }
        };

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