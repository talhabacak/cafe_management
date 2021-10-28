package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ContactActivity extends AppCompatActivity {
    private TextView drive,version,versionCurrent;
    private EditText edit_contact;
    private RecyclerView contactR;
    private ImageButton imageButton;
    private RecyclerView.LayoutManager layoutManager;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<Message> messages;
    private adapterContact adp;
    private String time,mekan,kim,versionS;
    private SimpleDateFormat sdf;
    private PackageInfo pInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        init();

    }

    public void init(){
        drive = (TextView) findViewById(R.id.drive);
        version = (TextView) findViewById(R.id.version);
        versionCurrent = (TextView) findViewById(R.id.versionCurrent);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        contactR = (RecyclerView) findViewById(R.id.contactR);
        edit_contact = (EditText) findViewById(R.id.edit_contact);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = getIntent();
        mekan = intent.getStringExtra("mekan");
        kim = intent.getStringExtra("who");

        messages = new ArrayList<Message>();

        layoutManager = new LinearLayoutManager(this);
        contactR.setLayoutManager(layoutManager);
        contactR.scrollToPosition(messages.size()-1);

        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionS = pInfo.versionName;
            versionCurrent.setText(versionS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        data();
        link();
        Version();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_contact.getText().toString().isEmpty()){
                    return;
                }
                String mesaj = edit_contact.getText().toString();
                String who = kim;
                sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SS");
                time = sdf.format(new Date());

                Message send = new Message(who,mesaj,time,false);

                reference.child("dukkanlar").child(mekan).child("contact").child(time).setValue(send);
                edit_contact.setText("");
            }
        });


    }

    public void data(){
        reference.child("dukkanlar").child(mekan).child("contact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message s = snapshot.getValue(Message.class);
                messages.add(s);
                adp = new adapterContact(context,messages,kim,mekan);
                contactR.setAdapter(adp);
                contactR.scrollToPosition(messages.size()-1);
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


    public void link(){
        reference.child("url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s= snapshot.getValue().toString();
                drive.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void Version(){
        reference.child("version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s= snapshot.getValue().toString();
                version.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
