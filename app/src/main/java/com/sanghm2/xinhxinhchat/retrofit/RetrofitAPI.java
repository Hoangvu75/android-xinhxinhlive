package com.sanghm2.xinhxinhchat.retrofit;

import android.app.DownloadManager;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.model.PostProductModel;
import com.sanghm2.xinhxinhchat.model.ProductModel;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    String Content_Type = "Content-Type: application/json";
    String pathAPI = "/api/v1";

    @Headers({Content_Type})
    @POST(pathAPI + "/scp/login")
    Call<JsonElement> LoginAccount(@Body HashMap body);

    @Headers({Content_Type})
    @GET(pathAPI + "/scp/user/profile")
    Call<JsonElement> getProfile(@Header("Authorization") String tokenLogin);

    //social
    @Headers({Content_Type})
    @POST(pathAPI + "/scp/social/facebook/login")
    Call<JsonElement> loginFacebookSocial(@Body HashMap body);

    @Headers({Content_Type})
    @POST("/api/v1/comment/owner")
    Call<JsonElement> postFacebookCommentOwner(@Header("Authorization") String tokenLogin,
                                               @Body HashMap body);


    @Headers({Content_Type})
    @POST("/api/v1/comment/viewer")
    Call<JsonElement> postFacebookCommentViewer(@Header("Authorization") String tokenLogin,
                                                @Body HashMap body);

    @Headers({Content_Type})
    @GET(pathAPI + "/scp/facebook/accounts/overall")
    Call<JsonElement> getAllAccountFB(@Header("Authorization") String tokenLogin);

    @Headers({Content_Type})
    @GET(pathAPI + "/customers/{id}")
    Call<JsonElement> getCustomerDetail(@Header("Authorization") String tokenLogin,
                                        @Path("id") String CustomerId);


    @Headers({Content_Type})
    @GET(pathAPI + "/conversations")
    Call<JsonElement> getConversations(@Header("Authorization") String tokenLogin,
                                        @Query("pageFBID") String pageFBID,
                                        @Query("type") String type,
                                        @Query("limit") int limit,
                                       @Query("skip") String skip,
                                       @Query("status") String status);
    @Headers({Content_Type})
    @GET(pathAPI + "/conversations/count-unread")
    Call<JsonElement> getCountUnread(@Header("Authorization") String tokenLogin,
                                     @Query("pageFBID") String pageFBID);

    @Headers({Content_Type})
    @GET(pathAPI + "/comment/conversations/{id}")
    Call<JsonElement> getComments(@Header("Authorization") String tokenLogin,
                                  @Path("id") String conversationsID,
                                  @Query("pageFBID") String pageFBID,
                                  @Query("limit") int limit);

    @Headers({Content_Type})
    @GET(pathAPI + "/catalog/product")
    Call<JsonElement> getProductList(@Header("Authorization") String tokenLogin,
                                 @Query("page") int page,
                                 @Query("limit") int limit);

    @Headers({Content_Type})
    @GET(pathAPI + "/catalog/product/{id}")
    Call<JsonElement> getProduct(@Header("Authorization") String tokenLogin,
                                 @Path("id") String productID);

    @Headers({Content_Type})
    @GET(pathAPI + "/catalog/product")
    Call<JsonElement> getProductByCategory(@Header("Authorization") String tokenLogin,
                                           @Query("page") int page,
                                           @Query("limit") int limit,
                                           @Query("category") String category);

    @Headers({Content_Type})
    @POST(pathAPI + "/catalog/product")
    Call<JsonElement> PostProduct(@Header("Authorization") String tokenLogin,
                                           @Body PostProductModel body);

    @Headers({Content_Type})
    @PUT(pathAPI + "/catalog/product/{id}")
    Call<JsonElement> EditProduct(@Header("Authorization") String tokenLogin, @Path("id") String id,
                                  @Body PostProductModel body);

    @Headers({Content_Type})
    @DELETE(pathAPI + "/catalog/product/{id}")
    Call<JsonElement> DeleteProduct(@Header("Authorization") String tokenLogin, @Path("id") String id);

    @Headers({Content_Type})
    @GET(pathAPI + "/catalog/category")
    Call<JsonElement> getCategory(@Header("Authorization") String tokenLogin);

    @Headers({Content_Type})
    @GET(pathAPI + "/marketing/campaign/broadcast")
    Call<JsonElement> getBroadcast(@Header("Authorization") String tokenLogin,
                                           @Query("limitTrending") int limitTrending,
                                           @Query("newestPage") int newestPage,
                                           @Query("newestLimit") int newestLimit,
                                           @Query("type") String type);

    @Headers({Content_Type})
    @GET(pathAPI + "/marketing/campaign/search")
    Call<JsonElement> getBroadcastByCategoryID(@Header("Authorization") String tokenLogin,
                                               @Query("page") int page,
                                               @Query("limit") int limit,
                                               @Query("categoryID") String categoryID);

    @Headers({Content_Type})
    @GET(pathAPI + "/marketing/campaign/search")
    Call<JsonElement> getBroadcastByTitle(@Header("Authorization") String tokenLogin,
                                          @Query("page") int page,
                                          @Query("limit") int limit,
                                          @Query("title") String title);

    @Headers({Content_Type})
    @GET(pathAPI + "/scp/user/search")
    Call<JsonElement> getSearchUser(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("name") String name);

    @Headers({Content_Type})
    @GET(pathAPI + "/marketing/block/user")
    Call<JsonElement> getBlockUser(@Header("Authorization") String tokenLogin);

    @Headers({Content_Type})
    @HTTP(method = "DELETE", path = "/api/v1/marketing/block/user", hasBody = true)
    Call<JsonElement> delBlockUser(@Header("Authorization") String tokenLogin,
                                   @Body HashMap body);

    @Headers({Content_Type})
    @HTTP(method = "POST", path = "/api/v1/marketing/block/user", hasBody = true)
    Call<JsonElement> postBlockUser(@Header("Authorization") String tokenLogin,
                                   @Body HashMap body);

    @Headers({Content_Type})
    @HTTP(method = "POST", path = "/api/v1/scp/follow", hasBody = true)
    Call<JsonElement> postFollow(@Header("Authorization") String tokenLogin,
                                 @Body HashMap body);

    @Headers({Content_Type})
    @GET(pathAPI + "/scp/follow/{id}/checked")
    Call<JsonElement> getCheckFollow(@Header("Authorization") String tokenLogin,
                                     @Path("id") String id);

    @Headers({Content_Type})
    @HTTP(method = "POST", path = "/api/v1/scp/un-follow", hasBody = true)
    Call<JsonElement> postUnFollow(@Header("Authorization") String tokenLogin,
                                   @Body HashMap body);

    @Headers({Content_Type})
    @GET(pathAPI + "/comment")
    Call<JsonElement> getCommentOfBroadcast(@Header("Authorization") String tokenLogin,
                                            @Query("campaignID") String campaignID,
                                            @Query("page") int page,
                                            @Query("limit") int limit,
                                            @Query("createdTimeSort") int createdTimeSort);

    @Headers({Content_Type})
    @POST(pathAPI + "/comment/viewer")
    Call<JsonElement> postCommentViewer(@Header("Authorization") String tokenLogin,
                                        @Body HashMap body);

    @Headers({Content_Type})
    @POST(pathAPI + "/marketing/report")
    Call<JsonElement> postReport(@Header("Authorization") String tokenLogin,
                                 @Body HashMap body);

    @Headers({Content_Type})
    @GET(pathAPI + "/catalog/campaign/{id}/products")
    Call<JsonElement> getProductInLivestream(@Path("id") String id);
}
