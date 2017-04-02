package com.sasnee.scribo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DebugHelper {

    private static final String TAG = "DebugHelper";

    private static FileOutputStream mOutput = null;
    private static OutputStreamWriter mOutputStreamWriter = null;

    public static String filePath;

    private static final String DEFAULT_JOURNAL_FILE = "logJournal.txt";
    private static final Boolean DEFAULT_RESET_OPTION = true; // Reset file contents everytime.

    private static String mLogJournalFile = filePath + DEFAULT_JOURNAL_FILE;
    private File mFile;
    private static Context mContext;

    /* DebugHelper class is singleton */
    private static DebugHelper sInstance = null;

    public final static int SEVERITY_LEVEL_ERROR    = 0x1;
    public final static int SEVERITY_LEVEL_WARN     = 0x2;
    public final static int SEVERITY_LEVEL_VERBOSE  = 0x4;
    public final static int SEVERITY_LEVEL_INFO     = 0x8;

    public final static long LOG_CATEGORY_0         = 0x1;
    public final static long LOG_CATEGORY_1         = 0x2;
    public final static long LOG_CATEGORY_2         = 0x4;
    public final static long LOG_CATEGORY_3         = 0x8;
    public final static long LOG_CATEGORY_4         = 0x10;
    public final static long LOG_CATEGORY_5         = 0x20;
    public final static long LOG_CATEGORY_6         = 0x40;
    public final static long LOG_CATEGORY_7         = 0x80;
    public final static long LOG_CATEGORY_8         = 0x100;
    public final static long LOG_CATEGORY_9         = 0x200;
    public final static long LOG_CATEGORY_10        = 0x400;
    public final static long LOG_CATEGORY_GENERAL   = 0x800;

    private final static boolean DEFAULT_ADB_BEHAVIOUR = true;
    private final static int DEFAULT_SEVERITY_LEVEL = SEVERITY_LEVEL_VERBOSE;
    private final static long DEFAULT_LOG_CATEGORY_MASK = LOG_CATEGORY_GENERAL;
    private static long logCategoryMask = LOG_CATEGORY_GENERAL;

    private static HashMap<Long, String>LogMaskLookupTable;

    private DebugHelper(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        init(context, DEFAULT_JOURNAL_FILE, DEFAULT_RESET_OPTION);
    }

    public static void init(Context context, String fileName) {
        init(context, fileName, DEFAULT_RESET_OPTION);
    }

    public static void init(Context context, Boolean resetFileContents) {
        init(context, DEFAULT_JOURNAL_FILE, resetFileContents);
    }

    public static void init(Context context, String fileName, Boolean resetFileContents) {
        if (sInstance == null) {
            synchronized (DebugHelper.class) {
                if (sInstance == null) {
                    sInstance = new DebugHelper(context);
                }
            }
        }

        if (!fileName.contains(".txt")) {
            // Append .txt to the file name to make life simpler!
            fileName = fileName + ".txt";
        }

        sInstance.setLogJournalFile(fileName);

        if (resetFileContents) {
            // Reset the file contents.
            startFromScratch();
        }

        prePopulateLookupTable();
    }

    private void setLogJournalFile(String fileName) {
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/AppData/" + mContext.getApplicationInfo().processName + "/";
        mLogJournalFile = filePath + fileName;

        mFile = new File(filePath);
        mFile.mkdirs();

        Log.i(TAG, "setLogJournalFile: Journal file = " + mLogJournalFile);
    }

    private static void prePopulateLookupTable() {
        LogMaskLookupTable = new HashMap<>();

        LogMaskLookupTable.put(LOG_CATEGORY_0,  "Log Category 0");
        LogMaskLookupTable.put(LOG_CATEGORY_1,  "Log Category 1");
        LogMaskLookupTable.put(LOG_CATEGORY_2,  "Log Category 2");
        LogMaskLookupTable.put(LOG_CATEGORY_3,  "Log Category 3");
        LogMaskLookupTable.put(LOG_CATEGORY_4,  "Log Category 4");
        LogMaskLookupTable.put(LOG_CATEGORY_5,  "Log Category 5");
        LogMaskLookupTable.put(LOG_CATEGORY_6,  "Log Category 6");
        LogMaskLookupTable.put(LOG_CATEGORY_7,  "Log Category 7");
        LogMaskLookupTable.put(LOG_CATEGORY_8,  "Log Category 8");
        LogMaskLookupTable.put(LOG_CATEGORY_9, "Log Category 9");
        LogMaskLookupTable.put(LOG_CATEGORY_10, "Log Category 10");
    }

    public static String getFilePath() {
        return mLogJournalFile;
    }

    private static void startFromScratch() {
        // Just open close the file in non-append mode to reset the contents.
        try {
            PrintWriter pw = new PrintWriter(mLogJournalFile);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "DebugHelper : Exception: " + e.getCause() + " | " + e.getMessage());
        }
    }

    public static boolean mapCustomLogMask(long defaultCategoryMask, String customLogMask) {
        boolean result = true;

        if (defaultCategoryMask >= LOG_CATEGORY_0 && defaultCategoryMask <= LOG_CATEGORY_10)
            LogMaskLookupTable.put(defaultCategoryMask, customLogMask);
        else
            result = false;

        return result;
    }

    private static long getLogCategoryFromCustomLogMask(String customLogMask) {
        long returnVal = -1;

        for (Long keyItem : LogMaskLookupTable.keySet()) {
            if (LogMaskLookupTable.get(keyItem).equals(customLogMask)) {
                returnVal = keyItem;
                break;
            }
        }

        return returnVal;
    }

    public static void enableDisableLogCategory(long category, boolean isEnable) {
        if (isEnable) {
            logCategoryMask = logCategoryMask | category;
        } else {
            logCategoryMask = logCategoryMask & ~category;
        }
    }

    public static void enableDisableLogCategory(String customLogMask, boolean isEnable) {

        long category = LOG_CATEGORY_GENERAL;
        boolean customLogMaskFound = false;

        for (Long keyItem : LogMaskLookupTable.keySet()) {
            if (LogMaskLookupTable.get(keyItem).equals(customLogMask)) {
                category = keyItem;
                customLogMaskFound = true;
                break;
            }
        }
        if (customLogMaskFound) {
            enableDisableLogCategory(category, isEnable);
        } else {
            Log.i(TAG, "enableDisableLogCategory: Unrecognized custom log mask: " + customLogMask);
        }
    }


    private static String logFunction(int severity, String tag, String logEntry,
                                      boolean shouldOutputToADB) {
        String severityLevel = null;

        switch (severity) {
            case SEVERITY_LEVEL_ERROR:
                severityLevel = "Error";
                Log.e(tag, logEntry);
                break;
            case SEVERITY_LEVEL_WARN:
                severityLevel = "Warning";
                if (shouldOutputToADB) {
                    Log.w(tag, logEntry);
                }
                break;
            case SEVERITY_LEVEL_VERBOSE:
                severityLevel = "Verbose";
                if (shouldOutputToADB) {
                    Log.v(tag, logEntry);
                }
                break;
            case SEVERITY_LEVEL_INFO:
                severityLevel = "Info";
                if (shouldOutputToADB) {
                    Log.i(tag, logEntry);
                }
                break;
            default:
                severityLevel = "Verbose";
                if (shouldOutputToADB) {
                    Log.v(tag, logEntry);
                }
                break;
        }

        return severityLevel;
    }

    synchronized private static void updateLogJournal(String tag, String logEntry,
                                                      String severityLevel) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String currentDateTime = sdf.format(new Date());

        try {
            mOutput = new FileOutputStream(mLogJournalFile, true);
            mOutputStreamWriter = new OutputStreamWriter(mOutput);
            mOutputStreamWriter.append(currentDateTime + ": " + tag + " |"
                                        + severityLevel + "|" + ": ");
            mOutputStreamWriter.append(logEntry);
            mOutputStreamWriter.append("\n");
            mOutputStreamWriter.flush();
            mOutputStreamWriter.close();
            mOutput.close();
        } catch (Exception e) {
            Log.e(TAG, "updateLogJournal : Exception: " + e.getCause() + "|" + e.getMessage());
            e.printStackTrace();
        }
    }

    synchronized private static void updateLogJournalInternal(String tag, String logEntry,
                                                      String severityLevel) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String currentDateTime = sdf.format(new Date());

        try {
            mOutput = mContext.openFileOutput(DEFAULT_JOURNAL_FILE, mContext.MODE_APPEND);
            mOutputStreamWriter = new OutputStreamWriter(mOutput);
            mOutputStreamWriter.append(currentDateTime + ": " + tag + " |"
                    + severityLevel + "|" + ": ");
            mOutputStreamWriter.append(logEntry);
            mOutputStreamWriter.append("\n");
            mOutputStreamWriter.flush();
            mOutputStreamWriter.close();
            mOutput.close();
        } catch (Exception e) {
            Log.e(TAG, "updateLogJournal : Exception: " + e.getCause() + "|" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void logRequest(String tag, String logEntry) {
        logRequest(tag, logEntry, DEFAULT_ADB_BEHAVIOUR, DEFAULT_SEVERITY_LEVEL,
                    DEFAULT_LOG_CATEGORY_MASK);
    }

    public static void logRequest(String tag, String logEntry, boolean showOnADBLogs) {
        logRequest(tag, logEntry, showOnADBLogs, DEFAULT_SEVERITY_LEVEL, DEFAULT_LOG_CATEGORY_MASK);
    }

    public static void logRequest(String tag, String logEntry, int severity) {
        logRequest(tag, logEntry, DEFAULT_ADB_BEHAVIOUR, severity, DEFAULT_LOG_CATEGORY_MASK);
    }

    public static void logRequest(String tag, String logEntry, String customLogMask) {
        long category = getLogCategoryFromCustomLogMask(customLogMask);
        if (category >= 0) {
            logRequest(tag, logEntry, DEFAULT_ADB_BEHAVIOUR, DEFAULT_SEVERITY_LEVEL, category);
        } else {
            Log.i(TAG, "logRequest: Unrecognized custom log mask : " + customLogMask);
        }
    }

    public static void logRequest(String tag, String logEntry, long category) {
        logRequest(tag, logEntry, DEFAULT_ADB_BEHAVIOUR, DEFAULT_SEVERITY_LEVEL, category);
    }

    public static void logRequest(String tag, String logEntry, boolean showOnADBLogs,
                                  int severity) {
        logRequest(tag, logEntry, showOnADBLogs, severity, DEFAULT_LOG_CATEGORY_MASK);
    }

    public static void logRequest(String tag, String logEntry, boolean showOnADBLogs,
                                  String customLogMask) {
        long category = getLogCategoryFromCustomLogMask(customLogMask);
        if (category >= 0) {
            logRequest(tag, logEntry, showOnADBLogs, DEFAULT_SEVERITY_LEVEL, category);
        } else {
            Log.i(TAG, "logRequest: Unrecognized custom log mask : " + customLogMask);
        }
    }

    public static void logRequest(String tag, String logEntry, boolean showOnADBLogs,
                                  long category) {
        logRequest(tag, logEntry, showOnADBLogs, DEFAULT_SEVERITY_LEVEL, category);
    }

    public static void logRequest(String tag, String logEntry, int severity, String customLogMask) {
        long category = getLogCategoryFromCustomLogMask(customLogMask);
        if (category >= 0) {
            logRequest(tag, logEntry, DEFAULT_ADB_BEHAVIOUR, severity, category);
        } else {
            Log.i(TAG, "logRequest: Unrecognized custom log mask : " + customLogMask);
        }
    }

    public static void logRequest(String tag, String logEntry, int severity, long category) {
        logRequest(tag, logEntry, DEFAULT_ADB_BEHAVIOUR, severity, category);
    }

    public static void logRequest(String tag, String logEntry, boolean showOnADBLogs,
                                  int severity, String customLogMask) {
        long category = getLogCategoryFromCustomLogMask(customLogMask);
        if (category >= 0) {
            logRequest(tag, logEntry, showOnADBLogs, severity, category);
        } else {
            Log.i(TAG, "logRequest: Unrecognized custom log mask : " + customLogMask);
        }
    }

    public static void logRequest(String tag, String logEntry, boolean showOnADBLogs,
                                  int severity, long category) {
        String severityLevel;

        boolean shouldOutputToADB = showOnADBLogs && ((category & logCategoryMask) > 0);

        if (tag == null) {
            tag = TAG;
        }

        severityLevel = logFunction(severity, tag, logEntry, shouldOutputToADB);

        // Write to journal file only if the logCategoryMask allows it.
        if ((category & logCategoryMask) > 0) {
            String state = android.os.Environment.getExternalStorageState();
            if (state.equals(android.os.Environment.MEDIA_MOUNTED))
                updateLogJournal(tag, logEntry, severityLevel);
        }
    }

    public static void sendLogFileByEmail() {
        sendLogFileByEmail(null);
    }

    public static void sendLogFileByEmail(List<String> emailList) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentDateTime = sdf.format(new Date());

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, "LogJournal - " + currentDateTime);
        i.putExtra(Intent.EXTRA_TEXT, "Attached log file");

        if (emailList != null && !emailList.isEmpty()) {
            String[] emails = emailList.toArray(new String[0]);
            i.putExtra(Intent.EXTRA_EMAIL, emails);
        }
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + mLogJournalFile));
        i.setType("text/plain");

        DebugHelper.logRequest(TAG, "sendLogFileByEmail: Emailing " + mLogJournalFile, true, DebugHelper.SEVERITY_LEVEL_INFO);

        ((Activity)mContext).startActivity(Intent.createChooser(i, "Send email"));
    }

    //Method to log the contents from the file
    public static void printLogs(){
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fis = mContext.openFileInput(DEFAULT_JOURNAL_FILE);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TESTING", "String builder :"+ sb.toString());
    }
}
