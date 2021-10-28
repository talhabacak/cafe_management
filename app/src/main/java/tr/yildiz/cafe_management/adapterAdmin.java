package tr.yildiz.cafe_management;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class adapterAdmin extends RecyclerView.Adapter<adapterAdmin.tanim> {
    Context context;
    List<User> list;

    public adapterAdmin(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public adapterAdmin.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyler_admin, parent, false);
        return new adapterAdmin.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterAdmin.tanim holder, int position) {
        holder.mail.setText(list.get(position).getMail());
        holder.ad.setText(list.get(position).getIsim());
        holder.soyad.setText(list.get(position).getSoyisim());
        holder.id.setText(list.get(position).getUserId());

        String temp = list.get(position).getUserId();
        Integer pos = position;
        holder.id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContactActivity.class);
                intent.putExtra("mekan",temp);
                intent.putExtra("who","Admin");
                context.startActivity(intent);
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


    public class tanim extends RecyclerView.ViewHolder {
        TextView ad,soyad,mail,id;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser users;
        LinearLayout backId;

        public tanim(View itemView) {
            super(itemView);
            ad = (TextView) itemView.findViewById(R.id.ad);
            soyad = (TextView) itemView.findViewById(R.id.soyad);
            mail = (TextView) itemView.findViewById(R.id.mail);
            id = (TextView) itemView.findViewById(R.id.id);
            backId = (LinearLayout) itemView.findViewById(R.id.backId);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            users = auth.getCurrentUser();
        }
    }
}
