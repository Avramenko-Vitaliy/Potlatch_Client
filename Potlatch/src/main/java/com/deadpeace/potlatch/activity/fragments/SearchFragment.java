package com.deadpeace.potlatch.activity.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.deadpeace.potlatch.Contract;
import com.deadpeace.potlatch.R;
import com.deadpeace.potlatch.adapter.gift.SearchAdapter;

/**
 * Created by Виталий on 22.10.2014.
 */
public class SearchFragment extends Fragment
{
    private SearchAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter=new SearchAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        final View search=inflater.inflate(R.layout.search_fragment,container,false);
        ((ListView) search.findViewById(R.id.list_search)).setAdapter(adapter);
        final Spinner spinner=(Spinner)search.findViewById(R.id.sorted_by);
        search.findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(Contract.LOG_TAG,"OnClick Search "+SearchFragment.class.getSimpleName());
                adapter.doSearchGifts(((EditText)search.findViewById(R.id.edit_search)).getText().toString(),spinner.getSelectedItemPosition()==0?Contract.byDate:Contract.byRelevance);
            }
        });

        ArrayAdapter<CharSequence> sorting = ArrayAdapter.createFromResource(getActivity(),R.array.sorted_list,android.R.layout.simple_spinner_item);
        sorting.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sorting);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id)
            {
                SearchAdapter.sortedBy(adapter.getGifts(),position==0?Contract.byDate:Contract.byRelevance);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        return search;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.loadGifts();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        adapter.interruptLoad();
    }
}

