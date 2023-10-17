package com.example.app_notes;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.yalantis.ucrop.UCrop;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private static final int REQUEST_SELECT_PICTURE = 101;

    public static SQLiteHelper dbHelper;

    private SQLiteDatabase database;
    private EditText editNota1, editNota2, editNota3;
    private Uri imageUri; // Agrega una variable para almacenar la URI de la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_main);
        dbHelper = new SQLiteHelper(this);
        database = dbHelper.getWritableDatabase();

        dbHelper = new SQLiteHelper(this);
        editNota1 = findViewById(R.id.Edit_nota);
        editNota2 = findViewById(R.id.Edit_nota2);
        editNota3 = findViewById(R.id.Edit_nota3);
        imageView = findViewById(R.id.imageView);

        Button btnAdd = findViewById(R.id.btnAdd);

        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String databaseName = sharedPreferences.getString("notas.db", "");

        // Crea un nuevo objeto SQLiteHelper con el nombre de la base de datos
        dbHelper = new SQLiteHelper(this);
        database = dbHelper.getReadableDatabase();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtén los valores de los campos de entrada
                String nota1 = editNota1.getText().toString();
                String nota2 = editNota2.getText().toString();
                String nota3 = editNota3.getText().toString();

                if (nota1.isEmpty() || nota2.isEmpty() || nota3.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
                } else {
                    // Llama al método de inserción en SQLiteHelper
                    long result = dbHelper.insertNota(nota1, nota2, nota3, imageUri.toString());

                    if (result != -1) {
                        Toast.makeText(MainActivity.this, "Se han guardado los valores correctamente", Toast.LENGTH_SHORT).show();
                        // Borra los campos después de guardar los valores
                        editNota1.getText().clear();
                        editNota2.getText().clear();
                        editNota3.getText().clear();
                        // No borres la imagen, simplemente establece la imagen predeterminada
                        imageView.setImageResource(R.drawable.add_image); // Cambia "imagen_predeterminada" al recurso de tu imagen predeterminada
                    } else {
                        Toast.makeText(MainActivity.this, "Error al guardar los valores", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Agrega un botón u otro elemento de interfaz para mostrar la alerta
        imageView.setOnClickListener(v -> showImageSelectionDialog());
    }

    private void showImageSelectionDialog() {
        final CharSequence[] items = {"Seleccionar desde la galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar una imagen");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Seleccionar desde la galería")) {
                    openGalleryForCrop();
                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void openGalleryForCrop() {
        // Intent para seleccionar una imagen desde la galería
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri sourceUri = data.getData(); // Obtiene la Uri de la imagen seleccionada
            imageUri = sourceUri; // Almacena la URI de la imagen

            // Crea una Uri de destino para la imagen recortada
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));

            UCrop.Options options = new UCrop.Options();
            options.setCompressionQuality(90);
            options.setHideBottomControls(true);
            options.setFreeStyleCropEnabled(true);
            options.setToolbarColor(ContextCompat.getColor(this, R.color.my_toolbar_color));

            UCrop.of(sourceUri, destinationUri)
                    .withOptions(options)
                    .start(this);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            imageView.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            // Manejar el error, si ocurre uno.
        }

        Button btnList = findViewById(R.id.btnList);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent = new Intent(MainActivity.this, RecordListActivity.class);

                startActivity(intent);
            }
        });

    }



}
