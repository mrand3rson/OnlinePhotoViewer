package com.example.onlinephotoviewer.app;

import com.example.onlinephotoviewer.mvp.models.ApiCommentIn;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageIn;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.ApiResponse;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.models.SignUserIn;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Andrei on 29.03.2018.
 */

public interface PhotoViewerApi {

    @GET("/api/image/{imageId}/comment")
    Call<ApiResponse<List<ApiCommentOut>>> getComments(@Header("Access-Token")String token,
                                                 @Path("imageId") int imageId,
                                                 @Query("page") int page);

    @POST("/api/image/{imageId}/comment")
    Call<ApiResponse<ApiCommentOut>> addComment(@Header("Access-Token")String token,
                                                @Body ApiCommentIn commentIn,
                                                @Path("imageId") int imageId);

    @DELETE("/api/image/{imageId}/comment/{commentId}")
    Call<ApiResponse<ApiCommentOut>> deleteComment(@Header("Access-Token")String token,
                                                   @Path("commentId") int commentId,
                                                   @Path("imageId") int imageId);


    @GET("/api/image")
    Call<ApiResponse<List<ApiImageOut>>> getImages(@Header("Access-Token")String token,
                                                   @Query("page") int page);

    @POST("/api/image")
    Call<ApiResponse<ApiImageOut>> addImage(@Header("Access-Token")String token,
                                            @Body ApiImageIn imageIn);

    @DELETE("/api/image/{id}")
    Call<ApiResponse<ApiImageOut>> deleteImage(@Header("Access-Token")String token,
                                               @Path("id") int imageId);


    @POST("/api/account/signin")
    Call<ApiResponse<SignUserOut>> signIn(@Body SignUserIn user);

    @POST("/api/account/signup")
    Call<ApiResponse<SignUserOut>> signUp(@Body SignUserIn user);
}
