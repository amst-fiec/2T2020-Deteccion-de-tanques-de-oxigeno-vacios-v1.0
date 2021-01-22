package com.example.tanquesoxigeno;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Estado_de_tanque extends AppCompatActivity {
    DatabaseReference db_reference;
    TextView txtPeso,txtMin,txtMax,txtAltura;
    ProgressBar pbp;
    HashMap<String,String> info_user;
    String alfombra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_de_tanque);
        txtPeso = (TextView) findViewById(R.id.txtPorcentaje);
        txtMax= (TextView) findViewById(R.id.txtMax);
        txtMin= (TextView) findViewById(R.id.txtMin);
        txtAltura= (TextView) findViewById(R.id.txtAltura);
        db_reference = FirebaseDatabase.getInstance().getReference();
        pbp = (ProgressBar)findViewById(R.id.progressBarP);
        info_user = MainActivity.getInfo_user();
        alfombra=info_user.get("alfombraID");
        leerPeso();
    }


    private void leerPeso() {
        db_reference.child(alfombra).child("Tanque").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    String peso = snapshot.child("peso").getValue().toString();
                    txtPeso.setText("Peso: "+ peso + " Kg");
                    String max = snapshot.child("pesoMax").getValue().toString();
                    txtMax.setText("Peso Maximo: "+ max + " Kg");
                    String min = snapshot.child("pesoMin").getValue().toString();
                    txtMin.setText("Peso Minimo: "+ min + " Kg");
                    String alt = snapshot.child("altura").getValue().toString();
                    txtAltura.setText("Altura: "+ alt+" m");
                    try {
                        float pesomaxf = Float.parseFloat(max);
                        float pesominf = Float.parseFloat(min);
                        float pesof = Float.parseFloat(peso);
                        if(pesof>=0.0 && pesof<=0.1){
                            Toast.makeText(Estado_de_tanque.this, "NO HAY NINGUN TANQUE", Toast.LENGTH_SHORT).show();
                        }

                        float pesoNeto = pesomaxf-pesominf;
                        float pes = pesof-pesominf;

                        int porc = Math.round((pes*100.0f)/pesoNeto);
                        if(porc>=0 && porc<=15){
                            Toast.makeText(Estado_de_tanque.this, "EL OXIGENO ESTA POR AGOTARSE", Toast.LENGTH_SHORT).show();
                        }
                        System.out.println(porc);
                        if(porc>15 && porc<=100){
                            pbp.setProgress(porc);
                            pbp.setProgressDrawable(getDrawable(R.drawable.pb_drawable));
                        }
                        else{
                            pbp.setProgress(porc);
                            pbp.setProgressDrawable(getDrawable(R.drawable.pb_drawable_2));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void irMenu(View view){
        Intent menu = new Intent(this,Menu.class);
        startActivity(menu);
    }
}