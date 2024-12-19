package com.parmesh.liquify.student_management.student.management.repository

import com.parmesh.liquify.student_management.student.management.domain.Student
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface studentRepo: MongoRepository<Student, String> {
    // Case-insensitive search using regex without using $options
    @Query("{'name': {\$regex: '^?0$', \$options: 'i'}}")
    fun search(s: String): List<Student>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Student>
}

interface UserRepository : PagingAndSortingRepository<Student, Long>

@Configuration
class MongoConfig {
    @Bean
    fun mongoTemplate(mongoDbFactory: MongoDatabaseFactory, mongoConverter: MappingMongoConverter): MongoTemplate {
        return MongoTemplate(mongoDbFactory, mongoConverter)
    }

    @Bean
    fun gridFsTemplate(mongoDbFactory: MongoDatabaseFactory, mongoConverter: MappingMongoConverter): GridFsTemplate {
        return GridFsTemplate(mongoDbFactory, mongoConverter)
    }
}

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
