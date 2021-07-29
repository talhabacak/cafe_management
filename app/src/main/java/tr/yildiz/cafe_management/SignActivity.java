package tr.yildiz.cafe_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class SignActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mail,isim,soyisim,password,passwordAgain;
    private Switch switch_mod;
    private Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        init();

        signin.setOnClickListener(this);
    }

    public void init(){
        mail = (EditText) findViewById((R.id.mail));
        isim = (EditText) findViewById((R.id.isim));
        soyisim = (EditText) findViewById((R.id.soyisim));
        password = (EditText) findViewById((R.id.password));
        passwordAgain = (EditText) findViewById((R.id.passwordAgain));
        switch_mod = (Switch) findViewById((R.id.switch_mod));
        signin = (Button) findViewById((R.id.signin));

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.signin){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}