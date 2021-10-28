package tr.yildiz.cafe_management;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class adapterContact extends RecyclerView.Adapter<adapterContact.tanim> {
    Context context;
    List<Message> list;
    String kim,mekan;

    public adapterContact(Context context, List<Message> list, String kim, String mekan) {
        this.context = context;
        this.list = list;
        this.kim = kim;
        this.mekan = mekan;
    }

    @NonNull
    @Override
    public adapterContact.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_contact, parent, false);
        return new adapterContact.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterContact.tanim holder, int position) {
         holder.message.setText(list.get(position).getText());
        if(list.get(position).getWho().equals("Admin")){
            holder.contactL.getBackground().setTint(Color.parseColor("#706F6D"));
            holder.message.setTextColor(Color.parseColor("#000000"));
        }
        else{
            holder.contactL.getBackground().setTint(Color.parseColor("#191717"));
            holder.message.setTextColor(Color.parseColor("#EDC4B1"));
        }

        if(kim.equals("Admin")){
            if(list.get(position).getWho().equals("Sen")){
                holder.reference.child("dukkanlar").child(mekan).child("contact").child(list.get(position).getTime()).child("visibled").setValue(true);
            }
        }
        else{
            if(list.get(position).getWho().equals("Admin")){
                holder.reference.child("dukkanlar").child(mekan).child("contact").child(list.get(position).getTime()).child("visibled").setValue(true);
            }
        }

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
        TextView message;
        LinearLayout contactL;
        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser users;

        public tanim(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
            contactL = (LinearLayout) itemView.findViewById(R.id.contactL);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            users = auth.getCurrentUser();

        }
    }
}
