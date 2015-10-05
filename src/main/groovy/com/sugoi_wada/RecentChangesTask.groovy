package com.sugoi_wada

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.ApkListing
import com.google.api.services.androidpublisher.model.Track
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by watyaa on 15/10/03.
 */
class RecentChangesTask extends DefaultTask {
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

        // 最新バージョンコードの取得
        def newerVersionCode = edits.tracks().list(applicationId, appEdit.id).execute()
                .tracks?.find {
            it.track == specifiedTrack
        }?.versionCodes?.sort()?.last()

        if (newerVersionCode == null) throw new IllegalStateException("${specifiedTrack} version code can't be found.")

        // 更新情報の更新
        apkListings.each { ApkListing apkListing ->
            edits.apklistings().update(applicationId, appEdit.id, newerVersionCode, apkListing.language, apkListing).execute()
        }

        edits.commit(applicationId, appEdit.id).execute()
    }
}
