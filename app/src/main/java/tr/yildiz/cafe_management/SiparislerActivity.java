package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SiparislerActivity extends AppCompatActivity {
    private RecyclerView yeniSiparisler;
    private RecyclerView.LayoutManager layoutManager,layoutManager1;
    private ImageButton imageButtonAllDel;
    private TextView textCorrect;
    private Button buttonEvet, buttonHayir;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String mekan;
    private ArrayList<Siparis> notCheck;
    private adapterSiparis adpNot;
    private Context context = this;
    private Dialog isCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siparisler);

        init();
    }

    public void init(){
        imageButtonAllDel = (ImageButton) findViewById(R.id.imageButtonAllDel);
        yeniSiparisler = (RecyclerView) findViewById(R.id.yeniSiparisler);
        layoutManager = new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
        yeniSiparisler.setLayoutManager(layoutManager1);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = getIntent();
        mekan = intent.getStringExtra("mekan");

        notCheck = new ArrayList<Siparis>();

        adpNot = new adapterSiparis(context,notCheck,mekan);
        yeniSiparisler.setAdapter(adpNot);

        dataNotCheck();

        imageButtonAllDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogEmin();
            }
        });

    }

    public void dataNotCheck(){
        reference.child("dukkanlar").child(mekan).child("order").child("notCheck").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot snapshot, @Nullable String previousChildName) {
                Siparis s = snapshot.getValue(Siparis.class);
                notCheck.add(s);
                Log.i("sonF",s.getIsim());
                adpNot = new adapterSiparis(context,notCheck,mekan);
                yeniSiparisler.setAdapter(adpNot);
            }

            @Override
            public void onChildChanged( DataSnapshot snapshot, @Nullable String previousChildName) {
                Siparis s = snapshot.getValue(Siparis.class);
                notCheck.remove(s);
                adpNot = new adapterSiparis(context,notCheck,mekan);
                yeniSiparisler.setAdapter(adpNot);
            }

            @Override
            public void onChildRemoved( DataSnapshot snapshot) {
                Siparis s = snapshot.getValue(Siparis.class);
                notCheck.remove(s);
                Log.i("sonRemoveF",s.getIsim());
                adpNot = new adapterSiparis(context,notCheck,mekan);
                yeniSiparisler.setAdapter(adpNot);
            }

            @Override
            public void onChildMoved( DataSnapshot snapshot, @Nullable String previousChildName) {
                Siparis s = snapshot.getValue(Siparis.class);
                notCheck.remove(s);
                adpNot = new adapterSiparis(context,notCheck,mekan);
                yeniSiparisler.setAdapter(adpNot);
            }

            @Override
            public void onCancelled( DatabaseError error) {

            }
        });
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

        textCorrect.setText("Bütün siparişleri silmek istediğinize emin misiniz?");

        buttonEvet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("dukkanlar").child(mekan).child("order").child("notCheck").removeValue();
                notCheck.clear();
                adpNot = new adapterSiparis(context,notCheck,mekan);
                yeniSiparisler.setAdapter(adpNot);
                Toast.makeText(context,"Bütün Siparişler Silindi",Toast.LENGTH_SHORT).show();
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

}