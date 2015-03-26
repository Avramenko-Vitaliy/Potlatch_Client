package com.deadpeace.potlatch.adapter.gift;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.support.LoaderImage;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 21.10.2014
 * Time: 9:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class GiftAdapter extends BaseAdapter
{
    private Thread thread;
    protected Context mContext;
    protected List<Gift> gifts;
    protected Lock lock=new ReentrantLock();

    public abstract List<Gift> loaderGifts()throws InterruptedException;

    protected final Handler HANDLER=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case Contract.LOAD_DONE:
                case Contract.LOAD_DONE_IMAGE:
                    Log.i(Contract.LOG_TAG,"Load Done "+GiftAdapter.class.getSimpleName());
                    break;
                case Contract.DEL_DONE:
                    Log.i(Contract.LOG_TAG,"Delete Done "+GiftAdapter.class.getSimpleName());
                    break;
            }
            GiftAdapter.this.notifyDataSetChanged();
        }
    };

    public GiftAdapter(Context context)
    {
        this.mContext=context;
    }

    public void loadGifts()
    {
        if(thread!=null)
            thread.interrupt();
        thread=new Thread(new Runnable()
        {
            private Lock lock=new ReentrantLock();

            @Override
            public void run()
            {
                try
                {
                    lock.lockInterruptibly();
                    while(!Thread.interrupted())
                    {
                        List<Gift> list=loaderGifts();
                        if(gifts==null)
                            gifts=list;
                        else
                        {
                            updateLikes(list);
                            list.removeAll(gifts);
                            gifts.addAll(0,list);
                        }
                        Log.i(Contract.LOG_TAG,"Getting list gifts for searching");
                        HANDLER.sendEmptyMessage(Contract.LOAD_DONE);
                        Thread.sleep(mContext.getSharedPreferences(Contract.SETTINGS,Context.MODE_PRIVATE).getInt(Contract.UPDATE,Contract.ALWAYS));
                    }
                }
                catch(InterruptedException e)
                {
                    Log.i(Contract.LOG_TAG,"Interrupted thread for update likes");
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

    public List<Gift> getGifts()
    {
        return gifts;
    }

    @Override
    public int getCount()
    {
        return gifts!=null?gifts.size():0;
    }

    @Override
    public Gift getItem(int position)
    {
        return gifts.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return gifts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Gift gift=getItem(position);
        Log.i(Contract.LOG_TAG,"Draw gift id:"+gift.getId());
        final LinearLayout layout=(LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.list_gift,null).findViewById(R.id.home_items);
        ((TextView)layout.findViewById(R.id.text_title)).setText(gift.getTitle());
        ((TextView)layout.findViewById(R.id.text_desc)).setText(gift.getDescription());
        ((TextView)layout.findViewById(R.id.text_who_post)).setText(gift.getCreator().getUsername());
        ((TextView)layout.findViewById(R.id.text_date_post)).setText(DateFormat.getDateFormat(mContext).format(gift.getDate()));
        final TextView countLike=(TextView)layout.findViewById(R.id.count_like);
        countLike.setText(""+gift.getCountLike());
        ImageView likeGift=(ImageView)layout.findViewById(R.id.image_like);
        likeGift.setImageResource(gift.isLikes(Contract.getUser())?R.drawable.heart:R.drawable.un_like);
        likeGift.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            //TODO inc/dec count like for gift
                            lock.lock();
                            gift.setLiked(PotlatchSvc.getPotlatchApi().likeOrUnlike(gift.getId()).getLiked());
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
        });
        Bitmap bitmap=Contract.mMemoryCache.get(gift.getCreator().getUsername());
        if(bitmap!=null)
            ((ImageView)layout.findViewById(R.id.image_user_avatar)).setImageBitmap(bitmap);
        else
        {
            Thread thread=new Thread(new LoaderImage(gift.getCreator().getUsername(),HANDLER));
            thread.setDaemon(true);
            thread.start();
        }
        bitmap=Contract.mMemoryCache.get("gift_"+gift.getId());
        if(bitmap!=null)
            ((ImageView)layout.findViewById(R.id.image_gift)).setImageBitmap(bitmap);
        else
        {
            Thread thread=new Thread(new LoaderImage("gift_"+gift.getId(),HANDLER));
            thread.setDaemon(true);
            thread.start();
        }
        return layout;
    }

    //TODO method for update like and obscene list for gift
    public void updateLikes(List<Gift> list)
    {
        for(Gift gift : list)
            if(gifts.indexOf(gift)>=0)
            {
                gifts.get(gifts.indexOf(gift)).setLiked(gift.getLiked());
                gifts.get(gifts.indexOf(gift)).setObscene(gift.getObscene());
            }
    }

    public void interruptLoad()
    {
        if(thread!=null)
        {
            thread.interrupt();
            thread=null;
        }
    }
}
