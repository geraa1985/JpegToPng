package com.geraa1985.jpegtopng.android

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.geraa1985.jpegtopng.mvp.model.IConverter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Converter : IConverter {

    @SuppressLint("SdCardPath")
    override fun convert(path: String): Single<String> = Single.fromCallable {
        try {
            Thread.sleep(8000)
            val bmp = BitmapFactory.decodeFile(path)
            val convertedImage = File("/sdcard/Download/convertedimg.png")
            val outStream = FileOutputStream(convertedImage)
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
            return@fromCallable "The conversion was successful"
        } catch (e: IOException) {
            throw e
        }
    }.subscribeOn(Schedulers.io())

}