package com.hackupcteam.itrash;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marc on 02/04/2016.
 */
public class FireBase {

    private Context context;
    private Firebase myFirebaseRef;
    private ArrayList<Product> myList;

    public FireBase (Context c, ArrayList<Product> p){
        context = c;
        Firebase.setAndroidContext(c);
        myFirebaseRef = new Firebase("https://itrashtest.firebaseio.com/");
        myFirebaseRef.orderByChild("time");//En teoria ordena
        myList = p;


    }

    public void realTimeText(final ListAdapter adapter, final ListView listView){
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context);
        noti.setContentTitle("New trash!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri);
        NotificationManager mn = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mn.notify(1, noti.build());
        Firebase.setAndroidContext(context);
        myFirebaseRef.orderByChild("time");//En teoria ordena
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,String> map = new HashMap<String, String>();
                Product p;
                myList.clear();
                int id = -1;
                String name = "Default name";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    for(DataSnapshot postpost : postSnapshot.getChildren()){
                        String k = postpost.getKey();
                        name = postpost.getValue(String.class);
                        map.put(k,name);
                    }
                    if(!map.containsKey("smallImageURL")){
                        map.put("smallImageURL","http://fs01.androidpit.info/a/d5/1c/alydl-ofertas-aldi-lidl-y-dia-d51cb1-w240.png");
                    }
                    if(!map.containsKey("time")){
                        map.put("time","999999999");
                    }
                    p = new Product(Long.parseLong(map.get("ean")),map.get("name"),map.get("description"),map.get("smallImageURL"),map.get("price"),map.get("time"));
                    myList.add(p);
                    map.clear();

                }

                Collections.sort(myList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        Integer s1 = Integer.parseInt(p1.getTime());
                        Integer s2 = Integer.parseInt(p2.getTime());
                        System.out.print("Sorting....");
                        return s1.compareTo(s2);
                    }
                });

                listView.setAdapter(adapter);
                //System.out.print(dataSnapshot.toString()+"\n");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void removeElement(String s){
        Firebase.setAndroidContext(context);
        myFirebaseRef.child(s).runTransaction(new Transaction.Handler() {
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null); // This removes the node.
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
                // Handle completion
            }
        });
    }


}
