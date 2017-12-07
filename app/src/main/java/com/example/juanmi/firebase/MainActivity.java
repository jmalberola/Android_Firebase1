package com.example.juanmi.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.juanmi.firebase.model.Disco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText text_titulo, text_anyo;
    Button boton_anyadir, boton_mostrar;
    ListView lista;

    DatabaseReference bbdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_titulo = (EditText) findViewById(R.id.editText);
        text_anyo = (EditText) findViewById(R.id.editText2);
        boton_anyadir = (Button) findViewById(R.id.button);
        boton_mostrar = (Button) findViewById(R.id.button2);
        lista = (ListView)findViewById(R.id.listView);

        bbdd = FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_discos));

        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayAdapter<String> adaptador;
                ArrayList<String> listado = new ArrayList<String>();

                for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                    Disco disco = datasnapshot.getValue(Disco.class);

                    String titulo = disco.getTitulo();
                    listado.add(titulo);
                }

                adaptador = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,listado);
                lista.setAdapter(adaptador);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        boton_anyadir.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String titulo = text_titulo.getText().toString();
                String anyo = text_anyo.getText().toString();

                if(!TextUtils.isEmpty(titulo)){
                    if(!TextUtils.isEmpty(anyo)) {

                        Disco d = new Disco(titulo,anyo);

                        String clave = bbdd.push().getKey();

                        bbdd.child(clave).setValue(d);

                        Toast.makeText(MainActivity.this, "Disco añadido", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Debes de introducir un anyo", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Debes de introducir un título", Toast.LENGTH_LONG).show();
                }

            }
        });

        boton_mostrar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                Query q=bbdd.orderByChild(getString(R.string.campo_anyo)).equalTo("1995");

                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int cont=0;
                        for(DataSnapshot datasnapshot: dataSnapshot.getChildren()){
                            cont++;
                            Toast.makeText(MainActivity.this, "He encontrado "+cont, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
