package com.example.planner.data.repository

import android.net.Uri
import com.example.planner.data.local.TasksDao
import com.example.planner.data.model.*
import com.example.planner.data.remote.TasksRemoteDataSource
import com.example.planner.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val remoteDataSource: TasksRemoteDataSource,
    private val localDataSource: TasksDao,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {

    init {
        firebaseDatabase.setPersistenceEnabled(true)
        getTasks().keepSynced(true)
        getUsers().keepSynced(true)
    }

    //Firebase Auth
    @ExperimentalCoroutinesApi
    fun firebaseSignIn(loginCredentials: LoginCredentials): Flow<Resource<Boolean>> = callbackFlow {

        val subscription = firebaseAuth.signInWithEmailAndPassword(
            loginCredentials.email,
            loginCredentials.password
        )

        subscription.addOnCompleteListener {

            if (it.isSuccessful) {

                val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!)
                subscriber.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        offer(
                            Resource(
                                Resource.Status.SUCCESS,
                                snapshot.child("first_time").value as Boolean,
                                "Sign in successful!"
                            )
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        offer { Resource.error(error.message, null) }
                    }
                })
            } else {

                offer(Resource.error(it.exception?.localizedMessage!!, null))
            }
        }

        awaitClose{ subscription.addOnCompleteListener {} }
    }

    @ExperimentalCoroutinesApi
    fun firebaseSignUp(registrationCredentials: RegistrationCredentials): Flow<Resource<User>> = callbackFlow {

        val listener = firebaseAuth.createUserWithEmailAndPassword(
            registrationCredentials.email,
            registrationCredentials.password
        ).addOnCompleteListener {

                if (it.isSuccessful) {

                    val firebaseUser = firebaseCurrentUser()
                    if (firebaseUser != null) {

                        getUsers().child(firebaseUser.uid).child("first_time").setValue(true)
                        getUsers().child(firebaseUser.uid).child("tasks").setValue("0")

                        val updateUserInfo =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(registrationCredentials.name)
                                .build()

                        firebaseUser.updateProfile(updateUserInfo)
                        firebaseUser.sendEmailVerification()
                    }

                    offer(Resource(Resource.Status.SUCCESS, null, "Sign up successful!"))

                } else {

                    offer(Resource.error(it.exception?.localizedMessage!!, null))
                }
        }
        awaitClose { listener.addOnCompleteListener{} }
    }

    @ExperimentalCoroutinesApi
    fun firebaseCheckFirstTime(): Flow<Resource<Boolean>> = callbackFlow {

        val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    offer(Resource.success(snapshot.child("first_time").value as Boolean))
                }

                override fun onCancelled(error: DatabaseError) {

                    offer{Resource.error(error.message, null)}
                }
            }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!)
        subscriber.addListenerForSingleValueEvent(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    fun removeFirstTimeUser() {

        getUsers().child(firebaseCurrentUser()?.uid!!).child("first_time").setValue(false)
    }

    fun removeSurveyFromUser(survey: Boolean) {

        getUsers().child(firebaseCurrentUser()?.uid!!).child("survey").setValue(survey)
    }

    fun setCategories(categories: List<Int>) {

        categories.forEach {

            getUsers().child(firebaseCurrentUser()?.uid!!).child("profile").child("categories").child(
                it.toString()
            ).setValue(-1)
        }
    }

    fun setAnswer(answer: Int, category: Int) {

        getUsers().child(firebaseCurrentUser()?.uid!!).child("profile").child("categories").child(
            category.toString()
        ).setValue(answer)
    }

    fun changeName(name: String) {

        val updateUserInfo =
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

        firebaseCurrentUser()?.updateProfile(updateUserInfo)
    }

    fun getUserName() = firebaseCurrentUser()?.displayName

    fun firebaseCurrentUser() = firebaseAuth.currentUser
    fun firebaseSignOut() = firebaseAuth.signOut()
    fun forgotPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)

    @ExperimentalCoroutinesApi
    fun changePassword(password: String) = callbackFlow {

       val listener = firebaseCurrentUser()?.
       updatePassword(password)?.
       addOnCompleteListener {

           if (it.isSuccessful)
               offer(Resource(Resource.Status.SUCCESS, null, "Password changed successfully!"))
           else
               offer(Resource(Resource.Status.ERROR, null, it.exception?.localizedMessage))
       }

        awaitClose{ listener?.addOnCompleteListener {} }
    }

    @ExperimentalCoroutinesApi
    fun deleteAccount() = callbackFlow {

        val listener = firebaseCurrentUser()?.delete()?.addOnCompleteListener {

            if (it.isSuccessful)
                offer(Resource(Resource.Status.SUCCESS, null, "Successfully deleted account"))
            else
                offer(Resource(Resource.Status.ERROR, null, it.exception?.localizedMessage))
        }

        awaitClose { listener?.addOnCompleteListener {} }
    }

    @ExperimentalCoroutinesApi
    fun checkSurveyStatus() = callbackFlow<Resource<Boolean>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                offer(Resource.success(snapshot.value as Boolean))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(Resource.error(error.message))
            }
        }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!).child("survey")
        subscriber.addListenerForSingleValueEvent(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    //Firebase Database

    @ExperimentalCoroutinesApi
    fun addTask(task: Task) = callbackFlow{

        val listener =
            getUsers()
                .child(firebaseCurrentUser()?.uid!!)
                .child("tasks")
                .child(task.id.toString().trim())
                .setValue(task)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        offer(Resource(Resource.Status.SUCCESS, null, null))
                    } else {
                        offer(Resource(Resource.Status.ERROR, null, null))
                    }
                }
        awaitClose { listener.addOnCompleteListener {} }
    }

    @ExperimentalCoroutinesApi
    fun getTaskID(task: Task) = callbackFlow<Resource<Task>>{

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val lastTaskID: Int = snapshot.children.last().key?.toInt()!!
                val newId = lastTaskID + 1

                task.id = newId
                offer(Resource.success(task))
            }

            override fun onCancelled(error: DatabaseError) {
                offer(Resource.error(error.message))
            }
        }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!).child("tasks")
        subscriber.addListenerForSingleValueEvent(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun setImage(selectedImage: Uri) = callbackFlow {

        val updateInfo = UserProfileChangeRequest.Builder()
            .setPhotoUri(selectedImage)
            .build()

        val listener = firebaseCurrentUser()?.updateProfile(updateInfo)?.addOnCompleteListener {

            if (it.isSuccessful)
                offer(Resource(Resource.Status.SUCCESS, null, null))
            else
                offer(Resource(Resource.Status.ERROR, null, it.exception?.localizedMessage))

        }

        awaitClose { listener?.addOnCompleteListener {} }
    }

    fun getImage() = firebaseCurrentUser()?.photoUrl

    fun getTodayTasks(tasks: List<Task>): List<Task> {

        tasks.sortedBy { it.dateFrom }

        val todayTasks = mutableListOf<Task>()

        tasks.forEach {

            val time: Long = it.dateFrom - Calendar.getInstance().timeInMillis + 86400002

            if (todayTasks.size < 7)
                if (time in 0..86400002)
                    todayTasks.add(it)
        }

        return todayTasks
    }

    fun getThisWeekTasks(tasks: List<Task>): List<Task> {

        tasks.sortedBy { it.dateFrom }

        val weekTasks = mutableListOf<Task>()

        tasks.forEach {

            val time: Long = it.dateFrom - Calendar.getInstance().timeInMillis

            if (weekTasks.size < 7)
                if (time in 0..604800000)
                    weekTasks.add(it)
        }

        return weekTasks
    }

    fun getThisMonthTasks(tasks: List<Task>): List<Task> {

        tasks.sortedBy { it.dateFrom }

        val monthTasks = mutableListOf<Task>()

        tasks.forEach {

            val time: Long = it.dateFrom - Calendar.getInstance().timeInMillis

            if (monthTasks.size < 7)
                if (time in 0..2592000000)
                    monthTasks.add(it)
        }

        return monthTasks
    }

    @ExperimentalCoroutinesApi
    fun getAllTasks() = callbackFlow<Resource<List<Task>>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val allTasks = mutableListOf<Task>()

                snapshot.children.forEach {

                    if (it.key != "0") {

                        val id = it.child("id").value as Long?
                        val icon = it.child("icon").value as Long
                        val color = it.child("color").value as Long
                        val reminder = it.child("reminder").value as Long

                        val task =  Task(
                            id?.toInt(),
                            it.child("type").value as Boolean,
                            it.child("title").value as String,
                            it.child("description").value as String,
                            icon.toInt(),
                            color.toInt(),
                            it.child("dateFrom").value as Long,
                            it.child("dateTo").value as Long,
                            it.child("allDay").value as Boolean,
                            reminder.toInt()
                        )

                        allTasks.add(task)
                    }
                }

                offer(Resource.success(allTasks))
            }

            override fun onCancelled(error: DatabaseError) {

                offer(Resource.error(error.message))
            }
        }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!).child("tasks")
        subscriber.addValueEventListener(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun getSurveyProfile() = callbackFlow<Resource<Profile>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val categories = mutableListOf<Int>()
                val answers = mutableListOf<Int>()

                snapshot.child("categories").children.forEach {

                    val category = it.key
                    val answer = it.value as Long

                    if (category != null) {
                        categories.add(category.toInt())
                        answers.add(answer.toInt())
                    }
                }

                offer(Resource.success(Profile(categories, answers)))
            }

            override fun onCancelled(error: DatabaseError) {

                offer(Resource.error(error.message))
            }
        }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!).child("profile")
        subscriber.addValueEventListener(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun checkDailyTasks() = callbackFlow<Resource<Boolean>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val lastRefresh = snapshot.value as Long
                val day = Calendar.getInstance().timeInMillis - lastRefresh

                if (day in 0..86400002) {

                    offer(Resource.success(false))
                } else {

                    getUsers().child(firebaseCurrentUser()?.uid!!).child("refresh").setValue(
                        Calendar.getInstance().timeInMillis
                    )
                    offer(Resource.success(true))
                }
            }

            override fun onCancelled(error: DatabaseError) {

                offer(Resource.error(error.message))
            }
        }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!).child("refresh")
        subscriber.addValueEventListener(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun addDailyTask(task: Task) = callbackFlow<Resource<Boolean>>{

        val listener =
            getUsers()
                .child(firebaseCurrentUser()?.uid!!)
                .child("tasks")
                .child(task.id.toString().trim())
                .setValue(task)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        offer(Resource(Resource.Status.SUCCESS, null, null))
                    } else {
                        offer(Resource(Resource.Status.ERROR, null, null))
                    }
                }
        awaitClose { listener.addOnCompleteListener {} }
    }

    @ExperimentalCoroutinesApi
    fun getDailyTasks(profile: Profile) = callbackFlow<Resource<List<Task>>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val allDailyTasks = mutableListOf<Task>()

                profile.categories.forEachIndexed { index, category ->

                    snapshot.children.forEach { dbCategory ->

                        if (category == dbCategory.key?.toInt()) {

                            dbCategory.children.forEach { dbAnswers ->

                                if (dbAnswers.key?.toInt() == profile.answers[index]) {

                                    val task =  Task(
                                        null,
                                        true,
                                        dbAnswers.children.shuffled().first().value as String,
                                        "",
                                        2131230873,
                                        -16776961,
                                        Calendar.getInstance().timeInMillis,
                                        Calendar.getInstance().timeInMillis,
                                        true,
                                        1
                                    )

                                    allDailyTasks.add(task)
                                }
                            }
                        }
                    }
                }

                offer(Resource.success(allDailyTasks))
            }

            override fun onCancelled(error: DatabaseError) {

                offer(Resource.error(error.message))
            }
        }

        val subscriber = getTasks()
        subscriber.addValueEventListener(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }

    @ExperimentalCoroutinesApi
    fun getCurrentDayTasks(date: Date) = callbackFlow<Resource<List<Task>>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val currentDayTasks = mutableListOf<Task>()

                snapshot.children.forEach {

                    val taskTime = it.child("dateFrom").value as Long

                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                    val taskDate = formatter.format(taskTime)
                    val selectedDate = formatter.format(date)

                    if (it.key != "0" && taskDate == selectedDate) {

                        val id = it.child("id").value as Long?
                        val icon = it.child("icon").value as Long
                        val color = it.child("color").value as Long
                        val reminder = it.child("reminder").value as Long

                        val task =  Task(
                            id?.toInt(),
                            it.child("type").value as Boolean,
                            it.child("title").value as String,
                            it.child("description").value as String,
                            icon.toInt(),
                            color.toInt(),
                            it.child("dateFrom").value as Long,
                            it.child("dateTo").value as Long,
                            it.child("allDay").value as Boolean,
                            reminder.toInt()
                        )
                        currentDayTasks.add(task)
                    }
                }

                offer(Resource.success(currentDayTasks))
            }

            override fun onCancelled(error: DatabaseError) {

                offer(Resource.error(error.message))
            }
        }

        val subscriber = getUsers().child(firebaseCurrentUser()?.uid!!).child("tasks")
        subscriber.addValueEventListener(listener)

        awaitClose { subscriber.removeEventListener(listener) }
    }



    private fun getUsers() = firebaseDatabase.reference.child("main").child("users")
    private fun getTasks() = firebaseDatabase.reference.child("main").child("tasks")

}