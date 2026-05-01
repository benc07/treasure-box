package com.example.hackchallenge.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BoxApiService {

    @GET("api/boxes/")
    suspend fun getAllBoxes(): BoxesEnvelope

    @GET("api/boxes/{id}/")
    suspend fun getBox(@Path("id") id: Int): BoxDto

    @POST("api/boxes/")
    suspend fun createBox(@Body req: CreateBoxRequest): BoxDto

    @DELETE("api/boxes/{id}/")
    suspend fun deleteBox(@Path("id") id: Int): BoxDto

    @POST("api/boxes/{id}/transactions/")
    suspend fun createTransaction(
        @Path("id") boxId: Int,
        @Body req: CreateTransactionRequest
    ): TransactionDto
}
