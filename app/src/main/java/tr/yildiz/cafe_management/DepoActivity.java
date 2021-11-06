package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DepoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Dialog inputUrun;
    private Spinner spinner_sinif,spinner_urun;
    private Button diaolog_urun_iptal,diaolog_urun_ekle,biten;
    private EditText editTextNumber,urunAra;
    private TextView text_sinif,text_isim,sinifT,isimT,dialog_urun,dialog_sinif;
    private ImageButton imageButton;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DepoUrun du;
    private ArrayAdapter arrayListAdapterSinif,arrayListAdapter;
    private ArrayList<String> sinif,urun;
    private ArrayList<DepoUrun> listDepo;
    private ArrayList<MasaUrun> statement;
    private adapterDepo adp;
    private String currentYear,currentMounth,currentDay, dialogSinif,dialogIsim, timeSiparisFormat, mekan,day,mounth,year;
    private SimpleDateFormat sdf;
    private ArrayList<Urun> searchUrun;
    private Integer tempCount = 0, countUrun = 0, countSinif = 0,statusC = 0;
    private SimpleDateFormat sdfSiparisFormat;
    private User person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depo);

        init();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUrun();
            }
        });

    }

    public void init(){
        recyclerView = (RecyclerView) findViewById((R.id.depo));
        biten = (Button) findViewById((R.id.biten));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        PersonData();

        sinif = new ArrayList<String>();
        urun = new ArrayList<String>();

        listDepo = new ArrayList<DepoUrun>();
        statement = new ArrayList<MasaUrun>();
        searchUrun = new ArrayList<Urun>();

        Intent intent = getIntent();
        mekan = intent.getStringExtra("mekan");

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listDepo.clear();
        dataInput();

        sdf = new SimpleDateFormat("yyyy");
        currentYear = sdf.format(new Date());
        sdf = new SimpleDateFormat("MM");
        currentMounth = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd");
        currentDay = sdf.format(new Date());

        reference.child("dukkanlar").child(mekan).child("statementDepo").child(currentYear).child(currentMounth).child(currentDay).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot snapshot, String previousChildName) {
                if(!snapshot.getValue().toString().isEmpty() ){
                    MasaUrun s = snapshot.getValue(MasaUrun.class);
                    statement.add(s);
                }
            }

            @Override
            public void onChildChanged( DataSnapshot snapshot, String previousChildName) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);

                for (int i=0; i<statement.size(); i++
                ) {
                    if(statement.get(i).getIsimUrun().equals(s.getIsimUrun())){
                        statement.set(i,s);
                    }
                }
            }

            @Override
            public void onChildRemoved( DataSnapshot snapshot) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);
                statement.remove(s);

            }

            @Override
            public void onChildMoved( DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });

        biten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DepoActivity.this,DepoBitenActivity.class);
                startActivity(intent);
            }
        });

    }

    public void createAdapterSinif(){
        arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
        spinner_sinif.setAdapter(arrayListAdapterSinif);
    }

    public void showUrun(){
        inputUrun = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(inputUrun.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        inputUrun.setContentView(R.layout.dialog_urun_ekle);

        editTextNumber = (EditText) inputUrun.findViewById(R.id.editTextNumber);
        diaolog_urun_ekle = (Button) inputUrun.findViewById(R.id.diaolog_urun_ekle);
        diaolog_urun_iptal = (Button) inputUrun.findViewById(R.id.diaolog_urun_iptal);
        spinner_sinif = (Spinner) inputUrun.findViewById(R.id.spinner_sinif);
        spinner_urun = (Spinner) inputUrun.findViewById(R.id.spinner_urun);
        dialog_urun = (TextView) inputUrun.findViewById(R.id.dialog_urun);
        dialog_sinif = (TextView) inputUrun.findViewById(R.id.dialog_sinif);
        imageButton = (ImageButton) inputUrun.findViewById(R.id.imageButton);
        text_isim = (TextView) inputUrun.findViewById(R.id.text_isim);
        text_sinif = (TextView) inputUrun.findViewById(R.id.text_sinif);
        isimT = (TextView) inputUrun.findViewById(R.id.isim);
        sinifT = (TextView) inputUrun.findViewById(R.id.sinif);
        urunAra = (EditText) inputUrun.findViewById(R.id.urunAra);

        statusC = 0;

        if(statusC == 0){
            spinner_sinif.setVisibility(View.GONE);
            spinner_urun.setVisibility(View.GONE);
            dialog_urun.setVisibility(View.GONE);
            dialog_sinif.setVisibility(View.GONE);
            urunAra.setVisibility(View.VISIBLE);
            sinifT.setVisibility(View.VISIBLE);
            isimT.setVisibility(View.VISIBLE);
            text_sinif.setVisibility(View.VISIBLE);
            text_isim.setVisibility(View.VISIBLE);
        }
        else {
            urunAra.setVisibility(View.GONE);
            sinifT.setVisibility(View.GONE);
            isimT.setVisibility(View.GONE);
            text_sinif.setVisibility(View.GONE);
            text_isim.setVisibility(View.GONE);
            spinner_sinif.setVisibility(View.VISIBLE);
            spinner_urun.setVisibility(View.VISIBLE);
            dialog_urun.setVisibility(View.VISIBLE);
            dialog_sinif.setVisibility(View.VISIBLE);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusC == 1){
                    spinner_sinif.setVisibility(View.GONE);
                    spinner_urun.setVisibility(View.GONE);
                    dialog_urun.setVisibility(View.GONE);
                    dialog_sinif.setVisibility(View.GONE);
                    urunAra.setVisibility(View.VISIBLE);
                    sinifT.setVisibility(View.VISIBLE);
                    isimT.setVisibility(View.VISIBLE);
                    text_sinif.setVisibility(View.VISIBLE);
                    text_isim.setVisibility(View.VISIBLE);
                    statusC = 0;
                }
                else {
                    urunAra.setVisibility(View.GONE);
                    sinifT.setVisibility(View.GONE);
                    isimT.setVisibility(View.GONE);
                    text_sinif.setVisibility(View.GONE);
                    text_isim.setVisibility(View.GONE);
                    spinner_sinif.setVisibility(View.VISIBLE);
                    spinner_urun.setVisibility(View.VISIBLE);
                    dialog_urun.setVisibility(View.VISIBLE);
                    dialog_sinif.setVisibility(View.VISIBLE);
                    statusC = 1;
                }

            }
        });

        urunAra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                for (Urun sTemp:searchUrun
                ) {
                    if(sTemp.getIsim().contains(temp)){
                        sinifT.setText(sTemp.getSinif());
                        isimT.setText(sTemp.getIsim());
                        break;
                    }
                }
            }
        });

        sinif.clear();
        dataSinif();
        createAdapterSinif();

        spinner_sinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                urun.clear();
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urun);
                spinner_urun.setAdapter(arrayListAdapter);
                dataUrun(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        diaolog_urun_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextNumber.getText().toString().isEmpty()){
                    Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(statusC == 0 && (isimT.getText().toString().isEmpty() || sinifT.getText().toString().isEmpty())){
                    Toast.makeText(context,"Eksik Bilgi",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(statusC==0){
                    dialogIsim = isimT.getText().toString();
                    dialogSinif = sinifT.getText().toString();
                }
                else {
                    dialogIsim = spinner_urun.getSelectedItem().toString();
                    dialogSinif = spinner_sinif.getSelectedItem().toString();
                }
                reference.child("dukkanlar").child(mekan).child("depo").child(dialogIsim).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        du = snapshot.getValue(DepoUrun.class);
                        Integer adetEkle = Integer.parseInt(editTextNumber.getText().toString());
                        adetEkle+= Integer.parseInt(du.getAdet());
                        reference.child("dukkanlar").child(mekan).child("depo").child(dialogIsim).child("adet").setValue(Integer.toString(adetEkle));

                        Integer adet = Integer.parseInt(editTextNumber.getText().toString());
                        Double alis = Double.parseDouble(du.getAlis());
                        Double satis = Double.parseDouble(du.getSatis());
                        Double alisTutar = alis * adet;
                        Double satisTutar = satis * adet;
                        MasaUrun temp = new MasaUrun(du.getIsim(),alis,satis,alisTutar,satisTutar,adet);
                        int control = 0;
                        for (MasaUrun m:statement
                             ) {
                            if(m.getIsimUrun().equals(temp.getIsimUrun())){
                                reference.child("dukkanlar").child(mekan).child("statementDepo").child(currentYear).child(currentMounth).child(currentDay).child(temp.getIsimUrun()).child("adet").setValue(m.getAdet()+temp.getAdet());
                                reference.child("dukkanlar").child(mekan).child("statementDepo").child(currentYear).child(currentMounth).child(currentDay).child(temp.getIsimUrun()).child("alisTutar").setValue(m.getAlisTutar()+temp.getAlisTutar());
                                reference.child("dukkanlar").child(mekan).child("statementDepo").child(currentYear).child(currentMounth).child(currentDay).child(temp.getIsimUrun()).child("satisTutar").setValue(m.getSatisTutar()+temp.getSatisTutar());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            reference.child("dukkanlar").child(mekan).child("statementDepo").child(currentYear).child(currentMounth).child(currentDay).child(temp.getIsimUrun()).setValue(temp);
                        }
                        listDepo.clear();
                        dataInput();

                        sdfSiparisFormat = new SimpleDateFormat("mm-ss-SS");
                        timeSiparisFormat = sdfSiparisFormat.format(new Date());
                        sdfSiparisFormat = new SimpleDateFormat("yyyy");
                        year = sdfSiparisFormat.format(new Date());
                        sdfSiparisFormat = new SimpleDateFormat("MM");
                        mounth = sdfSiparisFormat.format(new Date());
                        sdfSiparisFormat = new SimpleDateFormat("dd");
                        day = sdfSiparisFormat.format(new Date());

                        sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                        String timeL = sdfSiparisFormat.format(new Date());

                        Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Depo",editTextNumber.getText().toString()+ " adet " + dialogIsim + " eklendi -- ", timeL);

                        reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+ "-" +user.getUid()).setValue(l);

                        Toast.makeText(getApplicationContext(), "Ürün Eklendi", Toast.LENGTH_SHORT).show();
                        inputUrun.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });

        diaolog_urun_iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUrun.dismiss();
            }
        });

        inputUrun.getWindow().setAttributes(params);
        inputUrun.show();
    }

    public void dataSinif(){
        reference.child("dukkanlar").child(mekan).child("urunlerSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinner_sinif.setAdapter(arrayListAdapterSinif);

                countUrun = 0;

                reference.child("dukkanlar").child(mekan).child("urunler").child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Urun ss = snapshot.getValue(Urun.class);
                        searchUrun.add(ss);
                        countUrun++;
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

                countSinif++;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                sinif.remove(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinner_sinif.setAdapter(arrayListAdapterSinif);
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
        reference.child("dukkanlar").child(mekan).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urun.add(s.getIsim());
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urun);
                spinner_urun.setAdapter(arrayListAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urun.remove(previousChildName);
                urun.add(s.getIsim());
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urun);
                spinner_urun.setAdapter(arrayListAdapter);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Urun s = snapshot.getValue(Urun.class);
                urun.remove(s.getIsim());
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urun);
                spinner_urun.setAdapter(arrayListAdapter);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void dataInput(){

        reference.child("dukkanlar").child(mekan).child("depo").orderByChild("adet").startAt("10").endAt("60").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DepoUrun s =snapshot.getValue(DepoUrun.class);
                Log.i("adet",s.getIsim()+" - "+s.getAdet());
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

        reference.child("dukkanlar").child(mekan).child("depo").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DepoUrun s = snapshot.getValue(DepoUrun.class);
                Integer adet = Integer.parseInt(s.getAdet());
                if(adet>0){
                    listDepo.add(s);
                    adp = new adapterDepo(context,listDepo,mekan);
                    recyclerView.setAdapter(adp);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DepoUrun s = snapshot.getValue(DepoUrun.class);
                    for (DepoUrun d:listDepo
                         ) {
                        if(s.getIsim().equals(d.getIsim())){
                            d.setAdet(s.getAdet());

                        }
                    }
                    adp = new adapterDepo(context,listDepo,mekan);
                    recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                DepoUrun s = snapshot.getValue(DepoUrun.class);
                listDepo.remove(s);
                adp = new adapterDepo(context,listDepo,mekan);
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

    public void PersonData(){
        reference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                person = snapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}