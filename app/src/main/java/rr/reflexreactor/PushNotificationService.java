package rr.reflexreactor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Shweta on 1/17/2016.
 */
public class PushNotificationService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        String title=data.getString("title");

        Log.d("pushMsg:",message);
        if(title.equals("game_request_msg"))
        {
            String sender=data.getString("sender");
            String game_id=data.getString("game_id");
            Log.d("game-request","sender:"+sender+" game_id:"+game_id);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.appicon)
                            .setContentTitle("New request")
                            .setContentText("You have a new request from "+sender);
            Intent notificationIntent = new Intent(getApplicationContext(), RequestActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.putExtra("game_id", game_id);
            notificationIntent.putExtra("sender_name",sender);
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(intent);
            //mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            Notification notification=mBuilder.build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            int mId=1;
            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            mNotificationManager.notify(mId,notification);
        }
        else if(title.equals("start_game"))
        {
            Log.d("pushMsg:","start_game");
            if(RequestActivity.isActivityVisible)
           {
               String game_id=data.getString("game_id");

               Intent intent=new Intent("start_game");

               intent.putExtra("start_game_id",game_id);
               LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

               localBroadcastManager.sendBroadcast(intent);

           }

           /* NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                            .setContentTitle("Result")
                            .setContentText("All players have accepted your request");
            Intent notificationIntent = new Intent(getApplicationContext(), ResultActivity2.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //notificationIntent.putExtra("game_id", game_id);
            //notificationIntent.putExtra("sender_name",sender);
            PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(intent);
            //mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            Notification notification=mBuilder.build();
            notification.defaults |= Notification.DEFAULT_SOUND;
            int mId=2;
            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            mNotificationManager.notify(mId,notification);*/


        }
        else if(title.equals("host_left"))
        {
            Log.d("pushMsg:","host_left");
            if(RequestActivity.isActivityVisible)
            {
                String game_id=data.getString("game_id");
                String sender_name=data.getString("sender_name");

                Intent intent=new Intent("game_terminated");

                intent.putExtra("game_id",game_id);
                intent.putExtra("sender_name",sender_name);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

                localBroadcastManager.sendBroadcast(intent);

            }

        }

        //createNotification(mTitle, push_msg);
        /*NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("New request")
                        .setContentText("Hello World!");*/
        // Creates an explicit intent for an Activity in your app
        // int mId=0;
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent resultIntent=new Intent(getApplicationContext(),RequestActivity.class);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RequestActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );*/

        //PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),0,resultIntent,0);


    }


}