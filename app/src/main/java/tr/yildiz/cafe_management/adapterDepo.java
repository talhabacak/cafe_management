package tr.yildiz.cafe_management;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class adapterDepo extends RecyclerView.Adapter<adapterDepo.tanim> {
    Context context;
    private Dialog SilAdet,OdemeAdet;
    List<DepoUrun> list;
    Integer adetInput;
    String mekan;

    public adapterDepo(Context context, List<DepoUrun> list, String mekan) {
        this.context = context;
        this.list = list;
        this.mekan = mekan;
    }


    @NonNull
    @Override
    public tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_depo,parent,false);
        return new tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull tanim holder, int position) {
        holder.urun_isim.setText(list.get(position).getIsim());
        holder.urun_adet.setText(list.get(position).getAdet());
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
        TextView urun_isim;
        TextView urun_adet;
        Button iptal,tamam,satildi,sil;
        EditText editInputAdet;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;
        SimpleDateFormat sdf, sdfSiparisFormat;
        String year, month, day;
        ArrayList<MasaUrun> masa;
        Integer pos;
        User person;

        public tanim(View itemView){
            super(itemView);
            urun_isim =(TextView) itemView.findViewById(R.id.urun_isim);
            urun_adet =(TextView) itemView.findViewById(R.id.urun_adet);
            sil =(Button) itemView.findViewById(R.id.sil);
            satildi =(Button) itemView.findViewById(R.id.satildi);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            PersonData();

            masa = new ArrayList<MasaUrun>();

            sdf = new SimpleDateFormat("yyyy");
            year = sdf.format(new Date());
            sdf = new SimpleDateFormat("MM");
            month = sdf.format(new Date());
            sdf = new SimpleDateFormat("dd");
            day = sdf.format(new Date());

            dataDepo();

            sil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSilUrun();
                }
            });

            satildi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOdemeUrun();
                }
            });
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

            tamam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child("dukkanlar").child(person.getYer()).child("depo").child(urun_isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DepoUrun du = snapshot.getValue(DepoUrun.class);
                            Integer adetEkle = Integer.parseInt(du.getAdet());
                            adetInput = Integer.parseInt(editInputAdet.getText().toString());
                            if(adetInput>adetEkle){
                                Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.i("adet", String.valueOf(adetInput));
                            adetEkle -= adetInput;
                            reference.child("dukkanlar").child(person.getYer()).child("depo").child(urun_isim.getText().toString()).child("adet").setValue(Integer.toString(adetEkle));

                            Integer control = 0;
                            Integer adetS = Integer.parseInt(editInputAdet.getText().toString());
                            Log.i("masaDu",du.getIsim());
                            for (MasaUrun m:masa
                            ) {
                                Log.i("masaM",m.getIsimUrun());
                                if(m.getIsimUrun().equals(du.getIsim())){
                                    control = 1;
                                    m.setAdet(m.getAdet()-adetS);
                                    m.setAlisTutar(m.getAlisTutar() - adetS*m.getAlis());
                                    m.setSatisTutar(m.getSatisTutar() - adetS*m.getSatis());
                                    reference.child("dukkanlar").child(person.getYer()).child("statementDepo").child(year).child(month).child(day).child(urun_isim.getText().toString()).setValue(m);
                                }
                            }
                            if(control == 0) {
                                adetS *= -1;
                                MasaUrun ss = new MasaUrun(urun_isim.getText().toString(),Double.parseDouble(du.getAlis()),Double.parseDouble(du.getSatis()),Double.parseDouble(du.getAlis())*adetS,Double.parseDouble(du.getSatis())*adetS,adetS);
                                reference.child("dukkanlar").child(person.getYer()).child("statementDepo").child(year).child(month).child(day).child(urun_isim.getText().toString()).setValue(ss);
                            }

                            if(adetEkle==0){
                                removeAt(pos);
                            }

                            sdf = new SimpleDateFormat("HH-mm-ss-SS");
                            String time = sdf.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("yyyy");
                            year = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("MM");
                            String mounth = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("dd");
                            day = sdfSiparisFormat.format(new Date());

                            sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                            String timeL = sdfSiparisFormat.format(new Date());

                            Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Depo",editInputAdet.getText().toString() +" adet " + urun_isim.getText().toString() + " silindi", timeL);

                            reference.child("dukkanlar").child(person.getYer()).child("logs").child(year).child(mounth).child(day).child(time+user.getUid()).setValue(l);

                            Toast.makeText(context,"Ürün Silindi",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

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

            tamam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child("dukkanlar").child(person.getYer()).child("depo").child(urun_isim.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DepoUrun du = snapshot.getValue(DepoUrun.class);
                            Integer adetEkle = Integer.parseInt(du.getAdet());
                            adetInput = Integer.parseInt(editInputAdet.getText().toString());
                            if(adetInput>adetEkle){
                                Toast.makeText(context,"Bu kadar ürün mevcut değil",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.i("adet", String.valueOf(adetInput));
                            adetEkle -= adetInput;
                            Double alistutar = Double.parseDouble(du.getAlis()) * adetInput;
                            Double satistutar = Double.parseDouble(du.getSatis()) * adetInput;
                            reference.child("dukkanlar").child(person.getYer()).child("depo").child(urun_isim.getText().toString()).child("adet").setValue(Integer.toString(adetEkle));

                            if(adetEkle==0){
                                removeAt(pos);
                            }

                            sdf = new SimpleDateFormat("HH-mm-ss-SS");
                            String time = sdf.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("yyyy");
                            String year = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("MM");
                            String mounth = sdfSiparisFormat.format(new Date());
                            sdfSiparisFormat = new SimpleDateFormat("dd");
                            String day = sdfSiparisFormat.format(new Date());

                            sdfSiparisFormat = new SimpleDateFormat("HH-mm-ss");
                            String timeL = sdfSiparisFormat.format(new Date());

                            Loglar l = new Loglar(person.getIsim() +" "+ person.getSoyisim(),"Depo",editInputAdet.getText().toString() +" adet " + urun_isim.getText().toString() + " satışı girildi", timeL);

                            reference.child("dukkanlar").child(person.getYer()).child("logs").child(year).child(mounth).child(day).child(time+user.getUid()).setValue(l);

                            Toast.makeText(context,"Ürün Kullanıldı",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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


        public void dataDepo(){
            reference.child("dukkanlar").child(mekan).child("statementDepo").child(year).child(month).child(day).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    MasaUrun s = snapshot.getValue(MasaUrun.class);
                    masa.add(s);

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
