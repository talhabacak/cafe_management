package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainPageActivity extends AppCompatActivity {
    private TextView userName, businessName,uyari;
    private ImageView imageView;
    private Context context = this;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private User users;
    private Dialog alert;
    private UiModeManager uiModeManager;
    private MainFragment mainFragment;
    private Button buttonAlert;
    private Bundle bundle;
    private Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        init();

    }

    @Override
    protected void onPause() {
        super.onPause();
        bundle.putString("bildirim", "0");
        bundle.putString("bildirimAdmin", "0");
        dataUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bundle.putString("bildirim", "0");
        bundle.putString("bildirimAdmin", "0");
        dataUser();
    }

    public void init(){
        bundle = new Bundle();
        businessName = (TextView) findViewById((R.id.businessName));
        userName = (TextView) findViewById((R.id.userName));
        imageView = (ImageView) findViewById((R.id.imageView));
        mainFragment = new MainFragment();

        intent1 = new Intent(getApplicationContext(), MasalarActivity.class);

        bundle.putString("bildirim", "0");
        bundle.putString("bildirimAdmin", "0");

        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                dataFragmet();
                return false;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFragmet();
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        users = new User();

        dataUser();
    }
    public void dataUyari(){
        reference.child("dukkanlar").child(user.getUid()).child("uyariOdeme").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = snapshot.getValue().toString();
                showAlert(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dataUser(){
        reference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(User.class);
                userName.setText(users.getIsim() + " " + users.getSoyisim());

                if(users.getMod().equals("-1")){
                    dataContactAdmin();
                }
                else {
                    dataContact();
                }

                if(users.getMod().equals("0")){
                    dataCalisan();
                }

                intent1.putExtra("mod",users.getMod());

                bundle.putString("mod", users.getMod());
                bundle.putString("yer", users.getYer());
                bundle.putString("status", String.valueOf(users.getStatus()));
                mainFragment.setArguments(bundle);

                if(Integer.parseInt(users.getMod()) == 1){
                    if(users.getStatus() == 0){
                        String uyar = "Uygulamayı kullanamazsınız, verileriniz 1 hafta içinde silinecek. Kullanmak için iletişime geçin.";
                        showAlert(uyar);
                    }
                    else if(users.getStatus() == -1){
                        String uyar = "Ödeme süreniz geçti, 1 hafta içinde ödeme yapmazsanız hesabınız silinecek";
                        showAlert(uyar);
                    }

                }
                if(users.getStatus() == -10){
                    dataUyari();
                }
                dataBusinessName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void dataBusinessName(){
        Log.i("dukkan","5"+users.getYer());
        reference.child("dukkanlar").child(users.getYer()).child("isim").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s = "";
                if(snapshot == null){
                    businessName.setText("Bağlı İşletme Yok");
                    return;
                }
                if(!snapshot.getValue().toString().isEmpty()){
                    businessName.setText(snapshot.getValue().toString());
                }
                else {
                    businessName.setText("Bağlı İşletme Yok");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showAlert(String uyar){
        alert = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(alert.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alert.setContentView(R.layout.alert_odeme);

        buttonAlert = (Button) alert.findViewById(R.id.buttonAlert);
        uyari = (TextView) alert.findViewById(R.id.uyari);
        uyari.setText(uyar);

        buttonAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.getWindow().setAttributes(params);
        alert.show();
    }

    public void dataFragmet(){
        mainFragment.show(getSupportFragmentManager(),"menu");
        mainFragment.setOnItemClickListener(new MainFragment.OnClickListener() {
            @Override
            public void onClickClose() {
                mainFragment.dismiss();
            }

            @Override
            public void onClickOut() {
                auth.signOut();
                Intent intent6 = new Intent(getApplicationContext(), LoginActivity.class);
                mainFragment.dismiss();
                startActivity(intent6);
                finish();
            }

            @Override
            public void onClickMasa() {
                intent1.putExtra("mekan",users.getYer());
                mainFragment.dismiss();
                startActivity(intent1);
            }

            @Override
            public void onClickDepo() {
                Intent intent2 = new Intent(getApplicationContext(), DepoActivity.class);
                intent2.putExtra("mekan",users.getYer());
                mainFragment.dismiss();
                startActivity(intent2);
            }

            @Override
            public void onClickBusiness() {
                Intent intent4 = new Intent(getApplicationContext(), GelirGiderActivity.class);
                mainFragment.dismiss();
                startActivity(intent4);
            }

            @Override
            public void onClickGeneral() {
                Intent intent5 = new Intent(getApplicationContext(), KisiselActivity.class);
                mainFragment.dismiss();
                startActivity(intent5);
            }

            @Override
            public void onClickManage() {
                Intent intent3 = new Intent(getApplicationContext(), ManageActivity.class);
                mainFragment.dismiss();
                startActivity(intent3);
            }

            @Override
            public void onClickOption() {
                Intent intent7 = new Intent(getApplicationContext(), UserInfoActivity.class);
                mainFragment.dismiss();
                startActivity(intent7);
            }

            @Override
            public void onClickSiparis() {
                Intent intent8 = new Intent(getApplicationContext(), SiparislerActivity.class);
                intent8.putExtra("mekan",users.getYer());
                mainFragment.dismiss();
                startActivity(intent8);
            }

            @Override
            public void onClickContact() {
                Intent intent9 = new Intent(getApplicationContext(), ContactActivity.class);
                intent9.putExtra("mekan",users.getUserId());
                intent9.putExtra("who","Sen");
                mainFragment.dismiss();
                startActivity(intent9);
            }

            @Override
            public void onClickAdmin() {
                Intent intent10 = new Intent(getApplicationContext(), AdminContactActivity.class);
                mainFragment.dismiss();
                startActivity(intent10);
            }

            @Override
            public void onClickLogs() {
                Intent intentLogs = new Intent(getApplicationContext(), LogsActivity.class);
                mainFragment.dismiss();
                startActivity(intentLogs);
            }
        });
    }

    public void dataContact(){
        reference.child("dukkanlar").child(user.getUid()).child("contact").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message s = snapshot.getValue(Message.class);
                if(s!=null){
                    Log.i("mesajjj",s.getText());
                    if(s.getWho() != null){
                        if (s.getWho().equals("Admin")){
                            if(!s.getVisibled()){
                                Log.i("bildirim1","safsd");
                                bundle.putString("bildirim", "1");
                            }
                        }
                    }
                }
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

    public void dataContactAdmin(){
        reference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User u = snapshot.getValue(User.class);
                reference.child("dukkanlar").child(u.getUserId()).child("contact").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Message s = snapshot.getValue(Message.class);
                        Log.i("mesajjja",s.getText());
                        if(s!=null){
                            if(s.getWho() != null){
                                if (s.getWho().equals("Sen")){
                                    if(!s.getVisibled()){
                                        Log.i("bildirim2","safsd");
                                        bundle.putString("bildirimAdmin", "1");
                                    }
                                }
                            }
                        }
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

    public void dataCalisan(){
        Log.i("yyer",users.getYer());
        reference.child("dukkanlar").child(users.getYer()).child("calisanlar").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Worker w = snapshot.getValue(Worker.class);
                Log.i("calll",w.getIsim());
                bundle.putString("depoyetki", w.getDepo().toString());
                bundle.putString("orderyetki", w.getOrder().toString());

                intent1.putExtra("ode",w.getOde().toString());
                intent1.putExtra("sil",w.getSil().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}