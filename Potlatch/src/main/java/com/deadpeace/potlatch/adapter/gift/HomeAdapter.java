package com.deadpeace.potlatch.adapter.gift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.R;

import java.util.List;

/**
 * Created by Виталий on 10.11.2014.
 */
public class HomeAdapter extends GiftAdapter
{
    private Thread thread;

    public HomeAdapter(Context context)
    {
        super(context);
    }

    @Override
    public List<Gift> loaderGifts()
    {
        List<Gift> list=PotlatchSvc.getPotlatchApi().findByCreator(Contract.getUser().getId());
        list.addAll(PotlatchSvc.getPotlatchApi().findByGetting(Contract.getUser().getId()));
        return list;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        LinearLayout view=(LinearLayout)super.getView(position,convertView,parent);
        final Gift gift=getItem(position);
        RelativeLayout likeAndObscene=(RelativeLayout)view.findViewById(R.id.like_and_obscene);
        RelativeLayout viewObscene=(RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.view_obscene,null);
        ImageView obscene=(ImageView)viewObscene.findViewById(R.id.image_inappropriate);
        obscene.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        lock.lock();
                        try
                        {
                            //TODO inc/dec count obscene
                            gift.setObscene(PotlatchSvc.getPotlatchApi().obsceneOrDecent(gift.getId()).getObscene());
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
        obscene.setImageResource(gift.getCountObscene()>0?R.drawable.black_heart:R.drawable.black);
        ((TextView)viewObscene.findViewById(R.id.count_obscene)).setText(""+gift.getCountObscene());
        likeAndObscene.addView(viewObscene);
        View del=LayoutInflater.from(mContext).inflate(R.layout.for_delete,null);
        View btn_del=del.findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(gift.getCreator().equals(Contract.getUser()))
                            PotlatchSvc.getPotlatchApi().delGift(gift.getId());
                        else
                            PotlatchSvc.getPotlatchApi().delRecipients(Contract.getUser().getId(),gift.getId());
                        gifts.remove(gift);
                        HANDLER.sendEmptyMessage(Contract.DEL_DONE);
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });
        view.addView(del,0);
        return view;
    }

    public void addGift(List<Gift> list)
    {
        gifts.addAll(0,list);
    }

    public void addGift(Gift gift)
    {
        if(gift!=null)
            gifts.add(0,gift);
    }
}
