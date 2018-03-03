package com.develop.app.eventtop.api

import android.app.Activity
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

interface ServiceInterface {
    fun postJsonObject(path: String, params: JSONObject, completionHandler: (response: JSONObject?) -> Unit)
    fun getJsonObject(path: String, completionHandler: (response: JSONObject?) -> Unit)
    fun postJsonArray(path: String, params: JSONArray, completionHandler: (response: JSONArray?) -> Unit)
    fun getJsonArray(path: String, completionHandler: (response: JSONArray?) -> Unit)
    fun multipart(path: String, context: Context, activity: Activity, params: JSONObject, completionHandler: (response: String?) -> Unit)
}