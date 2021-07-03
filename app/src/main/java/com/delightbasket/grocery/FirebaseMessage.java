package com.delightbasket.grocery;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.delightbasket.grocery.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseMessage extends FirebaseMessagingService {
    private static final String TAG = "mmmmmm";


    public FirebaseMessage() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if(remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "01";
            if(remoteMessage.getNotification().getIcon() != null) {
                String imageUri = remoteMessage.getNotification().getIcon();

                Bitmap bitmap = getBitmapfromUrl(imageUri);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(icon)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap))
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setLights(Color.RED, 1000, 300)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.ic_launcher_foreground);


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String name = "Channel_001";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());


            } else {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(icon)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setLights(Color.RED, 1000, 300)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setSmallIcon(R.drawable.ic_launcher_foreground);


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    String name = "Channel_001";
                    String description = "Channel Description";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;

                    NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                    channel.setDescription(description);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(0, notificationBuilder.build());
            }


        }

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG, "getBitmapfromUrl: " + imageUrl);
            return bitmap;

        } catch(Exception e) {
            Log.d(TAG, "getBitmapfromUrl: " + e);
            e.printStackTrace();
            return null;

        }
    }
}
