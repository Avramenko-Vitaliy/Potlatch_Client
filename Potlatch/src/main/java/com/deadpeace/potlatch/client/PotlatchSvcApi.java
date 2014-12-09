package com.deadpeace.potlatch.client;

import com.deadpeace.potlatch.adapter.gift.Gift;
import com.deadpeace.potlatch.adapter.user.User;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedFile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DeadPeace
 * Date: 13.10.2014
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public interface PotlatchSvcApi
{
    public static final String ID="id";
    public static final String FILE="file";
    public static final String TITLE_PARAM="title";
    public static final String NAME_PARAM="name";
    public static final String GIFT_ID_PARAM="gift_id";
    public static final String USER_ID_PARAM="user_id";
    public static final String USER_PREFER_PARAM="preference";

    public static final String GIFT_SVC_PATH="/gift";
    public static final String GIFT_SVC_ID=GIFT_SVC_PATH+"/{"+ID+"}";
    public static final String GIFT_SVC_LIKE_OR_UNLIKE=GIFT_SVC_ID+"/like_or_unlike";
    public static final String GIFT_SVC_OBSCENE_OR_DECENT=GIFT_SVC_ID+"/obscene_or_decent";
    public static final String GIFT_SVC_UPLOAD=GIFT_SVC_PATH+"/upload";
    public static final String USER_SVC_LOGIN="/login";
    public static final String LOAD_IMAGE="/image/{"+NAME_PARAM+"}";
    public static final String TOKEN_PATH = "/oauth/token";
    public static final String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByTitle";
    public static final String GIFT_CREATOR_PATH="/user/{"+ID+"}"+GIFT_SVC_PATH;
    public static final String GIFT_NOT_CREATOR="/not_user/{"+ID+"}"+GIFT_SVC_PATH;
    public static final String GIFT_GETTING_PATH=GIFT_SVC_ID+"/getting";
    public static final String GIFT_SVC_DEL=GIFT_SVC_ID+"/del";
    public static final String GIFT_SVC_DEL_RECIPIENTS="/delRecipients";
    public static final String GIFT_SVC_SEND_RECIPIENTS=GIFT_SVC_ID+"/recipients/user/{"+USER_ID_PARAM+"}";
    public static final String USER_SVC_PREFERENCE="/user/{"+ID+"}/setPreference";

    //Дабавление нового подарка
    @POST(GIFT_SVC_PATH)
    public Gift addGift(@Body Gift g);

    //Получение списка всех подарков
    @GET(GIFT_SVC_PATH)
    public List<Gift> getGiftList();

    //отменить отметку что подарок понравился
    @POST(GIFT_SVC_LIKE_OR_UNLIKE)
    public Gift likeOrUnlike(@Path(ID)long id);

    @POST(GIFT_SVC_OBSCENE_OR_DECENT)
    public Gift obsceneOrDecent(@Path(ID)long id);

    //получение подарка по ID
    //если подарок не найден возвращаем ошибку 404
    @GET(GIFT_SVC_ID)
    public Gift getGiftById(@Path(ID)long id);

    //Прототип загрузки файла на сервер
    @Multipart
    @POST(GIFT_SVC_UPLOAD)
    public String uploadFile(@Part(FILE) TypedFile photo,@Part(NAME_PARAM)String name);

    //Прототип получения файла на клиентское приложение
    @GET(LOAD_IMAGE)
    public Response loadImage(@Path(NAME_PARAM) String name);

    //поиск подарков по названию
    @GET(GIFT_TITLE_SEARCH_PATH)
    public List<Gift> findByTitle(@Query(TITLE_PARAM) String title);

    //получение имени пользователя
    @GET(USER_SVC_LOGIN)
    public User getUser();

    @GET(GIFT_CREATOR_PATH)
    public List<Gift> findByCreator(@Path(ID) long id);

    @GET(GIFT_NOT_CREATOR)
    public List<Gift> findByCreatorNot(@Path(ID) long id);

    @GET(GIFT_GETTING_PATH)
    public List<Gift> findByGetting(@Path(ID) long id);

    @DELETE(GIFT_SVC_DEL)
    public boolean delGift(@Path(ID)long id);

    @DELETE(GIFT_SVC_DEL_RECIPIENTS)
    public boolean delRecipients(@Query(USER_ID_PARAM)long user_id,@Query(GIFT_ID_PARAM)long gift_id);

    @POST(GIFT_SVC_SEND_RECIPIENTS)
    public Gift sendRecipients(@Path(ID)long id,@Path(USER_ID_PARAM)long u_id);

    @POST(USER_SVC_PREFERENCE)
    public User setPreference(@Path(ID)long id,@Query(USER_PREFER_PARAM)String preference);
}
