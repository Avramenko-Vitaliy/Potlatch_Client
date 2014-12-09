package com.deadpeace.potlatch.activity.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.adapter.user.FriendAdapter;

/**
 * Created by Виталий on 15.11.2014.
 */
public class FriendFragment extends Fragment
{
    private FriendAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter=new FriendAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View friend=inflater.inflate(R.layout.friends_fragment,container,false);
        ((ListView) friend.findViewById(R.id.list)).setAdapter(adapter);
        return friend;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.loadUsers();
    }
}
