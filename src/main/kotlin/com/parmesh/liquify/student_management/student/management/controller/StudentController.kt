package com.parmesh.liquify.student_management.student.management.controller

import com.parmesh.liquify.student_management.student.management.domain.Student
import com.parmesh.liquify.student_management.student.management.service.StudentService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
import org.springframework.web.multipart.MultipartFile

@CrossOrigin
@RestController
@RequestMapping("/api/student")
@Service
class StudentController(private val studentService: StudentService) {

    //    Getting details of students using /get routing and GET method and without pagination
    @GetMapping("/list/all")
    fun getListofAllStudent(): List<Student> {
        return studentService.listStudent()
    }

    // Getting details of students using /get routing and GET method and pagination
    @GetMapping("/list/all/page")
    fun getAllStudentData(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "2") size: Int): List<Student> {
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

//   delete student all
    @DeleteMapping("/delete/all")
    fun deleteAllStudent() {
        return studentService.deleteAll()
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

//    Upload the file
    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): String {
        if(file.contentType == "text/csv") {
            return studentService.uploadAndSaveCSV(file.inputStream)
        }
        return studentService.uploadFile(file)
    }

//    download as pdf
    @GetMapping("/download/file/{id}")
    fun downloadPdf(@PathVariable id: String): ResponseEntity<ByteArray> {
        val (filename, inputStream) = studentService.downloadRecordPdf(id)  // No changes here
        val fileContent = inputStream.readBytes()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(fileContent)
    }

    @GetMapping("/download/user/{id}")
    fun downloadUserPdf(@PathVariable id: String): ResponseEntity<ByteArray> {
        val user = studentService.getStudentPdfId(id) ?: return ResponseEntity.notFound().build()

        // Generate the PDF
        val pdfContent = studentService.generateStudentPdf(user)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_info.pdf\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
            .body(pdfContent)
    }


//    send email
    @PostMapping("send/email/{id}")
    fun sendEmail(@PathVariable id: String) {
        val email = studentService.getEmailById(id)
        return studentService.sendEmail(email, "Student Information", "This is the student information")
    }

}