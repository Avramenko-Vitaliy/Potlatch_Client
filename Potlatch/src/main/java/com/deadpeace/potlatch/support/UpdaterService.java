package com.deadpeace.potlatch.support;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.activity.MainActivity;
import com.deadpeace.potlatch.adapter.gift.Gift;
import com.deadpeace.potlatch.client.PotlatchSvcApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Виталий on 19.11.2014.
 */
public class UpdaterService extends Service
{
    private Thread thread;
    private PendingIntent pendingIntent;
    private List<Gift> oldList;
    private Runnable runnable=new Runnable()
    {
        ReentrantLock lock=new ReentrantLock();

        @Override
        public void run()
        {
            try
            {
                lock.lockInterruptibly();
                while(!Thread.interrupted())
                {
                    PotlatchSvcApi api=PotlatchSvc.getPotlatchApi();
                    if(api!=null&&Contract.getUser()!=null)
                    {
                        if(oldList==null)
                        {
                            oldList=api.findByCreator(Contract.getUser().getId());
                            oldList.addAll(api.findByGetting(Contract.getUser().getId()));
                        }
                        Log.i(Contract.LOG_TAG,"Old list is size="+oldList.size());
                        List<Gift> list=api.findByGetting(Contract.getUser().getId());
                        Log.i(Contract.LOG_TAG,"New list is size="+list.size());
                        list.removeAll(oldList);
                        Log.i(Contract.LOG_TAG,"difference lists="+list.size());
                        if(!list.isEmpty())
                        {
                            if(pendingIntent!=null)
                                pendingIntent.send(UpdaterService.this,Contract.SUCCESS,new Intent().putParcelableArrayListExtra(Contract.LIST_GIFT,(ArrayList<Gift>) list));
                            else
                                sendNotification();
                            oldList.addAll(list);
                        }
                        Thread.sleep(getSharedPreferences(Contract.SETTINGS,MODE_PRIVATE).getInt(Contract.UPDATE,Contract.ALWAYS));
                    }
                }
            }
            catch(InterruptedException e)
            {
                Log.i(Contract.LOG_TAG,"Service is interrupted");
            }
            catch(PendingIntent.CanceledException e)
            {
                e.printStackTrace();
            }
            finally
            {
                lock.unlock();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        if(intent!=null)
        {
            if(intent.hasExtra(Contract.P_INTENT))
                pendingIntent=intent.getParcelableExtra(Contract.P_INTENT);
            if(intent.hasExtra(Contract.LIST_GIFT))
                oldList=intent.getParcelableArrayListExtra(Contract.LIST_GIFT);
        }
        if(thread!=null)
            thread.interrupt();
        thread=new Thread(runnable);
        thread.start();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        startService(intent);
        return new Binder();
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        pendingIntent=null;
        return super.onUnbind(intent);
    }

    //TODO send notification about recipients gift(s)
    private void sendNotification()
    {
        Notification.Builder builder=new Notification.Builder(getApplication())
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(),Contract.SEND_GIFT,new Intent(getApplicationContext(),MainActivity.class),PendingIntent.FLAG_CANCEL_CURRENT))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentText(getText(R.string.msg_get_gift))
                .setTicker(getText(R.string.msg_get_gift));
        NotificationManager manager=(NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.notify(Contract.NOTIFY_ID,builder.build());
    }
}
