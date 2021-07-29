package tr.yildiz.cafe_management;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ManageActivity extends AppCompatActivity  implements View.OnClickListener{
    private Button buttonSinif,buttonUrun,buttonMasa,buttonKaydet,buttonEkle;
    private EditText editTextNumberDecimalTuruncu,editTextNumberDecimalKirmizi,edit_text_calisanEkle;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        init();
        buttonUrun.setOnClickListener(this);
        buttonSinif.setOnClickListener(this);
        buttonMasa.setOnClickListener(this);
        buttonKaydet.setOnClickListener(this);
        buttonEkle.setOnClickListener(this);
    }

    public void init(){
        recyclerView = (RecyclerView) findViewById((R.id.calisanlar));
        buttonSinif = (Button) findViewById((R.id.buttonSinif));
        buttonUrun = (Button) findViewById((R.id.buttonUrun));
        buttonMasa = (Button) findViewById((R.id.buttonMasa));
        buttonKaydet = (Button) findViewById((R.id.buttonKaydet));
        buttonEkle = (Button) findViewById((R.id.buttonEkle));
        editTextNumberDecimalTuruncu = (EditText) findViewById((R.id.editTextNumberDecimalTuruncu));
        editTextNumberDecimalKirmizi = (EditText) findViewById((R.id.editTextNumberDecimalKirmizi));
        edit_text_calisanEkle = (EditText) findViewById((R.id.edit_text_calisanEkle));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonUrun:
                Intent intentUrun = new Intent(this, AyarlarUrunActivity.class);
                startActivity(intentUrun);
                break;

            case R.id.buttonSinif:
                Intent intentSinif = new Intent(this, AyarlarSinifActivity.class);
                startActivity(intentSinif);
                break;

            case R.id.buttonMasa:
                Intent intentMasa = new Intent(this, AyarlarMasalarActivity.class);
                startActivity(intentMasa);
                break;

            case R.id.buttonKaydet:

                break;

            case R.id.buttonEkle:

                break;
        }
    }
}