package com.example.tanquesoxigeno;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    static final int GOOGLE_SIGN_IN = 123;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    Button btn_login;
    EditText editUser;
    EditText editPass;
    static boolean tieneAlfombraCG;
    static boolean tieneAlfombraSG;
    static boolean userPassCorr;
    static String alfombra;
    static String conGoogle;
    DatabaseReference db_reference;
    public static HashMap<String, String> getInfo_user() {
        return info_user;
    }

    public static void setInfo_user(HashMap<String, String> info_user) {
        MainActivity.info_user = info_user;
    }

    static HashMap<String, String> info_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUser = (EditText) findViewById(R.id.editUsuario);
        editPass = (EditText) findViewById(R.id.editContra);
        db_reference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if(msg != null){
            if(msg.equals("cerrarSesion")){
                cerrarSesion();
            }
        }
    }

    public void iniciarSesion(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    public void iniciarSesionSinGoogle(View view){

        if(!(isOnline(getApplicationContext()))){
            Toast.makeText(this, "NO HAY CONEXION A INTERNET", Toast.LENGTH_SHORT).show();
        }
        else {
            userPassCorr = false;
            if ("".equals(editUser.getText().toString()) || "".equals(editPass.getText().toString())) {
                Toast.makeText(MainActivity.this, "POR FAVOR, INGRESE SU USUARIO Y CONTRASENA", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseDatabase.getInstance().getReference().child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String user = snapshot.getKey();
                            String pass = snapshot.getValue().toString();
                            if (user.equals(editUser.getText().toString()) && pass.equals(editPass.getText().toString())) {
                                userPassCorr = true;
                            }
                        }

                        if (userPassCorr == true) {
                            System.out.println("ANTES");
                            System.out.println(tieneAlfombraSG);
                            tieneAlfombraPorUsuarioSG(editUser.getText().toString());
                            System.out.println("AL FINAL");
                            System.out.println(tieneAlfombraSG);


                        }
                        if (userPassCorr == false) {
                            Toast.makeText(MainActivity.this, "POR FAVOR REVISE QUE SUS CREDENCIALES SEAN CORRECTAS", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("corriendo onActivity Result");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            }
            catch (ApiException e) {
                Log.w("TAG", "Fallo el inicio de sesión con google.", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        System.out.println("Corriendo firebase Auth");
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        System.out.println("error");
                        updateUI(null);
                    }
                });
    }

    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            conGoogle="SI";
            finish();
            tieneAlfombraPorUsuarioCG( user);

        } else {
            System.out.println("sin registrarse");
            conGoogle="NO";
        }
    }

    private void tieneAlfombraPorUsuarioSG(String username){
        db_reference = FirebaseDatabase.getInstance().getReference();
        tieneAlfombraSG= false;
        alfombra="";
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String rama = snapshot.getKey();
                    System.out.println(rama);
                    if(rama.startsWith("alfombra")){
                        String propietario = snapshot.child("propietario").getValue().toString();
                        if(editUser.getText().toString().equals(propietario)){
                            tieneAlfombraSG=true;
                            System.out.println("SI TIENE ALFOMBRA");
                            alfombra = rama;
                        }
                    }

                }
                if(tieneAlfombraSG==true){
                    Toast.makeText(MainActivity.this, "BIENVENIDO", Toast.LENGTH_SHORT).show();
                    info_user = new HashMap<String, String>();
                    info_user.put("user_name", editUser.getText().toString());
                    info_user.put("conGoogle", conGoogle);
                    info_user.put("alfombraID",alfombra);
                    Intent intent2 = new Intent(getBaseContext(), Menu.class);
                    intent2.putExtra("info_user", info_user);
                    startActivity(intent2);
                }
                if(tieneAlfombraSG==false){
                    Toast.makeText(MainActivity.this, "NO TIENE ALFOMBRAS ASOCIADAS A ESTA CUENTA", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        System.out.println("FINALIZANDO TODA LA FUNCION");
        System.out.println(tieneAlfombraSG);
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                }
            }
            if (capabilities != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        // Datos
                        return true;
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        // WiFi
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void tieneAlfombraPorUsuarioCG(FirebaseUser user){
        tieneAlfombraCG= false;
        alfombra="";
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String rama = snapshot.getKey();
                    System.out.println(rama);
                    if(rama.startsWith("alfombra")){
                        String propietario = snapshot.child("propietario").getValue().toString();
                        System.out.println(propietario);
                        System.out.println(user.getEmail());
                        if(user.getEmail().equals(propietario)){
                            tieneAlfombraCG=true;
                            System.out.println("SI TIENE ALFOMBRA");
                            alfombra = rama;
                        }
                    }

                }
                if(tieneAlfombraCG==true){
                    info_user = new HashMap<String, String>();
                    info_user.put("user_name", user.getEmail());
                    info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
                    info_user.put("conGoogle", conGoogle);
                    info_user.put("alfombraID",alfombra);
                    Intent intent = new Intent(getApplicationContext(), Menu.class);
                    intent.putExtra("info_user", info_user);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getBaseContext(), "NO TIENE ALFOMBRA ASOCIADA CON ESTA CUENTA", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {

            } });
    }

    public void registrarse(View view){

        FirebaseDatabase.getInstance().getReference().child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean existe=false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user = snapshot.getKey();
                    if (user.equals(editUser.getText().toString())) {
                        existe = true;
                    }
                }
                if(existe==true){
                    Toast.makeText(MainActivity.this, "NOMBRE DE USUARIO NO DISPONIBLE", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "REGISTRADO", Toast.LENGTH_SHORT).show();
                    DatabaseReference ref = db_reference.child("Usuarios");
                    ref.child(editUser.getText().toString()).setValue(editPass.getText().toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}