import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate

@Configuration
class MongoConfig {
    @Bean("mongoTemplate")
    public fun mongoTemplate(mongoDbFactory: MongoDatabaseFactory): MongoTemplate {
        return MongoTemplate(mongoDbFactory)
    }

    @Bean("customGridFsTemplate")
    public fun gridFsTemplate(mongoDbFactory: MongoDatabaseFactory, mongoTemplate: MongoTemplate): GridFsTemplate {
        return GridFsTemplate(mongoDbFactory, mongoTemplate.converter)
    }
}
