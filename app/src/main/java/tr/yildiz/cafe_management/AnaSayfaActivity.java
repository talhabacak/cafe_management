package tr.yildiz.cafe_management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AnaSayfaActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView TextViewKisisel,TextViewGelirGider,TextViewManage,TextViewDepo,TextViewMasalar;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);
        init();
    }

    public void init(){
        TextViewKisisel = (TextView) findViewById((R.id.TextViewKisisel));
        TextViewGelirGider = (TextView) findViewById((R.id.TextViewGelirGider));
        TextViewManage = (TextView) findViewById((R.id.TextViewManage));
        TextViewDepo = (TextView) findViewById((R.id.TextViewDepo));
        TextViewMasalar = (TextView) findViewById((R.id.TextViewMasalar));

        TextViewKisisel.setOnClickListener((View.OnClickListener) context);
        TextViewGelirGider.setOnClickListener((View.OnClickListener) context);
        TextViewManage.setOnClickListener((View.OnClickListener) context);
        TextViewDepo.setOnClickListener((View.OnClickListener) context);
        TextViewMasalar.setOnClickListener((View.OnClickListener) context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.TextViewMasalar:
                Intent intent1 = new Intent(context, MasalarActivity.class);
                startActivity(intent1);
                break;

            case R.id.TextViewDepo:
                Intent intent2 = new Intent(context, DepoActivity.class);
                startActivity(intent2);
                break;

            case R.id.TextViewManage:
                Intent intent3 = new Intent(context, ManageActivity.class);
                startActivity(intent3);
                break;

            case R.id.TextViewGelirGider:
                Intent intent4 = new Intent(context, GelirGiderActivity.class);
                startActivity(intent4);
                break;
            case R.id.TextViewKisisel:
                Intent intent5 = new Intent(context, KisiselActivity.class);
                startActivity(intent5);
                break;
        }
    }

    //Intent intent1 = new Intent(this_activity,MenuActivity.class);
}