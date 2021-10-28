package tr.yildiz.cafe_management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class LogsActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView texttarih, textbutton, textbuttonAllDel;
    private Context context = this;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private SimpleDateFormat sdf;
    private String year,month,day;
    private ArrayList<String> zaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        init();
    }

    public void init(){
        texttarih = (TextView) findViewById((R.id.texttarih));
        textbutton = (TextView) findViewById((R.id.textbutton));
        textbuttonAllDel = (TextView) findViewById((R.id.textbuttonAllDel));
        calendarView = (CalendarView) findViewById((R.id.calendarView));

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        zaman = new ArrayList<String>();

        sdf = new SimpleDateFormat("yyyy");
        year = sdf.format(new Date());
        sdf = new SimpleDateFormat("MM");
        month = sdf.format(new Date());
        sdf = new SimpleDateFormat("dd");
        day = sdf.format(new Date());

        zaman.add(year);
        zaman.add(month);
        zaman.add(day);

        texttarih.setText(zaman.get(2)+"/"+zaman.get(1)+"/"+zaman.get(0));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
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

                texttarih.setText(zaman.get(2)+"/"+zaman.get(1)+"/"+zaman.get(0));
            }
        });

        textbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowLogsActivity.class);
                intent.putStringArrayListExtra("time",zaman);
                startActivity(intent);
            }
        });

        textbuttonAllDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("dukkanlar").child(user.getUid()).child("logs").removeValue();
                Toast.makeText(context,"Bütün Hareketler Silindi",Toast.LENGTH_SHORT).show();

            }
        });

    }
}