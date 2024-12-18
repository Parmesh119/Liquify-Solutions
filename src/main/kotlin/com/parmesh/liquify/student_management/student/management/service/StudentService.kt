package com.parmesh.liquify.student_management.student.management.service

import com.parmesh.liquify.student_management.student.management.domain.Student
import com.parmesh.liquify.student_management.student.management.repository.studentRepo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class StudentService(private val studentRepo: studentRepo) {
    fun getAll(page: Int, size: Int): Page<Student> {
        val pageable = PageRequest.of(page, size)
        return studentRepo.findAll(pageable)
    }

    fun getStudentById(id: String): Student {
        return studentRepo.findById(id).orElseThrow { IllegalStateException("Not found") }
    }


    fun addStudentData(student: Student): Student {
        return studentRepo.save(student)
    }

    fun deleteById(id: String) {
        if(studentRepo.existsById(id)) {
            studentRepo.deleteById(id)
        }
    }

    fun update(student: Student): Student {
        val existingStudent = studentRepo.findById(student.id)
        if(existingStudent.isPresent) {
            val newStudent = studentRepo.save(student)
            return newStudent
        } else {
            throw IllegalStateException("No student found with id: ${student.id}")
        }
    }

    fun searchRecords(s: String): List<Student> {
        try {
            return studentRepo.search(".*$s.*")
        } catch (e: Exception) {
            throw IllegalArgumentException(e.message)
        }

    }
}