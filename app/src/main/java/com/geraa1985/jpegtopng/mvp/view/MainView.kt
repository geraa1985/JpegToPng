package com.geraa1985.jpegtopng.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState

@SingleState
interface MainView: MvpView {
    fun pickImage()
    fun checkPermissions()
    fun showSuccess(message: String)
    fun showError(message: String)
    fun showConvertDialog(path: String)
    fun hideConvertDialog()
    fun showStop(message: String)
    fun requestPermissions()
}