package tr.yildiz.cafe_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AyarlarSinifActivity extends AppCompatActivity {
    private Button buttonEkle,buttonUpdate,buttonSil;
    private EditText edit_text_ekle,edit_text_isimYeni;
    private Spinner spinnerGuncelle,spinnerSil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar_sinif);

        init();
    }

    public void init(){
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        buttonUpdate = (Button) findViewById((R.id.buttonUpdate));
        buttonSil = (Button) findViewById((R.id.buttonSil));
        edit_text_ekle = (EditText) findViewById((R.id.edit_text_ekle));
        edit_text_isimYeni = (EditText) findViewById((R.id.edit_text_isimYeni));
        spinnerGuncelle = (Spinner) findViewById((R.id.spinnerGuncelle));
        spinnerSil = (Spinner) findViewById((R.id.spinnerSil));
    }


}