package com.parmesh.liquify.student_management.student.management.service

import API
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.mongodb.BasicDBObject
import com.parmesh.liquify.student_management.student.management.domain.Student
import com.parmesh.liquify.student_management.student.management.repository.studentRepo
import org.apache.catalina.User
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.data.util.Pair
import java.io.ByteArrayOutputStream
import java.io.InputStream
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.opencsv.CSVReader
import jakarta.mail.internet.MimeMessage
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.messaging.Task
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.io.BufferedReader
import java.io.InputStreamReader

@Service
class StudentService(@Autowired private val restTempplate: RestTemplate, private val studentRepo: studentRepo, private val gridFsTemplate: GridFsTemplate, @Autowired private val mongoTemplate: MongoTemplate, @Autowired private val emailSender: JavaMailSender) {

//    list of all students
    fun listStudent(): List<Student> {
        return studentRepo.findAll()
    }

//    list of all students with pagination
    fun getAll(page: Int, size: Int): Page<Student> {
        val pageable = PageRequest.of(page, size)
        return studentRepo.findAll(pageable)
    }

//    get student by id
    fun getStudentById(id: String): Student {
        return studentRepo.findById(id).orElseThrow { IllegalStateException("Not found") }
    }

//    add new student
    fun addStudentData(student: Student): Student {
        return studentRepo.save(student)
    }

//    delete student by id
    fun deleteById(id: String) {
        if(studentRepo.existsById(id)) {
            studentRepo.deleteById(id)
        }
    }

//    delete all students
    fun deleteAll() {
        studentRepo.deleteAll()
    }

//    update student
    fun update(student: Student, id: String): Student {
        val existingStudent = studentRepo.findById(id)
        if(existingStudent.isPresent) {
            val newStudent = studentRepo.save(student)
            return newStudent
        } else {
            throw IllegalStateException("No student found with id: ${id}")
        }
    }

//    searching the records using @Query annotation method but still the using MongoTemplate is pending
    fun searchRecords(s: String): List<Student> {
        try {
            return studentRepo.search(".*$s.*")
        } catch (e: Exception) {
            throw IllegalArgumentException(e.message)
        }
    }

//  uploading the file and checking the header is valid or not
    fun uploadFile(file: MultipartFile): String {
        val metadata = BasicDBObject()
        metadata["contentType"] = file.contentType
        metadata["originalName"] = file.originalFilename
        val fileId: ObjectId = gridFsTemplate.store (
            file.inputStream, // File content as InputStream
            file.originalFilename, // Filename
            file.contentType,      // Content Type
            metadata               // Metadata
        )
        return fileId.toHexString()
    }

    //    header checking for csv file only
    fun uploadAndSaveCSV(file: InputStream): String {
        val requiredColumns = listOf("name", "age", "assignClass", "gender", "email")
        val reader = CSVReader(InputStreamReader(file))
        val header = reader.readNext()

        if (header == null) {
            throw IllegalArgumentException("header not present")
        }

        if (header.size  != requiredColumns.size) {
            throw IllegalArgumentException("size mismatch between headers")
        }

        header.forEachIndexed { index, headerItem ->
            if (headerItem.trim() != requiredColumns[index].trim()) {
                print(requiredColumns[index])
                print(headerItem)
                throw IllegalArgumentException("mismatch for $headerItem not as per requirement")
            }
        }

        val students = reader.readAll().map { record ->
            Student(
                name = record[0],
                age = record[1].toInt(),
                assignClass = record[2],
                gender = record[3],
                email = record[4]
            )
        }
        studentRepo.saveAll(students)
        return "success"
    }

//    fun uploadFile(file: MultipartFile): String {
//        // Define the expected header
//        val expectedHeader = listOf("name", "age", "assignClass", "gender", "email")
//
//        // Check if the file is empty
//        if (file.isEmpty) {
//            throw IllegalArgumentException("File is empty")
//        }
//
//        // Parse and validate the header
//        val reader = BufferedReader(InputStreamReader(file.inputStream))
//        val headerLine = reader.readLine() ?: throw IllegalArgumentException("File is empty or invalid")
//
//        val fileHeader = headerLine.split(",").map { it.trim() }
//
//        if (fileHeader != expectedHeader) {
//            throw IllegalArgumentException("Invalid file header. Expected: $expectedHeader, Found: $fileHeader")
//        }
//
//        // If header is valid, proceed to upload the file
//        val metadata = BasicDBObject()
//        metadata["contentType"] = file.contentType
//        metadata["originalName"] = file.originalFilename
//        val fileId: ObjectId = gridFsTemplate.store(
//            file.inputStream, // File content as InputStream
//            file.originalFilename, // Filename
//            file.contentType,      // Content Type
//            metadata               // Metadata
//        )
//
//        return fileId.toHexString()
//    }



//    download the file
    fun downloadRecordPdf(Id: String): kotlin.Pair<String, InputStream> {
        val file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").`is`(ObjectId(Id))))
            ?: throw IllegalStateException("File not found")

        val filename = file.filename
        val inputStream = gridFsTemplate.getResource(file).inputStream

        return Pair(filename, inputStream)  // Correct way to create a Pair in Kotlin
    }

//    get student by id and generate pdf
    fun getStudentPdfId(id: String): Student? {
        return studentRepo.findById(id).orElseThrow { IllegalStateException("Not found") }
    }

//    generate pdf for student
    fun generateStudentPdf(user: Student): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()

        // Create a PdfWriter
        val pdfWriter = PdfWriter(byteArrayOutputStream)

        // Create a PdfDocument
        val pdfDocument = PdfDocument(pdfWriter)

        // Create a Document for content
        val document = Document(pdfDocument)

        // Add content to PDF (you can customize this part)
        document.add(Paragraph("User Information"))
        document.add(Paragraph("Name: ${user.name}"))
        document.add(Paragraph("Age: ${user.age}"))
        document.add(Paragraph("Gender: ${user.gender}"))
        document.add(Paragraph("Email: ${user.email}"))

        // Close the document and writer
        document.close()
        pdfDocument.close()

        return byteArrayOutputStream.toByteArray()
    }

//    getting email address by id
    fun getEmailById(id: String): String {
        val student = studentRepo.findById(id).orElseThrow { IllegalStateException("Not found") }
        return student.email
    }

//    sending email
    fun sendEmail(to: String, subject: String, body: String) {
        val message: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true) // Set to true for HTML content
        emailSender.send(message)
    }

//    searching based on name and including pagination
    fun searchandPagination(name: String, pageable: Pageable): Page<Student> {
        return studentRepo.findByNameContainingIgnoreCase(name, pageable)
    }

//    filtering using Mongotemplate
    fun getFilteredTasks(name: String?, age: Int?, assignClass: String?): List<Student> {
        val query = Query()

        if (name != null || age != null || assignClass != null) {
            name?.let { query.addCriteria(Criteria.where("name").regex(it, "i")) }
            age?.let { query.addCriteria(Criteria.where("age").`is`(it)) }
            assignClass?.let { query.addCriteria(Criteria.where("assignClass").`is`(it)) }
        } else {
            // If no filter parameters are provided, return all tasks
            return mongoTemplate.findAll(Student::class.java)
        }

        return mongoTemplate.find(query, Student::class.java)
    }

//    fetching the data from external API
    private final val api_key = "5326e37d1d25994a5a5e669d808fcb86"
    private val api_url  = "https://api.openweathermap.org/data/2.5/forecast?lat=35&lon=139&appid=${api_key}"

    fun getWeather(lat: Double, lon: Double): API {
        val finalApi = api_url.replace("{lat}", lat.toString()).replace("{lon}", lon.toString())
        val responseEntity: ResponseEntity<API> = restTempplate.exchange(finalApi, HttpMethod.GET, null, API::class.java)
        return responseEntity.body ?: throw Exception("API response body is null")
    }

}