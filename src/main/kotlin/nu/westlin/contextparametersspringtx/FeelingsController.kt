package nu.westlin.contextparametersspringtx

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val service: FeelingsService
) {

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getById(@PathVariable id: FeelingId): ResponseEntity<Feeling> {
        val feeling = service.getById(id)
        return if (feeling != null) {
            ResponseEntity.ok(feeling)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody feeling: CreateFeelingDTO): ResponseEntity<Void> {
        val createdFeeling = service.create(feeling)

        val location: URI = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdFeeling.id)
            .toUri()

        return ResponseEntity.created(location).build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: FeelingId): ResponseEntity<Void> {
        val existed = service.delete(id)

        return if (existed) {
            ResponseEntity.noContent().build() // 204 No Content är standard vid DELETE
        } else {
            ResponseEntity.notFound().build()  // 404 Not Found
        }
    }
}