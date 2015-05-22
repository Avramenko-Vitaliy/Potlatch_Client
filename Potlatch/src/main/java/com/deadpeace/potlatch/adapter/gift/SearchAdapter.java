package com.deadpeace.potlatch.adapter.gift;

import android.content.Context;
import android.util.Log;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Виталий on 10.11.2014.
 */
public class SearchAdapter extends GiftAdapter
{
    public SearchAdapter(Context context)
    {
        super(context);
    }

    @Override
    public List<Gift> loaderGifts()
    {
        List<Gift> list=Collections.synchronizedList(PotlatchSvc.getPotlatchApi().findByCreatorNot(Contract.getUser().getId()));
        sortedBy(list,Contract.byDate);
        return list;
    }

    public void doSearchGifts(final String title,final Comparator<Gift> comparator)
    {

        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                lock.lock();
                try
                {
                    List<Gift> list;
                    if(title!=null)
                        //TODO find gifts by title
                        list=Collections.synchronizedList(PotlatchSvc.getPotlatchApi().findByTitle(title));
                    else
                        //TODO reset list gifts
                        list=Collections.synchronizedList(PotlatchSvc.getPotlatchApi().findByCreatorNot(Contract.getUser().getId()));
                    sortedBy(list,comparator);
                    gifts=list;
                    Log.i(Contract.LOG_TAG,"Find by title: "+title);
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

    public static void sortedBy(List<Gift> list,Comparator<Gift> comparator)
    {
        //TODO sorting by like or date
        if(list!=null)
            Collections.sort(list,comparator);
    }
}
