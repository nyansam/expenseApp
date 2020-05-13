package hu.ait.expenseapp.network

import hu.ait.expenseapp.data.CountryResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//host: https://api.exchangeratesapi.io
//
// PATH: /latest
//
// QUERY arguments: ?   base=EUR

interface CountryAPI {
    @GET("/countryCodeJSON")
    fun getCountry(
        @Query("lat") lat: String,
        @Query("lng") lng: String,
        @Query("username") username: String): Call<CountryResult>
}