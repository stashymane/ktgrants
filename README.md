# ktgrants

![Maven Central Version](https://img.shields.io/maven-central/v/dev.stashy.ktgrants/permissions?logo=apachemaven&label=central)
![Maven Snapshot](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fdev%2Fstashy%2Fktgrants%2Fpermissions%2Fmaven-metadata.xml&strategy=latestProperty&label=snapshot)

---
(soon to be) ergonomic permission/grant library

> [!WARNING]
> Currently only available as a snapshot. Check the snapshot instructions below.

## Usage

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

## KSP module (multiplatform)

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
