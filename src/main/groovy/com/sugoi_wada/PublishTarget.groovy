package com.sugoi_wada

import org.gradle.api.Named
import org.gradle.api.internal.project.ProjectInternal

/**
 * Created by watyaa on 15/10/03.
 */
class PublishTarget implements Named {
    String name
    ProjectInternal target

    String language
    String title
    String video
    File recentChangesFile
    File shortDescriptionFile
    File fullDescriptionFile
    File phoneScreenshotsDir

    public PublishTarget(String name) {
        super()
        this.name = name
        this.target = target
    }
}
