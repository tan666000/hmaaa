fun Project.configureBaseExtension() {
    extensions.findByType<BaseExtension>()?.run {
        compileSdkVersion(targetSdkVer)

        defaultConfig {
            minSdk = minSdkVer
            targetSdk = targetSdkVer
            versionCode = appVerCode
            versionName = appVerName

            consumerProguardFiles("proguard-rules.pro")
        }

        val config = localProperties.getProperty("fileDir")?.let {
            signingConfigs.create("config") {
                storeFile = file(it)
                storePassword = localProperties.getProperty("storePassword")
                keyAlias = localProperties.getProperty("keyAlias")
                keyPassword = localProperties.getProperty("keyPassword")
            }
        }

        // 修复buildTypes语法：显式获取BuildType容器并配置
        val buildTypes = this.buildTypes as NamedDomainObjectContainer<BuildType>
        buildTypes.all {
            signingConfig = config ?: signingConfigs["debug"]
        }
        buildTypes.named("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        compileOptions {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }

    extensions.findByType<ApplicationExtension>()?.run {
        // 同样修复ApplicationExtension中的release配置
        val buildTypes = this.buildTypes as NamedDomainObjectContainer<BuildType>
        buildTypes.named("release") {
            isShrinkResources = true
        }
    }
}
