package com.sugoi_wada

import org.gradle.api.NamedDomainObjectContainer

/**
 * Created by watyaa on 15/10/03.
 */
public class PublishExtension {
    final private NamedDomainObjectContainer<PublishTarget> listings
    String clientEmail
    File p12File

    public PublishExtension(NamedDomainObjectContainer<PublishTarget> listings) {
        this.listings = listings
    }

    public listings(Closure closure) {
        listings.configure(closure)
    }

    void clientEmail(String clientEmail) {
        this.clientEmail = clientEmail
    }

    void p12File(File p12File) {
        this.p12File = p12File
    }
}
