package com.enkefalostechnologies.calendarpro.util

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.DataStoreItemChange
import com.amplifyframework.datastore.generated.model.KnowYourDay
import com.amplifyframework.datastore.generated.model.ListGroup
import com.amplifyframework.datastore.generated.model.ReminderEnum
import com.amplifyframework.datastore.generated.model.RepeatDays
import com.amplifyframework.datastore.generated.model.RepeatType
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.amplifyframework.datastore.generated.model.SubscriptionType
import com.amplifyframework.datastore.generated.model.TaskType
import com.amplifyframework.datastore.generated.model.Tasks
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.WaterInTake
import com.enkefalostechnologies.calendarpro.util.AppUtil.getCurrentTemporalDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.getCurrentTemporalDateTime
import com.enkefalostechnologies.calendarpro.util.AppUtil.getStartAndEndOfMonthDates
import com.enkefalostechnologies.calendarpro.util.AppUtil.getTemporalDateOfCurrentWeek
import com.enkefalostechnologies.calendarpro.util.AppUtil.getTemporalDateTimeFromHrsAndMin
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDate
import com.enkefalostechnologies.calendarpro.util.Extension.dateToTemporalDateTime
import com.enkefalostechnologies.calendarpro.util.Extension.getNextDayFromDate
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class AmplifyDataModelUtil(context: Context) {
    fun setSubscription(
        email: String,
        validUpToDate: Date,
        subscriptionType: SubscriptionType? = SubscriptionType.NONE,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onFailureToSave: Consumer<DataStoreException>
    ) {
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), { matches ->
                if (matches.hasNext()) {
                    val original = matches.next()
                    val edited = original.copyOfBuilder()
                        .subscriptionType(subscriptionType)
                        .subscriptionValidUpTo(validUpToDate.dateToTemporalDate())
                        .subscriptionStatus(SubscriptionStatus.ACTIVE)
                        .build()
                    Amplify.DataStore.save(
                        edited,
                        onItemSaved,
                        onFailureToSave
                    )
                }
            },
            onFailureToSave
        )

    }


//    fun updateLoginTime(
//        email: String,
//        onItemSaved: Consumer<DataStoreItemChange<User>>,
//        onFailureToSave: Consumer<DataStoreException>,
//    ) {
//        val date = Date()
//        val offsetMillis = TimeZone.getDefault().getOffset(date.time).toLong()
//        val offsetSeconds = TimeUnit.MILLISECONDS.toSeconds(offsetMillis).toInt()
//        val temporalDateTime = Temporal.DateTime(date, offsetSeconds)
//        Amplify.DataStore.query(
//            User::class.java, Where.matches(User.EMAIL.eq(email)), { matches ->
//                if (matches.hasNext()) {
//                    val original = matches.next()
//                    val edited = original.copyOfBuilder()
//                        .build()
//                    Amplify.DataStore.save(
//                        edited,
//                        onItemSaved,
//                        onFailureToSave
//                    )
//                }
//            },
//            onFailureToSave
//        )
//    }


    fun fetchUserByEmail(
        email: String,
        onQueryResults: Consumer<Iterator<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), onQueryResults,
            onQueryFailure
        )
    }

    fun updateUser(
        name: String,
        email: String,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        var count = 0
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), {
                while (it.hasNext()) {
                    val user = it.next().copyOfBuilder()
                        .name(name)
                        .email(email).build()
                    count++
                    if (count == 2) {
                        Amplify.DataStore.save(user, {}, onQueryFailure)
                    } else {
                        Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
                    }
                }

            },
            onQueryFailure
        )
    }

    fun updateUserPicUrl(
        email: String,
        picUrl: String,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        var count = 0
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), {
                while (it.hasNext()) {
                    val user = it.next().copyOfBuilder()
                        .picUrl(picUrl).build()
                    count++
                    if (count == 2) {
                        Amplify.DataStore.save(user, {}, onQueryFailure)
                    } else {
                        Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
                    }
                }

            },
            onQueryFailure
        )
    }

    fun setUserVerified(
        email: String,
        verificationEnabled: Boolean,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        var count = 0
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), {
                while (it.hasNext()) {
                    val user = it.next().copyOfBuilder()
                        .isEmailVerified(verificationEnabled).build()
                    count++
                    if (count == 2) {
                        Amplify.DataStore.save(user, {}, onQueryFailure)
                    } else {
                        Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
                    }
                }
            },
            onQueryFailure
        )
    }

    fun setUserNotification(
        email: String,
        deviceId: String,
        notificationEnabled: Boolean,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        var count = 0
        Amplify.DataStore.query(
            User::class.java,
            Where.matches(User.EMAIL.eq(email).or(User.DEVICE_ID.eq(deviceId))),
            {
                while (it.hasNext()) {
                    val user = it.next().copyOfBuilder()
                        .isNotificationEnabled(notificationEnabled).build()
                    count++
                    if (count == 2) {
                        Amplify.DataStore.save(user, {}, onQueryFailure)
                    } else {
                        Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
                    }
                }
            },
            onQueryFailure
        )
    }

    fun setUserReminder(
        email: String,
        deviceId: String,
        reminderEnabled: Boolean,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        var count = 0
        Amplify.DataStore.query(
            User::class.java,
            Where.matches(User.EMAIL.eq(email).or(User.DEVICE_ID.eq(deviceId))),
            {
                while (it.hasNext()) {
                    val user = it.next().copyOfBuilder()
                        .isReminderEnabled(reminderEnabled).build()
                    count++
                    if (count == 2) {
                        Amplify.DataStore.save(user, {}, onQueryFailure)
                    } else {
                        Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
                    }
                }
            },
            onQueryFailure
        )
    }

    fun deletePhoto(
        email: String,
    ) {
        Amplify.DataStore.query(
            User::class.java, Where.matches(User.EMAIL.eq(email)), {
                while (it.hasNext()) {
                    val picUrl = it.next().picUrl
                    picUrl.replace(
                        "https://dailyplanner2707f971afff4d3d96da7d9ba27d2df482147-staging.s3.eu-north-1.amazonaws.com/public/",
                        ""
                    )
                    Amplify.Storage.remove(picUrl, {}, {})
                }

            },
            {}
        )
    }


    fun observeTask(onDataChange: Consumer<DataStoreItemChange<Tasks>>) {
        Amplify.DataStore.observe(
            Tasks::class.java,
            {},
            onDataChange,
            {},
            {}
        )
    }
    fun getUserDetails(email:String, onQueryResults:Consumer<Iterator<User>>, onQueryFailure:Consumer<DataStoreException>) {
        Amplify.DataStore.query(User::class.java,Where.matches(User.EMAIL.eq(email)),onQueryResults,onQueryFailure)
    }

    fun markTaskDone(
        taskId: String,
        email: String,
        deviceId: String,
        onItemSaved: Consumer<DataStoreItemChange<Tasks>>,
        onFailureToSave: Consumer<DataStoreException>,
    ) = Amplify.DataStore.query(
        Tasks::class.java,
        Where.matches(
            Tasks.ID.eq(taskId).and(Tasks.EMAIL.eq(email).or(Tasks.DEVICE_ID.eq(deviceId)))
        ),
        { matches ->
            if (matches.hasNext()) {
                val original = matches.next()
                val edited = original.copyOfBuilder()
                    .isCompleted(true)
                    .updatedAt(getCurrentTemporalDateTime())
                    .build()
                Amplify.DataStore.save(
                    edited,
                    onItemSaved,
                    onFailureToSave
                )
            }
        },
        onFailureToSave
    )

    fun markTaskAsUnDone(
        taskId: String,
        email: String,
        deviceId: String,
        onItemSaved: Consumer<DataStoreItemChange<Tasks>>,
        onFailureToSave: Consumer<DataStoreException>,
    ) = Amplify.DataStore.query(
        Tasks::class.java,
        Where.matches(
            Tasks.ID.eq(taskId).and(Tasks.EMAIL.eq(email).or(Tasks.DEVICE_ID.eq(deviceId)))
        ),
        { matches ->
            if (matches.hasNext()) {
                val original = matches.next()
                val edited = original.copyOfBuilder()
                    .isCompleted(false)
                    .updatedAt(getCurrentTemporalDateTime())
                    .build()
                Amplify.DataStore.save(
                    edited,
                    onItemSaved,
                    onFailureToSave
                )
            }
        },
        onFailureToSave
    )

    fun getTodaySTaskAndEvents(
        email: String,
        deviceId: String,
        onQueryResults: Consumer<Iterator<Tasks>>,
        onFailureToSave: Consumer<DataStoreException>,
    ) = Amplify.DataStore.query(
        Tasks::class.java,
        if (email == "") Tasks.DEVICE_ID.eq(deviceId)
            .and(Tasks.DATE.eq(AppUtil.getCurrentTemporalDate()))
        else Tasks.EMAIL.eq(email).and(Tasks.DATE.eq(AppUtil.getCurrentTemporalDate())),
        onQueryResults,
        onFailureToSave
    )


    fun renameListGroup(
        name: String,
        listGroupId: String,
        onItemRenamed: Consumer<DataStoreItemChange<ListGroup>>,
        onFailure: Consumer<DataStoreException>,
    ) {
        Amplify.DataStore.query(
            ListGroup::class.java,
            Where.matches(ListGroup.ID.eq(listGroupId)),
            { data ->
                val listGroup = data.next().copyOfBuilder().name(name).title(name).build()
                Amplify.DataStore.save(listGroup, onItemRenamed, onFailure)
            }, onFailure
        )
    }

    fun fetchTaskById(
        taskId: String,
        onSuccess: Consumer<Iterator<Tasks>>,
        onFailure: Consumer<DataStoreException>
    ) =
        Amplify.DataStore.query(
            Tasks::class.java,
            Where.matches(Tasks.ID.eq(taskId)),
            onSuccess,
            onFailure
        )

    fun setSubscriptionStatus(
        email: String,
        subscriptionStatus: SubscriptionStatus,
        validUpToDate: Date,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        Amplify.DataStore.query(
            User::class.java,
            Where.matches(User.EMAIL.eq(email)),
            {
                    val user = it.next().copyOfBuilder()
                        .subscriptionValidUpTo(validUpToDate.dateToTemporalDate())
                        .subscriptionStatus(subscriptionStatus)
                        .build()
                        Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
            },
            onQueryFailure
        )
    }
    fun setSubscriptionStatus(
        email: String,
        subscriptionStatus: SubscriptionStatus,
        onItemSaved: Consumer<DataStoreItemChange<User>>,
        onQueryFailure: Consumer<DataStoreException>
    ) {
        Amplify.DataStore.query(
            User::class.java,
            Where.matches(User.EMAIL.eq(email)),
            {
                val user = it.next().copyOfBuilder()
                    .subscriptionStatus(subscriptionStatus)
                    .build()
                Amplify.DataStore.save(user, onItemSaved, onQueryFailure)
            },
            onQueryFailure
        )
    }

    fun fetchNextDaysTask(
        email: String,
        deviceId: String,
        onSuccess: Consumer<Iterator<Tasks>>,
        onFailure: Consumer<DataStoreException>
    ) =
        Amplify.DataStore.query(
            Tasks::class.java,
            Where.matches(Tasks.EMAIL.eq(email).or(Tasks.DEVICE_ID.eq(deviceId)).and(Tasks.DATE.eq(Date().getNextDayFromDate().dateToTemporalDate()))),
            onSuccess,
            onFailure
        )
    fun fetchTodaySTask(
        email: String,
        deviceId: String,
        onSuccess: Consumer<Iterator<Tasks>>,
        onFailure: Consumer<DataStoreException>
    ) =
        Amplify.DataStore.query(
            Tasks::class.java,
            Where.matches(Tasks.EMAIL.eq(email).or(Tasks.DEVICE_ID.eq(deviceId)).and(Tasks.DATE.eq(AppUtil.getCurrentTemporalDate()))),
            onSuccess,
            onFailure
        )

    fun fetchHolidays(
        email: String,
        deviceId: String,
        onSuccess: Consumer<Iterator<Tasks>>,
        onFailure: Consumer<DataStoreException>
    ) {
        val query=if(email.isEmpty())
            Where.matches(
                Tasks.DEVICE_ID.eq(deviceId)
                    .and(Tasks.TASK_TYPE.eq(TaskType.HOLIDAY))
            )
        else
            Where.matches(
                Tasks.EMAIL.eq(email)
                    .and(Tasks.TASK_TYPE.eq(TaskType.HOLIDAY))
            )

        Amplify.DataStore.query(
            Tasks::class.java,
            query,
            onSuccess,
            onFailure
        )
    }
    fun updateReminderRequestCode(task:Tasks, requestCode:Int)= Amplify.DataStore.save(
            task.copyOfBuilder().reminderRequestCode(requestCode).build(),
            {},
            {}
        )
    fun updateNotificationRequestCode(task:Tasks, requestCode:Int)= Amplify.DataStore.save(
        task.copyOfBuilder().notiRequestCode(requestCode).build(),
        {},
        {}
    )
    fun updateCountrySelected(
        email:String,
        onSuccess: Consumer<DataStoreItemChange<User>>,
        onFailure: Consumer<DataStoreException>,
        countries:String
    ){
        Amplify.DataStore.query(
            User::class.java,
            Where.matches(User.EMAIL.eq(email)),
            {
                while (it.hasNext()) {
                    val user = it.next().copyOfBuilder()
                        .countrySelected(countries)
                        .build()
                    Amplify.DataStore.save(user, onSuccess, onFailure)
                }
            },
            onFailure
        )
    }

}