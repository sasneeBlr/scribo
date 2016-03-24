# Scribo

# Table of Contents
1. Gradle Dependency
2. Features
3. Usage
4. Sample application

---
# Gradle Dependency (jCenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```gradle
dependencies {
    compile 'com.sasnee.scribo:scribo:1.0.0'
}
```
---

# Features
Scribo allows capturing the debug logs of any android application into a file on external storage in addition to displaying
it on the adb terminal. This is ideal to capture logs of the application once it is deployed in the field. 
The captured logs are stored in the external storage directory under the path: <External Storage>/AppData/<application_name>/
(For eg: /storage/emulated/0/AppData/com.sasnee.scribosample/)

## Log Masks
Scribo supports 10 categories of log masks by default. This is useful to categorize logs from individual modules of the
application and enable/disable them individually. Scribo also supports setting custom strings to identify these default log
categories. For eg: "UI Module" can be mapped to Category 1, "Display Module" can be mapped to Category 2 and so on. Once this
is done, the logs belonging to these categories can be dynamically enabled/disabled.

## ADB Logging
Scribo can be configured to disable printing the logs on adb terminal and instead only send it to the file. This might be needed
in certain scenarios to enhance performance of the application.

---
# Usage
1. First, initialize scribo by invoking any of the below init() functions: 

```java
	DebugHelper.init(Context)
```
OR
```java
    DebugHelper.init(Context, fileName)
```
OR
```java
	DebugHelper.init(Context, fileName, resetFileContents)
```

Example:
```java
	DebugHelper.init(getApplicationContext());
```
- Logs are captured into file "logJournal.txt".
- File contents are reset everytime the application is invoked.

```java
	DebugHelper.init(getApplicationContext(), "CapturedLogs.txt");
```    
- Logs are captured into file "CapturedLogs.txt"
- File contents are reset everytime the application is invoked.

```java
	DebugHelper.init(getApplicationContext(), "CapturedLogs.txt", false); 
```
- Logs are captured into file "CapturedLogs.txt"
- File contents are NOT reset everytime the application is invoked.


2. Override the log mask supported by scribo with a custom log mask. (This is optional) 

```java
    DebugHelper.mapCustomLogMask(<Default Category>, <Override String>);
```
Example:
```java
	DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_0, "UI Module"); 
```
- Map Category 0 to "UI Module"

```java
    DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_4, "URL Loader Module"); 
```
- Map Category 4 to "URL Loader Module"


3. Enable/Disable the log categories.
```java
	DebugHelper.enableDisableLogCategory(category, isEnable);	
```
Example:    
```java
	DebugHelper.enableDisableLogCategory(DebugHelper.LOG_CATEGORY_1, true);
```	
- Enable Category 1 logs.
```java
	DebugHelper.enableDisableLogCategory("UI Module", false);
```
- Disable "UI Module" logs.


4. Send log request to scribo
```java
	DebugHelper.logRequest(<TAG>, <Log Message>, <Show on ADB Logs>, <Severity>, <Category>);
```
- <TAG> and <Log Message> arguments are mandatory.
- <Show on ADB Logs>, <Severity> and <Category> are optional.
- If <Show on ADB Logs> is not provided, it defaults to true, i.e the log will be shown on ADB terminal.
- If <Severity> is not provided, it defaults to SEVERITY_LEVEL_VERBOSE.
- If <Category> is not provided, it defaults to LOG_CATEGORY_GENERAL.

Example:
```java
	DebugHelper.logRequest(TAG, "Log message");
```
```java
DebugHelper.logRequest(TAG, "Log message", false);
```
- Do not display this log on the ADB terminal.
```java
	DebugHelper.logRequest(TAG, "Log message", DebugHelper.SEVERITY_LEVEL_ERROR);
```    
- Severity level ERROR  
```java	
	DebugHelper.logRequest(TAG, "Log message", DebugHelper.LOG_CATEGORY_1);
```
- Category 1 log
```java
	DebugHelper.logRequest(TAG, "Log message", false, DebugHelper.SEVERITY_LEVEL_WARN, DebugHelper.LOG_CATEGORY_1);
```