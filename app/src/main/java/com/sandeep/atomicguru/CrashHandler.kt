package com.sandeep.atomicguru

import android.content.Context
import android.content.Intent
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // 1. Get the full stack trace as a string.
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        val stackTraceString = stringWriter.toString()

        // 2. Launch our dedicated crash log activity.
        val intent = Intent(context, CrashLogActivity::class.java).apply {
            // Use these flags to start the activity in a new task. This is crucial.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Pass the error message to the new activity.
            putExtra(CrashLogActivity.EXTRA_CRASH_LOG, stackTraceString)
        }
        context.startActivity(intent)

        // 3. Terminate the crashed app process.
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}