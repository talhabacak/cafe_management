package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminContactActivity extends AppCompatActivity {
    private RecyclerView adminR;
    private RecyclerView.LayoutManager layoutManager;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<User> users;
    private adapterAdmin adp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_contact);

        init();

    }

    public void init(){
        adminR = (RecyclerView) findViewById(R.id.adminR);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        users = new ArrayList<User>();

        layoutManager = new LinearLayoutManager(this);
        adminR.setLayoutManager(layoutManager);
        Log.i("say","1");

        adp = new adapterAdmin(context,users);
        adminR.setAdapter(adp);
        Log.i("say","2");

        data();
    }

    public void data(){
        reference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User s = snapshot.getValue(User.class);
                users.add(s);
                Log.i("isiim",s.getIsim());
                adp = new adapterAdmin(context,users);
                adminR.setAdapter(adp);
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