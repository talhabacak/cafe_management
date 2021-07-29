package tr.yildiz.cafe_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AyarlarUrunActivity extends AppCompatActivity {
    private Button buttonMenu,buttonEkle,buttonUpdate,buttonSil;
    private EditText edit_text_ekle,editTextNumberDecimal_ekleAlis,editTextNumberDecimal_ekleSatis,editTextNumberDecimal_updateAlis,editTextNumberDecimal_updateSatis;
    private Spinner spinnerSil,spinnerUpdateSinif,spinnerUpdateIsim,spinnerEkleSinif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar_urun);

        init();
    }

    public void init(){
        buttonMenu = (Button) findViewById((R.id.buttonMenu));
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        buttonUpdate = (Button) findViewById((R.id.buttonUpdate));
        buttonSil = (Button) findViewById((R.id.buttonSil));
        edit_text_ekle = (EditText) findViewById((R.id.edit_text_ekle));
        editTextNumberDecimal_ekleAlis = (EditText) findViewById((R.id.editTextNumberDecimal_ekleAlis));
        editTextNumberDecimal_ekleSatis = (EditText) findViewById((R.id.editTextNumberDecimal_ekleSatis));
        editTextNumberDecimal_updateAlis = (EditText) findViewById((R.id.editTextNumberDecimal_updateAlis));
        editTextNumberDecimal_updateSatis = (EditText) findViewById((R.id.editTextNumberDecimal_updateSatis));
        spinnerSil = (Spinner) findViewById((R.id.spinnerSil));
        spinnerUpdateSinif = (Spinner) findViewById((R.id.spinnerUpdateSinif));
        spinnerUpdateIsim = (Spinner) findViewById((R.id.spinnerUpdateIsim));
        spinnerEkleSinif = (Spinner) findViewById((R.id.spinnerEkleSinif));

    }

}
