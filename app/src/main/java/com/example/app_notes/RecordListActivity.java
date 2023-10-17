package com.example.app_notes;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RecordListActivity extends AppCompatActivity {
    private ListView listView;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        listView = findViewById(R.id.listView);

        // Recupera los datos de la base de datos
        String query = "SELECT * FROM " + SQLiteHelper.TABLE_NOTAS;
        Cursor cursor = MainActivity.dbHelper.getData(query);

        // Configura el adaptador para mostrar los datos en la lista
        String[] fromColumns = {
                SQLiteHelper.COLUMN_NOTA1,
                SQLiteHelper.COLUMN_NOTA2,
                SQLiteHelper.COLUMN_NOTA3,
                SQLiteHelper.COLUMN_IMAGEN_URI
        };

        int[] toViews = {
                R.id.textViewNota1,
                R.id.textViewNota2,
                R.id.textViewNota3,
                R.id.imageView
        };

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_layout,
                cursor,
                fromColumns,
                toViews,
                0
        );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtiene los datos de la nota seleccionada
                Cursor cursor = (Cursor) adapter.getItem(position);
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_ID));
                String nota1 = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_NOTA1));
                String nota2 = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_NOTA2));
                String nota3 = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_NOTA3));
                String imagenUri = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_IMAGEN_URI));

                // Abre ViewEditItemActivity para mostrar y editar la nota
                Intent intent = new Intent(RecordListActivity.this, ViewEditItemActivity.class);
                intent.putExtra("itemId", itemId);
                intent.putExtra("nota1", nota1);
                intent.putExtra("nota2", nota2);
                intent.putExtra("nota3", nota3);
                intent.putExtra("imagen_uri", imagenUri);
                startActivity(intent);
            }
        });

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.imageView) {
                    // Maneja la carga de la imagen en la ImageView
                    String imagenUri = cursor.getString(columnIndex);
                    ImageView imageView = (ImageView) view;

                    if (imagenUri != null && !imagenUri.isEmpty()) {
                        // Carga la imagen desde la URI utilizando Picasso
                        Picasso.get()
                                .load(imagenUri)
                                .error(R.drawable.default_image) // Imagen de marcador de posición si hay un error
                                .into(imageView);
                    } else {
                        // Establece una imagen de marcador de posición si la URI es nula o vacía
                        imageView.setImageResource(R.drawable.default_image);
                    }

                    return true;
                }
                return false;
            }
        });

        // Asigna el adaptador a la lista
        listView.setAdapter(adapter);

        // Agrega un oyente de clic largo para eliminar elementos al mantener presionado
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Muestra una alerta de confirmación para eliminar el elemento
                showDeleteConfirmationDialog(id);
                return true;
            }
        });
    }

    private void showDeleteConfirmationDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Eliminar este elemento?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Lógica para eliminar el elemento en función de itemId
                        deleteItem(itemId);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Cierra el diálogo sin realizar ninguna acción
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void deleteItem(long itemId) {
        String whereClause = SQLiteHelper.COLUMN_ID + " = ?";
        String[] whereArgs = { String.valueOf(itemId) };
        int rowsDeleted = MainActivity.dbHelper.getWritableDatabase().delete(SQLiteHelper.TABLE_NOTAS, whereClause, whereArgs);

        if (rowsDeleted > 0) {
            // Actualiza la vista después de la eliminación
            updateListView();
        }
    }

    private void updateListView() {
        Cursor cursor = MainActivity.dbHelper.getData("SELECT * FROM " + SQLiteHelper.TABLE_NOTAS);
        adapter.changeCursor(cursor);
    }
}
