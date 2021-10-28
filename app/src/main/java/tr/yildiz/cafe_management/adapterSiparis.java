package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class adapterSiparis extends RecyclerView.Adapter<adapterSiparis.tanim>{

    Context context;
    List<Siparis> list;
    String mekan;

    public adapterSiparis(Context context, List<Siparis> list,String mekan) {
        this.context = context;
        this.list = list;
        this.mekan = mekan;
    }

    @NonNull
    @Override
    public adapterSiparis.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_siparis,parent,false);
        return new adapterSiparis.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterSiparis.tanim holder, int position) {
        holder.isim.setText(list.get(position).getIsim());
        holder.adet.setText(list.get(position).getAdet());
        holder.time.setText(list.get(position).getTime());
        holder.masanum.setText(list.get(position).getMasasinif()+" - "+list.get(position).getMasanum());

        if(list.get(position).getIptal().equals("1")){
            holder.siparisL.setBackgroundColor(Color.parseColor("#9E2F2F"));
            holder.masanum.setText("Sipariş İptal | "+list.get(position).getMasasinif()+" - "+list.get(position).getMasanum());
        }

        Integer positionButton = position;

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.reference.child("dukkanlar").child(mekan).child("order").child("notCheck").child(list.get(positionButton).getTimeFormat()).removeValue();
                Toast.makeText(context,"Sipariş Silindi",Toast.LENGTH_SHORT).show();
                removeAt(positionButton);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public class tanim extends  RecyclerView.ViewHolder {
        TextView isim;
        TextView adet;
        TextView time;
        TextView masanum;
        ImageButton imageButton;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;
        SimpleDateFormat sdf;
        String year, month, day, hour, minute, second, milisecond;
        ArrayList<MasaUrun> masa;
        LinearLayout siparisL;

        public tanim(View itemView) {
            super(itemView);

            isim = (TextView) itemView.findViewById(R.id.isim);
            adet = (TextView) itemView.findViewById(R.id.adet);
            time = (TextView) itemView.findViewById(R.id.time);
            masanum = (TextView) itemView.findViewById(R.id.masanum);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);
            siparisL = (LinearLayout) itemView.findViewById(R.id.siparisL);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            masa = new ArrayList<MasaUrun>();

            sdf = new SimpleDateFormat("yyyy");
            year = sdf.format(new Date());
            sdf = new SimpleDateFormat("MM");
            month = sdf.format(new Date());
            sdf = new SimpleDateFormat("dd");
            day = sdf.format(new Date());
            sdf = new SimpleDateFormat("HH");
            hour = sdf.format(new Date());
            sdf = new SimpleDateFormat("mm");
            minute = sdf.format(new Date());
            sdf = new SimpleDateFormat("ss");
            second = sdf.format(new Date());
            sdf = new SimpleDateFormat("SS");
            milisecond = sdf.format(new Date());

        }
    }
}
