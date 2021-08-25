package com.example.pdf_scanner.data.remote

import com.example.pdf_scanner.data.error.NETWORK_ERROR
import com.example.pdf_scanner.data.error.NO_INTERNET_CONNECTION
import com.example.pdf_scanner.utils.NetworkConnectivity
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class RemoteData @Inject constructor(
    private val networkConnectivity: NetworkConnectivity
){



    private suspend fun processCall(responseCall: suspend () -> Response<*>): Any?{
        if(!networkConnectivity.isConnected()){
            return NO_INTERNET_CONNECTION
        }
        return try{
            val response = responseCall.invoke()
            val responseCode = response.code()
            if(response.isSuccessful){
                response.body()
            }
            else{
                responseCode
            }
        }
        catch (e: IOException){
            NETWORK_ERROR
        }
    }
}