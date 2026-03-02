package model

import dev.stashy.ktgrants.permissions.data.Subject
import kotlin.jvm.JvmInline

@JvmInline
value class Id(val value: String) : Subject.Provider {
    override fun toSubject(): Subject = Subject(value)
}
