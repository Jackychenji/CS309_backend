package cs309.dormiselect.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class DormiselectBackendApplication

fun main(args: Array<String>) {
	runApplication<DormiselectBackendApplication>(*args)
}
