package com.example.giftishare.data.remote.firebase;

import android.util.Log;

import com.example.giftishare.data.model.Coupon;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class AppFirebaseDbHelper implements FirebaseDbHelper {

    public static final String TAG = AppFirebaseDbHelper.class.getSimpleName();

    public final static String FIREBASE_CATEGORY_COUPONS = "coupons";

    private static volatile AppFirebaseDbHelper INSTANCE;

    private final DatabaseReference mDatabase;

    private AppFirebaseDbHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static AppFirebaseDbHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (AppFirebaseDbHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppFirebaseDbHelper();
                }
            }
        }
        return INSTANCE;
    }

    // POST /coupons/:categoryName/:couponId
    @Override
    public void saveSaleCoupon(Coupon coupon) {
        // String key = coupon.getId();
        String category = coupon.getCategory();
        String key = mDatabase.child(FIREBASE_CATEGORY_COUPONS).child(category).push().getKey();
        Map<String, Object> couponValues = coupon.toMap();
        mDatabase.child(FIREBASE_CATEGORY_COUPONS)
                .child(category)
                .child(key)
                .updateChildren(couponValues);
    }


    @Override
    public void getSaleCoupons(String category, ValueEventListener listener) {
        mDatabase.child(FIREBASE_CATEGORY_COUPONS).child(category).addListenerForSingleValueEvent(listener);
    }

    @Override
    public void deleteCoupon(String category, String id) {
        mDatabase.child(FIREBASE_CATEGORY_COUPONS)
                .child(category)
                .orderByChild("id")
                .equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });
    }
}
