package com.geraa1985.jpegtopng.mvp.model

import io.reactivex.rxjava3.core.Single

interface IConverter {
    fun convert(path: String): Single<String>
}