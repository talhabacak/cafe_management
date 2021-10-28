package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class AyarlarMasalarActivity extends AppCompatActivity  implements View.OnClickListener {
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayAdapter arrayListAdapterSinif,arrayListAdapter;
    private ArrayList<String> sinif,masa;
    private Spinner spinnerEkleSinif,spinnerSilSinif,spinnerSil;
    private Button buttonEkle,buttonSil;
    private EditText edit_text_ekle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_ayarlar_masalar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        sinif = new ArrayList<String>();
        masa = new ArrayList<String>();

        spinnerSil = (Spinner) this.findViewById((R.id.spinnerSil));
        spinnerSilSinif = (Spinner) findViewById((R.id.spinnerSilSinif));
        spinnerEkleSinif = (Spinner) findViewById((R.id.spinnerEkleSinif));
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        buttonSil = (Button) findViewById((R.id.buttonSil));
        edit_text_ekle = (EditText) findViewById(R.id.edit_text_ekle);

        buttonSil.setOnClickListener(this);
        buttonEkle.setOnClickListener(this);

        dataSinif();

        spinnerSilSinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String s = parent.getSelectedItem().toString();
                masa.clear();
                dataMasa(s);
             }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addAdapterToSpinner(){

    }

    public void dataSinif(){
        reference.child("dukkanlar").child(user.getUid()).child("masalarSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSilSinif.setAdapter(arrayListAdapterSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterSinif);
                Log.i("sinif",s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.remove(previousChildName);
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSilSinif.setAdapter(arrayListAdapterSinif);
                spinnerEkleSinif.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                sinif.remove(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSilSinif.setAdapter(arrayListAdapterSinif);
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

    public void dataMasa(String sinif){
        reference.child("dukkanlar").child(user.getUid()).child("masalar").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                masa.add(s.getIsim());
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,masa);
                spinnerSil.setAdapter(arrayListAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                masa.remove(s.getIsim());
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,masa);
                spinnerSil.setAdapter(arrayListAdapter);
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
        switch (v.getId()){
            case R.id.buttonEkle:
                if(edit_text_ekle.getText().toString().isEmpty()){
                    Toast.makeText(this,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(edit_text_ekle.getText().toString()).child("isim").setValue(edit_text_ekle.getText().toString());
                reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerEkleSinif.getSelectedItem().toString()).child(edit_text_ekle.getText().toString()).child("isimKisisel").setValue("");
                Toast.makeText(getApplicationContext(),"Eklendi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSil:
                reference.child("dukkanlar").child(user.getUid()).child("masalar").child(spinnerSilSinif.getSelectedItem().toString()).child(spinnerSil.getSelectedItem().toString()).removeValue();
                reference.child("dukkanlar").child(user.getUid()).child("masa").child(spinnerSilSinif.getSelectedItem().toString()).child(spinnerSil.getSelectedItem().toString()).removeValue();
                Toast.makeText(getApplicationContext(),"Silindi",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}