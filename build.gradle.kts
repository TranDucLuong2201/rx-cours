// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.androidx.navigation.safeargs.kotlin) apply false

  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.parcelize) apply false
  alias(libs.plugins.kotlin.kapt) apply false

  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.dagger.hilt.android) apply false

  alias(libs.plugins.gradle.spotless) apply false
}

allprojects {
  afterEvaluate {
    extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension>()?.run {
      jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.AZUL
      }
    }
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
      jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
      freeCompilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
        "-Xopt-in=kotlin.ExperimentalStdlibApi",
        "-Xopt-in=kotlin.time.ExperimentalTime",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xopt-in=kotlinx.coroutines.FlowPreview",
      )
    }
  }

  val ktlintVersion = rootProject.libs.versions.ktlint.get()
  apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude(
        // Compose Multiplatform Res class
        "**/Res.kt",
        // Kotlin generated files
        "**/build/**/*.kt",
      )

      ktlint(ktlintVersion)

      trimTrailingWhitespace()
      indentWithSpaces()
      endWithNewline()
    }

    format("xml") {
      target("**/res/**/*.xml")
      targetExclude("**/build/**/*.xml")

      trimTrailingWhitespace()
      indentWithSpaces()
      endWithNewline()
    }

    kotlinGradle {
      target("**/*.gradle.kts", "*.gradle.kts")
      targetExclude("**/build/**/*.kts")

      ktlint(ktlintVersion)

      trimTrailingWhitespace()
      indentWithSpaces()
      endWithNewline()
    }
  }
}
