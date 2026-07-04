package nu.westlin.contextparametersspringtx

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

@RestController
@RequestMapping("/feelings")
class FeelingsController(
    private val repository: FeelingsRepository
) {

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getById(@PathVariable id: Int): ResponseEntity<Feeling> {
        val feeling = repository.getFeelingById(id)
        return if (feeling != null) {
            ResponseEntity.ok(feeling)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @Transactional
    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody feeling: Feeling): ResponseEntity<Void> {
        val createdFeeling = repository.create(feeling)

        val location: URI = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdFeeling.id)
            .toUri()

        return ResponseEntity.created(location).build()

    }
}