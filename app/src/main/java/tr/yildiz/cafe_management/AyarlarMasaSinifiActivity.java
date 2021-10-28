package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

public class AyarlarMasaSinifiActivity extends AppCompatActivity implements View.OnClickListener{
    private Button buttonEkle,buttonSil,buttonMasaEkle;
    private EditText edit_text_ekle,editTextNumberMasa;
    private Spinner spinnerSil,spinnerEkleSinif;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayAdapter arrayListAdapterSinif;
    private ArrayList<String> sinif;
    private ArrayList<MasaIsim> masa;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar_masa_sinifi);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        init();
        dataSinif();
    }

    public void init(){
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        buttonMasaEkle = (Button) findViewById((R.id.buttonMasaEkle));
        buttonSil = (Button) findViewById((R.id.buttonSil));
        edit_text_ekle = (EditText) findViewById((R.id.edit_text_ekle));
        editTextNumberMasa = (EditText) findViewById((R.id.editTextNumberMasa));
        spinnerSil = (Spinner) findViewById((R.id.spinnerSil));
        spinnerEkleSinif = (Spinner) findViewById((R.id.spinnerEkleSinif));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        buttonEkle.setOnClickListener(this);
        buttonSil.setOnClickListener(this);
        buttonMasaEkle.setOnClickListener(this);

        sinif = new ArrayList<String>();
        masa = new ArrayList<MasaIsim>();
    }

    public void dataSinif(){
        reference.child("dukkanlar").child(user.getUid()).child("masalarSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSil.setAdapter(arrayListAdapterSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterSinif);
                Log.i("sinif",s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.remove(previousChildName);
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSil.setAdapter(arrayListAdapterSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                sinif.remove(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSil.setAdapter(arrayListAdapterSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dataMasa(String Sinif){
        reference.child("dukkanlar").child(user.getUid()).child("masalar").child(Sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                reference.child("dukkanlar").child(user.getUid()).child("masa").child(Sinif).child(s.getIsim()).removeValue();
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
                int control = 0;
                for (String s:sinif
                     ) {
                    if(s.equals(edit_text_ekle.getText().toString())){
                        control = 1;
                        Toast.makeText(this,"Bu Sınıf Zaten Mevcut",Toast.LENGTH_SHORT).show();
                    }
                }
                if (control == 1){
                    return;
                }
                reference.child("dukkanlar").child(user.getUid()).child("masalarSinif").child(edit_text_ekle.getText().toString()).setValue(edit_text_ekle.getText().toString());
                Toast.makeText(getApplicationContext(),"Masa Sınıfı Eklendi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSil:
                dataMasa(spinnerSil.getSelectedItem().toString());
                reference.child("dukkanlar").child(user.getUid()).child("masa").child(spinnerSil.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerSil.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("masalarSinif").child(spinnerSil.getSelectedItem().toString()).removeValue();
                Toast.makeText(getApplicationContext(),"Masa Sınıfı Silindi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonMasaEkle:
                if(editTextNumberMasa.getText().toString().isEmpty()){
                    Toast.makeText(this,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer count= Integer.parseInt(editTextNumberMasa.getText().toString());
                reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("masa").child(spinnerEkleSinif.getSelectedItem().toString()).removeValue();
                for (int i = 1; i <= count; i++) {
                    reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(String.valueOf(i)).child("isim").setValue(String.valueOf(i));
                    reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(String.valueOf(i)).child("isimKisisel").setValue("");
                    reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(String.valueOf(i)).child("mode").setValue(0);
                    reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(String.valueOf(i)).child("ac").setValue("");
                    reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(String.valueOf(i)).child("son").setValue("");
                    reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(String.valueOf(i)).child("sonFormat").setValue("");
                }
                Toast.makeText(getApplicationContext(),"Masalar Oluşturuldu",Toast.LENGTH_SHORT).show();

                break;
        }
    }

}