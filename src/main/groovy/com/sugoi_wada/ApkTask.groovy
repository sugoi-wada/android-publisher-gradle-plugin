package com.sugoi_wada

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.Apk
import com.google.api.services.androidpublisher.model.ApkListing
import com.google.api.services.androidpublisher.model.Listing
import com.google.api.services.androidpublisher.model.Track
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by watyaa on 15/10/03.
 */
class ApkTask extends DefaultTask {
    private static final String MIMETYPE = "application/vnd.android.package-archive"

    String specifiedTrack
    String specifiedFlavorName

    @TaskAction
    def uploadNewApk() {
        File p12File = project.publish.p12File
        String clientEmail = project.publish.clientEmail

        logger.info "client email: ${clientEmail}"
        def flavor = project.("android").productFlavors.find { productFlavor ->
            productFlavor.name.equals(specifiedFlavorName)
        }
        String applicationId = flavor.applicationId
        logger.info "applicationId: ${applicationId}"

        def publisher = AndroidPublisherHelper.init(applicationId, clientEmail, p12File)
        def edits = publisher.edits()
        def appEdit = edits.insert(applicationId, null).execute()

        def apkFile = new FileContent(MIMETYPE, new File("${projectDir}/build/outputs/apk/app-${flavor.name}-release.apk"))
        def newApk = edits.apks().upload(applicationId, appEdit.id, apkFile).execute()

        def track = new Track()
        track.setTrack(specifiedTrack.toLowerCase())
        track.setVersionCodes([newApk.getVersionCode()])
        edits.tracks().update(applicationId, appEdit.id, track.track, track).execute()

        project.listings.sort {
            it.language
        }.each { listing ->
            def apkListing = new ApkListing()
            apkListing.language = listing.language
            apkListing.recentChanges = listing.recentChangesFile.text
            edits.apklistings().update(applicationId, appEdit.id, newApk.versionCode, listing.language, apkListing)
                    .execute()
        }

        edits.commit(applicationId, appEdit.getId()).execute()
    }
}
