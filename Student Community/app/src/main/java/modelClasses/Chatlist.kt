package modelClasses


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Chatlist(val id: String): Parcelable {
    constructor() : this("")
}
// class Chatlist {
// private var id:String=""
//
// constructor()
// constructor(id: String) {
// this.id = id
// }
// fun getId():String?{
// return id
// }
// fun setId(id: String?){
// this.id=id!!
