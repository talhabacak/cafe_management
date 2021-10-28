package tr.yildiz.cafe_management;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
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

public class adapterLog extends RecyclerView.Adapter<adapterLog.tanim> {
    Context context;
    List<Loglar> list;

    public adapterLog(Context context, List<Loglar> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public adapterLog.tanim onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_log, parent, false);
        return new adapterLog.tanim(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterLog.tanim holder, int position) {
        holder.name.setText(list.get(position).getWho());
        holder.activity.setText(list.get(position).getScreen());
        holder.timee.setText(list.get(position).getTime());
        holder.status.setText(list.get(position).getWhat());


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
        TextView name, activity, timee, status;

        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseAuth auth;
        FirebaseUser user;

        public tanim(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            activity = (TextView) itemView.findViewById(R.id.activity);
            timee = (TextView) itemView.findViewById(R.id.timee);
            status = (TextView) itemView.findViewById(R.id.status);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

        }
    }
}
