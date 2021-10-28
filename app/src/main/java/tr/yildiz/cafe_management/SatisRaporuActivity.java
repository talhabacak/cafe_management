package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SatisRaporuActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView adet,kar,alis,satis,tarihR;
    private adapterSatisRapor asr;
    private MasaUrun sum;
    private ArrayList<MasaUrun > masaUrun;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context = this;
    private String timeControl;
    private ArrayList<String> zaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satis_raporu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        masaUrun = new ArrayList<MasaUrun>();

        sum = new MasaUrun("toplam",0.0,0.0,0.0,0.0,0);

        Intent intent = getIntent();
        timeControl = intent.getStringExtra("timeControl");
        zaman = intent.getStringArrayListExtra("time");

        alis = (TextView) findViewById((R.id.alis));
        satis = (TextView) findViewById((R.id.satis));
        kar = (TextView) findViewById((R.id.kar));
        adet = (TextView) findViewById((R.id.adet));
        tarihR = (TextView) findViewById((R.id.tarihR));
        recyclerView = (RecyclerView) findViewById((R.id.satisR));
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        asr = new adapterSatisRapor(context,masaUrun);
        recyclerView.setAdapter(asr);

        if(timeControl.equals("1")){
            tarihR.setText(zaman.get(2)+"/"+zaman.get(1)+"/"+zaman.get(0));
            dataDay();
        }
        else if(timeControl.equals("2")){
            tarihR.setText(zaman.get(1)+"/"+zaman.get(0));
            dataMonth();
        }
        else if(timeControl.equals("3")){
            tarihR.setText(zaman.get(0));
            dataYear();
        }
    }

    public void dataDay(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);
                Log.i("masalar",s.getIsimUrun());
                masaUrun.add(s);
                sum.setAdet(sum.getAdet()+s.getAdet());
                sum.setSatisTutar(sum.getSatisTutar()+s.getSatisTutar());
                sum.setAlisTutar(sum.getAlisTutar()+s.getAlisTutar());
                Double karr = sum.getSatisTutar()-sum.getAlisTutar();
                adet.setText(sum.getAdet().toString());
                alis.setText(sum.getAlisTutar().toString());
                satis.setText(sum.getSatisTutar().toString());
                kar.setText(karr.toString());
                asr = new adapterSatisRapor(context,masaUrun);
                recyclerView.setAdapter(asr);

                Log.i("günu","1 "+s.getIsimUrun());
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

    public void dataMonth(){
        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        MasaUrun ss = snapshot.getValue(MasaUrun.class);
                        int control = 0;
                        for (MasaUrun temp:masaUrun
                             ) {
                            if(ss.getIsimUrun().equals(temp.getIsimUrun())){
                                temp.setAlisTutar(temp.getAlisTutar()+ss.getAlisTutar());
                                temp.setSatisTutar(temp.getSatisTutar()+ss.getSatisTutar());
                                temp.setAdet(temp.getAdet()+ss.getAdet());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            masaUrun.add(ss);
                        }
                        Log.i("masaGün",ss.getIsimUrun());
                        sum.setAdet(sum.getAdet()+ss.getAdet());
                        sum.setSatisTutar(sum.getSatisTutar()+ss.getSatisTutar());
                        sum.setAlisTutar(sum.getAlisTutar()+ss.getAlisTutar());
                        Double karr = sum.getSatisTutar()-sum.getAlisTutar();
                        adet.setText(sum.getAdet().toString());
                        alis.setText(sum.getAlisTutar().toString());
                        satis.setText(sum.getSatisTutar().toString());
                        kar.setText(karr.toString());
                        asr = new adapterSatisRapor(context,masaUrun);
                        recyclerView.setAdapter(asr);
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

         /*
                ArrayList<MasaUrun> masaList = s.getMu();
                Log.i("masalar",masaList.get(0).getIsimUrun());
                for (MasaUrun m:masaList
                     ) {
                    masaUrun.add(m);
                    sum.setAdet(sum.getAdet()+m.getAdet());
                    sum.setSatisTutar(sum.getSatisTutar()+m.getSatisTutar());
                    sum.setAlisTutar(sum.getAlisTutar()+m.getAlisTutar());
                    Double karr = sum.getSatisTutar()-sum.getAlisTutar();
                    adet.setText(sum.getAdet().toString());
                    alis.setText(sum.getAlisTutar().toString());
                    satis.setText(sum.getSatisTutar().toString());
                    kar.setText(karr.toString());
                }
                asr = new adapterSatisRapor(context,masaUrun);
                recyclerView.setAdapter(asr);
                ayUrun.add(s);
                Log.i("ay",ayUrun.toString());
*/
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

    public void dataYear(){
        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("masaAy",s);
                reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String ss = snapshot.getKey();
                        Log.i("masaGün",ss);
                        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(s).child(ss).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                MasaUrun sss = snapshot.getValue(MasaUrun.class);
                                Log.i("masaaaaaa",sss.getIsimUrun());
                                int control = 0;
                                for (MasaUrun temp:masaUrun
                                ) {
                                    if(sss.getIsimUrun().equals(temp.getIsimUrun())){
                                        temp.setAlisTutar(temp.getAlisTutar()+sss.getAlisTutar());
                                        temp.setSatisTutar(temp.getSatisTutar()+sss.getSatisTutar());
                                        temp.setAdet(temp.getAdet()+sss.getAdet());
                                        control = 1;
                                    }
                                }
                                if(control == 0){
                                    masaUrun.add(sss);
                                }
                                sum.setAdet(sum.getAdet()+sss.getAdet());
                                sum.setSatisTutar(sum.getSatisTutar()+sss.getSatisTutar());
                                sum.setAlisTutar(sum.getAlisTutar()+sss.getAlisTutar());
                                Double karr = sum.getSatisTutar()-sum.getAlisTutar();
                                adet.setText(sum.getAdet().toString());
                                alis.setText(sum.getAlisTutar().toString());
                                satis.setText(sum.getSatisTutar().toString());
                                kar.setText(karr.toString());
                                asr = new adapterSatisRapor(context,masaUrun);
                                recyclerView.setAdapter(asr);
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