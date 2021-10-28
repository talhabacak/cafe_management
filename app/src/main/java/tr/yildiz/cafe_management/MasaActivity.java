package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.DateInterval;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MasaActivity extends AppCompatActivity {
    private EditText isimkisisel,editTextNumber,urunAra;
    private TextView toplamTutar,textViewMasa,acilis_saat,son_saat,text_sinif,text_isim,sinifT,isimT,dialog_urun,dialog_sinif,textCorrect;
    private ImageButton imageButton;
    private Button ac,kapat,buttonEvet,buttonHayir;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context = this;
    private Dialog inputUrun;
    private Button diaolog_urun_ekle,diaolog_urun_iptal;
    private Spinner spinner_sinif,spinner_urun;
    private String isim,sinif, currentYear,currentMounth,currentDay,currentHour,currentMinute, isimK = "",mekan;
    private String kirmizi,turuncu,timeSiparisFormat,timeSiparis,dialogIsim,dialogSinif,ode,sil,mod;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<Urun> urunler;
    private ArrayList<Integer> adetler;
    private ArrayList<Double> tutarlar, alisTutarlar;
    private ArrayList<String> siniflar, urun, days;
    private ArrayList<MasaUrun> masa,statement;
    private ArrayList<Urun> searchUrun;
    private MasaIsim CurrentMasa;
    private ArrayAdapter arrayListAdapterSinif,arrayListAdapter;
    private adapterMasaUrun adp;
    private SimpleDateFormat sdf,sdfSiparis;
    private FirebaseFunctions f;
    private Integer control = 0, controlDepo = 0, countSinif = 0, countUrun = 0,tempCount = 0, statusC = 0;
    private Dialog isCorrect;
    private User person;
 //   private Notification myNotification;
 //   private NotificationManager managerN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentMasa.getMode() != 0){
                    showDialogUrun();
                }
                else{
                    Toast.makeText(context,"Masa Kapalı",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void init(){
        control = 0;
        isimkisisel = (EditText) findViewById((R.id.isimkisisel));
        toplamTutar = (TextView) findViewById((R.id.toplamTutar));
        textViewMasa = (TextView) findViewById((R.id.textViewMasa));
        acilis_saat = (TextView) findViewById((R.id.acilis_saat));
        son_saat = (TextView) findViewById((R.id.son_saat));
        ac = (Button) findViewById((R.id.ac));
        kapat = (Button) findViewById((R.id.kapat));

        recyclerView = (RecyclerView) findViewById((R.id.masalar));
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        isim = intent.getStringExtra("isim");
        sinif = intent.getStringExtra("sinif");
        mekan = intent.getStringExtra("mekan");
        mod = intent.getStringExtra("mod");
        kirmizi = intent.getStringExtra("kirmizi");
        turuncu = intent.getStringExtra("turuncu");

        if(mod.equals("0")){
            ode = intent.getStringExtra("ode");
            sil = intent.getStringExtra("sil");

            if(ode.equals("0")){
                kapat.setClickable(false);
            }
        }

        textViewMasa.setText(isim);

        siniflar= new ArrayList<String>();
        days= new ArrayList<String>();
        urunler= new ArrayList<Urun>();
        urun= new ArrayList<String>();
        adetler= new ArrayList<Integer>();
        tutarlar= new ArrayList<Double>();
        alisTutarlar= new ArrayList<Double>();
        masa = new ArrayList<MasaUrun>();
        statement = new ArrayList<MasaUrun>();
        searchUrun = new ArrayList<Urun>();

        CurrentMasa = new MasaIsim();

        adp = new adapterMasaUrun(context,mekan,masa,isim,sinif,son_saat,ode,sil,mod);
        recyclerView.setAdapter(adp);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        PersonData();

        sdf = new SimpleDateFormat("yyyy");
        currentYear = sdf.format(new Date());
        sdf = new SimpleDateFormat("MM");
        currentMounth = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd");
        currentDay = sdf.format(new Date());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("isimKisisel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    isimK = snapshot.getValue().toString();
                    isimkisisel.setText(isimK);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"HATA - "+error.toString(),Toast.LENGTH_SHORT).show();

            }
        });

        isimkisisel.setText(isimK);

        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                if (s != null) {
                    CurrentMasa.setIsim(s.getIsim());
                    CurrentMasa.setMode(s.getMode());
                    CurrentMasa.setIsimKisisel(s.getIsimKisisel());
                    if(s.getMode()!=0){
                        CurrentMasa.setAc(s.getAc());
                        acilis_saat.setText(s.getAc());
                        if(!s.getSon().isEmpty()){
                            CurrentMasa.setSon(s.getSon());
                            CurrentMasa.setSonFormat(s.getSonFormat());
                            son_saat.setText(s.getSon());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"HATA - "+error.toString(),Toast.LENGTH_SHORT).show();

            }
        });

        reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).addChildEventListener(new ChildEventListener() {
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

        days.clear();

        sdf = new SimpleDateFormat("yyyy");
        currentYear = sdf.format(new Date());
        sdf = new SimpleDateFormat("MM");
        currentMounth = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd");
        currentDay = sdf.format(new Date());

        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
     /*           myNotification = new Notification.Builder(MasaActivity.this,"my")
                        .setContentTitle("CAFE UYARISI")
                        .setContentText("Masa "+isim)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true).build();
                managerN = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
             //   managerN.notify(1,myNotification);
*/
                sdf = new SimpleDateFormat("yyyy");
                currentYear = sdf.format(new Date());
                sdf = new SimpleDateFormat("MM");
                currentMounth = sdf.format(new Date());
                sdf = new SimpleDateFormat("dd");
                currentDay = sdf.format(new Date());
                sdf = new SimpleDateFormat("HH");
                currentHour = sdf.format(new Date());
                sdf = new SimpleDateFormat("mm");
                currentMinute = sdf.format(new Date());

                String s = currentHour+":"+currentMinute;

                if(CurrentMasa.getMode() == 0){
                    acilis_saat.setText(s);
                    reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("ac").setValue(s);
                    reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("mode").setValue(1);
                    Toast.makeText(context,"Masa Açıldı",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"Masa zaten açık",Toast.LENGTH_SHORT).show();
                }

            }
        });

        kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mod.equals("0")){
                    if(!ode.equals("0")){
                        if (CurrentMasa.getMode() != 0) {
                            showDialogEmin();
                        }
                        else{
                            Toast.makeText(context,"Masa zaten kapalı",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(context,"Yetkiniz Yok",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if (CurrentMasa.getMode() != 0) {
                        showDialogEmin();
                    }
                    else{
                        Toast.makeText(context,"Masa zaten kapalı",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        dataMasam();

        isimkisisel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("isimKisisel").setValue(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void showDialogUrun(){
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

        controlDepo = 0;
        siniflar.clear();
        dataSinif();

        spinner_sinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                urun.clear();
                arrayListAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, urun);
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
                controlDepo = 0;
                if(editTextNumber.getText().toString().isEmpty()){
                    Toast.makeText(context,"Sayı Giriniz",Toast.LENGTH_SHORT).show();
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
                        DepoUrun du = snapshot.getValue(DepoUrun.class);
                        Integer adetEkle = Integer.parseInt(du.getAdet());
                        adetEkle -= Integer.parseInt(editTextNumber.getText().toString());
                        if(du.getAdisyon().equals("1")){
                            if(adetEkle < 0){
                                Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_LONG).show();
                                controlDepo = 1;
                                return;
                            }
                        }

                        Integer controlM = 0;
                        for (MasaUrun mu:masa
                        ) {
                            if(mu.getIsimUrun().equals(dialogIsim)){
                                controlM = 1;
                                reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).child(dialogIsim).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        MasaUrun m = snapshot.getValue(MasaUrun.class);
                                        Integer adetInput = Integer.parseInt(editTextNumber.getText().toString());
                                        Integer temp =m.getAdet();
                                        adetInput += temp;
                                        Double alistutar = m.getAlis() * adetInput;
                                        Double satistutar = m.getSatis() * adetInput;
                                        reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).child(dialogIsim).child("adet").setValue(adetInput);
                                        reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).child(dialogIsim).child("alisTutar").setValue(alistutar);
                                        reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).child(dialogIsim).child("satisTutar").setValue(satistutar);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        if(controlM == 0){
                            Double alis = Double.parseDouble(du.getAlis()) * Integer.parseInt(editTextNumber.getText().toString());
                            Double satis = Double.parseDouble(du.getSatis()) * Integer.parseInt(editTextNumber.getText().toString());

                            MasaUrun mU = new MasaUrun(dialogIsim, Double.parseDouble(du.getAlis()), Double.parseDouble(du.getSatis()), alis, satis, Integer.parseInt(editTextNumber.getText().toString()));
                            reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).child(dialogIsim).setValue(mU);
                        }

                        if(du.getAdisyon().equals("1")){
                            reference.child("dukkanlar").child(mekan).child("depo").child(dialogIsim).child("adet").setValue(Integer.toString(adetEkle));
                        }

                        if(controlDepo == 0){
                            Date dateSon;
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
                            String currentTime = simpleDateFormat.format(new Date());
                            try {
                                dateSon = simpleDateFormat.parse(currentTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Toast.makeText(context,"HATA - "+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                            sdf = new SimpleDateFormat("HH");
                            currentHour = sdf.format(new Date());
                            sdf = new SimpleDateFormat("mm");
                            currentMinute = sdf.format(new Date());
                            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
                            currentTime = simpledateformat.format(new Date());

                            reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("mode").setValue(1);
                            reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("son").setValue(currentHour+":"+currentMinute);
                            reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("sonFormat").setValue(currentTime);
                            son_saat.setText(currentHour+":"+currentMinute);
                        }

                        sdfSiparis = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SS");
                        timeSiparisFormat = sdfSiparis.format(new Date());
                        sdf = new SimpleDateFormat("HH:mm");
                        timeSiparis = sdf.format(new Date());
                        Siparis siparis = new Siparis(timeSiparis,timeSiparisFormat,dialogIsim,editTextNumber.getText().toString(),sinif,isim,"0");
                        reference.child("dukkanlar").child(mekan).child("order").child("notCheck").child(timeSiparisFormat).setValue(siparis);

                        SimpleDateFormat sdfSiparisFormat = new SimpleDateFormat("mm-ss-SS");
                        timeSiparisFormat = sdfSiparisFormat.format(new Date());
                        sdfSiparisFormat = new SimpleDateFormat("yyyy");
                        String year = sdfSiparisFormat.format(new Date());
                        sdfSiparisFormat = new SimpleDateFormat("MM");
                        String mounth = sdfSiparisFormat.format(new Date());
                        sdfSiparisFormat = new SimpleDateFormat("dd");
                        String day = sdfSiparisFormat.format(new Date());

                        sdfSiparisFormat = new SimpleDateFormat("mm-ss");
                        String timeL = sdfSiparisFormat.format(new Date());

                        Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Masa",sinif+ " " +isim + " - " + editTextNumber.getText().toString()+ " adet " + dialogIsim + " eklendi ", timeL);

                        reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+user.getUid()).setValue(l);


                        Toast.makeText(getApplicationContext(), "Ürün Eklendi", Toast.LENGTH_SHORT).show();
                        inputUrun.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context,"HATA - "+error.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

                inputUrun.dismiss();
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
                siniflar.add(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,siniflar);
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
                siniflar.remove(s);
                arrayListAdapterSinif = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,siniflar);
                spinner_sinif.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"HATA - "+error.toString(),Toast.LENGTH_SHORT).show();

            }
       });
    }

    public void dataUrun(String sinif){
        reference.child("dukkanlar").child(mekan).child("urunler").child(sinif).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Urun s = snapshot.getValue(Urun.class);
                urun.add(s.getIsim());
                arrayListAdapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, urun);
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
                urun.remove(s);
                arrayListAdapter = new ArrayAdapter(context,R.layout.support_simple_spinner_dropdown_item,urun);
                spinner_urun.setAdapter(arrayListAdapter);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"HATA - "+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dataMasam(){
        reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaUrun m = snapshot.getValue(MasaUrun.class);
                    masa.add(m);
                    Double toplam = 0.0;
                    for (MasaUrun d:masa
                    ) {
                        toplam += d.getSatisTutar().doubleValue();
                    }
                    toplamTutar.setText(Double.toString(toplam));
                    adp = new adapterMasaUrun(context,mekan,masa,isim,sinif,son_saat,ode,sil,mod);
                    recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);
                MasaUrun temp;
                for (MasaUrun d:masa
                ) {
                    if(s.getIsimUrun().equals(d.getIsimUrun())){
                        d.setAdet(s.getAdet());
                        d.setAlisTutar(s.getAlisTutar().doubleValue());
                        d.setSatisTutar(s.getSatisTutar().doubleValue());
                    }
                }

                Double toplam;
                toplam = 0.0;
                for (MasaUrun d:masa
                ) {
                    toplam += d.getSatisTutar();
                }

                if(s.getAdet() == 0){
                    reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).child(s.getIsimUrun()).removeValue();
                }
                toplamTutar.setText(Double.toString(toplam));
                adp = new adapterMasaUrun(context,mekan,masa,isim,sinif,son_saat,ode,sil,mod);
                recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);
                masa.remove(s);
                Double toplam;
                toplam = 0.0;
                for (MasaUrun d:masa
                ) {
                    toplam += d.getSatisTutar();
                }
                toplamTutar.setText(Double.toString(toplam));
                adp = new adapterMasaUrun(context,mekan,masa,isim,sinif,son_saat,ode,sil,mod);
                recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,"HATA - "+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    public  void close(String temp){
        masa.clear();
        acilis_saat.setText("");
        son_saat.setText("");
        toplamTutar.setText("0.00");
        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("isimKisisel").setValue("");
        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("mode").setValue(0);
        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("ac").setValue("");
        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("son").setValue("");
        reference.child("dukkanlar").child(mekan).child("masalar").child(sinif).child(isim).child("sonFormat").setValue("");
        reference.child("dukkanlar").child(mekan).child("masa").child(sinif).child(isim).removeValue();
        CurrentMasa.setMode(0);

        SimpleDateFormat sdfSiparisFormat = new SimpleDateFormat("mm-ss-SS");
        timeSiparisFormat = sdfSiparisFormat.format(new Date());
        sdfSiparisFormat = new SimpleDateFormat("yyyy");
        String year = sdfSiparisFormat.format(new Date());
        sdfSiparisFormat = new SimpleDateFormat("MM");
        String mounth = sdfSiparisFormat.format(new Date());
        sdfSiparisFormat = new SimpleDateFormat("dd");
        String day = sdfSiparisFormat.format(new Date());

        sdfSiparisFormat = new SimpleDateFormat("mm-ss");
        String timeL = sdfSiparisFormat.format(new Date());

        Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Masa",sinif+ " " +isim + " - masa kapatıldı - " + temp, timeL);

        reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+user.getUid()).setValue(l);

        Toast.makeText(context, "Masa Kapatıldı", Toast.LENGTH_SHORT).show();
    }

    public void showDialogEmin(){
        isCorrect = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(isCorrect.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        isCorrect.setContentView(R.layout.dialog_emin_misin);

        textCorrect = (TextView) isCorrect.findViewById(R.id.textCorrect);
        buttonEvet = (Button) isCorrect.findViewById(R.id.buttonEvet);
        buttonHayir = (Button) isCorrect.findViewById(R.id.buttonHayir);

        textCorrect.setText("Masayı kapatmak istediğinize emin misiniz?");

        buttonEvet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdf = new SimpleDateFormat("yyyy");
                currentYear = sdf.format(new Date());
                sdf = new SimpleDateFormat("MM");
                currentMounth = sdf.format(new Date());
                sdf = new SimpleDateFormat("dd");
                currentDay = sdf.format(new Date());

                String temp = "";
                for (MasaUrun mU:masa
                ) {
                    temp += mU.getAdet() + " adet " + mU.getIsimUrun() + "\n";
                    int control2 = 0;
                    for (MasaUrun sU:statement
                    ) {
                        if(mU.getIsimUrun().equals(sU.getIsimUrun())){
                            control2 = 1;
                            reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(mU.getIsimUrun()).child("adet").setValue(sU.getAdet()+mU.getAdet());
                            reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(mU.getIsimUrun()).child("alisTutar").setValue(sU.getAlisTutar()+mU.getAlisTutar());
                            reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(mU.getIsimUrun()).child("satisTutar").setValue(sU.getSatisTutar()+mU.getSatisTutar());
                        }
                    }
                    if(control2 == 0){
                        reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(mU.getIsimUrun()).setValue(mU);
                    }
                }
                close(temp);

                isCorrect.dismiss();
            }
        });

        buttonHayir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCorrect.dismiss();
            }
        });

        isCorrect.getWindow().setAttributes(params);
        isCorrect.show();
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