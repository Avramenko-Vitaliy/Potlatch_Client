package com.deadpeace.potlatch;

import android.graphics.Bitmap;
import android.util.LruCache;
import com.deadpeace.potlatch.adapter.gift.Gift;
import com.deadpeace.potlatch.adapter.user.User;

import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 22.10.2014
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class Contract
{
    public static final String LOG_TAG="Potlatch";
    public static final String SETTINGS="potlach_update";
    public static final String UPDATE="update";
    public static final String P_INTENT="p_intent";
    public static final String LIST_GIFT="liked";
    public static final String GIFT="gift";
    public static final int ALWAYS=0;
    public static final int ONE_MINUTE=1000*60;
    public static final int FIVE_MINUTES=ONE_MINUTE*5;
    public static final int SIXTY_MINUTES=FIVE_MINUTES*12;
    public static final int ERROR=-1;
    public static final int SUCCESS=1;
    public static final int LOAD_DONE_IMAGE=2;
    public static final int LOAD_DONE=3;
    public static final int ADD_GIFT=4;
    public static final int CAMERA_RESULT=5;
    public static final int DEL_DONE=6;
    public static final int SEND_GIFT=7;
    public static final int GET_LIKED=8;
    public static final int NOTIFY_ID=101;
    private static User user;

    private Contract(){}

    public static synchronized User getUser()
    {
        return user;
    }

    public static synchronized void setUser(User user)
    {
        Contract.user=user;
    }

    // Get max available VM memory, exceeding this amount will throw an
    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
    // int in its constructor.
    private static final int maxMemory=(int) (Runtime.getRuntime().maxMemory()/1024);
    // Use 1/8th of the available memory for this memory cache.
    private static final int cacheSize=maxMemory / 8;
    public static final LruCache<String, Bitmap> mMemoryCache=new LruCache<String, Bitmap>(cacheSize)
    {
        @Override
        protected int sizeOf(String key, Bitmap bitmap)
        {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };

    //TODO comparator for sorted by date
    public static Comparator byDate=new Comparator<Gift>()
    {
        @Override
        public int compare(Gift gift1,Gift gift2)
        {
            switch(new Date(gift1.getDate()).compareTo(new Date(gift2.getDate())))
            {
                case 1:
                    return -1;
                case -1:
                    return 1;
                default:
                    return 0;
            }
        }
    };

    //TODO comparator for sorted by relevance
    public static Comparator byRelevance=new Comparator<Gift>()
    {
        @Override
        public int compare(Gift gift1,Gift gift2)
        {
            return gift1.compareTo(gift2);
        }
    };
}
