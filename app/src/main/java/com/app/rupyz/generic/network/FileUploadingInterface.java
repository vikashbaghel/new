package com.app.rupyz.generic.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FileUploadingInterface {
    @POST("s3/upload/")
    Call<String> initFileUploading(@Field("type") String type,
                                   @Field("file_name") String file_name,
                                   @Field("content_type") String content_type,
                                   @Header("Authorization") String auth);
}
