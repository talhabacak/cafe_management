package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private RecyclerView rMenu;
    private RecyclerView.LayoutManager layoutManager;
    private Spinner sinifS;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<Urun> urun;
    private ArrayList<String> sinif;
    private adapterMenu aM;
    private ArrayAdapter sinifAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();
    }

    public void init(){
        rMenu = (RecyclerView) findViewById((R.id.rMenu));
        sinifS = (Spinner) findViewById((R.id.sinif));
        layoutManager = new LinearLayoutManager(this);
        rMenu.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        sinif = new ArrayList<String>();
        urun = new ArrayList<Urun>();

        dataSinif();

        aM = new adapterMenu(context,urun);
        rMenu.setAdapter(aM);

        sinifS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                urun.clear();
                dataUrun(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void dataSinif(){
        reference.child("dukkanlar").child(user.getUid()).child("urunlerSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.add(s);
                sinifAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                sinifS.setAdapter(sinifAdapter);
                Log.i("sinif",s);
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

    public void dataUrun(String sinif){
        reference.child("dukkanlar").child(user.getUid()).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urun.add(s);
                Log.i("isim",s.getIsim());

                aM = new adapterMenu(context,urun);
                rMenu.setAdapter(aM);
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

}