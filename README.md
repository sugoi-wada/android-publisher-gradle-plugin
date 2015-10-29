# Android Publisher Gradle Plugin
This plugin integrates Google Play Developer API (Publishing API) with the Gradle build system. With this plugin, you can upload apks and listings directly via command line, IntelliJ, Android Studio and other IDEs.

## Use

You have to create service account for use this plugin. Please [create service account](https://developers.google.com/android-publisher/getting_started). (OAuth clients weren't supported now.) And download p12 file from `Google Developers Console` > `[created account]` > `APIs & auth` > `Credentials`.

You also have to grant update permissions as needed. Please make sure that by [this help page](https://support.google.com/googleplay/android-developer/answer/2528691?hl=en).


Download [the latest .jar](https://github.com/sugoi-wada/android-publisher-gradle-plugin/releases) and consists of adding the following to your build.gradle file.

1. Add plugin dependency

        buildscript {
            dependencies {
                classpath files('path/to/androidpublisher-x.x.x.jar')
            }
        }

2.  Apply plugin

        apply plugin: 'publisher'

3.  Configure your publish resources

        publish {
            clientEmail "xxxx@developer.gserviceaccount.com"
            p12File file("path/to/p12File.p12")
        
            listings {
                japanese { // free word
                    language 'ja-JP' // BCP-47 language tag
                    title "${japaneseTitle}"
                    video "https://www.youtube.com/watch?v=${jpVideo}"
                    recentChangesFile file("path/to/recent-changes.txt")
                    shortDescriptionFile file("path/to/short-description.txt")
                    fullDescriptionFile file("path/to/full-description.txt")
                    phoneScreenshotsDir file("path/to/phone-screenshots")
                }
                english { // free word
                    language 'en-US' // BCP-47 language tag
                    title "${englishTitle}"
                    video "https://www.youtube.com/watch?v=${enVideo}"
                    recentChangesFile file("path/to/recent-changes.txt")
                    shortDescriptionFile file("path/to/short-description.txt")
                    fullDescriptionFile file("path/to/full-description.txt")
                    phoneScreenshotsDir file("path/to/phone-screenshots")
                }
        }

4.  Run the following from terminal. You can see new Google Play Tasks.

        ./gradlew tasks
        
### Require

- Signing
- Product Flavor