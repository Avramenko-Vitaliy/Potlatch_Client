package com.deadpeace.potlatch.adapter.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.support.LoaderImage;

import java.util.List;

/**
 * Created by Виталий on 17.11.2014.
 */
public abstract class UserAdapter extends BaseAdapter
{
    protected List<User> users;
    protected Context mContext;

    protected final Handler HANDLER=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case Contract.LOAD_DONE:
                case Contract.LOAD_DONE_IMAGE:
                    Log.i(Contract.LOG_TAG,"Load Done "+UserAdapter.class.getSimpleName());
                    UserAdapter.this.notifyDataSetChanged();
                    break;
                case Contract.SEND_GIFT:
                    Log.i(Contract.LOG_TAG,"Gift sent "+UserAdapter.class.getSimpleName());
                    Toast.makeText(mContext,R.string.msg_sent,Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public UserAdapter(Context context)
    {
        mContext=context;
    }

    public abstract void loadUsers();

    @Override
    public int getCount()
    {
        return users!=null?users.size():0;
    }

    @Override
    public User getItem(int position)
    {
        return users!=null?users.get(position):null;
    }

    @Override
    public long getItemId(int position)
    {
        return users!=null?users.get(position).getId():0;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        View layout=LayoutInflater.from(mContext).inflate(R.layout.list_friend,null);
        User user=users.get(position);
        ((TextView)layout.findViewById(R.id.user_preference)).setText(user.getPreference());
        ((TextView)layout.findViewById(R.id.user_name)).setText(user.getUsername());
        Bitmap bitmap=Contract.mMemoryCache.get(user.getUsername());
        if(bitmap!=null)
            ((ImageView)layout.findViewById(R.id.image_user)).setImageBitmap(bitmap);
        else
        {
            Thread thread=new Thread(new LoaderImage(user.getUsername(),HANDLER));
            thread.setDaemon(true);
            thread.start();
        }
        return layout;
    }
}
