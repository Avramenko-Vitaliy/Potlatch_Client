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
    String RES_JSON=".json";

    String ID="id";
    String FILE="file";
    String TITLE_PARAM="title";
    String NAME_PARAM="name";
    String USER_NAME_PARAM="username";
    String PASSWORD_PARAM="password";
    String GIFT_ID_PARAM="gift_id";
    String USER_ID_PARAM="user_id";
    String USER_PREFER_PARAM="preference";

    String GIFT_SVC_PATH="/gift";
    String GIFT_SVC_ID=GIFT_SVC_PATH+"/{"+ID+"}";
    String GIFT_SVC_LIKE_OR_UNLIKE=GIFT_SVC_ID+"/like_or_unlike";
    String GIFT_SVC_OBSCENE_OR_DECENT=GIFT_SVC_ID+"/obscene_or_decent";
    String GIFT_SVC_UPLOAD=GIFT_SVC_PATH+"/upload";
    String USER_SVC_LOGIN="/user";
    String SVC_LOGIN="/login";
    String SVC_LOGOUT="/logout";
    String LOAD_IMAGE="/image/{"+NAME_PARAM+"}";
    String GIFT_TITLE_SEARCH_PATH = GIFT_SVC_PATH + "/search/findByTitle";
    String GIFT_CREATOR_PATH="/user/{"+ID+"}"+GIFT_SVC_PATH;
    String GIFT_NOT_CREATOR="/not_user/{"+ID+"}"+GIFT_SVC_PATH;
    String GIFT_GETTING_PATH=GIFT_SVC_ID+"/getting";
    String GIFT_SVC_DEL=GIFT_SVC_ID+"/del";
    String GIFT_SVC_DEL_RECIPIENTS="/delRecipients";
    String GIFT_SVC_SEND_RECIPIENTS=GIFT_SVC_ID+"/recipients/user/{"+USER_ID_PARAM+"}";
    String USER_SVC_PREFERENCE="/user/{"+ID+"}/setPreference";

    //Дабавление нового подарка
    @POST(GIFT_SVC_PATH+RES_JSON)
    Gift addGift(@Body Gift g);

    //Получение списка всех подарков
    @GET(GIFT_SVC_PATH+RES_JSON)
    List<Gift> getGiftList();

    //отменить отметку что подарок понравился
    @POST(GIFT_SVC_LIKE_OR_UNLIKE+RES_JSON)
    Gift likeOrUnlike(@Path(ID)long id);

    @POST(GIFT_SVC_OBSCENE_OR_DECENT+RES_JSON)
    Gift obsceneOrDecent(@Path(ID)long id);

    //получение подарка по ID
    //если подарок не найден возвращаем ошибку 404
    @GET(GIFT_SVC_ID+RES_JSON)
    Gift getGiftById(@Path(ID)long id);

    //Прототип загрузки файла на сервер
    @Multipart
    @POST(GIFT_SVC_UPLOAD+RES_JSON)
    String uploadFile(@Part(FILE) TypedFile photo,@Part(NAME_PARAM)String name);

    //Прототип получения файла на клиентское приложение
    @GET(LOAD_IMAGE+RES_JSON)
    Response loadImage(@Path(NAME_PARAM) String name);

    //поиск подарков по названию
    @GET(GIFT_TITLE_SEARCH_PATH+RES_JSON)
    List<Gift> findByTitle(@Query(TITLE_PARAM) String title);

    //получение имени пользователя
    @GET(USER_SVC_LOGIN+RES_JSON)
    User getUser();

    @GET(GIFT_CREATOR_PATH+RES_JSON)
    List<Gift> findByCreator(@Path(ID) long id);

    @GET(GIFT_NOT_CREATOR+RES_JSON)
    List<Gift> findByCreatorNot(@Path(ID) long id);

    @GET(GIFT_GETTING_PATH+RES_JSON)
    List<Gift> findByGetting(@Path(ID) long id);

    @DELETE(GIFT_SVC_DEL+RES_JSON)
    boolean delGift(@Path(ID)long id);

    @DELETE(GIFT_SVC_DEL_RECIPIENTS+RES_JSON)
    boolean delRecipients(@Query(USER_ID_PARAM)long user_id,@Query(GIFT_ID_PARAM)long gift_id);

    @POST(GIFT_SVC_SEND_RECIPIENTS+RES_JSON)
    Gift sendRecipients(@Path(ID)long id,@Path(USER_ID_PARAM)long u_id);

    @POST(USER_SVC_PREFERENCE+RES_JSON)
    User setPreference(@Path(ID)long id,@Query(USER_PREFER_PARAM)String preference);

    @FormUrlEncoded
    @POST(SVC_LOGIN)
    Void login(@Field(USER_NAME_PARAM) String username, @Field(PASSWORD_PARAM) String pass);

    @GET(SVC_LOGOUT)
    Void logout();
}
