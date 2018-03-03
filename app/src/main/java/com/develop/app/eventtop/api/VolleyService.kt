package com.develop.app.eventtop.api

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.develop.app.eventtop.utils.ImageUtility
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*

class VolleyService : ServiceInterface {

    override fun postJsonArray(path: String, params: JSONArray, completionHandler: (response: JSONArray?) -> Unit) {
        val request = object : JsonArrayRequest(Request.Method.POST, path, params,
                Response.Listener<JSONArray> { response ->
                    Log.d("res", "200 POST response ok $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    if (error.message != null && error.localizedMessage != null) {
                        Log.d("err", error.message)
                        Log.d("localizedMessage", error.localizedMessage)
                    }
                    completionHandler(null)
                })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        Backend.instance?.addToRequestQueue(request)
    }

    override fun getJsonArray(path: String, completionHandler: (response: JSONArray?) -> Unit) {
        val request = object : StringRequest(Request.Method.GET, path,
                Response.Listener<String> { response ->
                    Log.d("res", "200 GET response ok $response")
                    val obj = JSONArray(response)
                    completionHandler(obj)
                },
                Response.ErrorListener { error ->
                    if (error.message != null && error.localizedMessage != null) {
                        Log.d("err", error.message)
                        Log.d("localizedMessage", error.localizedMessage)
                    }
                    completionHandler(null)
                })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }
        }

        Backend.instance?.addToRequestQueue(request)
    }

    override fun postJsonObject(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit) {
        val request = object : JsonObjectRequest(Request.Method.POST, path, params,
                Response.Listener<JSONObject> { response ->
                    Log.d("res", "200 POST response ok $response")
                    completionHandler(response)
                },
                Response.ErrorListener { error ->
                    if (error.message != null && error.localizedMessage != null) {
                        Log.d("err", error.message)
                        Log.d("localizedMessage", error.localizedMessage)
                    }
                    completionHandler(null)
                })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }
        }

        Backend.instance?.addToRequestQueue(request)
    }

    override fun getJsonObject(path: String, completionHandler: (response: JSONObject?) -> Unit) {
        val request = object : StringRequest(Request.Method.GET, path,
                Response.Listener<String> { response ->
                    Log.d("res", "200 GET response ok $response")
                    val obj = JSONObject(response)
                    completionHandler(obj)
                },
                Response.ErrorListener { error ->
                    if (error.message != null && error.localizedMessage != null) {
                        Log.d("err", error.message)
                        Log.d("localizedMessage", error.localizedMessage)
                    }
                    completionHandler(null)
                })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                return headers
            }
        }

        Backend.instance?.addToRequestQueue(request)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun multipart(path: String, context: Context, activity: Activity, params: JSONObject, completionHandler: (response: String?) -> Unit) {

        Thread(Runnable {
            val accountType = params.getString("account_type")
            val avatar = params.getString("avatar")

            // Initialize a new file
            var file = File(avatar)
            // Get the content-type
            val contentType = ImageUtility.getMimeType(file.absolutePath)
            // Convert this file to bitmap
            var bitmap: Bitmap = ImageUtility.fileToBitmap(file.absolutePath)
            // Check the if image is rotated while being picked from gallery
            bitmap = ImageUtility.rotateImage(bitmap, file.absolutePath)
            // Create a new file name
            val newFileName: String = UUID.randomUUID().toString() + "." + file.extension
            // Get a new path
            val newFilePath: String = ImageUtility.bitmapToFile(bitmap, context, newFileName, activity)
            // Assign the new path to a new file
            file = File(newFilePath)

            val client = OkHttpClient()
            val fileBody = RequestBody.create(MediaType.parse(contentType), file)

            if (accountType == "1") {
                val fullName = params.getString("full_name")
                val userName = params.getString("user_name")
                val password = params.getString("password")
                val email = params.getString("email")
                val phoneNumber = params.getString("phone_number")
                val city = params.getString("city")
                val gender = params.getString("gender")

                val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", contentType)
                        .addFormDataPart("full_name", fullName)
                        .addFormDataPart("user_name", userName)
                        .addFormDataPart("password", password)
                        .addFormDataPart("email", email)
                        .addFormDataPart("phone_number", phoneNumber)
                        .addFormDataPart("city", city)
                        .addFormDataPart("gender", gender)
                        .addFormDataPart("account_type", accountType)
                        .addFormDataPart("avatar", file.absolutePath.substringAfterLast('/') + 1, fileBody)
                        .build()

                val request = okhttp3.Request.Builder()
                        .addHeader("Content-Type", "multipart/form-data")
                        .url(path)
                        .post(requestBody)
                        .build()

                var response: okhttp3.Response? = null

                try {
                    response = client.newCall(request).execute()

                    if (response!!.isSuccessful) {
                        completionHandler(response.body().string())
                    } else {
                        completionHandler(response.message())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    response?.body()?.close()
                }

            } else {
                val compNameTxt = params.getString("compNameTxt")
                val compUserNameTxt = params.getString("compUserNameTxt")
                val compEmailTxt = params.getString("compEmailTxt")
                val compPasswordTxt = params.getString("compPasswordTxt")
                val compPhoneTxt = params.getString("compPhoneTxt")
                val compAddressTxt = params.getString("compAddressTxt")
                val compWebsiteTxt = params.getString("compWebsiteTxt")

                val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("type", contentType)
                        .addFormDataPart("company_name", compNameTxt)
                        .addFormDataPart("user_name", compUserNameTxt)
                        .addFormDataPart("company_email", compEmailTxt)
                        .addFormDataPart("company_password", compPasswordTxt)
                        .addFormDataPart("company_phone_number", compPhoneTxt)
                        .addFormDataPart("company_city", compAddressTxt)
                        .addFormDataPart("company_description", compWebsiteTxt)
                        .addFormDataPart("account_type", accountType)
                        .addFormDataPart("company_avatar", file.absolutePath.substringAfterLast('/') + 1, fileBody)
                        .build()

                val request = okhttp3.Request.Builder()
                        .addHeader("Content-Type", "multipart/form-data")
                        .url(path)
                        .post(requestBody)
                        .build()

                var response: okhttp3.Response? = null

                try {
                    response = client.newCall(request).execute()

                    if (response!!.isSuccessful) {
                        completionHandler(response.body().string())
                    } else {
                        completionHandler(response.message())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    response?.body()?.close()
                }
            }
        }).start()
    }
}


