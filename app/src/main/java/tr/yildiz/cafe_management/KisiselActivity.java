package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class KisiselActivity extends AppCompatActivity implements View.OnClickListener {
    private CalendarView calendarBas;
    private Button tamam,iptal,giderEkle,gelirEkle,gelirGiderGoster;
    private RadioButton radioButtonDay,radioButtonMonth,radioButtonYear;
    private EditText edit_number, edit_title;
    private TextView tarih,d_tarih,d_title;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Context context = this;
    private ArrayList<String > zaman;
    private Integer yilBas, ayBas, gunBas;
    private Dialog input;
    private SimpleDateFormat sdf;
    private String year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisisel);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        init();

    }

    public void init(){
        tarih = (TextView) findViewById(R.id.tarih);
        gelirEkle = (Button) findViewById(R.id.gelirEkle);
        giderEkle = (Button) findViewById(R.id.giderEkle);
        gelirGiderGoster = (Button) findViewById(R.id.gelirGiderGoster);
        calendarBas = (CalendarView) findViewById(R.id.calendarBas);
        radioButtonDay = (RadioButton) findViewById(R.id.radioButtonDay);
        radioButtonMonth = (RadioButton) findViewById(R.id.radioButtonMonth);
        radioButtonYear = (RadioButton) findViewById(R.id.radioButtonYear);


        sdf = new SimpleDateFormat("yyyy");
        year = sdf.format(new Date());
        sdf = new SimpleDateFormat("MM");
        month = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd");
        day = sdf.format(new Date());

        zaman = new ArrayList<String>();

        zaman.add(year);
        zaman.add(month);
        zaman.add(day);

        tarih.setText(zaman.get(2)+"/"+zaman.get(1)+"/"+zaman.get(0));

        calendarBas.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                zaman.set(0,String.valueOf(year));
                month++;
                if(month<10){
                    zaman.set(1, "0"+ String.valueOf(month));
                }
                else {
                    zaman.set(1, String.valueOf(month));
                }
                if(dayOfMonth<10){
                    zaman.set(2, "0"+ String.valueOf(dayOfMonth));
                }
                else {
                    zaman.set(2, String.valueOf(dayOfMonth));
                }
                tarih.setText(zaman.get(2)+"/"+zaman.get(1)+"/"+zaman.get(0));
            }
        });

        gelirGiderGoster.setOnClickListener(this);
        gelirEkle.setOnClickListener(this);
        giderEkle.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gelirGiderGoster:
                if(zaman.get(0).equals("0")){
                    Toast.makeText(context,"Tarihini Seçiniz",Toast.LENGTH_SHORT).show();
                    return;
                }

                String timeControl = "0";
                if (radioButtonDay.isChecked()){
                    Log.i("gün","seçili");
                    timeControl = "1";
                    Intent intent1 = new Intent(context, GeneralReportActivity.class);
                    intent1.putExtra("timeControl",timeControl);
                    intent1.putStringArrayListExtra("time",zaman);
                    startActivity(intent1);
                }
                else if (radioButtonMonth.isChecked()){
                    Log.i("ay","seçili");
                    timeControl = "2";
                    Intent intent1 = new Intent(context, GeneralReportActivity.class);
                    intent1.putExtra("timeControl",timeControl);
                    intent1.putStringArrayListExtra("time",zaman);
                    startActivity(intent1);
                }
                else if (radioButtonYear.isChecked()){
                    Log.i("yıl","seçili");
                    timeControl = "3";
                    Intent intent1 = new Intent(context, GeneralReportActivity.class);
                    intent1.putExtra("timeControl",timeControl);
                    intent1.putStringArrayListExtra("time",zaman);
                    startActivity(intent1);
                }
                break;

            case R.id.giderEkle:
                showDialog("0");
                break;

            case R.id.gelirEkle:
                showDialog("1");
                break;
        }
    }

    public void showDialog(String control){
        input = new Dialog(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(input.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        input.setContentView(R.layout.diaolog_gelir_gider);

        tamam = (Button) input.findViewById(R.id.tamam);
        iptal = (Button) input.findViewById(R.id.iptal);
        edit_number = (EditText) input.findViewById(R.id.edit_number);
        edit_title = (EditText) input.findViewById(R.id.edit_title);
        d_tarih = (TextView) input.findViewById(R.id.d_tarih);
        d_title = (TextView) input.findViewById(R.id.d_title);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String time = simpleDateFormat.format(new Date());

        simpleDateFormat = new SimpleDateFormat("yyyy");
        String yil = simpleDateFormat.format(new Date());
        simpleDateFormat = new SimpleDateFormat("MM");
        String ay = simpleDateFormat.format(new Date());
        simpleDateFormat = new SimpleDateFormat("dd");
        String gun = simpleDateFormat.format(new Date());

        d_tarih.setText(time);
        if (control.equals("0")){
            d_title.setText("Gider Ekle");
        }
        else{
            d_title.setText("Gelir Ekle");
        }

        tamam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_number.getText().toString().isEmpty() || edit_title.getText().toString().isEmpty()){
                    Toast.makeText(context,"Bilgiler Eksik", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double num = Double.parseDouble(edit_number.getText().toString());
                Economy s = new Economy(edit_title.getText().toString(),num);
                if (control.equals("0")){
                    reference.child("dukkanlar").child(user.getUid()).child("statementExpenditureGeneral").child(yil).child(ay).child(gun).child(edit_title.getText().toString()).setValue(s);
                    Toast.makeText(context,"Gider Eklendi",Toast.LENGTH_SHORT).show();
                }
                else{
                    reference.child("dukkanlar").child(user.getUid()).child("statementIncomeGeneral").child(yil).child(ay).child(gun).child(edit_title.getText().toString()).setValue(s);
                    Toast.makeText(context,"Gelir Eklendi",Toast.LENGTH_SHORT).show();
                }
                input.dismiss();
            }
        });

        iptal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.dismiss();
            }
        });

        input.getWindow().setAttributes(params);
        input.show();
    }

}