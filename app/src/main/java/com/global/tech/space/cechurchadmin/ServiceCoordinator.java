package com.global.tech.space.cechurchadmin;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.global.tech.space.cechurchadmin.attendance.AttendanceActivity;
import com.global.tech.space.cechurchadmin.attendance.SearchMembersActivity;
import com.global.tech.space.cechurchadmin.models.ChurchService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceCoordinator extends Service {

    private CEService service;
    private ChurchService current;
//    private final Coordinator coordinator = new Coordinator();

    public ServiceCoordinator() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = CEService.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beginService();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return coordinator;
        return null;
    }

    public static void beginService(Context context, String name )
    {
        Intent intent = new Intent( context, ServiceCoordinator.class );
        intent.putExtra( "service_name", name );

        ContextCompat.startForegroundService( context, intent );
    }

    public void beginService()
    {
        Log.d( "Service_Coordinator", "Coordinating the service" );

        current = new ChurchService();
        ServiceCoordinator.this.notify( current.name );

        Call<ChurchService> begin = service.getCeApi().beginService(current);
        begin.enqueue(new Callback<ChurchService>() {
            @Override
            public void onResponse(Call<ChurchService> call, Response<ChurchService> response) {
                if ( response.isSuccessful() )
                {
                    current = response.body();
                    if  ( service != null )
                    {
//                        ServiceCoordinator.this.notify( current.name );
                        Log.d( "Service_Coordinator", "Taking Attendee " + current );
                        takeAttendance();
                    }
                }
                else
                {
                        Log.d( "Service_Coordinator", "response error " + response.message() );
                        Log.d( "Service_Coordinator", "response error " + response.toString() );
                }
            }

            @Override
            public void onFailure(Call<ChurchService> call, Throwable t) {
                Log.d("Service_Coordinator", "Church Service not started " + t.getMessage() );
            }
        });

    }

    private void takeAttendance()
    {
        Intent service = new Intent( this, AttendanceActivity.class );
        service.putExtra( "service", current.toGson() );
//        startActivityForResult( service, AttendanceActivity.ADD_SERVICE );
        startActivity( service );
    }

    private void notify( String name  )
    {
        Notification service = getNotification( name );
        startForeground(getResources().getInteger( R.integer.notification_id), service);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify( getResources().getInteger( R.integer.notification_id), service );
    }

    private Notification getNotification(String serviceName)
    {
        Intent notificationIntent = new Intent(this, SearchMembersActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder builder = getBuilder();
        return builder.setContentTitle( serviceName )
                .setContentText( String.format(Locale.ENGLISH, "Attendee %d", 0) )
                .setSmallIcon(R.drawable.logo)
                .setPriority( Notification.PRIORITY_HIGH )
                .setContentIntent(pendingIntent)
                .build();
    }

    private Notification.Builder getBuilder()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(this, getString(R.string.channel_id));
        }
        return new Notification.Builder( this );
    }

//    private class Coordinator extends Binder
//    {
//        private void beginService()
//        {
//            ServiceCoordinator.this.beginService();
//        }
//    }

}
