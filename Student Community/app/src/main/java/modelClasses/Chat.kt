package modelClasses


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Chat(val sender: String, val message: String, val receiver: String,
            val isseen: Boolean, val url:String, val messageId: String): Parcelable {
    constructor() : this("", "", "",false,"","")
}
// package modelClasses
//
// class Chat {
// private var sender:String = ""
// private var message:String = ""
// private var receiver:String = ""
// private var isseen = false
// private var url:String = ""
// private var messageId:String = ""
//
// constructor()
// constructor(
// sender: String,
// message:String,
// receiver:String,
// isseen:Boolean,
// url:String,
// messageId:String
// ) {
// this.sender = sender
// this.message=message
// this.receiver=receiver
// this.isseen= isseen
// this.url=url
// this.messageId=messageId
//
//
// }
//
//
//
// fun getSender():String?{
// return sender
// }
// fun setSender(sender: String?){
// this.sender=sender!!
// }
// fun getMessage():String?{
// return message
// }
// fun setMessage(message: String?){
// this.message=message!!
// }
// fun getReceiver():String?{
// return receiver
// }
// fun setReceiver(sender: String?){
// this.receiver=receiver!!
// }
// fun isIsSeen():Boolean{
// return isseen
// }
// fun setIsSeen(isseen: Boolean?){
// this.isseen=isseen!!
// }
// fun getUrl():String?{
// return url
// }
// fun setUrl(url: String?){
// this.url=url!!
// }
// fun getMesssageId():String?{
// return messageId
// }
// fun setMessageId(messageId: String?){
// this.messageId=messageId!!
// }
// }