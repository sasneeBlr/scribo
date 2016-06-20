# Scribo
A simple easy-to-use debug utility which helps you capture the logs simultaneously to adb terminal as well to a file on SD Card.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Scribo-green.svg?style=true)](https://android-arsenal.com/details/1/3431)

# Table of Contents
1. [Gradle Dependency] (https://github.com/sasneeBlr/scribo#gradle-dependency-jcenter)
2. [Features] (https://github.com/sasneeBlr/scribo#features)
3. [Usage] (https://github.com/sasneeBlr/scribo#usage)

---
# Gradle Dependency (jCenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```gradle
dependencies {
    compile 'com.sasnee.scribo:scribo:1.2.0'
}
```
---

# Features
Scribo allows capturing the debug logs of any android application into a file on external storage in addition to displaying
it on the adb terminal. This is ideal to capture logs of the application once it is deployed in the field. 
The captured logs are stored in the external storage directory under the path: `<External Storage>/AppData/<application_name>/` <br>
(For eg: /storage/emulated/0/AppData/com.sasnee.scribosample/)

### Log Masks
Scribo supports 10 categories of log masks by default. This is useful to categorize logs from individual modules of the
application and enable/disable them individually. Scribo also supports setting custom strings to identify these default log
categories. <br> 
For eg: "UI Module" can be mapped to Category 1, "Display Module" can be mapped to Category 2 and so on. Once this
is done, the logs belonging to these categories can be dynamically enabled/disabled.

### ADB Logging
Scribo can be configured to disable printing the logs on adb terminal and instead only send it to the file. This might be needed
in certain scenarios to enhance performance of the application.

### Retrieve Log File
Starting v1.2.0, scribo provides with an option to easily retrieve the log file from the device. This new API can be used to send the log file containing the debug logs to be sent from the device. When this API is invoked, user gets a list of services that can be used to send the log file from the device. (Bluetooth, Email, ShareIT etc). If the intention is to email the log file, scribo exposes an API to prepopulate the list of people to whom the email needs to be sent.

---

# Usage
* First, initialize scribo by invoking any of the below init() functions: 

```java
	DebugHelper.init(Context)
	DebugHelper.init(Context, fileName)
	DebugHelper.init(Context, fileName, resetFileContents)
```
   Example:
```java
	DebugHelper.init(this);
	// Logs are captured into file "logJournal.txt". <br>
	// File contents are reset everytime the application is invoked.
```

```java
	DebugHelper.init(this, "CapturedLogs.txt");
	// Logs are captured into file "CapturedLogs.txt". <br>
	// File contents are reset everytime the application is invoked.
```    

```java
	DebugHelper.init(this, "CapturedLogs.txt", false); 
	// Logs are captured into file "CapturedLogs.txt". <br>
	// File contents are NOT reset everytime the application is invoked.
```

**Please note the init() function needs to be invoked from the main activity of the application.**

* Override the log mask supported by scribo with a custom log mask. (This is optional) 

```java
    DebugHelper.mapCustomLogMask(<Default Category>, <Override String>);
```
  Example:
```java
	DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_0, "UI Module"); 
	// Map Category 0 to "UI Module"
```

```java
	DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_4, "URL Loader Module"); 
	// Map Category 4 to "URL Loader Module"
```	


* Enable/Disable the log categories.
```java
	DebugHelper.enableDisableLogCategory(category, isEnable);	
```

  Example:    
```java
	DebugHelper.enableDisableLogCategory(DebugHelper.LOG_CATEGORY_1, true);
	// Enable Category 1 logs.
```	

```java
	DebugHelper.enableDisableLogCategory("UI Module", false);
	// Disable "UI Module" logs.
```


* Send log request to scribo
```java
	DebugHelper.logRequest(<TAG>, <Log Message>, <Show on ADB Logs>, <Severity>, <Category>);
	// <TAG> and <Log Message> arguments are mandatory. 
	// <Show on ADB Logs>, <Severity> and <Category> are optional. 
	// If <Show on ADB Logs> is not provided, it defaults to true, i.e the log will be shown on ADB terminal.
	// If <Severity> is not provided, it defaults to SEVERITY_LEVEL_VERBOSE.
	// If <Category> is not provided, it defaults to LOG_CATEGORY_GENERAL.
```

  Example:
```java
	DebugHelper.logRequest(TAG, "Log message");
```

```java
	DebugHelper.logRequest(TAG, "Log message", false);
	// Do not display this log on the ADB terminal.
```

```java
	DebugHelper.logRequest(TAG, "Log message", DebugHelper.SEVERITY_LEVEL_ERROR);
```

```java	
	DebugHelper.logRequest(TAG, "Log message", DebugHelper.LOG_CATEGORY_1);
```

```java
	DebugHelper.logRequest(TAG, "Log message", false, DebugHelper.SEVERITY_LEVEL_WARN, DebugHelper.LOG_CATEGORY_1);
```

* Email the log file.

```java
    DebugHelper.sendLogFileByEmail();
    // Do not prepopulate the "To" field of the email. 
    // Enter the email IDs manually before sending the log file.
```

```java
    DebugHelper.sendLogFileByEmail(List<String> emailList);
    // Prepopulate the "To" field of the email with the list of emailIDs mentioned in emailList.
    // This saves some effort in case the log file is always meant to be sent to same list of people.
```
