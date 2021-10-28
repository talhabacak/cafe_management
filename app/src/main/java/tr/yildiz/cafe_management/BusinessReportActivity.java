package tr.yildiz.cafe_management;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.List;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class BusinessReportActivity extends AppCompatActivity {
    private static final int STORAGE_CODE = 1000;
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
    private Economy sumIncome,sumExpenditure,sumIncomeUrun,sumExpenditureDepo;
    private MasaUrun sumDepo, sumUrun;
    private ArrayList<Economy> income,expenditure;
    private ArrayList<MasaUrun > masaUrun,depoUrun;
    private ImageButton pdfwrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_report);
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
        pdfwrite = (ImageButton) findViewById((R.id.pdfwrite));

        layoutManager = new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
        rGelir.setLayoutManager(layoutManager);
        rGider.setLayoutManager(layoutManager1);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        income = new ArrayList<Economy>();
        expenditure = new ArrayList<Economy>();
        masaUrun = new ArrayList<MasaUrun>();
        depoUrun = new ArrayList<MasaUrun>();

        sumIncomeUrun = new Economy("Ürün Satış",0.0);
        sumExpenditureDepo = new Economy("Ürün Alış",0.0);
        sumIncome = new Economy("toplam",0.0);
        sumExpenditure = new Economy("toplam",0.0);
        sumDepo = new MasaUrun("toplam",0.0,0.0,0.0,0.0,0);
        sumUrun = new MasaUrun("toplam",0.0,0.0,0.0,0.0,0);

        Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
        karT.setText(kar.toString());

        income.add(sumIncomeUrun);
        expenditure.add(sumExpenditureDepo);

        Intent intent = getIntent();
        timeControl = intent.getStringExtra("timeControl");
        zaman = intent.getStringArrayListExtra("time");

        abrGelir = new adapterBusinessReport(context,income);
        rGelir.setAdapter(abrGelir);
        abrGider = new adapterBusinessReport(context,expenditure);
        rGider.setAdapter(abrGider);

        if(timeControl.equals("1")){
            tarih.setText(zaman.get(2)+"-"+zaman.get(1)+"-"+zaman.get(0));
            dataDayUrun();
            dataDayDepo();
            dataDayExpenditure();
            dataDayIncome();
        }
        else if(timeControl.equals("2")){
            tarih.setText(zaman.get(1)+"-"+zaman.get(0));
            dataMonthUrun();
            dataMonthDepo();
            dataMonthExpenditure();
            dataMonthIncome();
        }
        else if(timeControl.equals("3")){
            tarih.setText(zaman.get(0));
            dataYearUrun();
            dataYearDepo();
            dataYearExpenditure();
            dataYearIncome();
        }

        pdfwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlVersion();
            }
        });

    }

    public void ControlVersion(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,STORAGE_CODE);
                Log.i("111","111");
            }
            else{
                Log.i("222","222");
                PdfWrite();
            }
        }
        else{
            Log.i("333","333");
            PdfWrite();
        }

    }

    public void PdfWrite(){
        Document doc = new Document();

        String time = tarih.getText().toString();

        String filename= new String("CafeMaliDurum");
        String filepath = Environment.getExternalStorageDirectory() + "/" + filename + "-" + time + ".pdf";

        List listIncome = new List();
        List listExpenditure = new List();

        Log.i("pdf1","pdf1");

        try {
            PdfWriter.getInstance(doc, new FileOutputStream(filepath));
            doc.open();

            Log.i("pdf2","pdf2");

            doc.add(new Paragraph("\nGelir\n"));
            for (Economy i:
                 income) {
                listIncome.add(i.getIsim() + " ................ " + i.getFiyat());
            }
            doc.add(listIncome);

            doc.add(new Paragraph("\n\n\nGider\n"));
            for (Economy e:
                    expenditure) {
                listExpenditure.add(e.getIsim() + " ................ " + e.getFiyat());
            }
            doc.add(listExpenditure);

            Log.i("pdf3","pdf3");
            doc.close();

            Intent intentshare = new Intent(Intent.ACTION_SEND);
            intentshare.setType("application/pdf");
            intentshare.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath));

            startActivity(Intent.createChooser(intentshare,"Dosyayı paylaş ...."));



        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_CODE: {
                //izin
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PdfWrite();
                } else {
                    Toast.makeText(context, "İzin Almanız Gerekiyor", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    public void dataDayUrun(){
        Log.i("ggg",zaman.get(0)+zaman.get(1)+zaman.get(2));
        reference.child("dukkanlar").child(user.getUid()).child("statement").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MasaUrun s = snapshot.getValue(MasaUrun.class);
                Log.i("masalar",s.getIsimUrun());
                masaUrun.add(s);
                sumUrun.setSatisTutar(sumUrun.getSatisTutar()+s.getSatisTutar());
                sumIncome.setFiyat(sumIncome.getFiyat()+s.getSatisTutar());
                gelirT.setText(sumIncome.getFiyat().toString());
                income.get(0).setFiyat(sumUrun.getSatisTutar());
                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                karT.setText(kar.toString());
                abrGelir = new adapterBusinessReport(context,income);
                rGelir.setAdapter(abrGelir);
                Log.i(income.get(0).getIsim(),"1 "+income.get(0).getFiyat().toString());
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
                        sumUrun.setSatisTutar(sumUrun.getSatisTutar()+ss.getSatisTutar());
                        sumIncome.setFiyat(sumIncome.getFiyat()+ss.getSatisTutar());
                        gelirT.setText(sumIncome.getFiyat().toString());
                        income.get(0).setFiyat(sumUrun.getSatisTutar());
                        Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                        karT.setText(kar.toString());
                        abrGelir = new adapterBusinessReport(context,income);
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

                                sumUrun.setSatisTutar(sumUrun.getSatisTutar()+sss.getSatisTutar());
                                income.get(0).setFiyat(sumUrun.getSatisTutar());
                                sumIncome.setFiyat(sumIncome.getFiyat()+sss.getSatisTutar());
                                gelirT.setText(sumIncome.getFiyat().toString());
                                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                                karT.setText(kar.toString());
                                abrGelir = new adapterBusinessReport(context,income);
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
                depoUrun.add(s);
                sumDepo.setAlisTutar(sumDepo.getAlisTutar()+s.getAlisTutar());
                expenditure.get(0).setFiyat(sumDepo.getAlisTutar());
                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+s.getAlisTutar());
                giderT.setText(sumExpenditure.getFiyat().toString());
                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                karT.setText(kar.toString());

                abrGider = new adapterBusinessReport(context,expenditure);
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
                        int control = 0;
                        for (MasaUrun temp:depoUrun
                        ) {
                            if(ss.getIsimUrun().equals(temp.getIsimUrun())){
                                temp.setAlisTutar(temp.getAlisTutar()+ss.getAlisTutar());
                                temp.setSatisTutar(temp.getSatisTutar()+ss.getSatisTutar());
                                temp.setAdet(temp.getAdet()+ss.getAdet());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            depoUrun.add(ss);
                        }
                        Log.i("masaGün",ss.getIsimUrun());

                        sumDepo.setAlisTutar(sumDepo.getAlisTutar()+ss.getAlisTutar());
                        expenditure.get(0).setFiyat(sumDepo.getAlisTutar());
                        sumExpenditure.setFiyat(sumExpenditure.getFiyat()+ss.getAlisTutar());
                        giderT.setText(sumExpenditure.getFiyat().toString());
                        abrGider = new adapterBusinessReport(context,expenditure);
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
                                Log.i("masaaaaaa",sss.getIsimUrun());
                                int control = 0;
                                for (MasaUrun temp:depoUrun
                                ) {
                                    if(sss.getIsimUrun().equals(temp.getIsimUrun())){
                                        temp.setAlisTutar(temp.getAlisTutar()+sss.getAlisTutar());
                                        temp.setSatisTutar(temp.getSatisTutar()+sss.getSatisTutar());
                                        temp.setAdet(temp.getAdet()+sss.getAdet());
                                        control = 1;
                                    }
                                }
                                if(control == 0){
                                    depoUrun.add(sss);
                                }

                                sumDepo.setAlisTutar(sumDepo.getAlisTutar()+sss.getAlisTutar());
                                expenditure.get(0).setFiyat(sumDepo.getAlisTutar());
                                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+sss.getAlisTutar());
                                giderT.setText(sumExpenditure.getFiyat().toString());
                                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                                karT.setText(kar.toString());
                                abrGider = new adapterBusinessReport(context,expenditure);
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
                Log.i("masalar",s.getIsim());
                income.add(s);
                sumIncome.setFiyat(sumIncome.getFiyat()+s.getFiyat());
                gelirT.setText(sumIncome.getFiyat().toString());
                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                karT.setText(kar.toString());
                abrGelir = new adapterBusinessReport(context,income);
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
                        int control = 0;
                        for (Economy temp:income
                        ) {
                            if(ss.getIsim().equals(temp.getIsim())){
                                temp.setFiyat(temp.getFiyat()+ss.getFiyat());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            income.add(ss);
                        }
                        Log.i("masaGün",ss.getIsim());
                        sumIncome.setFiyat(sumIncome.getFiyat()+ss.getFiyat());
                        gelirT.setText(sumIncome.getFiyat().toString());
                        Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                        karT.setText(kar.toString());
                        abrGelir = new adapterBusinessReport(context,income);
                        rGelir.setAdapter(abrGelir);       }

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
                                Economy ss = snapshot.getValue(Economy.class);
                                int control = 0;
                                for (Economy temp:income
                                ) {
                                    if(ss.getIsim().equals(temp.getIsim())){
                                        temp.setFiyat(temp.getFiyat()+ss.getFiyat());
                                        control = 1;
                                    }
                                }
                                if(control == 0){
                                    income.add(ss);
                                }
                                Log.i("masaGün",ss.getIsim());
                                sumIncome.setFiyat(sumIncome.getFiyat()+ss.getFiyat());
                                gelirT.setText(sumIncome.getFiyat().toString());
                                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                                karT.setText(kar.toString());
                                abrGelir = new adapterBusinessReport(context,income);
                                rGelir.setAdapter(abrGelir);            }

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
                Log.i("masalar",s.getIsim());
                expenditure.add(s);
                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+s.getFiyat());
                giderT.setText(sumExpenditure.getFiyat().toString());
                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                karT.setText(kar.toString());
                abrGider = new adapterBusinessReport(context,expenditure);
                rGider.setAdapter(abrGider);
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
                        int control = 0;
                        for (Economy temp:expenditure
                        ) {
                            if(ss.getIsim().equals(temp.getIsim())){
                                temp.setFiyat(temp.getFiyat()+ss.getFiyat());
                                control = 1;
                            }
                        }
                        if(control == 0){
                            expenditure.add(ss);
                        }
                        Log.i("masaGün",ss.getIsim());
                        sumExpenditure.setFiyat(sumExpenditure.getFiyat()+ss.getFiyat());
                        giderT.setText(sumExpenditure.getFiyat().toString());
                        Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                        karT.setText(kar.toString());
                        abrGider = new adapterBusinessReport(context,expenditure);
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
                                Economy ss = snapshot.getValue(Economy.class);
                                int control = 0;
                                for (Economy temp:expenditure
                                ) {
                                    if(ss.getIsim().equals(temp.getIsim())){
                                        temp.setFiyat(temp.getFiyat()+ss.getFiyat());
                                        control = 1;
                                    }
                                }
                                if(control == 0){
                                    expenditure.add(ss);
                                }
                                Log.i("masaGün",ss.getIsim());
                                sumExpenditure.setFiyat(sumExpenditure.getFiyat()+ss.getFiyat());
                                giderT.setText(sumExpenditure.getFiyat().toString());
                                Double kar = sumIncome.getFiyat() - sumExpenditure.getFiyat();
                                karT.setText(kar.toString());
                                abrGider = new adapterBusinessReport(context,expenditure);
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