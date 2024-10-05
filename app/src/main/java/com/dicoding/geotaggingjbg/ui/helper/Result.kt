package com.dicoding.geotaggingjbg.ui.helper

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
}

enum class State {
    SUCCESS,
    ERROR,
    LOADING
}