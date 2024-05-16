package com.enkefalostechnologies.calendarpro.model

import com.amplifyframework.datastore.generated.model.Tasks

data class TaskModel (
    var position:Int,
    var task: Tasks
)