package authorization

import Foo
import Id
import dev.stashy.ktgrants.authorization.api.Authorizer
import dev.stashy.ktgrants.authorization.api.ContextualAuthorizer
import dev.stashy.ktgrants.authorization.or
import dev.stashy.ktgrants.permissions.api.hasPermission
import dev.stashy.ktgrants.permissions.dsl.GrantDsl.Companion.on
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DslTest {
    private val foo = Foo(Id("foo"), "test")
    private val config = AuthModel

    @Test
    fun `authorizable verification`() {
        val authorizer = Authorizer<Foo, AuthModel> {
            on(Reading or Creating) {
                ensure { actor.hasPermission { Read on target } }
            }
        }
        val actor = config.actor(foo, setOf(config.Read on foo))

        with(config) {
            assertTrue(authorizer.verify(foo, actor, Reading))
            assertTrue(authorizer.verify(foo, actor, Creating))
            assertFalse(authorizer.verify(foo, actor, Writing))
        }
    }

    @Test
    fun `authorizer is usable as contextual authorizer with unit context`() {
        val authorizer: ContextualAuthorizer<Foo, AuthModel, Unit> = Authorizer<Foo, AuthModel> {
            on(Reading) {
                ensure { true }
            }
        }
        val actor = config.actor(foo, emptySet())

        with(config) {
            assertTrue(authorizer.verify(foo, actor, Reading, Unit))
            assertFalse(authorizer.verify(foo, actor, Writing, Unit))
        }
    }

    @Test
    fun `contextual authorizable verification`() {
        val authorizer =
            ContextualAuthorizer<Foo, AuthModel, String> { reason ->
                on(Writing) {
                    ensure { reason == "allowed" }
                }
            }
        val actor = config.actor(foo, emptySet())

        with(config) {
            assertTrue(authorizer.verify(foo, actor, Writing, "allowed"))
            assertFalse(authorizer.verify(foo, actor, Writing, "denied"))
        }
    }
}
