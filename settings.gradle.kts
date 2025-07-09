pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PDF Editor"
include(":app")
include(":feature:viewer")
include(":feature:editor")
include(":feature:file_manager")
include(":data")
include(":domain")
include(":common:ui")
include(":common:utils")
include(":pdf_engine_wrapper")
