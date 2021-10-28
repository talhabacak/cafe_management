package tr.yildiz.cafe_management;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class MyIntentService extends IntentService {

    private static final  String TAG = MyIntentService.class.getSimpleName();
    private Integer kirmizi, turuncu, mode = 1;
    private String mekan, sonFormat;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Vibrator vb;
    private ArrayList<MasaIsim> masaIsim;
    private ArrayList<String> siniflar;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MyIntentService() {
        super("MyWorkerThread");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        kirmizi = Integer.parseInt(intent.getStringExtra("kirmizi"));
        turuncu = Integer.parseInt(intent.getStringExtra("turuncu"));
        mekan = intent.getStringExtra("mekan");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        siniflar = new ArrayList<String>();
        masaIsim = new ArrayList<MasaIsim>();
        Integer sleepCount = kirmizi;

        data();

        Log.i("kirmizi", String.valueOf(kirmizi));
        while (true){
            int count = 0;
            String notiff = "";
            for (MasaIsim countMasa:masaIsim
                 ) {
                sonFormat = countMasa.getSonFormat();
                if (!sonFormat.isEmpty()){
                    long time;
                    try {
                        time = betweenTime(sonFormat);
                        if(time >= kirmizi){
                            reference.child("dukkanlar").child(mekan).child("masalar").child(siniflar.get(count)).child(countMasa.getIsim()).child("mode").setValue(3);
                            mode = 3;
                            if(time == kirmizi) {
                                notiff += siniflar.get(count) + " - " + countMasa.getIsim() + "\n";
                                vb.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
                            }
                        }
                        else if(time >= turuncu){
                            reference.child("dukkanlar").child(mekan).child("masalar").child(siniflar.get(count)).child(countMasa.getIsim()).child("mode").setValue(2);
                            mode = 2;
                        }
                        else{
                            reference.child("dukkanlar").child(mekan).child("masalar").child(siniflar.get(count)).child(countMasa.getIsim()).child("mode").setValue(1);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    if(countMasa.getAc().isEmpty()){
                        reference.child("dukkanlar").child(mekan).child("masalar").child(siniflar.get(count)).child(countMasa.getIsim()).child("mode").setValue(0);
                    }
                }
                count++;
            }

            if (!notiff.isEmpty()){
                Notif(notiff);
            }

            try {
                Thread.sleep(59000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public long betweenTime (String time1) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm");

        String time2 = simpleDateFormat.format(new Date());

        // Parsing the Time Period
        Date date1 = simpleDateFormat.parse(time1);
        Date date2 = simpleDateFormat.parse(time2);

        // Calculating the difference in milliseconds
        long differenceInMilliSeconds = Math.abs(date2.getTime() - date1.getTime());
        long differenceInMinutes = (differenceInMilliSeconds / (60 * 1000));

        return differenceInMinutes;
    }

    public void data(){
        reference.child("dukkanlar").child(mekan).child("masalar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                reference.child("dukkanlar").child(mekan).child("masalar").child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        MasaIsim ss = snapshot.getValue(MasaIsim.class);
                        siniflar.add(s);
                        masaIsim.add(ss);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String s = snapshot.getKey();
                reference.child("dukkanlar").child(mekan).child("masalar").child(s).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        MasaIsim ss = snapshot.getValue(MasaIsim.class);
                        int i = 0;
                        for (MasaIsim m:masaIsim
                        ) {
                            if(ss.getIsim().equals(m.getIsim()) && siniflar.get(i).equals(s)){
                                m.setSonFormat(ss.getSonFormat());
                            }
                            i++;
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Notif(String veri){
        String CHANNEL_ID = "myNotification";
        String CHANNEL_NAME = "Masa Uyarı";
        int NOTIFICATION_ID = 52;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone soundRingtone = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
        soundRingtone.play();

        AudioAttributes att = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_feedback_24)
                    .setContentTitle("Masa Uyarısı")
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
                //    .setStyle(new NotificationCompat.MessagingStyle())
                 //   .setVibrate()
                 //   .setLights(Color.RED)
                    .setSound(soundUri)
                    .setContentText(veri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)

                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }
        else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_feedback_24)
                    .setContentTitle("Masa Uyarısı")
                    .setWhen(System.currentTimeMillis())
                    .setShowWhen(true)
             //       .setLights()
               //     .setVibrate()
                    .setSound(soundUri)
                    .setContentText(veri)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)

                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }
    }
}
