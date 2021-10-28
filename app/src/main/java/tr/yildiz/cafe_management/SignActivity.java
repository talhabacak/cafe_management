package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class SignActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mail,isim,soyisim,password,passwordAgain,dukkan,tel;
    private Switch switch_mod;
    private Button signin;
    private FirebaseDatabase database;
    private DatabaseReference reference,referenceDukkan;
    private FirebaseAuth auth;
    private FirebaseUser users;
    private String mod;
    //private FirebaseAuth.AuthStateListener authListener;
    private boolean contrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

        signin.setOnClickListener(this);
    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }
*/
    public void init(){
        dukkan = (EditText) findViewById((R.id.dukkan));
        mail = (EditText) findViewById((R.id.mail));
        isim = (EditText) findViewById((R.id.isim));
        soyisim = (EditText) findViewById((R.id.soyisim));
        password = (EditText) findViewById((R.id.password));
        passwordAgain = (EditText) findViewById((R.id.passwordAgain));
        tel = (EditText) findViewById((R.id.tel));
        switch_mod = (Switch) findViewById((R.id.switch_mod));
        signin = (Button) findViewById((R.id.signin));

        mod = "0";

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");
        referenceDukkan = database.getReference();
        auth = FirebaseAuth.getInstance();

        switch_mod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dukkan.setVisibility(View.VISIBLE);
                }
                else{
                    dukkan.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signin){
            if(!control()){
                return;
            }
        }
    }

    public boolean control(){
        if(mail.getText().toString().isEmpty() || isim.getText().toString().isEmpty() || soyisim.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordAgain.getText().toString().isEmpty()){
            Toast.makeText(this,"Boş yer bırakmayınız",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!password.getText().toString().equals(passwordAgain.getText().toString())){
            Toast.makeText(this,"Şifreler eşleşmiyor",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.getText().toString().length()<6){
            Toast.makeText(this,"Şifre en az 6 haneli olmalı",Toast.LENGTH_SHORT).show();
        }
        if(switch_mod.isChecked()){
            if(dukkan.getText().toString().isEmpty()){
                Toast.makeText(this,"Dükkanınızın İsmini Giriniz",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        String mailS = mail.getText().toString();
        String passwordS = password.getText().toString();
        auth.createUserWithEmailAndPassword(mailS,passwordS).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    contrl = true;
                    FirebaseUser user = auth.getCurrentUser();
                    Log.i("true","true");
                    Toast.makeText(getApplicationContext(),"Kayıt Başarılı",Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("false","false");
                    Toast.makeText(getApplicationContext(),"Bir HATA meydana geldi",Toast.LENGTH_SHORT).show();
                    contrl = false;
                }
            }
        });

        auth.signInWithEmailAndPassword(mail.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    createNewUser();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Hata",Toast.LENGTH_SHORT).show();
                    contrl = false;
                }
            }
        });

        return contrl;
    }

    public void createNewUser(){
        if(switch_mod.isChecked()){
            mod = "1";
        }
        users = auth.getCurrentUser();

        User user = new User(isim.getText().toString(), soyisim.getText().toString(), mail.getText().toString(), password.getText().toString(), mod, users.getUid(),users.getUid(),tel.getText().toString(),0);
        reference.child(users.getUid()).setValue(user);
        referenceDukkan.child("dukkanlar").child(users.getUid()).child("uyarilar").child("kirmizi").setValue(45);
        referenceDukkan.child("dukkanlar").child(users.getUid()).child("uyarilar").child("turuncu").setValue(30);

        referenceDukkan.child("dukkanlar").child(users.getUid()).child("uyariOdeme").setValue("");
        if( mod.equals("1")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String currentYear = sdf.format(new Date());
            sdf = new SimpleDateFormat("MM");
            String currentMounth = sdf.format(new Date());
            sdf = new SimpleDateFormat("dd");
            String currentDay = sdf.format(new Date());
            referenceDukkan.child("dukkanlar").child(users.getUid()).child("isim").setValue(dukkan.getText().toString());
        }
        else{
            referenceDukkan.child("dukkanlar").child(users.getUid()).child("isim").setValue("");
        }
    }
}