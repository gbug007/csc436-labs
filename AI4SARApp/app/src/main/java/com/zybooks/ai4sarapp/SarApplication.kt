package com.zybooks.ai4sarapp

import android.app.Application
import com.zybooks.ai4sarapp.data.SarRepository
import com.zybooks.ai4sarapp.data.SarService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SarApplication : Application() {
    lateinit var sarRepository: SarRepository

    override fun onCreate() {
        super.onCreate()

        val sarService: SarService by lazy {
            val retrofit: Retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // replace with localhost eventually?
                .baseUrl("http://10.0.2.2:8000/") // API url
                .build()
            retrofit.create(SarService::class.java)
        }

        sarRepository = SarRepository(
            applicationContext,
            sarService
        )
    }
}