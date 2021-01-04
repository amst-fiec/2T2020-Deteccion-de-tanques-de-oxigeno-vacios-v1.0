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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Estado_de_tanque extends AppCompatActivity {
    DatabaseReference db_reference;
    TextView txtPeso,txtMin,txtMax,txtAltura;
    ProgressBar pbp;
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
        leerPeso();
    }


    private void leerPeso() {
        db_reference.child("Usuario").child("alfombra").child("Tanque").addValueEventListener(new ValueEventListener() {
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
                        float pesoNeto = pesomaxf-pesominf;
                        float pes = pesof-pesominf;
                        int porc = Math.round((pes*100.0f)/pesoNeto);
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
        Intent menu = new Intent(getBaseContext(),Menu.class);
        startActivity(menu);
    }
}