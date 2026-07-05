package nu.westlin.contextparametersspringtx

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToFeelingIdConverter : Converter<String, FeelingId> {
    override fun convert(source: String): FeelingId {
        // Här tvingar vi Spring att gå via din valideringslogik!
        val numericId = source.toInt()
        return FeelingId(numericId)
    }
}