import dev.stashy.ktgrants.permissions.api.Permissible
import dev.stashy.ktgrants.permissions.data.Grant
import dev.stashy.ktgrants.permissions.data.Subject
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Id(val value: String) : Subject.Provider {
    override fun toSubject(): Subject = Subject(value)
}

@Serializable
data class Foo(
    val id: Id,
    val name: String
) : Permissible.Entity by Permissible<Foo>(id) {
    companion object : Permissible.Type by Permissible<Foo>()
}

@Serializable
data class Bar(
    val id: Id,
    val description: String
) : Permissible.Entity by Permissible<Bar>({ id.toSubject() }) {
    companion object : Permissible.Type by Permissible<Bar>()
}

object Grants {
    val Read = Grant("read")
    val Write = Grant("write")

    val Create = Grant("create")
    val Delete = Grant("delete")

    val FullControl = Grant("full_control")
    val Admin = Grant("admin")
    val Owner = Grant("owner")
}
