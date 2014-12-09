package com.deadpeace.potlatch.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.activity.fragments.FriendFragment;
import com.deadpeace.potlatch.activity.fragments.HomeFragment;
import com.deadpeace.potlatch.activity.fragments.SearchFragment;
import com.deadpeace.potlatch.adapter.gift.Gift;

import java.util.List;


public class MainActivity extends Activity
{
    public interface OnListenerResult
    {
        void onResultListener(int requestCode,int resultCode,Intent data);
    }

    private OnListenerResult listenerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ActionBar actionBar=getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(R.string.page_home).setTabListener(new ActionBar.TabListener()
        {
            private HomeFragment home=new HomeFragment();

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
            {
                ft.replace(R.id.fragment_container,home);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
            {
                ft.remove(home);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
            {

            }
        }));
        actionBar.addTab(actionBar.newTab().setText(R.string.page_search).setTabListener(new ActionBar.TabListener()
        {
            private SearchFragment search=new SearchFragment();

            @Override
            public void onTabSelected(ActionBar.Tab tab,FragmentTransaction ft)
            {
                ft.replace(R.id.fragment_container,search);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab,FragmentTransaction ft)
            {
                ft.remove(search);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab,FragmentTransaction ft)
            {

            }
        }));
        actionBar.addTab(actionBar.newTab().setText(R.string.page_friends).setTabListener(new ActionBar.TabListener()
        {
            private FriendFragment friend=new FriendFragment();

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
            {
                ft.replace(R.id.fragment_container,friend);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
            {
                ft.remove(friend);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
            {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_add:
                startActivityForResult(new Intent(MainActivity.this,NewGift.class),Contract.ADD_GIFT);
                return true;
            case R.id.minute_always:
                getSharedPreferences(Contract.SETTINGS,Context.MODE_PRIVATE).edit().putInt(Contract.UPDATE,Contract.ALWAYS).apply();
                return true;
            case R.id.minute_one:
                getSharedPreferences(Contract.SETTINGS,Context.MODE_PRIVATE).edit().putInt(Contract.UPDATE,Contract.ONE_MINUTE).apply();
                return true;
            case R.id.minute_five:
                getSharedPreferences(Contract.SETTINGS,Context.MODE_PRIVATE).edit().putInt(Contract.UPDATE,Contract.FIVE_MINUTES).apply();
                return true;
            case R.id.minute_sixty:
                getSharedPreferences(Contract.SETTINGS,Context.MODE_PRIVATE).edit().putInt(Contract.UPDATE,Contract.SIXTY_MINUTES).apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        switch(getSharedPreferences(Contract.SETTINGS,Context.MODE_PRIVATE).getInt(Contract.UPDATE,Contract.ALWAYS))
        {
            case Contract.ALWAYS:
                menu.findItem(R.id.minute_always).setChecked(true);
                return true;
            case Contract.ONE_MINUTE:
                menu.findItem(R.id.minute_one).setChecked(true);
                return true;
            case Contract.FIVE_MINUTES:
                menu.findItem(R.id.minute_five).setChecked(true);
                return true;
            case Contract.SIXTY_MINUTES:
                menu.findItem(R.id.minute_sixty).setChecked(true);
                return true;
            default:
                return super.onPrepareOptionsMenu(menu);
        }
    }

    public void setListenerResult(OnListenerResult listenerResult)
    {
        this.listenerResult=listenerResult;
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(listenerResult!=null)
            listenerResult.onResultListener(requestCode,resultCode,data);
    }
}
