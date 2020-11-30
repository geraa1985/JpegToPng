package com.geraa1985.jpegtopng.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.geraa1985.jpegtopng.databinding.ActivityMainBinding
import com.geraa1985.jpegtopng.mvp.presenter.MainPresenter
import com.geraa1985.jpegtopng.mvp.view.MainView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView, View.OnClickListener {

    companion object {
        private const val PERMISSIONS_CODE = 111
        private const val PICK_PHOTO_FOR_CONVERT = 777
    }

    private lateinit var binding: ActivityMainBinding
    private val presenter by moxyPresenter {
        MainPresenter(
            Converter(),
            AndroidSchedulers.mainThread()
        )
    }

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var convertDialog: AlertDialog

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnOpen.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnOpen.id -> { presenter.buttonClicked() }
        }
    }

    override fun pickImage() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        intent.type = "image/jpeg"
        startActivityForResult(intent, PICK_PHOTO_FOR_CONVERT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_FOR_CONVERT && resultCode == RESULT_OK) {
            val selectedImage = data?.data
            val filePathColumn = arrayOf("_data")
            val cursor = selectedImage?.let {
                contentResolver.query(
                    it, filePathColumn, null, null, null
                )
            }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val picturePath = columnIndex?.let { cursor.getString(it) }
            cursor?.close()
            picturePath?.let { presenter.readImage(it) }
        }
    }

    @SuppressLint("ShowToast")
    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("ShowToast")
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showConvertDialog(path: String) {
        convertDialog = AlertDialog.Builder(this)
            .setTitle("Conversion")
            .setMessage("Conversion in progress...\n$path")
            .setNegativeButton("Cancel") { _, _ ->
                presenter.stopConvert()
            }
            .setCancelable(false)
            .show()
    }

    override fun hideConvertDialog() {
        convertDialog.cancel()
    }

    override fun showStop(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun checkPermissions() {
        presenter.checkPermissions(permissions, fun(string: String) = checkSelfPermission(string), PackageManager.PERMISSION_GRANTED)
    }

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        presenter.onRequestPermissionsResult(
            requestCode, grantResults, PackageManager.PERMISSION_GRANTED,
            PERMISSIONS_CODE, permissions.size
        )
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}