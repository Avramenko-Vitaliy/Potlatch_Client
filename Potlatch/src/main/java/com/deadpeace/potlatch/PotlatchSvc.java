package com.deadpeace.potlatch;

import android.content.Context;
import android.content.Intent;
import com.deadpeace.potlatch.activity.LoginActivity;
import com.deadpeace.potlatch.client.PotlatchSvcApi;
import com.deadpeace.potlatch.client.SecuredRestBuilder;
import com.deadpeace.potlatch.unsafe.EasyHttpClient;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 21.10.2014
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class PotlatchSvc
{
    public static final String CLIENT_ID = "mobile";

    //TODO exchange IP address
    private static final String server="https://192.168.1.101:8443";
    private static PotlatchSvcApi potlatchApi;

    public static synchronized PotlatchSvcApi getOrShowLogin(Context ctx)
    {
        if (potlatchApi!= null||Contract.getUser()!=null)
            return potlatchApi;
        else
        {
            Intent intent = new Intent(ctx, LoginActivity.class);
            ctx.startActivity(intent);
            return null;
        }
    }

    public static PotlatchSvcApi getPotlatchApi()
    {
        return potlatchApi;
    }

    public static synchronized PotlatchSvcApi init(String user,String pass)
    {
        potlatchApi=new SecuredRestBuilder()
                .setLoginEndpoint(server + PotlatchSvcApi.TOKEN_PATH)
                .setUsername(user)
                .setPassword(pass)
                .setClientId(CLIENT_ID)
                .setClient(new ApacheClient(new EasyHttpClient()))
                .setEndpoint(server).setLogLevel(RestAdapter.LogLevel.FULL).build()
                .create(PotlatchSvcApi.class);
        return potlatchApi;
    }
}
