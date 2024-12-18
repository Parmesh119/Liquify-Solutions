package com.parmesh.liquify.student_management.student.management.repository

import com.parmesh.liquify.student_management.student.management.domain.Student
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

import org.springframework.data.repository.PagingAndSortingRepository

interface studentRepo: MongoRepository<Student, String> {
    // Case-insensitive search using regex without using $options
    @Query("{'name': {\$regex: '^?0$', \$options: 'i'}}")
    fun search(s: String): List<Student>
}

interface UserRepository : PagingAndSortingRepository<Student, Long>

//
//package com.parmesh.liquify.student_management.student.management.repository
//
//import com.parmesh.liquify.student_management.student.management.domain.Student
//import org.springframework.data.mongodb.repository.MongoRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.mongodb.core.MongoTemplate
//import org.springframework.data.mongodb.core.query.Query
//import org.springframework.data.mongodb.core.query.Criteria
//
//interface studentRepo : MongoRepository<Student, String> {
//
//    @Autowired
//    private lateinit var mongoTemplate: MongoTemplate
//
//    fun search(s: String): List<Student> {
//        val query = Query()
//        query.addCriteria(Criteria.where("name").regex("(?i)$s")) // (?i) for case-insensitive search
//        return mongoTemplate.find(query, Student::class.java)
//    }
//}
