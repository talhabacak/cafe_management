package tr.yildiz.cafe_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterBusinessReport  extends RecyclerView.Adapter<adapterBusinessReport.tanim> {
    Context context;
    List<Economy> list;

    public adapterBusinessReport(Context context, List<Economy> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public adapterBusinessReport.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclet_business_report, parent, false);
        return new adapterBusinessReport.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterBusinessReport.tanim holder, int position) {
        holder.urun_isim.setText(list.get(position).getIsim());
        holder.urun_fiyat.setText(list.get(position).getFiyat().toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class tanim extends RecyclerView.ViewHolder {
        TextView urun_isim,urun_fiyat;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;

        public tanim(View itemView) {
            super(itemView);
            urun_isim = (TextView) itemView.findViewById(R.id.urun_isim);
            urun_fiyat = (TextView) itemView.findViewById(R.id.urun_fiyat);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

        }
    }
}
