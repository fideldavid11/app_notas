package com.example.app_notes;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

public class ViewEditItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    private ImageView imageView;
    private EditText editNota1;
    private EditText editNota2;
    private EditText editNota3;
    private Button btnChangeImage;
    private Button btnSaveChanges;
    private long itemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_item);

        imageView = findViewById(R.id.imageView);
        editNota1 = findViewById(R.id.Edit_nota);
        editNota2 = findViewById(R.id.Edit_nota2);
        editNota3 = findViewById(R.id.Edit_nota3);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Obtiene los datos de la nota y la imagen actual
        Intent intent = getIntent();
        if (intent != null) {
            itemId = intent.getLongExtra("itemId", -1); // Obtén el ID de la nota
            String nota1 = intent.getStringExtra("nota1");
            String nota2 = intent.getStringExtra("nota2");
            String nota3 = intent.getStringExtra("nota3");
            String imageUri = intent.getStringExtra("imagen_uri");

            // Muestra los datos en las vistas
            editNota1.setText(nota1);
            editNota2.setText(nota2);
            editNota3.setText(nota3);

            // Carga la imagen actual
            Picasso.get().load(imageUri).into(imageView);
        }

        // Maneja el clic en el botón Cambiar Imagen
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        // Maneja el clic en el botón Guardar Cambios
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Muestra la imagen seleccionada en el ImageView
            Picasso.get().load(selectedImageUri).into(imageView);
        }
    }

    private void saveChanges() {
        // Obtén los nuevos valores de las notas
        String newNota1 = editNota1.getText().toString();
        String newNota2 = editNota2.getText().toString();
        String newNota3 = editNota3.getText().toString();

        // Actualiza los datos de la nota en la base de datos
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_NOTA1, newNota1);
        values.put(SQLiteHelper.COLUMN_NOTA2, newNota2);
        values.put(SQLiteHelper.COLUMN_NOTA3, newNota3);

        // Si se seleccionó una nueva imagen, actualiza la URI de la imagen
        if (selectedImageUri != null) {
            values.put(SQLiteHelper.COLUMN_IMAGEN_URI, selectedImageUri.toString());
        }

        // Actualiza la nota en la base de datos utilizando el ID
        int rowsUpdated = MainActivity.dbHelper.getWritableDatabase().update(
                SQLiteHelper.TABLE_NOTAS,
                values,
                SQLiteHelper.COLUMN_ID + " = ?",
                new String[] { String.valueOf(itemId) }
        );

        if (rowsUpdated > 0) {
            // Notifica al usuario que los cambios se guardaron con éxito
            Toast.makeText(this, "Cambios guardados con éxito", Toast.LENGTH_SHORT).show();
        } else {
            // Notifica al usuario que ocurrió un error al guardar los cambios
            Toast.makeText(this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
        }

        // Cierra la actividad actual
        finish();
    }
}