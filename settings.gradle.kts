pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        }
    }
// Tệp build cấp cao nơi bạn có thể thêm cấu hình chung cho tất cả các dự án/module con.
plugins {
    id("com.android.application") version "8.1.4" apply false
}

    rootProject.name = "AppBanHang"
    include(":app")
