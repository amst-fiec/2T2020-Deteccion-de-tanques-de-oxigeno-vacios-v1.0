package com.example.tanquesoxigeno;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Menu extends AppCompatActivity {
    TextView txt_name;
    ImageView imv_photo;
    ProgressBar pb;
    DatabaseReference db_reference;
    HashMap <String,String> info_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        info_user = MainActivity.getInfo_user();

        txt_name = findViewById(R.id.textUsername);
        imv_photo = findViewById(R.id.imageFotoPerfil);

        txt_name.setText(info_user.get("user_name"));
        //txt_name.setText("Holiii");
        String photo = info_user.get("user_photo");
        Picasso.with(getApplicationContext()).load(photo).into(imv_photo);

        db_reference = FirebaseDatabase.getInstance().getReference();
        pb = (ProgressBar) findViewById(R.id.barraBateria);
        leerBateria();
    }

    public void irEstado(View view){
        Intent estado = new Intent(getBaseContext(),Estado_de_tanque.class);
        startActivity(estado);
    }

    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    private void leerBateria() {
        db_reference.child("Usuario").child("alfombra").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String bat = snapshot.child("bateria").getValue().toString();
                    try {
                        int porc = Integer.parseInt(bat);
                        if(porc>15 && porc<=100){
                            pb.setProgress(porc);
                            pb.setProgressDrawable(getDrawable(R.drawable.pb_drawable));
                        }
                        else{
                            pb.setProgress(porc);
                            pb.setProgressDrawable(getDrawable(R.drawable.pb_drawable_2));
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


}