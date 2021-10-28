package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView text_isim,text_soyisim,text_tel,text_password,text_mail;
    private EditText editTextTextPersonName,editTextTextSoyIsim,editTextPhone;
    private Button buttonIsim,buttonSoyisim,buttonTel;
    private CheckBox checkBoxPassword;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private User u;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        init();

        checkBoxPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    text_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Log.i("tip", String.valueOf(text_password.getInputType()));

                }
            }
        });

    }

    public void init(){
        text_isim = (TextView) findViewById(R.id.text_isim);
        text_soyisim = (TextView) findViewById(R.id.text_soyisim);
        text_tel = (TextView) findViewById(R.id.text_tel);
        text_mail = (TextView) findViewById(R.id.text_mail);
        text_password = (TextView) findViewById(R.id.text_password);
        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);
        editTextTextSoyIsim = (EditText) findViewById(R.id.editTextTextSoyIsim);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        buttonIsim = (Button) findViewById(R.id.buttonIsim);
        buttonSoyisim = (Button) findViewById(R.id.buttonSoyisim);
        buttonTel = (Button) findViewById(R.id.buttonTel);
        checkBoxPassword = (CheckBox) findViewById(R.id.checkBoxPassword);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        dataUser();

        buttonIsim.setOnClickListener(this);
        buttonSoyisim.setOnClickListener(this);
        buttonTel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonIsim:
                if (editTextTextPersonName.getText().toString().isEmpty()){
                    Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("users").child(user.getUid()).child("isim").setValue(editTextTextPersonName.getText().toString());
                dataUser();
                Toast.makeText(context,"İsim Değiştirildi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSoyisim:
                if (editTextTextSoyIsim.getText().toString().isEmpty()){
                    Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("users").child(user.getUid()).child("soyisim").setValue(editTextTextSoyIsim.getText().toString());
                dataUser();
                Toast.makeText(context,"Soyisim Değiştirildi",Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonTel:
                if (editTextPhone.getText().toString().isEmpty()){
                    Toast.makeText(context,"Bilgiler Eksik",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child("users").child(user.getUid()).child("tel").setValue(editTextPhone.getText().toString());
                dataUser();
                Toast.makeText(context,"Telefon Numarası Değiştirildi",Toast.LENGTH_SHORT).show();
                if(true){
                    return;
                }
                break;
        }
    }

    public void dataUser(){
        reference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                text_tel.setText(u.getTel());
                text_isim.setText(u.getIsim());
                text_soyisim.setText(u.getSoyisim());
                text_password.setText(u.getSifre());
                text_mail.setText(u.getMail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}