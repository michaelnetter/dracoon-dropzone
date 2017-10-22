# dracoon-dropzone
A cross-platform dropzone to easily share files with [DRACOON](https://www.dracoon.com)

# Download
[Version 1.1.0 for Windows](https://github.com/michaelnetter/dracoon-dropzone/releases/download/1.1.0/Dropzone-Win.zip) (use jar file below if Windows prevents execution of downloaded files)

<!--- [Version 1.1.0 for Mac OS](https://github.com/michaelnetter/dracoon-dropzone/releases/download/1.1.0/Dropzone-Mac.dmg) -->

[Version 1.1.0 for all platforms (jar file) (Windows, Mac OS, Linux)](https://github.com/michaelnetter/dracoon-dropzone/releases/download/1.1.0/Dropzone-All.zip)


## Installation
Prerequisites: 
If you are using Oracle Java 8 or lower, you need to install the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files (available at [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html)).

Execute the application and right-click on the tray icon to setup your account.

To start with Windows ([more details](http://tunecomp.net/add-app-to-startup/)):

 1. Press the following key combination: Win+R
 2. Enter shell:startup and press enter
 3. Make a shortcut to dracoon-dropzone in the opening folder

To start with Mac OS ([more details](https://support.apple.com/kb/PH25590?locale=en_US)):

 1. Open system preferences
 2. Choose Users & Groups and select your account
 3. Choose Login items, click the Add button and select the dracoon-dropzone app

## Disclaimer
[DRACOON](http://www.dracoon.com) is a secure cloud storage solution. The icon is property of *DRACOON GmbH*.

## General Information
Allows to define a *hot corner* where files can be dropped. These files are uploaded to *DRACOON* and a private download link is copied to clipboard. Third parties can use this link to download these files.

Tested with Windows 10 and Mac OS Sierra.

Click <a href="https://www.dracoon.com/service/free-trial/"> here</a>  to get a *DRACOON* demo account.

## Usage
Simply drag one or more files you'd like to share on the action in your Dropzone grid. The files will be uploaded and a private download link is automatically created and placed in your clipboard. Simply paste it wherever you need it.

![enter image description here](http://michaelnetter.com/sds-dropzone/github_screenshot.png)

By holding the Control Key while releasing your files, you can set a password for the download link. Please keep in mind that password policies might be enforced. Holding the ALT key while releasing your files will let the file and download link expire in 14 days.

## Compile
Prerequisites: Java JDK 8, Maven 3.x

Use the following command to compile for Windows:

    mvn clean compile -Pwindows assembly:single package

Use the following command to compile for Mac OS:

    mvn clean compile -Pmac package appbundle:bundle