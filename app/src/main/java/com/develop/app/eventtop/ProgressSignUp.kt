package com.develop.app.eventtop

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.develop.app.eventtop.api.ApiController
import com.develop.app.eventtop.api.BaseUrl
import com.develop.app.eventtop.api.VolleyService
import com.develop.app.eventtop.utils.ImageUtility
import com.develop.app.eventtop.utils.ProgressDialog
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.*

class ProgressSignUp : AppCompatActivity() {

    private val PERMISSION_CODE = 2225
    private val TAG = "ProgressSignUp"
    private var isFirstTime = true
    private lateinit var params: JSONObject

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_sign_up)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val title: TextView = findViewById(R.id.title)
        val typeface = Typeface.createFromAsset(assets, "STV_0.ttf")
        title.typeface = typeface
        title.text = "اختر صورة"

        val viewGroup = findViewById<ViewGroup>(R.id.container)
        TextModifier.replaceTypeface(viewGroup, this)

        val circleImageView: CircleImageView = findViewById(R.id.circleImageView)

        val croppedImage: ByteArray? = if (intent != null) {
            intent.getByteArrayExtra("croppedImage")
        } else {
            null
        }

        val userPhoto: Bitmap? = if (croppedImage != null) {
            BitmapFactory.decodeByteArray(croppedImage, 0, croppedImage.count())
        } else {
            null
        }

        val d: Drawable = resources.getDrawable(R.drawable.user_photo)
        val defaultPhoto: Bitmap = ImageUtility.drawableToBitmap(d)

        val avatar = if (userPhoto != null) {
            ImageUtility.bitmapToFile(userPhoto, this, UUID.randomUUID().toString() + ".jpg" , this)
        } else {
            ImageUtility.bitmapToFile(defaultPhoto, this, UUID.randomUUID().toString() + ".jpg", this)
        }

        params = JSONObject()

        val preferences3 = getSharedPreferences("account_type_preferences", Context.MODE_PRIVATE)
        val accountType = preferences3.getInt("account_type", 0)

        if (accountType != 0) {
            if (accountType == 1) {
                val preferences = getSharedPreferences("signup_preferences", Context.MODE_PRIVATE)

                val fullNameTxt = preferences.getString("fullNameTxt", "0")
                val userNameTxt = preferences.getString("userNameTxt", "0")
                val passwordTxt = preferences.getString("passwordTxt", "0")
                val emailTxt = preferences.getString("emailTxt", "0")
                val phoneTxt = preferences.getString("phoneTxt", "0")
                val gender = preferences.getString("gender", "1")
                val city = preferences.getString("city", "1")

                params.put("account_type", accountType.toString())
                params.put("full_name", fullNameTxt)
                params.put("user_name", userNameTxt)
                params.put("password", passwordTxt)
                params.put("email", emailTxt)
                params.put("phone_number", phoneTxt)
                params.put("city", city)
                params.put("gender", gender)
                params.put("avatar", avatar)
            } else {
                val preferences = getSharedPreferences("signup_comp_preferences", Context.MODE_PRIVATE)

                val compNameTxt = preferences.getString("compNameTxt", "0")
                val compUserNameTxt = preferences.getString("compUserNameTxt", "0")
                val compEmailTxt = preferences.getString("compEmailTxt", "0")
                val compPasswordTxt = preferences.getString("compPasswordTxt", "0")
                val compPhoneTxt = preferences.getString("compPhoneTxt", "0")
                val compAddressTxt = preferences.getString("compAddressTxt", "1")
                val compWebsiteTxt = preferences.getString("compWebsiteTxt", "1")

                params.put("account_type", accountType.toString())
                params.put("compNameTxt", compNameTxt)
                params.put("compUserNameTxt", compUserNameTxt)
                params.put("compEmailTxt", compEmailTxt)
                params.put("compPasswordTxt", compPasswordTxt)
                params.put("compPhoneTxt", compPhoneTxt)
                params.put("compAddressTxt", compAddressTxt)
                params.put("compWebsiteTxt", compWebsiteTxt)
                params.put("avatar", avatar)
            }
        }

        val userImage: ImageView = findViewById(R.id.circleImageView)
        userImage.setOnClickListener({ _ ->
            requestPermission()
        })

        val button1: Button = findViewById(R.id.button)
        button1.setOnClickListener { _ ->
            requestPermission()
        }

        val button2: Button = findViewById(R.id.button2)
        button2.setOnClickListener({ view ->
            val progress = ProgressDialog(this, this)
            progress.setMessage("جاري تسجيل حساب جديد")

            val service = VolleyService()
            val api = ApiController(service)
            api.multipart(BaseUrl().url + "/auth/register", this, this, params, completionHandler = {response ->
                Log.d(TAG, response)
                val obj = JSONObject(response)

                if (obj.has("status")) {
                    progress.dismiss()

                    runOnUiThread {
                        SweetAlertDialog(this@ProgressSignUp, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("تم تسحيل الحساب")
                                .setContentText(obj.getString("message"))
                                .setConfirmClickListener { sweetAlertDialog ->
                                    sweetAlertDialog.cancel()
                                    startActivity(Intent(this, SignIn::class.java))
                                }
                                .show()
                    }
                } else {
                    progress.dismiss()
                    val mSnack: Snackbar = Snackbar.make(view, "اسم المستخدم او البريد الالكتروني موجود مسبقا", Snackbar.LENGTH_SHORT)
                    TextModifier.replaceTypeface(mSnack, this)
                }
            })
        })

        if (userPhoto != null) {
            circleImageView.setImageBitmap(userPhoto)
            button2.text = "استمرار"
        } else {
            circleImageView.setImageBitmap(defaultPhoto)
        }

        val button3: Button = findViewById(R.id.button3)
        button3.setOnClickListener({ _ ->
            if (accountType == 1) {
                startActivity(Intent(this, SignUp::class.java))
            } else {
                startActivity(Intent(this, SignUpCompany::class.java))
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            val preferences = getSharedPreferences("window_preferences", Context.MODE_PRIVATE)
            val isFromGallery = preferences.getBoolean("isFromGallery", false)

            if (isFromGallery) {
                startActivity(Intent(this, ViewGallery::class.java))
            } else {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val preferences = getSharedPreferences("window_preferences", Context.MODE_PRIVATE)
        val isFromGallery = preferences.getBoolean("isFromGallery", false)

        if (isFromGallery) {
            startActivity(Intent(this, ViewGallery::class.java))
        } else {
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, ViewGallery::class.java)
            startActivity(intent)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionExplanation()
                return
            } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && isFirstTime) {
                isFirstTime = false
                val permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    TODO("VERSION.SDK_INT < JELLY_BEAN")
                }
                ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_CODE)
                return
            } else {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun showPermissionExplanation() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle("طلب السماح")
        alertBuilder.setMessage("السماح بالوصول الى ملفات التخزين")
        alertBuilder.setPositiveButton("سماح", { _, _ ->
            val permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN")
            }
            ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_CODE)
        }).setNegativeButton("رفض", { dialogInterface, _ ->
            dialogInterface.dismiss()
        }).create().show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, ViewGallery::class.java)
                startActivity(intent)
                Log.d(TAG, "GRANTED")
            } else {
                Log.d(TAG, "DENIED")
            }
        }
    }
}
