package com.example.breaktrial.Notifications


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Token(var token: String): Parcelable {
    constructor() : this("")
}