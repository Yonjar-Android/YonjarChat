package com.example.yonjarchat.data.retrofit.interfaces

import com.example.yonjarchat.domain.models.images.ImgBBResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgbbApi {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Part("key") apiKey: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody
        ): Response<ImgBBResponse>

}