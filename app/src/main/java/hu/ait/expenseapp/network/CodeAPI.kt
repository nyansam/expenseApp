package hu.ait.expenseapp.network

import hu.ait.expenseapp.data.CodeResult

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//host: https://api.exchangeratesapi.io
//
// PATH: /latest
//
// QUERY arguments: ?   base=EUR

interface CodeAPI {
    @GET("rest/v2/name/{country}")
    fun getCode(@Path("country") country: String): Call<List<CodeResult>>

}