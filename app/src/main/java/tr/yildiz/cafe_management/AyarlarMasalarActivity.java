package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AyarlarMasalarActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Dialog inputMasa;
    private EditText diaolog_masa_isim;
    private Button diaolog_masa_ekle,diaolog_masa_iptal;
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar_masalar);

        init();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogMasa();
            }
        });
    }

    public void init(){
        recyclerView = (RecyclerView) findViewById((R.id.Masalar));

    }

    public void showDialogMasa(){
        inputMasa = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(inputMasa.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        inputMasa.setContentView(R.layout.dialog_masa_ekle);

        diaolog_masa_isim = (EditText) inputMasa.findViewById(R.id.diaolog_masa_isim);
        diaolog_masa_ekle = (Button) inputMasa.findViewById(R.id.diaolog_masa_ekle);
        diaolog_masa_iptal = (Button) inputMasa.findViewById(R.id.diaolog_masa_iptal);

        diaolog_masa_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Masa Eklendi",Toast.LENGTH_SHORT).show();
            }
        });

        diaolog_masa_iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMasa.dismiss();
            }
        });

        inputMasa.getWindow().setAttributes(params);
        inputMasa.show();
    }

}