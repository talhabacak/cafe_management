package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;

public class AyarlarSinifActivity extends AppCompatActivity  implements View.OnClickListener {
    private Button buttonEkle,buttonUpdate,buttonSil;
    private EditText edit_text_ekle,edit_text_isimYeni;
    private Spinner spinnerGuncelle,spinnerSil;
    private FirebaseDatabase database;
    private DatabaseReference reference,reference1;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayAdapter arrayListAdapterSinif;
    private ArrayList<String> sinif;
    private ArrayList<Urun> urun;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar_sinif);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();
    }

    public void init(){
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        buttonUpdate = (Button) findViewById((R.id.buttonUpdate));
        buttonSil = (Button) findViewById((R.id.buttonSil));
        edit_text_ekle = (EditText) findViewById((R.id.edit_text_ekle));
        edit_text_isimYeni = (EditText) findViewById((R.id.edit_text_isimYeni));
        spinnerGuncelle = (Spinner) findViewById((R.id.spinnerGuncelle));
        spinnerSil = (Spinner) findViewById((R.id.spinnerSil));

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        reference1 = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        buttonEkle.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        buttonSil.setOnClickListener(this);

        urun = new ArrayList<Urun>();
        sinif = new ArrayList<String>();

        dataSinif();

        spinnerGuncelle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                urun.clear();
                updateData(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEkle:
                if(edit_text_ekle.getText().toString().isEmpty()){
                    Toast.makeText(this,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit_text_ekle.getText().toString().contains(".") || edit_text_ekle.getText().toString().contains("/") || edit_text_ekle.getText().toString().contains(",") ){
                    Toast.makeText(this,"'/' ,'.',',' Gibi Noktalama İşaretlerini Kullanmayınız",Toast.LENGTH_SHORT).show();
                    return;
                }

                int controlEkleSinif = 0;
                for (String s:sinif
                     ) {
                    if(s.equals(edit_text_ekle.getText().toString())){
                        Toast.makeText(this,"Bu Sınıf Zaten Mevcut",Toast.LENGTH_SHORT).show();
                        controlEkleSinif = 1;
                    }
                }
                if(controlEkleSinif == 1){
                    return;
                }
                reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").child(edit_text_ekle.getText().toString()).setValue(edit_text_ekle.getText().toString());
                Toast.makeText(getApplicationContext(),"Ürün Sınıfı Eklendi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonUpdate:
                if(edit_text_isimYeni.getText().toString().isEmpty()){
                    Toast.makeText(this,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit_text_isimYeni.getText().toString().contains(".") || edit_text_isimYeni.getText().toString().contains("/") || edit_text_isimYeni.getText().toString().contains(",") ){
                    Toast.makeText(this,"'/' ,'.',',' Gibi Noktalama İşaretlerini Kullanmayınız",Toast.LENGTH_SHORT).show();
                    return;
                }

                int controlUpdateSinif = 0;
                for (String s:sinif
                ) {
                    if(s.equals(edit_text_isimYeni.getText().toString())){
                        Toast.makeText(this,"Bu Sınıf Zaten Mevcut",Toast.LENGTH_SHORT).show();
                        controlUpdateSinif = 1;
                    }
                }
                if(controlUpdateSinif == 1){
                    return;
                }
                for (Urun u:urun) {
                    reference.child("dukkanlar").child(user.getUid()).child("urunler").child(edit_text_isimYeni.getText().toString()).child(u.getIsim()).setValue(u);
                }
                reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").child(edit_text_isimYeni.getText().toString()).setValue(edit_text_isimYeni.getText().toString());
                reference.child("dukkanlar").child(user.getUid()).child("urunler").child(spinnerGuncelle.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").child(spinnerGuncelle.getSelectedItem().toString()).removeValue();
                Toast.makeText(getApplicationContext(),"Ürün Sınıfı Güncellendi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSil:
                reference.child("dukkanlar").child(user.getUid()).child("urunler").child(spinnerSil.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").child(spinnerSil.getSelectedItem().toString()).removeValue();
                Toast.makeText(getApplicationContext(),"Silindi",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void updateData(String sinif){
        reference.child("dukkanlar").child(user.getUid()).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun u = snapshot.getValue(Urun.class);
                urun.add(u);
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

    public void dataSinif(){
        reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue(String.class);
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSil.setAdapter(arrayListAdapterSinif);
                spinnerGuncelle.setAdapter(arrayListAdapterSinif);
                Log.i("sinif",s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.remove(previousChildName);
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSil.setAdapter(arrayListAdapterSinif);
                spinnerGuncelle.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                sinif.remove(s);
                arrayListAdapterSinif = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSil.setAdapter(arrayListAdapterSinif);
                spinnerGuncelle.setAdapter(arrayListAdapterSinif);
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
