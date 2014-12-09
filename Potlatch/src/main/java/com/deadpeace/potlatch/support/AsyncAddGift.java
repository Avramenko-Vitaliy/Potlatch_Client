package com.deadpeace.potlatch.support;

import android.os.AsyncTask;
import com.deadpeace.potlatch.PotlatchSvc;
import com.deadpeace.potlatch.activity.NewGift;
import com.deadpeace.potlatch.adapter.gift.Gift;
import retrofit.mime.TypedFile;

import java.io.File;

/**
 * Created by Виталий on 10.11.2014.
 */
public class AsyncAddGift extends AsyncTask<Void,Void,Gift>
{
    private final TypedFile file;
    private Gift gift;
    private final NewGift parent;

    public AsyncAddGift(Gift gift,File file,NewGift parent)
    {
        this.file=new TypedFile("image/jpg",file);
        this.gift=gift;
        this.parent=parent;
    }

    @Override
    protected void onPreExecute()
    {
        parent.startProgress();
        super.onPreExecute();
    }

    @Override
    protected Gift doInBackground(Void... params)
    {
        try
        {
            //TODO upload a gift to the server
            gift=PotlatchSvc.getPotlatchApi().addGift(gift);
            PotlatchSvc.getPotlatchApi().uploadFile(file,"gift_"+gift.getId()+".jpg");
            return gift;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Gift s)
    {
        parent.endProgress(s);
        super.onPostExecute(s);
    }
}
