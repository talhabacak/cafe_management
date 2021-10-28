package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterSatisRapor extends RecyclerView.Adapter<adapterSatisRapor.tanim> {
    Context context;
    List<MasaUrun> list;

    public adapterSatisRapor(Context context, List<MasaUrun> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public adapterSatisRapor.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_satis_rapor, parent, false);
        return new adapterSatisRapor.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterSatisRapor.tanim holder, int position) {
        holder.urun_isim.setText(list.get(position).getIsimUrun());
        holder.urun_adet.setText(list.get(position).getAdet().toString());
        holder.urun_alis.setText(list.get(position).getAlisTutar().toString());
        holder.urun_satis.setText(list.get(position).getSatisTutar().toString());
        Double kar = list.get(position).getSatisTutar()-list.get(position).getAlisTutar();
        holder.urun_kar.setText(kar.toString());
        if(holder.status == 1){
            holder.urun_alis.setVisibility(View.VISIBLE);
            holder.urun_satis.setVisibility(View.VISIBLE);
            holder.urun_kar.setVisibility(View.VISIBLE);
        }
        else {
            holder.urun_alis.setVisibility(View.GONE);
            holder.urun_satis.setVisibility(View.GONE);
            holder.urun_kar.setVisibility(View.GONE);
        }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.status == 0){
                    holder.urun_alis.setVisibility(View.VISIBLE);
                    holder.urun_satis.setVisibility(View.VISIBLE);
                    holder.urun_kar.setVisibility(View.VISIBLE);
                    holder.status = 1;
                }
                else {
                    holder.urun_alis.setVisibility(View.GONE);
                    holder.urun_satis.setVisibility(View.GONE);
                    holder.urun_kar.setVisibility(View.GONE);
                    holder.status = 0;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class tanim extends RecyclerView.ViewHolder {
        TextView urun_isim,urun_adet,urun_alis,urun_satis,urun_kar;
        ImageButton imageButton;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;
        Integer status;

        public tanim(View itemView) {
            super(itemView);
            urun_isim = (TextView) itemView.findViewById(R.id.urun_isim);
            urun_adet = (TextView) itemView.findViewById(R.id.urun_adet);
            urun_alis = (TextView) itemView.findViewById(R.id.urun_alis);
            urun_satis = (TextView) itemView.findViewById(R.id.urun_satis);
            urun_kar = (TextView) itemView.findViewById(R.id.urun_kar);
            imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            status = 0;

        }
    }
}
