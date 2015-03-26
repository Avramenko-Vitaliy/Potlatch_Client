package com.deadpeace.potlatch.activity.fragments;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.activity.MainActivity;
import com.deadpeace.potlatch.adapter.gift.Gift;
import com.deadpeace.potlatch.adapter.gift.HomeAdapter;
import com.deadpeace.potlatch.support.LoaderImage;
import com.deadpeace.potlatch.support.UpdaterService;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 22.10.2014
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class HomeFragment extends Fragment
{
    private ServiceConnection connection=new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name,IBinder service)
        {
            Log.i(Contract.LOG_TAG,"Connected to service");
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.i(Contract.LOG_TAG,"Disconnected from service");
        }
    };

    private HomeAdapter adapter;
    private View home;

    private final Handler HANDLER=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
                case Contract.LOAD_DONE_IMAGE:
                    Log.i(Contract.LOG_TAG,"Load Done "+HomeFragment.class.getSimpleName());
                    ImageView image=(ImageView) home.findViewById(R.id.image_yours_avatar);
                    image.setImageBitmap(Contract.mMemoryCache.get(Contract.getUser().getUsername()));
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter=new HomeAdapter(getActivity());
        Intent intent=new Intent(getActivity(),UpdaterService.class);
        PendingIntent pendingIntent=getActivity().createPendingResult(Contract.GET_LIKED,new Intent(),0);
        intent.putExtra(Contract.P_INTENT,pendingIntent);
        ((MainActivity)getActivity()).setListenerResult(new MainActivity.OnListenerResult()
        {
            @Override
            public void onResultListener(int requestCode,int resultCode,Intent data)
            {
                if(resultCode==Contract.SUCCESS)
                {
                    switch(requestCode)
                    {
                        case Contract.SEND_GIFT:
                            if(data.hasExtra(Contract.LIST_GIFT))
                                adapter.addGift(data.<Gift>getParcelableArrayListExtra(Contract.LIST_GIFT));
                            break;
                        case Contract.ADD_GIFT:
                            if(data.hasExtra(Contract.GIFT))
                                adapter.addGift(data.<Gift>getParcelableExtra(Contract.GIFT));
                            break;
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        home=inflater.inflate(R.layout.home_fragment, container, false);
        ((ListView) home.findViewById(R.id.list)).setAdapter(adapter);
        ((TextView) home.findViewById(R.id.text_yours_name)).setText(Contract.getUser().getUsername());
        EditText preference=(EditText) home.findViewById(R.id.text_preference);
        preference.setText(Contract.getUser().getPreference());
        Bitmap bitmap=Contract.mMemoryCache.get(Contract.getUser().getUsername());
        if(bitmap!=null)
            ((ImageView) home.findViewById(R.id.image_yours_avatar)).setImageBitmap(bitmap);
        else
        {
            Thread thread=new Thread(new LoaderImage(Contract.getUser().getUsername(),HANDLER));
            thread.setDaemon(true);
            thread.start();
        }
        return home;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.loadGifts();
        PendingIntent pendingIntent=getActivity().createPendingResult(Contract.SEND_GIFT,new Intent(),0);
        getActivity().bindService(new Intent(getActivity(),UpdaterService.class).putExtra(Contract.P_INTENT,pendingIntent).putParcelableArrayListExtra(Contract.LIST_GIFT,(ArrayList<Gift>)adapter.getGifts()),connection,Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause()
    {
        getActivity().unbindService(connection);
        EditText preference=(EditText) home.findViewById(R.id.text_preference);
        final String str=preference.getText().toString();
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized(Contract.getUser())
                {
                    PotlatchSvc.getPotlatchApi().setPreference(Contract.getUser().getId(),str);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        if(adapter!=null)
            adapter.interruptLoad();
        super.onPause();
    }
}
