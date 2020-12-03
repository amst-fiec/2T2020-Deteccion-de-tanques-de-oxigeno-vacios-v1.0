package com.example.tanquesoxigeno;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Estado_de_tanque extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_de_tanque);
    }

    public void irMenu(View view){
        Intent menu = new Intent(getBaseContext(),Menu.class);
        startActivity(menu);
    }
}