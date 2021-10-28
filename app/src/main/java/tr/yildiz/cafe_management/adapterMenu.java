package tr.yildiz.cafe_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterMenu extends RecyclerView.Adapter<adapterMenu.tanim> {
    Context context;
    List<Urun> list;

    public adapterMenu(Context context, List<Urun> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public adapterMenu.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_menu, parent, false);
        return new adapterMenu.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterMenu.tanim holder, int position) {
        holder.urun_isim.setText(list.get(position).getIsim());
        holder.urun_alis.setText(list.get(position).getAlis());
        holder.urun_satis.setText(list.get(position).getSatis());
        if(list.get(position).getDepo().equals("1")){
            holder.depo.setChecked(true);
        }
        else {
            holder.depo.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class tanim extends RecyclerView.ViewHolder {
        TextView urun_alis,urun_satis,urun_isim;
        CheckBox depo;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;

        public tanim(View itemView) {
            super(itemView);
            urun_alis = (TextView) itemView.findViewById(R.id.urun_alis);
            urun_satis = (TextView) itemView.findViewById(R.id.urun_satis);
            urun_isim = (TextView) itemView.findViewById(R.id.urun_isim);
            depo = (CheckBox) itemView.findViewById((R.id.depo));

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
        }
    }
}
