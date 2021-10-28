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

public class GeneralReportActivity extends AppCompatActivity {
    private RecyclerView rGelir,rGider;
    private RecyclerView.LayoutManager layoutManager,layoutManager1;
    private TextView tarih, gelirT, giderT, karT;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private adapterBusinessReport abrGelir,abrGider;
    private String timeControl;
    private ArrayList<String> zaman;
    private Economy sumIncome,sumExpenditure,sumIncomeBusiness,sumExpenditureBusiness,sumGenralIncome,sumGeneralExpenditure;
    private ArrayList<Economy> generalExpenditure,generalIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_report);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

    }

    public void init(){
        tarih = (TextView) findViewById((R.id.tarih));
        gelirT = (TextView) findViewById((R.id.gelirT));
        giderT = (TextView) findViewById((R.id.giderT));
        karT = (TextView) findViewById((R.id.karT));
        rGelir = (RecyclerView) findViewById((R.id.rGelir));
        rGider = (RecyclerView) findViewById((R.id.rGider));
        layoutManager = new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
        rGelir.setLayoutManager(layoutManager);
        rGider.setLayoutManager(layoutManager1);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        generalIncome = new ArrayList<Economy>();
        generalExpenditure = new ArrayList<Economy>();

        sumIncomeBusiness = new Economy("İşletme Gelir",0.0);
        sumExpenditureBusiness = new Economy("İşletme Gider",0.0);

        sumIncome = new Economy("toplam",0.0);
        sumExpenditure = new Economy("toplam",0.0);
        sumGenralIncome =  new Economy("toplam",0.0);
        sumGeneralExpenditure = new Economy("toplam",0.0);

        Double kar = sumGenralIncome.getFiyat() - sumExpenditure.getFiyat();
        karT.setText(kar.toString());

        generalIncome.add(sumIncomeBusiness);
        generalExpenditure.add(sumExpenditureBusiness);

        Intent intent = getIntent();
        timeControl = intent.getStringExtra("timeControl");
        zaman = intent.getStringArrayListExtra("time");

        abrGelir = new adapterBusinessReport(context,generalIncome);
        rGelir.setAdapter(abrGelir);
        abrGider = new adapterBusinessReport(context,generalExpenditure);
        rGider.setAdapter(abrGider);

        if(timeControl.equals("1")){
            tarih.setText(zaman.get(2)+"/"+zaman.get(1)+"/"+zaman.get(0));
            dataDayUrun();
            dataDayDepo();
            dataDayExpenditure();
            dataDayIncome();
            dataDayGeneralIncome();
            dataDayGeneralExpenditure();
        }
        else if(timeControl.equals("2")){
            tarih.setText(zaman.get(1)+"/"+zaman.get(0));
            dataMonthUrun();
            dataMonthDepo();
            dataMonthExpenditure();
            dataMonthIncome();
            dataMonthGeneralIncome();
            dataMonthGeneralExpenditure();
        }
        else if(timeControl.equals("3")){
            tarih.setText(zaman.get(0));
            dataYearUrun();
            dataYearDepo();
            dataYearExpenditure();
            dataYearIncome();
            dataYearGeneralIncome();
            dataYearGeneralExpenditure();
        }
    }

    public void dataDayUrun(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);

                sumIncome.setFiyat(sumIncome.getFiyat()+s.getSatisTutar());
                sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+s.getSatisTutar());
                generalIncome.get(0).setFiyat(sumIncome.getFiyat());

                gelirT.setText(sumGenralIncome.getFiyat().toString());
                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGelir = new adapterBusinessReport(context,generalIncome);
                rGelir.setAdapter(abrGelir);
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

    public void dataMonthUrun(){
        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        MasaUrun ss = snapshot.getValue(MasaUrun.class);

                        sumIncome.setFiyat(sumIncome.getFiyat()+ss.getSatisTutar());
                        sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+ss.getSatisTutar());
                        generalIncome.get(0).setFiyat(sumIncome.getFiyat());

                        gelirT.setText(sumGenralIncome.getFiyat().toString());
                        Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                        karT.setText(kar.toString());

                        abrGelir = new adapterBusinessReport(context,generalIncome);
                        rGelir.setAdapter(abrGelir);
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

    public void dataYearUrun(){
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

                                sumIncome.setFiyat(sumIncome.getFiyat()+sss.getSatisTutar());
                                generalIncome.get(0).setFiyat(sumIncome.getFiyat());
                                sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+sss.getSatisTutar());

                                gelirT.setText(sumGenralIncome.getFiyat().toString());
                                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                                karT.setText(kar.toString());

                                abrGelir = new adapterBusinessReport(context,generalIncome);
                                rGelir.setAdapter(abrGelir);
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

    public void dataDayDepo(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statementDepo").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);
                Log.i("masalar",s.getIsimUrun());

                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+s.getAlisTutar());
                generalExpenditure.get(0).setFiyat(sumExpenditure.getFiyat());
                sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+s.getAlisTutar());

                giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGider = new adapterBusinessReport(context,generalExpenditure);
                rGider.setAdapter(abrGider);
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

    public void dataMonthDepo(){
        reference.child("dukkanlar").child(user.getUid()).child("statementDepo").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementDepo").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        MasaUrun ss = snapshot.getValue(MasaUrun.class);

                        sumExpenditure.setFiyat(sumExpenditure.getFiyat()+ss.getAlisTutar());
                        generalExpenditure.get(0).setFiyat(sumExpenditure.getFiyat());
                        sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+ss.getAlisTutar());

                        giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                        Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                        karT.setText(kar.toString());

                        abrGider = new adapterBusinessReport(context,generalExpenditure);
                        rGider.setAdapter(abrGider);
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

    public void dataYearDepo(){
        reference.child("dukkanlar").child(user.getUid()).child("statementDepo").child(zaman.get(0)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("masaAy",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementDepo").child(zaman.get(0)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String ss = snapshot.getKey();
                        Log.i("masaGün",ss);
                        reference.child("dukkanlar").child(user.getUid()).child("statementDepo").child(zaman.get(0)).child(s).child(ss).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                MasaUrun sss = snapshot.getValue(MasaUrun.class);

                                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+sss.getAlisTutar());
                                generalExpenditure.get(0).setFiyat(sumExpenditure.getFiyat());
                                sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+sss.getAlisTutar());

                                giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                                karT.setText(kar.toString());

                                abrGider = new adapterBusinessReport(context,generalExpenditure);
                                rGider.setAdapter(abrGider);
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

    public void dataDayIncome(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeBusiness").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Economy s = snapshot.getValue(Economy.class);

                sumIncome.setFiyat(sumIncome.getFiyat()+s.getFiyat());
                sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+s.getFiyat());
                generalIncome.get(0).setFiyat(sumIncome.getFiyat());

                gelirT.setText(sumGenralIncome.getFiyat().toString());
                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGelir = new adapterBusinessReport(context,generalIncome);
                rGelir.setAdapter(abrGelir);
                Log.i("günu","1 "+s.getIsim());
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

    public void dataMonthIncome(){
        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeBusiness").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementIncomeBusiness").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Economy ss = snapshot.getValue(Economy.class);
                        sumIncome.setFiyat(sumIncome.getFiyat()+ss.getFiyat());
                        sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+ss.getFiyat());
                        generalIncome.get(0).setFiyat(sumIncome.getFiyat());

                        gelirT.setText(sumGenralIncome.getFiyat().toString());
                        Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                        karT.setText(kar.toString());

                        abrGelir = new adapterBusinessReport(context,generalIncome);
                        rGelir.setAdapter(abrGelir);
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

    public void dataYearIncome(){
        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeBusiness").child(zaman.get(0)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("masaAy",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementIncomeBusiness").child(zaman.get(0)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String ss = snapshot.getKey();
                        Log.i("masaGün",ss);
                        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeBusiness").child(zaman.get(0)).child(s).child(ss).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Economy sss = snapshot.getValue(Economy.class);
                                sumIncome.setFiyat(sumIncome.getFiyat()+sss.getFiyat());
                                sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+sss.getFiyat());
                                generalIncome.get(0).setFiyat(sumIncome.getFiyat());

                                gelirT.setText(sumGenralIncome.getFiyat().toString());
                                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                                karT.setText(kar.toString());

                                abrGelir = new adapterBusinessReport(context,generalIncome);
                                rGelir.setAdapter(abrGelir);
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

    public void dataDayExpenditure(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureBusiness").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Economy s = snapshot.getValue(Economy.class);

                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+s.getFiyat());
                sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+s.getFiyat());
                generalExpenditure.get(0).setFiyat(sumExpenditure.getFiyat());

                giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGider = new adapterBusinessReport(context,generalExpenditure);
                rGelir.setAdapter(abrGider);
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

    public void dataMonthExpenditure(){
        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureBusiness").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureBusiness").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Economy ss = snapshot.getValue(Economy.class);
                        sumExpenditure.setFiyat(sumExpenditure.getFiyat()+ss.getFiyat());
                        sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+ss.getFiyat());
                        generalExpenditure.get(0).setFiyat(sumExpenditure.getFiyat());

                        giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                        Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                        karT.setText(kar.toString());

                        abrGider = new adapterBusinessReport(context,generalExpenditure);
                        rGelir.setAdapter(abrGider);
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

    public void dataYearExpenditure(){
        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureBusiness").child(zaman.get(0)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("masaAy",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureBusiness").child(zaman.get(0)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String ss = snapshot.getKey();
                        Log.i("masaGün",ss);
                        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureBusiness").child(zaman.get(0)).child(s).child(ss).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Economy sss = snapshot.getValue(Economy.class);
                                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+sss.getFiyat());
                                sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+sss.getFiyat());
                                generalExpenditure.get(0).setFiyat(sumExpenditure.getFiyat());

                                giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                                karT.setText(kar.toString());

                                abrGider = new adapterBusinessReport(context,generalExpenditure);
                                rGelir.setAdapter(abrGider);

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

    public void dataDayGeneralIncome(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Economy s = snapshot.getValue(Economy.class);

                generalIncome.add(s);
                sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+s.getFiyat());

                gelirT.setText(sumGenralIncome.getFiyat().toString());
                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGelir = new adapterBusinessReport(context,generalIncome);
                rGelir.setAdapter(abrGelir);
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

    public void dataMonthGeneralIncome(){
        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Economy ss = snapshot.getValue(Economy.class);
                        int control = 0;
                        for (Economy temp:generalIncome
                        ) {
                            if(ss.getIsim().equals(temp.getIsim())){
                                temp.setFiyat(temp.getFiyat()+ss.getFiyat());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            generalIncome.add(ss);
                        }
                        sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+ss.getFiyat());

                        gelirT.setText(sumGenralIncome.getFiyat().toString());
                        Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                        karT.setText(kar.toString());

                        abrGelir = new adapterBusinessReport(context,generalIncome);
                        rGelir.setAdapter(abrGelir);
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

    public void dataYearGeneralIncome(){
        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(zaman.get(0)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("masaAy",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(zaman.get(0)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String ss = snapshot.getKey();
                        Log.i("masaGün",ss);
                        reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(zaman.get(0)).child(s).child(ss).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Economy sss = snapshot.getValue(Economy.class);
                                int control = 0;
                                for (Economy temp:generalIncome
                                ) {
                                    if(sss.getIsim().equals(temp.getIsim())){
                                        temp.setFiyat(temp.getFiyat()+sss.getFiyat());
                                        control = 1;
                                    }
                                }
                                if(control == 0){
                                    generalIncome.add(sss);
                                }
                                sumGenralIncome.setFiyat(sumGenralIncome.getFiyat()+sss.getFiyat());

                                gelirT.setText(sumGenralIncome.getFiyat().toString());
                                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                                karT.setText(kar.toString());

                                abrGelir = new adapterBusinessReport(context,generalIncome);
                                rGelir.setAdapter(abrGelir);
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

    public void dataDayGeneralExpenditure(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Economy s = snapshot.getValue(Economy.class);

                generalExpenditure.add(s);
                sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+s.getFiyat());

                giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGider = new adapterBusinessReport(context,generalExpenditure);
                rGider.setAdapter(abrGider);
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

    public void dataMonthGeneralExpenditure(){
        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(zaman.get(0)).child(zaman.get(1)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("günnn",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(zaman.get(0)).child(zaman.get(1)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Economy ss = snapshot.getValue(Economy.class);
                        int control = 0;
                        for (Economy temp:generalExpenditure
                        ) {
                            if(ss.getIsim().equals(temp.getIsim())){
                                temp.setFiyat(temp.getFiyat()+ss.getFiyat());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            generalExpenditure.add(ss);
                        }
                        sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+ss.getFiyat());

                        giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                        Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                        karT.setText(kar.toString());

                        abrGider = new adapterBusinessReport(context,generalExpenditure);
                        rGider.setAdapter(abrGider);
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

    public void dataYearGeneralExpenditure(){
        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(zaman.get(0)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                Log.i("masaAy",s);
                reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(zaman.get(0)).child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String ss = snapshot.getKey();
                        Log.i("masaGün",ss);
                        reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(zaman.get(0)).child(s).child(ss).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                Economy sss = snapshot.getValue(Economy.class);
                                int control = 0;
                                for (Economy temp:generalExpenditure
                                ) {
                                    if(sss.getIsim().equals(temp.getIsim())){
                                        temp.setFiyat(temp.getFiyat()+sss.getFiyat());
                                        control = 1;
                                    }
                                }
                                if(control == 0){
                                    generalExpenditure.add(sss);
                                }
                                sumGeneralExpenditure.setFiyat(sumGeneralExpenditure.getFiyat()+sss.getFiyat());

                                giderT.setText(sumGeneralExpenditure.getFiyat().toString());
                                Double kar = sumGenralIncome.getFiyat() - sumGeneralExpenditure.getFiyat();
                                karT.setText(kar.toString());

                                abrGider = new adapterBusinessReport(context,generalExpenditure);
                                rGider.setAdapter(abrGider);
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