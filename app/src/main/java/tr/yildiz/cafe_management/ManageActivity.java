package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageActivity extends AppCompatActivity  implements View.OnClickListener{
    private Button buttonSinif,buttonUrun,buttonMasaSinifi,buttonKaydet,buttonEkle;
    private EditText editTextNumberDecimalTuruncu,editTextNumberDecimalKirmizi,edit_text_calisanEkle;
  //  private RecyclerView recyclerView;
     private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<User> users;
    private ArrayList<Worker> calisanlar;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private adapterCalisan adp;
    private Context context = this;

    private int control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();
        dataUyari();
        dataCalisan();
        dataUsers();

        buttonUrun.setOnClickListener(this);
        buttonSinif.setOnClickListener(this);
        buttonMasaSinifi.setOnClickListener(this);
        buttonKaydet.setOnClickListener(this);
        buttonEkle.setOnClickListener(this);

    }

    public void init(){
        recyclerView = (RecyclerView) findViewById((R.id.calisanlar));
        buttonSinif = (Button) findViewById((R.id.buttonSinif));
        buttonUrun = (Button) findViewById((R.id.buttonUrun));
        buttonMasaSinifi = (Button) findViewById((R.id.buttonMasaSinifi));
        buttonKaydet = (Button) findViewById((R.id.buttonKaydet));
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        editTextNumberDecimalTuruncu = (EditText) findViewById((R.id.editTextNumberDecimalTuruncu));
        editTextNumberDecimalKirmizi = (EditText) findViewById((R.id.editTextNumberDecimalKirmizi));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        users = new ArrayList<User>();
        calisanlar = new ArrayList<Worker>();


    }

    public void dataUyari(){
        reference.child("dukkanlar").child(user.getUid()).child("uyarilar").child("kirmizi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                editTextNumberDecimalKirmizi.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("dukkanlar").child(user.getUid()).child("uyarilar").child("turuncu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                editTextNumberDecimalTuruncu.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dataUsers(){
        reference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User u = snapshot.getValue(User.class);
                users.add(u);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonUrun:
                Intent intentUrun = new Intent(this, AyarlarUrunActivity.class);
                startActivity(intentUrun);
                break;

            case R.id.buttonSinif:
                Intent intentSinif = new Intent(this, AyarlarSinifActivity.class);
                startActivity(intentSinif);
                break;

            case R.id.buttonMasaSinifi:
                Intent intentMasaSinif = new Intent(this, AyarlarMasaSinifiActivity.class);
                startActivity(intentMasaSinif);
                break;

            case R.id.buttonKaydet:
                if(editTextNumberDecimalTuruncu.getText().toString().isEmpty() || editTextNumberDecimalKirmizi.getText().toString().isEmpty()){
                    Toast.makeText(this,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("dukkanlar").child(user.getUid()).child("uyarilar").child("kirmizi").setValue(Integer.parseInt(editTextNumberDecimalKirmizi.getText().toString()));
                reference.child("dukkanlar").child(user.getUid()).child("uyarilar").child("turuncu").setValue(Integer.parseInt(editTextNumberDecimalTuruncu.getText().toString()));
                Toast.makeText(this,"Uyarılar Kaydedildi",Toast.LENGTH_SHORT).show();
                break;

            case R.id.buttonEkle:
                control = 0;
                for (User u:users) {
                    Log.i("kullanıcı",u.getUserId());
                    if(edit_text_calisanEkle.getText().toString().equals(u.getMail())){
                        if(u.getMod().equals("1")){
                            Toast.makeText(context,"Yönetici Eklenemez",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            reference.child("users").child(u.getUserId()).child("yer").setValue(user.getUid());
                            u.setYer(user.getUid());
                            Worker w = new Worker(u.getIsim(),u.getSoyisim(),u.getMail(),u.getSifre(),u.getMod(),u.getUserId(),u.getYer(),u.getTel(),u.getStatus(),0,0,0,0);
                            reference.child("dukkanlar").child(user.getUid()).child("calisanlar").child(u.getUserId()).setValue(w);
                            control = 1;
                            Toast.makeText(this,"Çalışan Eklendi",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                if (control == 0) {
                    Toast.makeText(this,"Böyle bir çalışan maili yok",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void dataCalisan(){
        reference.child("dukkanlar").child(user.getUid()).child("calisanlar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Worker u = snapshot.getValue(Worker.class);
                calisanlar.add(u);
                adp = new adapterCalisan(context,calisanlar);
                recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Worker d = null;
                for (Worker c:calisanlar
                     ) {
                    if (c.getUserId().equals(snapshot.getKey())){
                        d =c;
                    }
                }
                calisanlar.remove(d);
                adp = new adapterCalisan(context,calisanlar);
                recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}