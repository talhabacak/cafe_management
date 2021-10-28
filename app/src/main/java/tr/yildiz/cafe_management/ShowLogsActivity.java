package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowLogsActivity extends AppCompatActivity {
    private TextView textViewDate;
    private ImageButton imageButtonDel;
    private RecyclerView logsR;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context = this;
    private ArrayList<Loglar > logs;
    private adapterLog adl;
    private ArrayList<String> zaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_logs);

        init();

    }

    public void init(){
        logs = new ArrayList<Loglar>();

        Intent intent = getIntent();
        zaman = intent.getStringArrayListExtra("time");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textViewDate = (TextView) findViewById((R.id.textViewDate));
        imageButtonDel = (ImageButton) findViewById((R.id.imageButtonDel));
        logsR = (RecyclerView) findViewById((R.id.logsR));

        layoutManager = new LinearLayoutManager(this);
        logsR.setLayoutManager(layoutManager);
        adl = new adapterLog(context,logs);
        logsR.setAdapter(adl);

        Log.i("hareket0", "11");
        LogData();

        textViewDate.setText(zaman.get(2)+ "/" +zaman.get(1)+ "/" +zaman.get(0));

        imageButtonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("dukkanlar").child(user.getUid()).child("logs").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).removeValue();
                Intent intent = new Intent(getApplicationContext(), LogsActivity.class);
                Toast.makeText(context,"Bu Tarihteki Hareketler Silindi",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }

    public void LogData(){
        reference.child("dukkanlar").child(user.getUid()).child("logs").child(zaman.get(0)).child(zaman.get(1)).child(zaman.get(2)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Loglar s = snapshot.getValue(Loglar.class);
                logs.add(s);
                adl = new adapterLog(context,logs);
                logsR.setAdapter(adl);
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