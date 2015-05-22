package com.deadpeace.potlatch.adapter.gift;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.support.LoaderImage;

import java.util.Collections;
import java.util.List;

/**
 * Created by Виталий on 17.11.2014.
 */
public class SendGiftAdapter extends GiftAdapter
{
    public SendGiftAdapter(Context context)
    {
        super(context);
    }

    @Override
    public List<Gift> loaderGifts()
    {
        return Collections.synchronizedList(PotlatchSvc.getPotlatchApi().findByCreator(Contract.getUser().getId()));
    }

    @Override
    public void loadGifts()
    {
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                lock.lock();
                try
                {
                    gifts=loaderGifts();
                    Log.i(Contract.LOG_TAG,"Getting list gift");
                    HANDLER.sendEmptyMessage(Contract.LOAD_DONE);
                }
                finally
                {
                    lock.unlock();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        ImageView view=new ImageView(mContext);
        view.setAdjustViewBounds(true);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Gift gift=getItem(position);
        Bitmap bitmap=Contract.mMemoryCache.get("gift_"+gift.getId());
        if(bitmap!=null)
            view.setImageBitmap(bitmap);
        else
        {
            Thread thread=new Thread(new LoaderImage("gift_"+gift.getId(),HANDLER));
            thread.setDaemon(true);
            thread.start();
        }
        return view;
    }
}
