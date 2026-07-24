import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.SubjectProvider
import dev.stashy.ktgrants.permissions.api.Permissible
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Id(val value: String) : SubjectProvider {
    override fun toSubject(): Subject = Subject(value)
}

@Serializable
data class Foo(
    val id: Id,
    val name: String
) : Permissible.Entity by Foo.withSubject(id) {
    companion object : Permissible.Type by Permissible.Type<Foo>()
}

@Serializable
data class Bar(
    val id: Id,
    val description: String
) : Permissible.Entity by Permissible.Entity(Bar, id) {
    companion object : Permissible.Type by Permissible.Type<Bar>()
}

@Serializable
object System : Permissible.Type by Permissible.Type<System>()
