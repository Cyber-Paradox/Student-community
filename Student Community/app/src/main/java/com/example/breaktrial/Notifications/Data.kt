package com.example.breaktrial.Notifications


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Data(var user: String, var icon: Int, var body: String, var title: String, var sented: String): Parcelable {
    constructor() : this("",0,"","","")
}
/*
class Data {
    private var user: String = ""
    private var icon = 0
    private var body: String = ""
    private var title: String = ""
    private var sented: String = ""
}*/