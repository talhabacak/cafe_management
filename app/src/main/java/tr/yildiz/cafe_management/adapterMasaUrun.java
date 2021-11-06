package tr.yildiz.cafe_management;


import android.app.Dialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class adapterMasaUrun extends RecyclerView.Adapter<adapterMasaUrun.tanim> {
    Context context;
    private Dialog EkleAdet,SilAdet,OdemeAdet;
    TextView sonSaat;
    List<MasaUrun> list;
    String isimMasa,isimSinif,mekan,sil,ode,mod;

    public adapterMasaUrun(Context context,String mekan, List<MasaUrun>list,String isimMasa,String isimSinif, TextView sonSaat, String ode, String sil, String mod) {
        this.context = context;
        this.mekan = mekan;
        this.list = list;
        this.isimMasa = isimMasa;
        this.sonSaat = sonSaat;
        this.isimSinif = isimSinif;
        this.sil = sil;
        this.ode = ode;
        this.mod = mod;
    }

    @NonNull
    @Override
    public tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_masa,parent,false);
        return new tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tanim holder, int position) {
        holder.isim.setText(list.get(position).getIsimUrun());
        holder.adet.setText(Integer.toString(list.get(position).getAdet()));
        holder.tutar.setText(Double.toString(list.get(position).getSatisTutar()));
        holder.imageButtonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.Plus();
            }
        });
        if(holder.status == 0){
            holder.sil.setVisibility(View.GONE);
            holder.ikram.setVisibility(View.GONE);
            holder.odeme.setVisibility(View.GONE);
        }
        else {
            holder.sil.setVisibility(View.VISIBLE);
            holder.ikram.setVisibility(View.VISIBLE);
            holder.odeme.setVisibility(View.VISIBLE);

            if(mod.equals("0")){
                if(ode.equals("0")){
                    holder.ikram.setClickable(false);
                    holder.odeme.setClickable(false);
                }
                if(sil.equals("0")){
                    holder.sil.setClickable(false);
                }
            }
        }
        holder.imageButtonOdemeAyrinti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.status == 0){
                    holder.sil.setVisibility(View.VISIBLE);
                    holder.ikram.setVisibility(View.VISIBLE);
                    holder.odeme.setVisibility(View.VISIBLE);

                    if(mod.equals("0")){
                        if(ode.equals("0")){
                            holder.ikram.setClickable(false);
                            holder.odeme.setClickable(false);
                        }
                        if(sil.equals("0")){
                            holder.sil.setClickable(false);
                        }
                    }

                    holder.status = 1;
                }
                else {
                    holder.sil.setVisibility(View.GONE);
                    holder.ikram.setVisibility(View.GONE);
                    holder.odeme.setVisibility(View.GONE);
                    holder.status = 0;
                }
            }
        });
        Integer temp = position;
        holder.pos = temp;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public class tanim extends  RecyclerView.ViewHolder{
        private TextView isim,adet,tutar;
        private ImageButton imageButtonPlus,imageButtonOdemeAyrinti;
        private Button iptal, tamam;
        private Button ikram,sil,odeme;
        private EditText editInputAdet;
        private FirebaseDatabase database;
        private DatabaseReference reference;
        private FirebaseAuth auth;
        private FirebaseUser user;
        private Integer adetInput;
        private String currentYear,currentMounth,currentDay,currentHour,currentMinute;
        private SimpleDateFormat sdf,sdfSiparis,sdfSiparisFormat;
        private ArrayList<MasaUrun> statement;
        Integer status, controlDepo = 0;
        String timeSiparisFormat,timeSiparis;
        Integer pos;
        private User person;

        public tanim(View itemView){
            super(itemView);
            isim =(TextView) itemView.findViewById(R.id.isim);
            adet =(TextView) itemView.findViewById(R.id.adet);
            tutar =(TextView) itemView.findViewById(R.id.tutar);
            imageButtonPlus =(ImageButton) itemView.findViewById(R.id.imageButtonPlus);
            imageButtonOdemeAyrinti =(ImageButton) itemView.findViewById(R.id.imageButtonOdemeAyrinti);
            ikram =(Button) itemView.findViewById(R.id.ikram);
            sil =(Button) itemView.findViewById(R.id.sil);
            odeme =(Button) itemView.findViewById(R.id.odeme);
            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            statement = new ArrayList<MasaUrun>();

            status = 0;
            controlDepo = 0;

            PersonData();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            currentYear = sdf.format(new Date());
            sdf = new SimpleDateFormat("MM");
            currentMounth = sdf.format(new Date());
            sdf = new SimpleDateFormat("dd");
            currentDay = sdf.format(new Date());
            sdf = new SimpleDateFormat("HH");
            currentHour = sdf.format(new Date());
            sdf = new SimpleDateFormat("mm");
            currentMinute = sdf.format(new Date());

            statement.clear();
            reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded( DataSnapshot snapshot, String previousChildName) {
                    if(!snapshot.getValue().toString().isEmpty() ){
                        MasaUrun s = snapshot.getValue(MasaUrun.class);
                        statement.add(s);
                        Log.i("statementtt",s.getIsimUrun());
                    }
                }

                @Override
                public void onChildChanged( DataSnapshot snapshot, String previousChildName) {

                }

                @Override
                public void onChildRemoved( DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved( DataSnapshot snapshot, String previousChildName) {

                }

                @Override
                public void onCancelled( DatabaseError error) {

                }
            });

            ikram.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showIkramUrun();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Hata, İşlem gerçekleşmedi", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            sil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showSilUrun();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Hata, İşlem gerçekleşmedi", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            odeme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showOdemeUrun();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Hata, İşlem gerçekleşmedi", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

        public void showIkramUrun(){
            EkleAdet = new Dialog(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(EkleAdet.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            EkleAdet.setContentView(R.layout.dialog_adet);

            iptal = (Button) EkleAdet.findViewById(R.id.iptal);
            tamam = (Button) EkleAdet.findViewById(R.id.tamam);
            editInputAdet = (EditText) EkleAdet.findViewById(R.id.editInputAdet);

            tamam.setText("İkram Et");

            tamam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editInputAdet.getText().toString().isEmpty()){
                        Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Integer.parseInt(editInputAdet.getText().toString())> Integer.parseInt(adet.getText().toString())){
                        Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_SHORT).show();
                        return;
                    }
                        reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            MasaUrun m = snapshot.getValue(MasaUrun.class);
                            adetInput = Integer.parseInt(editInputAdet.getText().toString());
                            Double odemeAlis = adetInput * m.getAlis();


                            sdf = new SimpleDateFormat("yyyy");
                            currentYear = sdf.format(new Date());
                            sdf = new SimpleDateFormat("MM");
                            currentMounth = sdf.format(new Date());
                            sdf = new SimpleDateFormat("dd");
                            currentDay = sdf.format(new Date());

                            int control2 = 0;
                            for (MasaUrun sU : statement
                            ) {
                                Log.i("statement21", sU.getIsimUrun());
                                if (m.getIsimUrun().equals(sU.getIsimUrun())) {
                                    Log.i("statement2", sU.getIsimUrun());
                                    control2 = 1;
                                    Log.i("control2", "1");
                                    reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("adet").setValue(sU.getAdet() + adetInput);
                                    reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("alisTutar").setValue(sU.getAlisTutar() + m.getAlis() * adetInput);
                                }
                            }
                            if (control2 == 0) {
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).setValue(m);
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("adet").setValue(adetInput);
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("alisTutar").setValue(adetInput * m.getAlis());
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("satisTutar").setValue(0);
                                Log.i("control2", "0");
                            }

                            Double alistutar = m.getAlisTutar();
                            Double satistutar = m.getAlisTutar();
                            adetInput = m.getAdet() - adetInput;
                            alistutar = m.getAlis() * adetInput;
                            satistutar = m.getSatis() * adetInput;
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("adet").setValue(adetInput);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("alisTutar").setValue(alistutar);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("satisTutar").setValue(satistutar);

                            if(adetInput == 0){
                                removeAt(pos);
                            }

                            sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss-SS");
                            timeSiparisFormat = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("yyyy");
                            String year = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("MM");
                            String mounth = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("dd");
                            String day = sdfSiparisFormat.format(new Date());

                            sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                            String timeL = sdfSiparisFormat.format(new Date());

                            Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Masa", isimSinif+ " " +isimMasa + " - " + editInputAdet.getText().toString() + " adet  "+ isim.getText().toString() + " ikram edildi", timeL);

                            reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+user.getUid()).setValue(l);

                            Toast.makeText(context, "Ürün İkram edildi", Toast.LENGTH_SHORT).show();
                            EkleAdet.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(context,"Ürün İkram edildi",Toast.LENGTH_SHORT).show();
                    EkleAdet.dismiss();
                }
            });

            iptal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EkleAdet.dismiss();
                }
            });

            EkleAdet.getWindow().setAttributes(params);
            EkleAdet.show();
        }

        public void showSilUrun(){
            SilAdet = new Dialog(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(SilAdet.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            SilAdet.setContentView(R.layout.dialog_adet);

            iptal = (Button) SilAdet.findViewById(R.id.iptal);
            tamam = (Button) SilAdet.findViewById(R.id.tamam);
            editInputAdet = (EditText) SilAdet.findViewById(R.id.editInputAdet);

            tamam.setText("Sil");

            tamam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editInputAdet.getText().toString().isEmpty()){
                        Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Integer.parseInt(editInputAdet.getText().toString())> Integer.parseInt(adet.getText().toString())){
                        Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reference.child("dukkanlar").child(mekan).child("depo").child(isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DepoUrun du = snapshot.getValue(DepoUrun.class);
                            Integer adetEkle = Integer.parseInt(du.getAdet());
                            adetInput = Integer.parseInt(editInputAdet.getText().toString());
                            Log.i("adet", String.valueOf(adetInput));
                            adetEkle += adetInput;
                            if(du.getAdisyon().equals("1")){
                                reference.child("dukkanlar").child(mekan).child("depo").child(isim.getText().toString()).child("adet").setValue(Integer.toString(adetEkle));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            MasaUrun m = snapshot.getValue(MasaUrun.class);
                            adetInput = Integer.parseInt(editInputAdet.getText().toString());
                            adetInput = m.getAdet() - adetInput;
                            Double alistutar = m.getAlisTutar();
                            Double satistutar = m.getAlisTutar();
                            alistutar = m.getAlis() * adetInput;
                            satistutar = m.getSatis() * adetInput;

                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("adet").setValue(adetInput);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("alisTutar").setValue(alistutar);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("satisTutar").setValue(satistutar);

                            if(adetInput == 0){
                                removeAt(pos);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    sdfSiparisFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SS");
                    timeSiparisFormat = sdfSiparisFormat.format(new Date());
                    sdfSiparis = new SimpleDateFormat("HH:mm");
                    timeSiparis = sdfSiparis.format(new Date());
                    Siparis siparis = new Siparis(timeSiparis,timeSiparisFormat,isim.getText().toString(),editInputAdet.getText().toString(),isimSinif,isimMasa,"1");
                    reference.child("dukkanlar").child(mekan).child("order").child("notCheck").child(timeSiparisFormat).setValue(siparis);

                    sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss-SS");
                    timeSiparisFormat = sdfSiparisFormat.format(new Date());
                    sdfSiparisFormat = new SimpleDateFormat("yyyy");
                    String year = sdfSiparisFormat.format(new Date());
                    sdfSiparisFormat = new SimpleDateFormat("MM");
                    String mounth = sdfSiparisFormat.format(new Date());
                    sdfSiparisFormat = new SimpleDateFormat("dd");
                    String day = sdfSiparisFormat.format(new Date());

                    sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                    String timeL = sdfSiparisFormat.format(new Date());

                    Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Masa", isimSinif+ " " +isimMasa + " - " + editInputAdet.getText().toString() + " adet "+ isim.getText().toString() + " ürün silindi", timeL);

                    reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+user.getUid()).setValue(l);

                    Toast.makeText(context,"Ürün Silindi",Toast.LENGTH_SHORT).show();
                    SilAdet.dismiss();
                }
            });

            iptal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SilAdet.dismiss();
                }
            });

            SilAdet.getWindow().setAttributes(params);
            SilAdet.show();
        }

        public void showOdemeUrun(){
            OdemeAdet = new Dialog(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(OdemeAdet.getWindow().getAttributes());
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            OdemeAdet.setContentView(R.layout.dialog_adet);

            iptal = (Button) OdemeAdet.findViewById(R.id.iptal);
            tamam = (Button) OdemeAdet.findViewById(R.id.tamam);
            editInputAdet = (EditText) OdemeAdet.findViewById(R.id.editInputAdet);

            tamam.setText("Ödeme Al");

            tamam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editInputAdet.getText().toString().isEmpty()){
                        Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Integer.parseInt(editInputAdet.getText().toString())> Integer.parseInt(adet.getText().toString())){
                        Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            MasaUrun m = snapshot.getValue(MasaUrun.class);
                            adetInput = Integer.parseInt(editInputAdet.getText().toString());
                            Double odemeAlis = adetInput* m.getAlis();
                            Double odemeSatis = adetInput* m.getSatis();

                            sdf = new SimpleDateFormat("yyyy");
                            currentYear = sdf.format(new Date());
                            sdf = new SimpleDateFormat("MM");
                            currentMounth = sdf.format(new Date());
                            sdf = new SimpleDateFormat("dd");
                            currentDay = sdf.format(new Date());

                            int control2 = 0;
                            for (MasaUrun sU:statement
                            ) {
                                Log.i("statement21",sU.getIsimUrun());
                                if(m.getIsimUrun().equals(sU.getIsimUrun())){
                                    Log.i("statement2",sU.getIsimUrun());
                                    control2 = 1;
                                    Log.i("control2","1");
                                    reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("adet").setValue(sU.getAdet()+adetInput);
                                    reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("alisTutar").setValue(sU.getAlisTutar()+m.getAlis()*adetInput);
                                    reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("satisTutar").setValue(sU.getSatisTutar()+m.getSatis()*adetInput);
                                }
                            }
                            if(control2 == 0){
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).setValue(m);
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("adet").setValue(adetInput);
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("alisTutar").setValue(adetInput*m.getAlis());
                                reference.child("dukkanlar").child(mekan).child("statement").child(currentYear).child(currentMounth).child(currentDay).child(isim.getText().toString()).child("satisTutar").setValue(adetInput*m.getSatis());
                                Log.i("control2","0");

                            }

                            Double alistutar = m.getAlisTutar();
                            Double satistutar = m.getAlisTutar();
                            adetInput = m.getAdet() - adetInput;
                            alistutar = m.getAlis() * adetInput;
                            satistutar = m.getSatis() * adetInput;
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("adet").setValue(adetInput);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("alisTutar").setValue(alistutar);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("satisTutar").setValue(satistutar);

                            sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss-SS");
                            timeSiparisFormat = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("yyyy");
                            String year = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("MM");
                            String mounth = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("dd");
                            String day = sdfSiparisFormat.format(new Date());

                            sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                            String timeL = sdfSiparisFormat.format(new Date());

                            Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Masa", isimSinif+ " " +isimMasa + " - " + editInputAdet.getText().toString() + " adet "+ isim.getText().toString() + " ödeme alındı", timeL);

                            reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+user.getUid()).setValue(l);


                            if(adetInput == 0){
                                removeAt(pos);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast.makeText(context,"Ödeme Yapıldı",Toast.LENGTH_SHORT).show();
                    OdemeAdet.dismiss();
                }
            });

            iptal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OdemeAdet.dismiss();
                }
            });

            OdemeAdet.getWindow().setAttributes(params);
            OdemeAdet.show();
        }

        public void Plus(){
            controlDepo = 0;
            reference.child("dukkanlar").child(mekan).child("depo").child(isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DepoUrun du = snapshot.getValue(DepoUrun.class);
                    Integer adetEkle = Integer.parseInt(du.getAdet());
                    adetEkle -= 1;
                    if(du.getAdisyon().equals("1")){
                        if(adetEkle < 0){
                            Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_LONG).show();
                            controlDepo = 1;
                            return;
                        }
                    }
                    reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            MasaUrun m = snapshot.getValue(MasaUrun.class);
                            Integer adetInput = 1;
                            Integer temp =m.getAdet();
                            adetInput += temp;
                            Double alistutar = m.getAlis() * adetInput;
                            Double satistutar = m.getSatis() * adetInput;
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("adet").setValue(adetInput);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("alisTutar").setValue(alistutar);
                            reference.child("dukkanlar").child(mekan).child("masa").child(isimSinif).child(isimMasa).child(isim.getText().toString()).child("satisTutar").setValue(satistutar);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if(du.getAdisyon().equals("1")){
                        reference.child("dukkanlar").child(mekan).child("depo").child(isim.getText().toString()).child("adet").setValue(Integer.toString(adetEkle));
                    }

                    if(controlDepo == 0){
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
                        String currentTime = simpleDateFormat.format(new Date());

                        sdf = new SimpleDateFormat("HH");
                        currentHour = sdf.format(new Date());
                        sdf = new SimpleDateFormat("mm");
                        currentMinute = sdf.format(new Date());
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
                        currentTime = simpledateformat.format(new Date());

                        reference.child("dukkanlar").child(mekan).child("masalar").child(isimSinif).child(isimMasa).child("mode").setValue(1);
                        reference.child("dukkanlar").child(mekan).child("masalar").child(isimSinif).child(isimMasa).child("son").setValue(currentHour+":"+currentMinute);
                        reference.child("dukkanlar").child(mekan).child("masalar").child(isimSinif).child(isimMasa).child("sonFormat").setValue(currentTime);
                        sonSaat.setText(currentHour+":"+currentMinute);
                    }

                    sdfSiparisFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SS");
                    timeSiparisFormat = sdfSiparisFormat.format(new Date());
                    sdfSiparis = new SimpleDateFormat("HH:mm");
                    timeSiparis = sdfSiparis.format(new Date());
                    Siparis siparis = new Siparis(timeSiparis,timeSiparisFormat,isim.getText().toString(),"1",isimSinif,isimMasa,"0");
                    reference.child("dukkanlar").child(mekan).child("order").child("notCheck").child(timeSiparisFormat).setValue(siparis);

                    sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss-SS");
                    timeSiparisFormat = sdfSiparisFormat.format(new Date());
                    sdfSiparisFormat = new SimpleDateFormat("yyyy");
                    String year = sdfSiparisFormat.format(new Date());
                    sdfSiparisFormat = new SimpleDateFormat("MM");
                    String mounth = sdfSiparisFormat.format(new Date());
                    sdfSiparisFormat = new SimpleDateFormat("dd");
                    String day = sdfSiparisFormat.format(new Date());


                    sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                    String timeL = sdfSiparisFormat.format(new Date());

                    Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Masa", isimSinif+ " " +isimMasa + " - " + " 1 adet " + isim.getText().toString() + " ürün eklendi", timeL);

                    reference.child("dukkanlar").child(mekan).child("logs").child(year).child(mounth).child(day).child(timeSiparisFormat+user.getUid()).setValue(l);

                    Toast.makeText(context, "Ürün Eklendi", Toast.LENGTH_SHORT).show();
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

}

