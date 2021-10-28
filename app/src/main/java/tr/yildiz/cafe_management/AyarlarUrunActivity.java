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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AyarlarUrunActivity extends AppCompatActivity  implements View.OnClickListener{
    private Button buttonMenu,buttonEkle,buttonUpdate,buttonSil;
    private EditText edit_text_ekle,editTextNumberDecimal_ekleAlis,editTextNumberDecimal_ekleSatis,editTextNumberDecimal_updateAlis,editTextNumberDecimal_updateSatis;
    private Spinner spinnerSil,spinnerUpdateSinif,spinnerUpdateIsim,spinnerEkleSinif,spinnerSilSinif;
    private CheckBox checkBox;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayAdapter arrayListAdapterSil;
    private ArrayAdapter arrayListAdapterUpdate;
    private ArrayAdapter arrayListAdapterSilSinif;
    private ArrayAdapter arrayListAdapterUpdateSinif;
    private ArrayAdapter arrayListAdapterEkleSinif;
    private ArrayList<String> urunSil;
    private ArrayList<String> urunUpdate;
    private ArrayList<String> urunSinif;
    private ArrayList<String> urunEkle;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar_urun);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

        spinnerEkleSinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sinif = parent.getSelectedItem().toString();
                urunEkle.clear();
                dataUrunEkle(sinif);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSilSinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sinif = parent.getSelectedItem().toString();
                Log.i("sinifS",sinif+"1");
                urunSil.clear();
                dataUrunSil(sinif);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerUpdateSinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sinif1 = parent.getSelectedItem().toString();
                Log.i("sinifS",sinif1+"1");
                urunUpdate.clear();
                dataUrunUpdate(sinif1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void init(){
        buttonMenu = (Button) findViewById((R.id.buttonMenu));
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        buttonUpdate = (Button) findViewById((R.id.buttonUpdate));
        buttonSil = (Button) findViewById((R.id.buttonSil));
        edit_text_ekle = (EditText) findViewById((R.id.edit_text_ekle));
        editTextNumberDecimal_ekleAlis = (EditText) findViewById((R.id.editTextNumberDecimal_ekleAlis));
        editTextNumberDecimal_ekleSatis = (EditText) findViewById((R.id.editTextNumberDecimal_ekleSatis));
        editTextNumberDecimal_updateAlis = (EditText) findViewById((R.id.editTextNumberDecimal_updateAlis));
        editTextNumberDecimal_updateSatis = (EditText) findViewById((R.id.editTextNumberDecimal_updateSatis));
        checkBox = (CheckBox) findViewById((R.id.checkBox));
        spinnerSil = (Spinner) findViewById((R.id.spinnerSil));
        spinnerSilSinif = (Spinner) findViewById((R.id.spinnerSilSinif));
        spinnerUpdateSinif = (Spinner) findViewById((R.id.spinnerUpdateSinif));
        spinnerUpdateIsim = (Spinner) findViewById((R.id.spinnerUpdateIsim));
        spinnerEkleSinif = (Spinner) findViewById((R.id.spinnerEkleSinif));
        urunSinif = new ArrayList<String>();
        urunUpdate = new ArrayList<String>();
        urunSil = new ArrayList<String>();
        urunEkle = new ArrayList<String>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        buttonMenu.setOnClickListener(this);
        buttonSil.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        buttonEkle.setOnClickListener(this);

        dataSinif();
    }

    public void dataSinif(){
        reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                urunSinif.add(s);
                arrayListAdapterSilSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                arrayListAdapterUpdateSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                arrayListAdapterEkleSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                spinnerSilSinif.setAdapter(arrayListAdapterSilSinif);
                spinnerUpdateSinif.setAdapter(arrayListAdapterUpdateSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterEkleSinif);
                Log.i("sinif",s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                urunSinif.remove(previousChildName);
                urunSinif.add(s);
                arrayListAdapterSilSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                arrayListAdapterUpdateSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                arrayListAdapterEkleSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                spinnerSilSinif.setAdapter(arrayListAdapterSilSinif);
                spinnerUpdateSinif.setAdapter(arrayListAdapterUpdateSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterEkleSinif);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                urunSinif.remove(s);
                arrayListAdapterSilSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                arrayListAdapterUpdateSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                arrayListAdapterEkleSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSinif);
                spinnerSilSinif.setAdapter(arrayListAdapterSilSinif);
                spinnerUpdateSinif.setAdapter(arrayListAdapterUpdateSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterEkleSinif);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dataUrunEkle(String sinif){
        reference.child("dukkanlar").child(user.getUid()).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urunEkle.add(s.getIsim());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Urun s = snapshot.getValue(Urun.class);
                urunEkle.remove(s.getIsim());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Hata\n"+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dataUrunUpdate(String sinif){
        reference.child("dukkanlar").child(user.getUid()).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urunUpdate.add(s.getIsim());
                arrayListAdapterUpdate = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunUpdate);
                spinnerUpdateIsim.setAdapter(arrayListAdapterUpdate);
                Log.i("isim",s.getIsim());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Urun s = snapshot.getValue(Urun.class);
                urunUpdate.remove(s.getIsim());
                arrayListAdapterUpdate = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunUpdate);
                spinnerUpdateIsim.setAdapter(arrayListAdapterUpdate);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Hata\n"+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dataUrunSil(String sinif){
        reference.child("dukkanlar").child(user.getUid()).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urunSil.add(s.getIsim());
                arrayListAdapterSil = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSil);
                spinnerSil.setAdapter(arrayListAdapterSil);
                Log.i("isim",s.getIsim());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urunSil.remove(previousChildName);
                urunSil.add(s.getIsim());
                arrayListAdapterSil = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSil);
                spinnerSil.setAdapter(arrayListAdapterSil);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Urun s = snapshot.getValue(Urun.class);
                urunSil.remove(s.getIsim());
                arrayListAdapterSil = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urunSil);
                spinnerSil.setAdapter(arrayListAdapterSil);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Hata\n"+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMenu:
                Intent intent = new Intent(getApplicationContext(),MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonEkle:
                if(edit_text_ekle.getText().toString().isEmpty() || editTextNumberDecimal_ekleAlis.getText().toString().isEmpty() || editTextNumberDecimal_ekleSatis.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Eksik Bilgi",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edit_text_ekle.getText().toString().contains(".") || edit_text_ekle.getText().toString().contains("/") || edit_text_ekle.getText().toString().contains(",") ){
                    Toast.makeText(this,"Ürün İsmine '/' ,'.',',' Gibi Noktalama İşaretlerini Kullanmayınız",Toast.LENGTH_SHORT).show();
                    return;
                }
                int controlEkle = 0;
                for (String s:urunEkle
                     ) {
                    if(s.equals(edit_text_ekle.getText().toString())){
                        controlEkle = 1;
                        Toast.makeText(context,"Bu Ürün Zaten Mevcut",Toast.LENGTH_SHORT).show();
                    }
                }
                if(controlEkle == 1){
                    return;
                }
                String depo = "0";
                if(checkBox.isChecked()){
                    depo = "1";
                }
                Double alisEkle = Double.parseDouble(editTextNumberDecimal_ekleAlis.getText().toString());
                Double satisEkle = Double.parseDouble(editTextNumberDecimal_ekleSatis.getText().toString());
                Log.i("alis", String.valueOf(alisEkle));
                Urun urun = new Urun(edit_text_ekle.getText().toString(),spinnerEkleSinif.getSelectedItem().toString(), editTextNumberDecimal_ekleAlis.getText().toString(), editTextNumberDecimal_ekleSatis.getText().toString(),depo);
                reference.child("dukkanlar").child(user.getUid()).child("urunler").child(spinnerEkleSinif.getSelectedItem().toString()).child(urun.getIsim()).setValue(urun);
                DepoUrun du = new DepoUrun(edit_text_ekle.getText().toString(), editTextNumberDecimal_ekleAlis.getText().toString(), editTextNumberDecimal_ekleSatis.getText().toString(),"0",urun.getDepo());
                reference.child("dukkanlar").child(user.getUid()).child("depo").child(urun.getIsim()).setValue(du);
                Toast.makeText(getApplicationContext(),"Eklendi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonUpdate:
                if(editTextNumberDecimal_updateAlis.getText().toString().isEmpty() || editTextNumberDecimal_updateSatis.getText().toString().isEmpty() || spinnerUpdateIsim.getSelectedItem().toString().isEmpty() || spinnerUpdateSinif.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Eksik Bilgi",Toast.LENGTH_SHORT).show();
                    return;
                }
                depo = "0";
                if(checkBox.isChecked()){
                    depo = "1";
                }
                Double alisUpdate = Double.parseDouble(editTextNumberDecimal_updateAlis.getText().toString());
                Double satisUpdate = Double.parseDouble(editTextNumberDecimal_updateSatis.getText().toString());
                Urun urunUpdate = new Urun(spinnerUpdateIsim.getSelectedItem().toString(),spinnerUpdateSinif.getSelectedItem().toString(), editTextNumberDecimal_updateAlis.getText().toString(), editTextNumberDecimal_updateSatis.getText().toString(),depo);
                reference.child("dukkanlar").child(user.getUid()).child("urunler").child(spinnerUpdateSinif.getSelectedItem().toString()).child(urunUpdate.getIsim()).setValue(urunUpdate);
                reference.child("dukkanlar").child(user.getUid()).child("depo").child(urunUpdate.getIsim()).child("alis").setValue(editTextNumberDecimal_updateAlis.getText().toString());
                reference.child("dukkanlar").child(user.getUid()).child("depo").child(urunUpdate.getIsim()).child("satis").setValue(editTextNumberDecimal_updateSatis.getText().toString());

                Toast.makeText(getApplicationContext(),"Güncellendi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSil:
                if(spinnerSilSinif.getSelectedItem().toString().isEmpty() || spinnerSil.getSelectedItem().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Eksik Bilgi",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("dukkanlar").child(user.getUid()).child("urunler").child(spinnerSilSinif.getSelectedItem().toString()).child(spinnerSil.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("depo").child(spinnerSil.getSelectedItem().toString()).removeValue();
                Toast.makeText(getApplicationContext(),"Silindi",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}