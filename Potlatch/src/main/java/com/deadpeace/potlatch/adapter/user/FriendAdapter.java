package com.deadpeace.potlatch.adapter.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.adapter.gift.SendGiftAdapter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Виталий on 17.11.2014.
 */
public class FriendAdapter extends UserAdapter
{
    private long selectedId=-1;

    public FriendAdapter(Context context)
    {
        super(context);
    }

    @Override
    public void loadUsers()
    {
        users=Contract.getUser().getFriends();
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent)
    {
        View view=super.getView(position,convertView,parent);
        ImageButton button=(ImageButton)view.findViewById(R.id.btn_send);
        button.setImageResource(R.drawable.gift);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GridView grid=new GridView(mContext);
                grid.setNumColumns(3);
                SendGiftAdapter adapter=new SendGiftAdapter(mContext);
                adapter.loadGifts();
                grid.setAdapter(adapter);
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent,View view,int position,long id)
                    {
                        selectedId=id;
                    }
                });
                new AlertDialog.Builder(mContext).setTitle(R.string.send_gift).setCancelable(true).setView(grid).setPositiveButton(R.string.str_send,new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
                        if(selectedId!=-1)
                        {
                            Thread thread=new Thread(new Runnable()
                            {
                                private ReentrantLock lock=new ReentrantLock();

                                @Override
                                public void run()
                                {
                                    lock.lock();
                                    try
                                    {
                                        PotlatchSvc.getPotlatchApi().sendRecipients(selectedId,getItemId(position));
                                        HANDLER.sendEmptyMessage(Contract.SEND_GIFT);
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
                        else
                        {
                            Toast.makeText(mContext,R.string.msg_select,Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
            }
        });
        return view;
    }
}
