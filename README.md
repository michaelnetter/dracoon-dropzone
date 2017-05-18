# sds-dropzone
A cross-platform dropzone to easily share files with *SSP Secure Data Space*

# Download
[Version 1.0 for Windows](https://github.com/michaelnetter/sds-dropzone/releases/download/1.0/Dropzone-1.0.0.exe)

[Version 1.0 for Mac OS](https://github.com/michaelnetter/sds-dropzone/releases/download/1.0/dropzone-client-1.0.0.dmg)

## Installation
Execute the application and right-click on the tray icon to setup your account.

To start with Windows ([more details](http://tunecomp.net/add-app-to-startup/)):

 1. Press the following key combination: Win+R
 2. Enter: shell:startup and press enter
 3. Make a shortcut to sds-dropzone in the opening folder

To start with Mac OS([more details](https://support.apple.com/kb/PH25590?locale=en_US)):

 1. Open system preferences
 2. Choose Users & Groups and select your account
 3. Choose Login items, click the Add button and select the sds-dropzone app

## Disclaimer
*Secure Data Space* is a secure cloud storage solution (http://secure-data-space.com). The icon is property of *SSP Europe GmbH*.

## General Information
Allows to define a *hot corner* where files can be dropped. These files are uploaded to *SSP Secure Data Space* and a private download link is copied to clipboard. Third parties can use this link to download these files.

Tested with Windows 10 and Mac OS Sierra.

Click <a href="https://www.secure-data-space.com/en/service/free-trial/"> here</a>  to get a *SSP Secure Data Space* demo account.

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