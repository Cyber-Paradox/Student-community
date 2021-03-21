package modelClasses


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users(val uid: String, val username: String, val profile: String,
           val cover:String, val status:String, val search: String,val facebook: String,val insta: String): Parcelable {
    constructor() : this("", "", "","","","","","")
}

/*class Users {
    private var uid:String=""
    private var username:String=""
    private var profile:String=""
    private var cover:String=""
    private var status:String=""
    private var search:String=""
    private var facebook:String=""
    private var insta:String=""

    constructor()


    constructor(
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        insta: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.insta = insta
    }
    fun getUID():String?{
        return uid
    }
    fun setUID(uid:String){
        this.uid=uid
    }
    fun getUserName():String?{
        return username
    }
    fun setUsername(username:String){
        this.username=username
    }
    fun getProfile():String{
        return profile
    }
    fun setProfile(profile:String){
        this.profile=profile
    }
    fun getCover():String{
        return cover
    }
    fun setCover(cover:String){
        this.cover=cover
    }
    fun getStatus():String{
        return status
    }
    fun setStatus(status:String){
        this.status=status
    }
    fun getSearch():String{
        return search
    }
    fun setSearch(search:String){
        this.search=search
    }
    fun getFacebook():String{
        return facebook
    }
    fun setFacebook(facebook:String){
        this.facebook=facebook
    }
    fun getInsta():String{
        return insta
    }
    fun setInsta(insta:String){
        this.insta=insta
    }



}*/