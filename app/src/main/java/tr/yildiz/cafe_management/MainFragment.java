package tr.yildiz.cafe_management;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainFragment extends BottomSheetDialogFragment implements View.OnClickListener{
    private TextView masalar,depo,manage,business,general,options,siparis,contacts,admin,bildirim,bildirimAdmin,logs;
    private ImageButton imageButtonClose,imageButtonOut;
    private OnClickListener listener;
    private String yer,mod,status,notif,notifAdmin,odeyetki,silyetki,depoyetki,orderyetki;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    public void init(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        masalar = (TextView) getView().findViewById((R.id.masalar));
        siparis = (TextView) getView().findViewById((R.id.siparis));
        depo = (TextView) getView().findViewById((R.id.depo));
        manage = (TextView) getView().findViewById((R.id.manage));
        logs = (TextView) getView().findViewById((R.id.logs));
        business = (TextView) getView().findViewById((R.id.business));
        general = (TextView) getView().findViewById((R.id.general));
        options = (TextView) getView().findViewById((R.id.options));
        contacts = (TextView) getView().findViewById((R.id.contacts));
        bildirim = (TextView) getView().findViewById((R.id.bildirim));
        bildirimAdmin = (TextView) getView().findViewById((R.id.bildirimAdmin));
        admin = (TextView) getView().findViewById((R.id.admin));
        imageButtonClose = (ImageButton) getView().findViewById((R.id.imageButtonClose));
        imageButtonOut = (ImageButton) getView().findViewById((R.id.imageButtonOut));


        masalar.setOnClickListener(this);
        depo.setOnClickListener(this);
        manage.setOnClickListener(this);
        logs.setOnClickListener(this);
        business.setOnClickListener(this);
        general.setOnClickListener(this);
        imageButtonClose.setOnClickListener(this);
        imageButtonOut.setOnClickListener(this);
        options.setOnClickListener(this);
        siparis.setOnClickListener(this);
        contacts.setOnClickListener(this);
        admin.setOnClickListener(this);

        yer = getArguments().getString("yer");
        mod = getArguments().getString("mod");
        status = getArguments().getString("status");
        notif = getArguments().getString("bildirim");
        notifAdmin = getArguments().getString("bildirimAdmin");

        Log.i("bildirimm",notif+"-"+notifAdmin);

        if(!mod.equals("-1")){
            admin.setVisibility(View.GONE);
            bildirimAdmin.setVisibility(View.GONE);
            if(notif.equals("1")){
                bildirim.setVisibility(View.VISIBLE);
            }
            else {
                bildirim.setVisibility(View.INVISIBLE);
            }
        }
        else {
            contacts.setVisibility(View.GONE);
            bildirim.setVisibility(View.GONE);
            if(notifAdmin.equals("1")){
                bildirimAdmin.setVisibility(View.VISIBLE);
            }
            else {
                bildirimAdmin.setVisibility(View.INVISIBLE);
            }

        }

        if(status.equals("-10")){
            masalar.setVisibility(View.INVISIBLE);
            depo.setVisibility(View.GONE);
            manage.setVisibility(View.GONE);
            business.setVisibility(View.GONE);
            general.setVisibility(View.GONE);
            siparis.setVisibility(View.GONE);
            logs.setVisibility(View.GONE);
        }

        if(mod.equals("0")){
            depoyetki = getArguments().getString("depoyetki");
            orderyetki = getArguments().getString("orderyetki");

            if(depoyetki.equals("0")){
                depo.setVisibility(View.GONE);
            }

            if(orderyetki.equals("0")){
                siparis.setVisibility(View.GONE);
            }

            general.setVisibility(View.GONE);
            business.setVisibility(View.GONE);
            manage.setVisibility(View.GONE);
            logs.setVisibility(View.GONE);
            if(yer.isEmpty()){
                masalar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.masalar:
                listener.onClickMasa();
                break;
            case R.id.depo:
                listener.onClickDepo();
                break;
            case R.id.manage:
                listener.onClickManage();
                break;
            case R.id.logs:
                listener.onClickLogs();
                break;
            case R.id.business:
                listener.onClickBusiness();
                break;
            case R.id.general:
                listener.onClickGeneral();
                break;
            case R.id.imageButtonClose:
                listener.onClickClose();
                break;
            case R.id.imageButtonOut:
                listener.onClickOut();
                break;
            case R.id.options:
                listener.onClickOption();
                break;
            case R.id.siparis:
                listener.onClickSiparis();
                break;
            case R.id.contacts:
                listener.onClickContact();
                break;
            case R.id.admin:
                listener.onClickAdmin();
                break;
        }
    }

    public interface OnClickListener{
        void onClickClose();
        void onClickOut();
        void onClickMasa();
        void onClickDepo();
        void onClickBusiness();
        void onClickGeneral();
        void onClickManage();
        void onClickOption();
        void onClickSiparis();
        void onClickContact();
        void onClickAdmin();
        void onClickLogs();
    }

    public void setOnItemClickListener(OnClickListener listener){
        this.listener = listener;
    }


}