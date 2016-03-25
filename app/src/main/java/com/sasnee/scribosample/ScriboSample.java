package com.sasnee.scribosample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.sasnee.scribo.DebugHelper;

public class ScriboSample extends Activity {
    private static final String TAG = "ScriboSample";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dhsample);

        /* Initialize the library */
        DebugHelper.init(getApplicationContext());

        Log.i(TAG, "onResume: Journal file: " + DebugHelper.getFilePath());

        /* Map default categories to custom log masks. */
        DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_0, "CATEGORY 0");
        DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_5, "CATEGORY 5");
        DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_7, "CATEGORY 7");
        DebugHelper.mapCustomLogMask(DebugHelper.LOG_CATEGORY_10, "CATEGORY 10");

        /* Enable/Disable the log category either based on the default categories... */
        DebugHelper.enableDisableLogCategory(DebugHelper.LOG_CATEGORY_4, true);
        DebugHelper.enableDisableLogCategory(DebugHelper.LOG_CATEGORY_8, false);
        DebugHelper.enableDisableLogCategory(DebugHelper.LOG_CATEGORY_9, true);

        /* ...or based on the custom log masks defined above... */
        DebugHelper.enableDisableLogCategory("CATEGORY 0", true);
        DebugHelper.enableDisableLogCategory("CATEGORY 5", false);
        DebugHelper.enableDisableLogCategory("CATEGORY 7", true);

        /* Even if you have mapped default category to a custom log mask, you can still
         * use the default category defined in the library. */
        DebugHelper.enableDisableLogCategory(DebugHelper.LOG_CATEGORY_10, true);


        /* Now, use any of the below listed variants of the API
         * DebugHelper.logRequest() to pass the log string to the library */

        /* Variant 1 */
        DebugHelper.logRequest(TAG, "Variant type 1");

        /* Variant 2 */
        DebugHelper.logRequest(TAG, "Variant type 2", true/* Show on ADB logs */);

        /* Variant 3 */
        DebugHelper.logRequest(TAG, "Variant type 3", DebugHelper.SEVERITY_LEVEL_INFO);

        /* Variant 4 */
        DebugHelper.logRequest(TAG, "Variant type 4", "CATEGORY 5");

        /* Variant 5 */
        DebugHelper.logRequest(TAG, "Variant type 5", DebugHelper.LOG_CATEGORY_4);

        /* Variant 6 */
        DebugHelper.logRequest(TAG, "Variant type 6",
                                true /* Show on ADB logs */, DebugHelper.SEVERITY_LEVEL_VERBOSE);

        /* Variant 7 */
        DebugHelper.logRequest(TAG, "Variant type 7",
                                false /* Show on ADB logs */, "CATEGORY 5");

        /* Variant 8 */
        DebugHelper.logRequest(TAG, "Variant type 8",
                                false /* Show on ADB logs */, DebugHelper.LOG_CATEGORY_9);

        /* Variant 9 */
        DebugHelper.logRequest(TAG, "Variant type 9",
                                DebugHelper.SEVERITY_LEVEL_INFO, "CATEGORY 7");

        /* Variant 10 */
        DebugHelper.logRequest(TAG, "Variant type 10",
                                DebugHelper.SEVERITY_LEVEL_VERBOSE, DebugHelper.LOG_CATEGORY_7);

        /* Variant 11 */
        DebugHelper.logRequest(TAG, "Variant type 11", true /* Show on ADB logs */,
                                DebugHelper.SEVERITY_LEVEL_WARN, "CATEGORY 10");

        /* Variant 12 */
        DebugHelper.logRequest(TAG, "Variant type 12", true /* Show on ADB logs */,
                                DebugHelper.SEVERITY_LEVEL_WARN, DebugHelper.LOG_CATEGORY_GENERAL);

    }
}
