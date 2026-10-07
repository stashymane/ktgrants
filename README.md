# ktgrants

![Maven Central Version](https://img.shields.io/maven-central/v/dev.stashy.ktgrants/permissions?logo=apachemaven&label=central)
![Maven Snapshot](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fstashy%2Fktgrants%2Fpermissions%2Fmaven-metadata.xml&strategy=latestProperty&label=snapshot)

---
ergonomic permission and authorization library

> [!WARNING]
> Currently only available as a snapshot. Check the snapshot instructions below.  
> Full release depends on [Kotlin multi-field value class support][MFVC support].

## Setup

> [!NOTE]
> For a full example, check out the sample submodule.

`versions.toml`

```toml
[versions]
ktgrants = "version"

[libs]
ktgrants-permissions = { module = "dev.stashy.ktgrants:permissions", version.ref = "ktgrants" }
ktgrants-authorization = { module = "dev.stashy.ktgrants:authorization", version.ref = "ktgrants" }
```

<details>
<summary>Snapshot versions</summary>

`settings.gradle.kts` or `build.gradle.kts`

```kotlin
repositories {
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "Central Portal Snapshots"

        content {
            // only include specific libraries for snapshots here
            includeGroup("dev.stashy.ktgrants")
        }
    }
    mavenCentral()
}
```

</details>

## Example

Based on the source available in the `sample` module.

### Model

```kotlin
object PermissionModel : PermissionConfig {
    val Read by Grant
    val Write by Grant
    val Create by Grant
    val Delete by Grant

    val Admin by Grant
    // ...

    override val resolver: PermissionResolver = resolverOf {
        grants {
            Admin provides setOf(Read, Write, Create, Delete)
        }
        wildcard {
            grant = true // enables matching grants against wildcard, e.g. `foo:subject:*` matching any grant
        }
    }
}
```

### Permissions

```kotlin
data class Foo(val id: Id) : Permissible.Entity by Foo.withSubject(id) {
    companion object : Permissible.Type by Permissible.Type<Foo>()
}

context(PermissionModel) {
    // entity we want to authorize for
    val foo = Foo(id = Id("bar"), content = "baz")

    // pretend this is data fetched from the database
    val user = User(
        id = Id("user-1"), // `Id` implements `SubjectProvider`
        permissions = setOf(permission { Read any Foo }) // gives user "foo:*:read" permission
    )

    // user context expands permission entries based on the current model
    val userContext = UserContext(user)

    if (userContext.hasPermission { Read on foo }) { // checks if user has "foo:bar:read"
        // do things - succeeds since `user` has `foo:*:read` permission
    } else {
        throw IllegalArgumentException("User ${user.id} does not have permission to read ${foo.id}")
    }
}
```

### Authorization

```kotlin
// authorizers should preferably be constructed only once,
// therefore putting them inside of classes is discouraged
private object FooAuthorizer : Authorizer<Foo, PermissionModel> by Authorizer({
    on(Reading or Creating) {
        ensure { actor.hasPermission { Read on target } }
        // additional checks if needed
    }
    on(Writing) {
        ensure { actor.hasPermission { Write on target } }
    }
    on(Deleting) {
        deny()
    }
})

// updated Foo class from the Permissions example, now implementing Authorized<...>
data class Foo(val id: Id) : Permissible.Entity by Foo.withSubject(id),
    Authorized<Foo, PermissionModel> by Authorized.by(FooAuthorizer) {
    companion object : Permissible.Type by Permissible.Type<Foo>()
}

context(PermissionModel) {
    // same setup as before
    val foo = Foo(id = Id("bar"), content = "baz")
    val user = User(
        id = Id("user-1"),
        permissions = setOf(permission { Read any Foo })
    )
    val userContext = UserContext(user)

    // the important bit:
    if (userContext.authorize { Reading the foo }) {
        // etc.
    }
}
```

[MFVC support]: https://youtrack.jetbrains.com/issue/KT-77734
