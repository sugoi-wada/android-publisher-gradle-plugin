package com.sugoi_wada

import org.gradle.api.*

public class PublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.configure(project) {
            if (it.hasProperty("android")) {
                def listings = project.container(PublishTarget) {
                    String langName = it.toString()

                    android.productFlavors.all { productFlavor ->
                        def updateListings = project.task("updateListings${langName.capitalize()}${productFlavor.name.capitalize()}", type: PublishTask)
                        updateListings.group = 'Google Play'
                        updateListings.description = 'Update The Google Play Linstings for language.'
                        updateListings.specifiedLang = langName
                        updateListings.specifiedFlavorName = productFlavor.name
                    }

                    project.extensions.create(it, PublishTarget, langName)
                }
                def publish = new PublishExtension(listings)
                project.convention.plugins.publish = publish
                project.extensions.publish = publish

                android.productFlavors.all { productFlavor ->
                    def updateListings = project.task("updateListings${productFlavor.name.capitalize()}", type: PublishTask)
                    updateListings.group = 'Google Play'
                    updateListings.description = 'Update The Google Play Listings for all listings.'
                    updateListings.specifiedLang = '-'
                    updateListings.specifiedFlavorName = productFlavor.name

                    ["Alpha", "Beta", "Production"].each { inputTrack ->
                        def apkTask = project.task("updateApk${inputTrack}${productFlavor.name.capitalize()}", type: ApkTask)
                        apkTask.group = 'Google Play'
                        apkTask.description = 'Upload the APK file to Google Play.'
                        apkTask.specifiedTrack = inputTrack
                        apkTask.specifiedFlavorName = productFlavor.name
                        apkTask.dependsOn "assemble${productFlavor.name.capitalize()}Release"

                        def recentChangesTask = project.task("updateRecentChanges${inputTrack}${productFlavor.name.capitalize()}", type: RecentChangesTask)
                        recentChangesTask.group = 'Google Play'
                        recentChangesTask.description = 'Update the recent changes of the newest apk.'
                        recentChangesTask.specifiedTrack = inputTrack
                        recentChangesTask.specifiedFlavorName = productFlavor.name
                    }
                }
            }
        }
    }
}


