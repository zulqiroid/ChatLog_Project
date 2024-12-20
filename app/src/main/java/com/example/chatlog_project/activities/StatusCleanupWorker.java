package com.example.chatlog_project.activities;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StatusCleanupWorker extends Worker {
    public StatusCleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        long currentTime = new Date().getTime();
        long expiryTime = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("status");
        statusRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                task.getResult().getChildren().forEach(snapshot -> {
                    String userId = snapshot.getKey();
                    if (userId != null) {
                        snapshot.child("statuses").getChildren().forEach(statusSnapshot -> {
                            Long timestamp = statusSnapshot.child("timestamp").getValue(Long.class);
                            if (timestamp != null && currentTime - timestamp >= expiryTime) {
                                statusSnapshot.getRef().removeValue();
                            }
                        });

                        // Clean up user status metadata if no statuses are left
                        if (!snapshot.child("statuses").hasChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    }
                });
            }
        });

        return Result.success();
    }
}
