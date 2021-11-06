package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DepoBitenActivity extends AppCompatActivity {

    private ArrayList<DepoUrun> listDepo;
    private adapterDepoBiten adp;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depo_biten);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

    }
    public void init(){
        recyclerView = (RecyclerView) findViewById((R.id.depo));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        listDepo = new ArrayList<DepoUrun>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dataInput();

    }


    public void dataInput(){
        reference.child("dukkanlar").child(user.getUid()).child("depo").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DepoUrun s = snapshot.getValue(DepoUrun.class);
                Integer adet = Integer.parseInt(s.getAdet());
                if(adet<1 && s.getAdisyon().equals("1")){
                    listDepo.add(s);

                    adp = new adapterDepoBiten(context,listDepo);
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
                        Integer adet = Integer.parseInt(d.getAdet());
                        if(adet>0){
                            listDepo.remove(d);
                        }
                    }
                }
                adp = new adapterDepoBiten(context,listDepo);
                recyclerView.setAdapter(adp);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                DepoUrun s = snapshot.getValue(DepoUrun.class);
                listDepo.remove(s);
                adp = new adapterDepoBiten(context,listDepo);
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
}