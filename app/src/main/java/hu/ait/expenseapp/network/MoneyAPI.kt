package hu.ait.expenseapp.network

import hu.ait.expenseapp.data.MoneyResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

//host: https://api.exchangeratesapi.io
//
// PATH: /latest
//
// QUERY arguments: ?   base=EUR

interface MoneyAPI {
    @GET("/latest")
    fun getMoney(@Query("base") base: String) : Call<MoneyResult>
}