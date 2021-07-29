package tr.yildiz.cafe_management;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import tr.yildiz.cafe_management.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText username,password;
    private CheckBox checkBox;
    private TextView textViewToSign;
    private Button login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        textViewToSign.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    public void init(){
        textViewToSign = (TextView) findViewById((R.id.textViewToSign));
        username = (EditText) findViewById((R.id.username));
        password = (EditText) findViewById((R.id.password));
        login = (Button) findViewById((R.id.login));
        checkBox = (CheckBox) findViewById((R.id.checkBox));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                Intent intent = new Intent(this, AnaSayfaActivity.class);
                startActivity(intent);
                break;

            case R.id.textViewToSign:
                Intent intent1 = new Intent(this, SignActivity.class);
                startActivity(intent1);
                break;

        }

    }
}