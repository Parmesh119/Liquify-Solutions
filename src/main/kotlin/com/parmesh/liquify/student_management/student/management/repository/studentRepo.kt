package com.parmesh.liquify.student_management.student.management.repository

import com.parmesh.liquify.student_management.student.management.domain.Student
import org.springframework.data.mongodb.repository.MongoRepository

interface studentRepo: MongoRepository<Student, String>