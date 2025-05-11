plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.androidx.navigation.safeargs.kotlin)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.dagger.hilt.android)
}

android {
  namespace = "com.rxmobileteam.pet"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.rxmobileteam.pet"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    defaultConfig {
      defaultConfig {
        javaCompileOptions {
          annotationProcessorOptions {
            compilerArgumentProviders(
              RoomSchemaArgProvider(
                File(projectDir, "schemas").apply {
                  if (!exists()) {
                    check(mkdirs()) { "Cannot create $this" }
                  }
                },
              ),
            )
          }
        }
      }
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".debug"
      isMinifyEnabled = false
      isShrinkResources = false
    }

    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    viewBinding = true
    buildConfig = true
  }

  flavorDimensions += "default"

  productFlavors {
    create("dev") {
      applicationIdSuffix = ".dev"
      versionNameSuffix = "-dev"
      resValue("string", "app_name", "Pets Dev")
      buildConfigField("String", "API_DOMAIN", "\"https://api.thecatapi.com/v1/\"")
      buildConfigField("String", "API_KEY", "\"bed9cfb9-fad4-41ae-bc4d-09cf7cb552bf\"")
    }
    create("product") {
      applicationIdSuffix = ".product"
      versionNameSuffix = "-product"
      resValue("string", "app_name", "Pets")
      buildConfigField("String", "API_DOMAIN", "\"https://api.thecatapi.com/v1/\"")
      buildConfigField("String", "API_KEY", "\"bed9cfb9-fad4-41ae-bc4d-09cf7cb552bf\"")
    }
  }
  sourceSets {
    getByName("main") {
      assets {
        srcDirs("src/main/assets")
      }
    }
  }
}

dependencies {
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.google.material.view)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.fragment)
  implementation(libs.bundles.androidx.lifecycle)
  implementation(libs.bundles.androidx.navigation)
  implementation(libs.androidx.startup)
  implementation(libs.androidx.swiperefreshlayout)
  implementation(libs.androidx.recyclerview)

  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  ksp(libs.androidx.room.compiler)

  testImplementation(libs.junit)
  testImplementation(libs.androidx.fragment.testing)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.espresso.core)

  implementation(libs.dagger.hilt.android)
  ksp(libs.dagger.hilt.compiler)

  implementation(libs.bundles.retrofit.okhttp3)
  implementation(libs.bundles.moshi)
  implementation(libs.timber)
  implementation(libs.glide)
  implementation(libs.lottie)
  implementation(libs.flowExt)
}

class RoomSchemaArgProvider(
  @get:InputDirectory
  @get:PathSensitive(PathSensitivity.RELATIVE)
  val schemaDir: File,
) : CommandLineArgumentProvider {
  override fun asArguments(): Iterable<String> {
    // Note: If you're using KSP, change the line below to return
    // listOf("room.schemaLocation=${schemaDir.path}").
    return listOf("-Aroom.schemaLocation=${schemaDir.path}")
  }
}

