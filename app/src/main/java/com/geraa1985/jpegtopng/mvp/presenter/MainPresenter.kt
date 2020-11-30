package com.geraa1985.jpegtopng.mvp.presenter

import com.geraa1985.jpegtopng.mvp.model.IConverter
import com.geraa1985.jpegtopng.mvp.view.MainView
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter

class MainPresenter(private val iConverter: IConverter, private val uiThread: Scheduler) :
    MvpPresenter<MainView>() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var convertDisposable: Disposable
    private var isAccess = false

    fun buttonClicked() {
        viewState.checkPermissions()
        if (!isAccess) {
            viewState.requestPermissions()
        } else {
            viewState.pickImage()
        }
    }

    fun readImage(path: String) {
        viewState.showConvertDialog(path)
        convertDisposable = iConverter.convert(path)
            .observeOn(uiThread)
            .subscribe({
                viewState.hideConvertDialog()
                viewState.showSuccess(it)
            }, { error ->
                error.message?.let { viewState.showError(it) }
            })
        compositeDisposable.add(convertDisposable)
    }

    fun stopConvert() {
        convertDisposable.dispose()
        viewState.showStop("Conversoin has been stoped")
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        granted: Int,
        code: Int,
        count: Int
    ) {
        if (requestCode == code) {
            if (grantResults.size == count &&
                grantResults[0] == granted &&
                grantResults[1] == granted
            ) {
                isAccess = true
            }
        }
        isAccess = false
    }

    fun checkPermissions(
        permissions: Array<String>,
        permissionStatus: ((String) -> Int),
        granted: Int
    ) {
        var count = 0
        for (permission in permissions) {
            val pStatus = permissionStatus(permission)
            if (pStatus == granted) {
                count++
            }
        }
        if (count == 2) {
            isAccess = true
        } else {
            viewState.requestPermissions()
        }
    }
}
