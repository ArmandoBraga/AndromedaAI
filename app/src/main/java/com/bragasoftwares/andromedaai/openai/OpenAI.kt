package com.example.myapplicationai5

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

private val client = OkHttpClient()

class OpenAI(private val apiKey: String, private val prompt: String) {

    fun callAPI(callback: (String?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://api.openai.com/v1/completions"

            val json = JSONObject()
            json.put("prompt", prompt)
            json.put("model", "text-davinci-003")
            json.put("max_tokens", 4000)
            json.put("temperature", 0)


            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            try {
                val response = OkHttpClient().newCall(request).execute()
                val result = response.body?.string()
                GlobalScope.launch(Dispatchers.Main) {
                    callback(result)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}