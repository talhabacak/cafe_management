package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MasaActivity extends AppCompatActivity {
    private EditText isimkisisel;
    private RecyclerView recyclerView;
    private Context context = this;
    private Dialog inputUrun;
    private Button diaolog_urun_ekle,diaolog_urun_iptal;
    private Spinner spinner_sinif,spinner_urun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa);

        init();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                showDialogUrun();
            }
        });
    }

    public void init(){
        isimkisisel = (EditText) findViewById((R.id.isimkisisel));
        recyclerView = (RecyclerView) findViewById((R.id.Masa));

    }

    public void showDialogUrun(){
        inputUrun = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(inputUrun.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        inputUrun.setContentView(R.layout.dialog_urun_ekle);

        spinner_sinif = (Spinner) inputUrun.findViewById(R.id.spinner_sinif);
        spinner_urun = (Spinner) inputUrun.findViewById(R.id.spinner_urun);
        diaolog_urun_ekle = (Button) inputUrun.findViewById(R.id.diaolog_urun_ekle);
        diaolog_urun_iptal = (Button) inputUrun.findViewById(R.id.diaolog_urun_iptal);

        diaolog_urun_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Ürün Eklendi",Toast.LENGTH_SHORT).show();
            }
        });

        diaolog_urun_iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUrun.dismiss();
            }
        });

        inputUrun.getWindow().setAttributes(params);
        inputUrun.show();
    }

}