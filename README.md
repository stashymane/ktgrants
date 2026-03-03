# ktgrants

![Maven Central Version](https://img.shields.io/maven-central/v/dev.stashy.ktgrants/permissions?logo=apachemaven&label=central)
![Maven Snapshot](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fstashy%2Fktgrants%2Fpermissions%2Fmaven-metadata.xml&strategy=latestProperty&label=snapshot)

---
(soon to be) ergonomic permission/grant library

> [!WARNING]
> Currently only available as a snapshot. Check the snapshot instructions below.

## Setup

> [!NOTE]
> For a full example, check out the sample submodule.

`versions.toml`

```toml
[versions]
ktgrants = "version"

[libs]
ktgrants-permissions = { module = "dev.stashy.ktgrants:permissions", version.ref = "ktgrants" }
ktgrants-ksp = { module = "dev.stashy.ktgrants:ksp", version.ref = "ktgrants" }
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

## KSP module

```kotlin
kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

dependencies {
    add("kspCommonMainMetadata", libs.ktgrants.ksp)
}

ksp {
    arg("ktgrants.package", "generated")
}

tasks {
    withType<KotlinCompilationTask<*>>().configureEach {
        if (name != "kspCommonMainKotlinMetadata") {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

```

## Example

Based on the source available in the `sample` module.

```kotlin
val foo = Foo(id = Id("bar"), content = "baz")
val user = User(
    id = Id("user-1"),
    permissions = setOf(permission { Read any Foo })
)
val userContext = UserContext(user)

if (userContext.hasPermission { Read on foo }) { // checks if user has "foo:bar:read"
    // do things - succeeds since `user` has `Read any Foo` permission (translates to "foo:*:read")
} else {
    throw IllegalArgumentException("User ${user.id} does not have permission to read ${foo.id}")
}
```
