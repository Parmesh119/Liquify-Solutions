package com.parmesh.liquify.student_management.student.management.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "student-info")
data class Student (

    @Id var id: String = UUID.randomUUID().toString(),
    var name: String,
    var age: Int,
    var assignClass: String
)