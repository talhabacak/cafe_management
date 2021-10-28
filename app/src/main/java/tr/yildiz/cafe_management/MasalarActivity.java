package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MasalarActivity extends AppCompatActivity {
    private Spinner spinnerSinif;
    private TextView masaTasi,masaBirlestir;
    private ArrayAdapter arrayListAdapterSinif;
    private ArrayList<String> sinif,masa;
    private ArrayList<MasaIsim> masaIsim;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String mod, mekan, currentYear,currentMounth,currentDay,selectedSinif,ode,sil;
    private User users;
    private ArrayList<Integer> uyari;
    private GridView masalar;
    private Context context = this;
    private adapterMasa masaArrayAdapter;
    private SimpleDateFormat sdf;
    private Integer control=0, masaTasiControl = 0, masaBirlestirControl = 0, selectedPosition = -1;
    private MasaIsim selected;
    private ArrayList<MasaUrun> selectedUrun, birlestirUrun;
    private ArrayList<Integer> controlBirlestir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masalar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();
    }

    public void init(){
        control = 0;
        masaTasiControl = 0;
        masaBirlestirControl = 0;

        spinnerSinif = (Spinner) findViewById(R.id.spinnerSinif);
        masaTasi = (TextView) findViewById(R.id.masaTasi);
        masaBirlestir = (TextView) findViewById(R.id.masaBirlestir);

        masa = new ArrayList<String>();
        sinif = new ArrayList<String>();
        masaIsim = new ArrayList<MasaIsim>();
        selectedUrun = new ArrayList<MasaUrun>();
        birlestirUrun = new ArrayList<MasaUrun>();
        uyari = new ArrayList<Integer>();
        controlBirlestir = new ArrayList<Integer>();
        users = new User();
        selected = new MasaIsim();

        masalar = (GridView) findViewById(R.id.masalar);
        masaArrayAdapter = new adapterMasa(masaIsim,context);
        masalar.setAdapter(masaArrayAdapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = getIntent();
        mekan = intent.getStringExtra("mekan");
        ode = intent.getStringExtra("ode");
        sil = intent.getStringExtra("sil");
        mod = intent.getStringExtra("mod");
        dataUyari();

        dataSinif();

        sdf = new SimpleDateFormat("yyyy");
        currentYear = sdf.format(new Date());
        sdf = new SimpleDateFormat("MM");
        currentMounth = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd");
        currentDay = sdf.format(new Date());

        spinnerSinif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                masaIsim.clear();
                String s = parent.getSelectedItem().toString();
                masa.clear();
                dataMasa(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        masaTasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(masaBirlestirControl == 0){
                    if(masaTasiControl == 0){
                        masaTasiControl = 1;
                        masaTasi.setText("Masa Taşı : Açık");
                        Toast.makeText(context,"Masa Taşıma Açıldı",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        masaTasiControl = 0;
                        masaTasi.setText("Masa Taşı : Kapalı");
                        selectedPosition = -1;
                        Toast.makeText(context,"Masa Taşıma Kaptıldı",Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(context,"Masa Birleştirme Açıkken Açılamaz",Toast.LENGTH_SHORT).show();
                }
            }
        });

        masaBirlestir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(masaTasiControl == 0){
                    if(masaBirlestirControl == 0){
                        masaBirlestirControl = 1;
                        masaBirlestir.setText("Masa Birleştir : Açık");
                        Toast.makeText(context,"Masa Birleştirme Açıldı",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        masaBirlestirControl = 0;
                        masaBirlestir.setText("Masa Birleştir : Kapalı");
                        selectedPosition = -1;
                        Toast.makeText(context,"Masa Birleştirme Kaptıldı",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context,"Masa Taşıma Açıkken Açılamaz",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void dataSinif(){
        reference.child("dukkanlar").child(mekan).child("masalarSinif").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSinif.setAdapter(arrayListAdapterSinif);
             }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getValue().toString();
                sinif.remove(previousChildName);
                sinif.add(s);
                arrayListAdapterSinif = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSinif.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                sinif.remove(s);
                arrayListAdapterSinif = new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,sinif);
                spinnerSinif.setAdapter(arrayListAdapterSinif);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dataMasa(String sinif1){
         reference.child("dukkanlar").child(mekan).child("masalar").child(sinif1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot,String previousChildName) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                long time;
                if(s.getSonFormat() != null){
                    if (!s.getSonFormat().isEmpty()){
                        try {
                            time = betweenTime(s.getSonFormat());
                            Integer kirmizi;
                            Integer turuncu;
                            if(uyari.get(0)> uyari.get(1)){
                                kirmizi = uyari.get(0);
                                turuncu = uyari.get(1);
                            }
                            else {
                                kirmizi = uyari.get(1);
                                turuncu = uyari.get(0);
                            }
                            if(time > kirmizi){
                                reference.child("dukkanlar").child(mekan).child("masalar").child(sinif1).child(s.getIsim()).child("mode").setValue(3);
                                s.setMode(3);
                            }else if(time > turuncu){
                                reference.child("dukkanlar").child(mekan).child("masalar").child(sinif1).child(s.getIsim()).child("mode").setValue(2);
                                s.setMode(2);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(s.getIsimKisisel() != null){
                    if(!s.getIsimKisisel().isEmpty()){
                        Log.i("masaKisiselIsim",s.getIsimKisisel());
                    }
                }
                Log.i("masaIsim",s.getIsim());
                Log.i("masaAc",s.getAc());
                Log.i("masaSon",s.getSon());
                Log.i("masaSonFormat",s.getSonFormat());
                Log.i("masaMode",s.getMode().toString());
                Log.i("masaKisiselIsim",s.getIsimKisisel());

                masa.add(s.getIsim());
                masaIsim.add(s);
                masaArrayAdapter = new adapterMasa(masaIsim,context);
                masalar.setAdapter(masaArrayAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                int i = 0;
                int y = 0;
                for (MasaIsim m:masaIsim
                ) {
                    if(s.getIsim().equals(m.getIsim())){
                        y = i;
                        m.setMode(s.getMode());
                        if(s.getIsimKisisel() != null){
                            if(!s.getIsimKisisel().isEmpty()){
                                m.setIsimKisisel(s.getIsimKisisel());
                            }
                            else {
                                m.setIsimKisisel("");
                            }
                        }
                        else{
                            m.setIsimKisisel("");
                        }
                        m.setSon(s.getSon());
                        m.setSonFormat(s.getSonFormat());
                        m.setAc(s.getAc());
                    }
                    i++;
                }
                masaArrayAdapter = new adapterMasa(masaIsim,context);
                masalar.setAdapter(masaArrayAdapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MasaIsim s = snapshot.getValue(MasaIsim.class);
                masa.remove(s.getIsim());
                masaIsim.add(s);
                masaArrayAdapter = new adapterMasa(masaIsim,context);
                masalar.setAdapter(masaArrayAdapter);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Hata\n"+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        masalar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(masaTasiControl == 1){
                    selected = new MasaIsim();
                    selected.setIsimKisisel(masaIsim.get(position).getIsimKisisel());
                    selected.setIsim(masaIsim.get(position).getIsim());
                    selected.setAc(masaIsim.get(position).getAc());
                    selected.setMode(masaIsim.get(position).getMode());
                    selected.setSon(masaIsim.get(position).getSon());
                    selected.setSonFormat(masaIsim.get(position).getSonFormat());
                    masaIsim.get(position).setMode(4);
                    selectedPosition = position;
                    selectedSinif = spinnerSinif.getSelectedItem().toString();
                    selectedUrun.clear();
                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            MasaUrun s = snapshot.getValue(MasaUrun.class);
                            selectedUrun.add(s);
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
                    masaTasiControl = 2;
                    Toast.makeText(context,"Seçildi",Toast.LENGTH_SHORT).show();
                }
                else if(masaTasiControl == 2){
                    if (selectedPosition == -1){
                        Toast.makeText(context,"Masa Seçiniz",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(masaIsim.get(position).getMode() != 0){
                        Toast.makeText(context,"Taşımaya Çalıştığınız Masa Dolu",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (MasaUrun temp:selectedUrun
                    ) {
                        reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).child(temp.getIsimUrun()).setValue(temp);
                    }
                    selected.setIsim(masaIsim.get(position).getIsim());
                    if (selected.getIsimKisisel() == null){
                        selected.setIsimKisisel("");
                    }

                    reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("ac").setValue(selected.getAc());
                    reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("isimKisisel").setValue(selected.getIsimKisisel());
                    reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("mode").setValue(selected.getMode());
                    reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("son").setValue(selected.getSon());
                    reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("sonFormat").setValue(selected.getSonFormat());
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("ac").setValue("");
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("isimKisisel").setValue("");
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("mode").setValue(0);
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("son").setValue("");
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("sonFormat").setValue("");

                    reference.child("dukkanlar").child(mekan).child("masa").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).removeValue();
                    masaTasiControl = 0;
                    selectedPosition = -1;
                    masaTasi.setText("Masa Taşı : Kapalı");

                    Toast.makeText(context,"Masa Taşındı",Toast.LENGTH_SHORT).show();
                }

                else if (masaBirlestirControl == 1){
                    selected = new MasaIsim();
                    selected.setIsimKisisel(masaIsim.get(position).getIsimKisisel());
                    selected.setIsim(masaIsim.get(position).getIsim());
                    selected.setAc(masaIsim.get(position).getAc());
                    selected.setMode(masaIsim.get(position).getMode());
                    selected.setSon(masaIsim.get(position).getSon());
                    selected.setSonFormat(masaIsim.get(position).getSonFormat());
                    masaIsim.get(position).setMode(4);
                    selectedPosition = position;
                    selectedSinif = spinnerSinif.getSelectedItem().toString();
                    selectedUrun.clear();
                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            MasaUrun s = snapshot.getValue(MasaUrun.class);
                            selectedUrun.add(s);
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
                    masaBirlestirControl = 2;
                    Toast.makeText(context,"Seçildi",Toast.LENGTH_SHORT).show();
                }
                else if (masaBirlestirControl == 2){
                    if (selectedPosition == -1) {
                        Toast.makeText(context, "Masa Seçiniz", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(masaIsim.get(position).getMode() == 0){
                        Toast.makeText(context,"Birleştirmeye Çalıştığınız Masa Boş",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    controlBirlestir.clear();
                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            MasaUrun s = snapshot.getValue(MasaUrun.class);
                            int i = 0;
                            for (MasaUrun sU:selectedUrun
                            ) {
                                if(s.getIsimUrun().equals(sU.getIsimUrun())){
                                    controlBirlestir.add(i);
                                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).child(s.getIsimUrun()).child("adet").setValue(s.getAdet()+sU.getAdet());
                                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).child(s.getIsimUrun()).child("alisTutar").setValue(s.getAlisTutar()+sU.getAlisTutar());
                                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).child(s.getIsimUrun()).child("satisTutar").setValue(s.getSatisTutar()+sU.getSatisTutar());
                                }
                                i++;
                            }
                            i=0;
                            for (MasaUrun m : selectedUrun
                            ) {
                                int j = 0;
                                for (Integer k:controlBirlestir
                                ) {
                                    if(k == i) {
                                        j = 1;
                                    }

                                }
                                if (j == 0){
                                    reference.child("dukkanlar").child(mekan).child("masa").child(spinnerSinif.getSelectedItem().toString()).child(masa.get(position)).child(m.getIsimUrun()).setValue(m);
                                }
                                i++;
                            }
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

                    selected.setIsim(masaIsim.get(position).getIsim());
                    if (selected.getIsim() == null){
                        selected.setIsimKisisel("");
                    }

                    try {
                        long timeS = betweenTime(masaIsim.get(position).getSonFormat());
                        long timeB = betweenTime(masaIsim.get(selectedPosition).getSonFormat());
                          if(timeS < timeB){
                             reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("sonFormat").setValue(masaIsim.get(selectedPosition).getSonFormat());
                            reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("son").setValue(masaIsim.get(selectedPosition).getSon());
                            reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("mode").setValue(selected.getMode());
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        long timeAcS = betweenTime2(masaIsim.get(position).getAc());
                        long timeAcB = betweenTime2(masaIsim.get(selectedPosition).getAc());
                        if(timeAcS < timeAcB){
                             reference.child("dukkanlar").child(mekan).child("masalar").child(spinnerSinif.getSelectedItem().toString()).child(masaIsim.get(position).getIsim()).child("ac").setValue(masaIsim.get(selectedPosition).getAc());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("ac").setValue("");
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("isimKisisel").setValue("");
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("mode").setValue(0);
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("son").setValue("");
                    reference.child("dukkanlar").child(mekan).child("masalar").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).child("sonFormat").setValue("");

                    reference.child("dukkanlar").child(mekan).child("masa").child(selectedSinif).child(masaIsim.get(selectedPosition).getIsim()).removeValue();
                    masaBirlestirControl = 0;
                    selectedPosition = -1;

                    masaBirlestir.setText("Masa Birleştir : Kapalı");
                    Toast.makeText(context,"Masalar Birleştirildi",Toast.LENGTH_SHORT).show();
                }

                else if(masaBirlestirControl == 0 && masaBirlestirControl == 0){
                    if (control.equals(0)){
                        reference.child("dukkanlar").child(mekan).child("statementDay").child(currentYear+"-"+currentMounth+"-"+currentDay).setValue(currentYear+"-"+currentMounth+"-"+currentDay);
                        reference.child("dukkanlar").child(mekan).child("statement").child(currentYear+"/"+currentMounth+"/"+currentDay).setValue("");
                    }
                    Integer kirmizi;
                    Integer turuncu;
                    if(uyari.get(0)> uyari.get(1)){
                        kirmizi = uyari.get(0);
                        turuncu = uyari.get(1);
                    }
                    else {
                        kirmizi = uyari.get(1);
                        turuncu = uyari.get(0);
                    }

                    Intent intent = new Intent(MasalarActivity.this,MasaActivity.class);
                    intent.putExtra("sinif",spinnerSinif.getSelectedItem().toString());
                    intent.putExtra("isim",masa.get(position));
                    intent.putExtra("mekan",mekan);
                    intent.putExtra("kirmizi",kirmizi.toString());
                    intent.putExtra("turuncu",turuncu.toString());
                    intent.putExtra("mod",mod);
                    if(mod.equals("0")){
                        intent.putExtra("ode",ode);
                        intent.putExtra("sil",sil);
                    }
                    startActivity(intent);
                }
                masaArrayAdapter = new adapterMasa(masaIsim,context);
                masalar.setAdapter(masaArrayAdapter);
            }
        });

    }

    public long betweenTime (String time1) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");

        String time2 = simpleDateFormat.format(new Date());

        // Parsing the Time Period
        Date date1 = simpleDateFormat.parse(time1);
        Date date2 = simpleDateFormat.parse(time2);

        // Calculating the difference in milliseconds
        long differenceInMilliSeconds = Math.abs(date2.getTime() - date1.getTime());
        long differenceInMinutes = (differenceInMilliSeconds / (60 * 1000));

        return differenceInMinutes;
    }

    public long betweenTime2 (String time1) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String time2 = simpleDateFormat.format(new Date());

        // Parsing the Time Period
        Date date1 = simpleDateFormat.parse(time1);
        Date date2 = simpleDateFormat.parse(time2);

        // Calculating the difference in milliseconds
        long differenceInMilliSeconds = Math.abs(date2.getTime() - date1.getTime());
        long differenceInMinutes = (differenceInMilliSeconds / (60 * 1000)) % 60;

        return differenceInMinutes;
    }

    public void dataUyari(){
        reference.child("dukkanlar").child(mekan).child("uyarilar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                uyari.add(snapshot.getValue(Integer.class));
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

    public void dataMod(){
        reference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                if(u.getMod().equals("0")){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}