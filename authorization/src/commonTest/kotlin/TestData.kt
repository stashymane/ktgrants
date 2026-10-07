import authorization.AuthModel
import dev.stashy.ktgrants.authorization.api.Authorized
import dev.stashy.ktgrants.authorization.api.Authorizer
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.Subject
import dev.stashy.ktgrants.permissions.SubjectProvider
import dev.stashy.ktgrants.permissions.api.Permissible
import dev.stashy.ktgrants.permissions.api.hasPermission
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Id(val value: String) : SubjectProvider {
    override fun toSubject(): Subject = Subject(value)
}

@Serializable
object System : Permissible.Type by Permissible.Type<System>()

@Serializable
data class Foo(
    val id: Id,
    val name: String
) : Permissible.Entity by Foo.withSubject(id),
    Authorized<Foo, AuthModel> by Authorized.by(FooAuthorizer) {
    companion object : Permissible.Type by Permissible.Type<Foo>()
}

@Serializable
data class Bar(
    val id: Id,
    val description: String
) : Permissible.Entity by Permissible.Entity(Bar, id) {
    companion object : Permissible.Type by Permissible.Type<Bar>()
}

private object FooAuthorizer : Authorizer<Foo, AuthModel> by Authorizer({
    on(Reading or Creating) {
        ensure { actor.hasPermission { Read on target } }
    }
})
