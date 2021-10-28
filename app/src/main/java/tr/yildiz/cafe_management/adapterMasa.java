package tr.yildiz.cafe_management;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.ArrayList;
import java.util.List;

public class adapterMasa extends BaseAdapter {
    private ArrayList<MasaIsim> masa;
    private Context context;
    private TextView masaIsim,masaNo;
    private LinearLayout layoutG;

    public adapterMasa (ArrayList<MasaIsim> masa, Context context){
        this.masa = masa;
        this.context = context;
    }

    @Override
    public int getCount() {
        return masa.size();
    }

    @Override
    public Object getItem(int position) {
        return masa.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if( convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_masa, null);
        }
        masaIsim = convertView.findViewById(R.id.masaView);
        masaNo = convertView.findViewById(R.id.masaViewNo);
        layoutG = convertView.findViewById(R.id.layoutG);
        String masaIsimKisisel = "";
        if(masa.get(position).getIsimKisisel() != null){
            if(!masa.get(position).getIsimKisisel().isEmpty()){
                masaIsimKisisel = masa.get(position).getIsimKisisel();
            }
        }
        masaNo.setText(masa.get(position).getIsim());
        masaIsim.setText(masaIsimKisisel);

        if(masa.get(position).getMode() == 0){
            Drawable textDrawable = layoutG.getBackground();
            textDrawable = DrawableCompat.wrap(textDrawable);
            DrawableCompat.setTint(textDrawable, Color.parseColor("#7986CB"));
            layoutG.setBackground(textDrawable);
        }
        else if(masa.get(position).getMode() == 1){
            Drawable textDrawable = layoutG.getBackground();
            textDrawable = DrawableCompat.wrap(textDrawable);
            DrawableCompat.setTint(textDrawable, Color.parseColor("#64FA64"));
            layoutG.setBackground(textDrawable);
        }
        else if(masa.get(position).getMode() == 2){
            Drawable textDrawable = layoutG.getBackground();
            textDrawable = DrawableCompat.wrap(textDrawable);
            DrawableCompat.setTint(textDrawable, Color.parseColor("#FFB941"));
            layoutG.setBackground(textDrawable);
        }
        else if(masa.get(position).getMode() == 3){
            Drawable textDrawable = layoutG.getBackground();
            textDrawable = DrawableCompat.wrap(textDrawable);
            DrawableCompat.setTint(textDrawable, Color.parseColor("#FF3030"));
            layoutG.setBackground(textDrawable);
        }
        else if(masa.get(position).getMode() == 4){
            Drawable textDrawable = layoutG.getBackground();
            textDrawable = DrawableCompat.wrap(textDrawable);
            DrawableCompat.setTint(textDrawable, Color.parseColor("#A6FFDF"));
            layoutG.setBackground(textDrawable);
        }

        return convertView;
    }

}
