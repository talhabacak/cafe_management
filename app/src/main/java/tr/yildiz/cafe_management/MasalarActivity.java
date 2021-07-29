package tr.yildiz.cafe_management;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MasalarActivity extends AppCompatActivity {
    private ListView masalar;
    private TextView masa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masalar);

        init();

     //     ArrayAdapter<TextView> adapterMasalar = new ArrayAdapter<TextView>(this, android.R.layout.simple_list_item_1,android.R.id.text1,masa);
    }

    public void init(){
      //  masalar = (ListView) findViewById(R.id.);


    }

}