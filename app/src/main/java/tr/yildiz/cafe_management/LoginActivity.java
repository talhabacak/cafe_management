package tr.yildiz.cafe_management;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.yildiz.cafe_management.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText username,password;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private TextView textViewToSign;
    private Button login;
    private FirebaseAuth auth;
    private FirebaseUser users;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private User u;
    private ArrayList<Integer> uyari;
    private Integer turuncu,kirmizi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();
        textViewToSign.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void init(){
        textViewToSign = (TextView) findViewById((R.id.textViewToSign));
        username = (EditText) findViewById((R.id.username));
        password = (EditText) findViewById((R.id.password));
        login = (Button) findViewById((R.id.login));
        progressBar = (ProgressBar) findViewById((R.id.progressBar));

        uyari = new ArrayList<Integer>();
        u = new User();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        users = auth.getCurrentUser();

        if (users != null){
            data();
        }
        else {
            progressBar.setVisibility(View.GONE);
        }

    }


    public void startIntentService(){
        Intent myIntent = new Intent(LoginActivity.this, MyIntentService.class);
        myIntent.putExtra("mekan",u.getYer());
        myIntent.putExtra("kirmizi",String.valueOf(kirmizi));
        myIntent.putExtra("turuncu",String.valueOf(turuncu));
        startService(myIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                login();
                break;

            case R.id.textViewToSign:
                Intent intent1 = new Intent(this, SignActivity.class);
                startActivity(intent1);
                break;
        }
    }

    public void login(){
        if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            Toast.makeText(this,"Eksik bilgi girmeyiniz",Toast.LENGTH_SHORT).show();
        }
        else{
            auth.signInWithEmailAndPassword(username.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        auth = FirebaseAuth.getInstance();
                        users = auth.getCurrentUser();
                        data();
                        Toast.makeText(getApplicationContext(),"Giriş Başarılı",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Bilgiler Hatalı",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public  void  data(){
        reference.child("users").child(users.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                reference.child("dukkanlar").child(u.getYer()).child("uyarilar").child("kirmizi").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        kirmizi = snapshot.getValue(Integer.class);
                        reference.child("dukkanlar").child(u.getYer()).child("uyarilar").child("turuncu").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                turuncu = snapshot.getValue(Integer.class);
                                startIntentService();
                                Intent intent = new Intent(getApplicationContext(),MainPageActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}