package com.example.breaktrial

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.breaktrial.AdapterClasses.ChatsAdapter
import com.example.breaktrial.AdapterClasses.UserAdapter
import com.example.breaktrial.Fragments.APIService
import com.example.breaktrial.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.FirebaseDatabaseKtxRegistrar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import modelClasses.Chat
import modelClasses.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit:String? = null
    var firebaseUser:FirebaseUser?=null
    var chatsAdapter: ChatsAdapter?=null
    var mChatlist : List<Chat>?=null
    lateinit var recylcer_view_chats: RecyclerView
    var reference: DatabaseReference?=null

    var notify = false
    var apiService: APIService?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title =""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent= Intent(this@MessageChatActivity,WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiService=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser
recylcer_view_chats=findViewById(R.id.recycler_view_chats)
        recylcer_view_chats.setHasFixedSize(true)
        val linearLayoutManager =LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recylcer_view_chats.layoutManager = linearLayoutManager

         reference = FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit!!)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)
                username_mchat.text = user!!.username
                Picasso.get().load(user.profile).into(profile_image_mchat)
                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.profile)//user.getProfile()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        send_message_btn.setOnClickListener {
            notify = true
            val message = text_message.text.toString()
            if(message==""){
                Toast.makeText(this@MessageChatActivity,"Please write a message first...",Toast.LENGTH_LONG).show()
            }
            else{
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }

        attach_image_file_btn.setOnClickListener {
            notify=true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }
        seenMessage(userIdVisit!!)
    }



    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messagekey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messagekey
        reference.child("Chats")
            .child(messagekey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatLists")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit!!)
                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists()){
                                chatsListReference.child("id").setValue(userIdVisit)
                            }

                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatLists")
                                .child(userIdVisit!!)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }
                    })

                }
            }

        //implement the notifications using fcm
        val usersReference = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(Users::class.java)
                if(notify){
                    sendNotification(receiverId, user!!.username, message)
                }
                notify=false
            }
        })
    }

    private fun sendNotification(receiverId: String?, username: String, message: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(dataSnapshot in p0.children){
                    val token:Token?=dataSnapshot.getValue(Token::class.java)
                    val data = Data(
                        firebaseUser!!.uid,
                        R.drawable.brk,
                        "$username: $message",
                        "New Message",
                        userIdVisit!!
                    )

                    val sender = Sender(data,token!!.token.toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code()==200){
                                    if(response.body()!!.success!= 1){
                                        Toast.makeText(this@MessageChatActivity,"Failed, Nothing Happened!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                        })


                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==438 &&resultCode==Activity.RESULT_OK&&data!=null&&data.data!=null){
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Image is uploading , please wait.....")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filepath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filepath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filepath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                progressBar.dismiss()
                                //implement the notifications using fcm
                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)
                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(Users::class.java)
                                        if(notify){
                                            sendNotification(userIdVisit, user!!.username, "sent you an image.")
                                        }
                                        notify=false
                                    }
                                })
                            }
                        }
                }
            }
        }
    }
    private fun retrieveMessages(senderId: String,receiverId: String? ,receiverImageUrl:String?) {
mChatlist = ArrayList()
        val  reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatlist as ArrayList<Chat>).clear()
                for (snapshot in p0.children)
                {
                    val  chat = snapshot.getValue(Chat::class.java)
                    if(chat!!.receiver.equals(senderId) && chat.sender.equals((receiverId))
                                || chat.receiver.equals(receiverId)&& chat.sender.equals(senderId))
                    {
                        (mChatlist as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(this@MessageChatActivity, (mChatlist as ArrayList<Chat>),receiverImageUrl!!)
                recylcer_view_chats.adapter=chatsAdapter
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    var seenListener: ValueEventListener?=null
private fun seenMessage(userId: String)
{
    val reference = FirebaseDatabase.getInstance().reference.child("Chats")
     seenListener = reference.addValueEventListener(object: ValueEventListener{
         override fun onDataChange(p0: DataSnapshot) {
            for (dataSnapshot in p0.children)
            {
                val chat = dataSnapshot.getValue(Chat::class.java)
                if (chat!!.receiver.equals(firebaseUser!!.uid) && chat.sender.equals(userId))
                {
                    val hashMap = HashMap<String, Any>()
                    hashMap["isseen"] = true
                    dataSnapshot.ref.updateChildren(hashMap)
                }
            }
         }

         override fun onCancelled(p0: DatabaseError) {

         }
     })
}

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}
