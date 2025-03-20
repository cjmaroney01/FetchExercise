package com.colinmaroney.fetchexercise.utils

/**
 * I found this on Stack Overflow.  Apparently InstantTaskExecutorRule
 * does not work with Junit5 and I needed to use this to make one of
 * my test suites run.   It is not my code, and I would not have
 * known how to write it myself w/o some research.
 */
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class InstantTaskExecutorRuleForJUnit5 : AfterEachCallback, BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}