package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class adapterCalisan extends RecyclerView.Adapter<adapterCalisan.tanim> {
    Context context;
    List<Worker> list;

    public adapterCalisan(Context context, List<Worker> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public adapterCalisan.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_calisan, parent, false);
        return new adapterCalisan.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterCalisan.tanim holder, int position) {
        holder.isim.setText(list.get(position).getIsim());
        holder.soyisim.setText(list.get(position).getSoyisim());
        holder.tel.setText(list.get(position).getTel());

        holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(position).getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Worker w = snapshot.getValue(Worker.class);
                if(w.getOde()==1){
                    holder.odeme.setChecked(true);
                }
                else{
                    holder.odeme.setChecked(false);
                }

                if(w.getDepo()==1){
                    holder.depo.setChecked(true);
                }
                else{
                    holder.depo.setChecked(false);
                }

                if(w.getSil()==1){
                    holder.sil.setChecked(true);
                }
                else{
                    holder.sil.setChecked(false);
                }

                if(w.getOrder()==1){
                    holder.siparis.setChecked(true);
                }
                else{
                    holder.siparis.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Integer pos = position;

        holder.odeme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("odeme").setValue(1);
                }
                else{
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("odeme").setValue(0);
                }
            }
        });

        holder.sil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("sil").setValue(1);
                }
                else{
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("sil").setValue(0);
                }

            }
        });

        holder.depo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("depo").setValue(1);
                }
                else{
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("depo").setValue(0);
                }

            }
        });

        holder.siparis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("order").setValue(1);
                }
                else{
                    holder.reference.child("dukkanlar").child(holder.user.getUid()).child("calisanlar").child(list.get(pos).getUserId()).child("order").setValue(0);
                }

            }
        });

        holder.calisanSil(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class tanim extends RecyclerView.ViewHolder {
        TextView isim, soyisim, tel;
        Button buttonSil;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;
        CheckBox odeme,sil,siparis,depo;

        public tanim(View itemView) {
            super(itemView);
            isim = (TextView) itemView.findViewById(R.id.isim);
            soyisim = (TextView) itemView.findViewById(R.id.soyisim);
            tel = (TextView) itemView.findViewById(R.id.tel);
            buttonSil = (Button) itemView.findViewById(R.id.buttonSil);
            odeme = (CheckBox) itemView.findViewById((R.id.odeme));
            sil = (CheckBox) itemView.findViewById((R.id.sil));
            depo = (CheckBox) itemView.findViewById((R.id.depo));
            siparis = (CheckBox) itemView.findViewById((R.id.siparis));

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
        }

        public void calisanSil(Worker u){
            buttonSil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child("dukkanlar").child(user.getUid()).child("calisanlar").child(u.getUserId()).removeValue();
                    reference.child("users").child(u.getUserId()).child("yer").setValue(u.getUserId());
                    Toast.makeText(context,"Çalışan Silindi",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
