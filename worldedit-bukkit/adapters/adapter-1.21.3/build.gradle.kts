import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension

plugins {
    id("buildlogic.adapter")
}

repositories {
    maven("https://github.com/Euphillya/FoliaDevBundle/raw/gh-pages/")
}

dependencies {
    // https://repo.papermc.io/service/rest/repository/browse/maven-public/io/papermc/paper/dev-bundle/
    the<PaperweightUserDependenciesExtension>().foliaDevBundle("1.21.3-R0.1-SNAPSHOT")
}
