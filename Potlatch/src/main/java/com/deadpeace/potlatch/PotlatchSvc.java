package com.deadpeace.potlatch;

import com.deadpeace.potlatch.client.PotlatchSvcApi;
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
//    public static final String CLIENT_ID = "mobile";

    //TODO exchange IP address
    private static final String SERVER=
//            "https://192.168.1.104:8443";
            "https://10.0.0.27:8443";
    private static PotlatchSvcApi potlatchApi=new RestAdapter.Builder()
            .setClient(new ApacheClient(new EasyHttpClient()))
            .setEndpoint(SERVER)
            .setLogLevel(RestAdapter.LogLevel.FULL).build()
            .create(PotlatchSvcApi.class);

//    public static synchronized PotlatchSvcApi getOrShowLogin(Context ctx)
//    {
//        try
//        {
//            potlatchApi.login()
//        }
//        catch()
//        {
//            Intent intent = new Intent(ctx, LoginActivity.class);
//            ctx.startActivity(intent);
//            return null;
//        }
//        if (potlatchApi!= null||Contract.getUser()!=null)
//            return potlatchApi;
//        else
//        {
//            Intent intent = new Intent(ctx, LoginActivity.class);
//            ctx.startActivity(intent);
//            return null;
//        }
//    }

    public static PotlatchSvcApi getPotlatchApi()
    {
        return potlatchApi;
    }

//    public static synchronized PotlatchSvcApi init(String user,String pass)
//    {
//        potlatchApi=new SecuredRestBuilder()
//                .setLoginEndpoint(SERVER + PotlatchSvcApi.TOKEN_PATH)
//                .setUsername(user)
//                .setPassword(pass)
//                .setClientId(CLIENT_ID)
//                .setClient(new ApacheClient(new EasyHttpClient()))
//                .setEndpoint(SERVER).setLogLevel(RestAdapter.LogLevel.FULL).build()
//                .create(PotlatchSvcApi.class);
//        return potlatchApi;
//    }
}
