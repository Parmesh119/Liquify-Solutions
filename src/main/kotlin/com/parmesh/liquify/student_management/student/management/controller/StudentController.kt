package com.parmesh.liquify.student_management.student.management.controller

import com.parmesh.liquify.student_management.student.management.domain.Student
import com.parmesh.liquify.student_management.student.management.service.StudentService
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
@RequestMapping("/api/student")
@Service
class StudentController(private val studentService: StudentService) {

    // Getting details of students using /get routing and GET method
    //    var l = mutableListOf<List>()
    @GetMapping("/list/all")
    fun getAllStudentData(@RequestParam(defaultValue = "0") page: Int,
                          @RequestParam(defaultValue = "2") size: Int): List<Student> {
        return studentService.getAll(page, size).content
    }

//    getting student by id
    @GetMapping("/get/{id}")
    fun getById(@PathVariable id: String): Student {
        return studentService.getStudentById(id)
    }

//    add new student
    @PostMapping("/add/new")
    fun addStudent(@RequestBody student: Student): Student {
        return studentService.addStudentData(student)
    }

//    delete student
    @DeleteMapping("/delete/{id}")
    fun deleteStudent(@PathVariable id: String) {
        return studentService.deleteById(id)
    }

//    Update student data
    @PutMapping("/update")
    fun updateStudent(@RequestBody student: Student): Student  {
        return studentService.update(student)
    }

//    search by name
    @GetMapping("/get/search")
    fun searchByName(@RequestParam("s", defaultValue = "") s: String): List<Student> {
        return studentService.searchRecords(s)
    }
}