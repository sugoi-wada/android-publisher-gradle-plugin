package com.sugoi_wada

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.Listing
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by watyaa on 15/10/03.
 */
class PublishTask extends DefaultTask {
    private static final String PHONESCREENSHOTS = "phonescreenshots"

    String specifiedLang
    String specifiedFlavorName

    @TaskAction
    def updateListings() {
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

        project.listings.sort {
            it.language
        }.each { listing ->
            if (specifiedLang.equals('-') || specifiedLang.equals(listing.name)) {
                def newListing = new Listing()
                newListing.setLanguage(listing.language)
                newListing.setTitle(listing.title)
                newListing.setVideo(listing.video)
                newListing.setShortDescription(listing.shortDescriptionFile.text)
                newListing.setFullDescription(listing.fullDescriptionFile.text)

                logger.debug "update listing"
                edits.listings()
                        .update(applicationId, appEdit.id, newListing.language, newListing)
                        .execute()

                logger.debug "delete all phonescreenshots"
                edits.images().deleteall(applicationId, appEdit.id, newListing.language, PHONESCREENSHOTS)
                        .execute();

                listing.phoneScreenshotsDir.listFiles().findAll() {
                    it.isFile()
                }.sort() {
                    it.name
                }.collect {
                    def mimeType = "image/png"
                    new FileContent(mimeType, it)
                }.each {
                    logger.debug "upload phonescreenshots..."
                    edits.images().upload(applicationId, appEdit.id, newListing.language, PHONESCREENSHOTS, it)
                            .execute()
                }
            }
        }
        logger.debug "commit all"
        edits.commit(applicationId, appEdit.getId()).execute()
        logger.info "Successfully!!!!"
    }
}
