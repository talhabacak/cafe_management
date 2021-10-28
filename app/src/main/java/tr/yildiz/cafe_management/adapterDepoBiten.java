package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterDepoBiten extends RecyclerView.Adapter<adapterDepoBiten.tanim> {
    Context context;
    private Dialog SilAdet, OdemeAdet;
    List<DepoUrun> list;
    Integer adetInput;

    public adapterDepoBiten(Context context, List<DepoUrun> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public adapterDepoBiten.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_depobiten, parent, false);
        return new adapterDepoBiten.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterDepoBiten.tanim holder, int position) {
        holder.urun_isim.setText(list.get(position).getIsim());
        holder.urun_adet.setText(list.get(position).getAdet());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class tanim extends RecyclerView.ViewHolder {
        TextView urun_isim;
        TextView urun_adet;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;

        public tanim(View itemView) {
            super(itemView);
            urun_isim = (TextView) itemView.findViewById(R.id.urun_isim);
            urun_adet = (TextView) itemView.findViewById(R.id.urun_adet);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

        }
    }
}