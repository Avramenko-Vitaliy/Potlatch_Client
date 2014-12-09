package com.deadpeace.potlatch.support;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import retrofit.mime.TypedByteArray;

import java.io.ByteArrayInputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Виталий on 25.10.2014.
 */
public class LoaderImage implements Runnable
{
    private final String NAME;
    private final Handler HANDLER;
    private Lock lock=new ReentrantLock();

    public LoaderImage(String name,Handler handler)
    {
        this.NAME=name;
        this.HANDLER=handler;
    }

    @Override
    public void run()
    {
        lock.lock();
        try
        {
            //TODO Thread for loads images from server
            if(Contract.mMemoryCache.get(NAME)==null)
                Contract.mMemoryCache.put(NAME, BitmapFactory.decodeStream(new ByteArrayInputStream(((TypedByteArray)PotlatchSvc.getPotlatchApi().loadImage(NAME).getBody()).getBytes())));
            Log.i(Contract.LOG_TAG,"Loaded image: "+NAME);
            HANDLER.sendEmptyMessage(Contract.LOAD_DONE_IMAGE);
        }
        finally
        {
            lock.unlock();
        }
    }
}
