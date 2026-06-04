package com.windowsphonelauncher

import org.junit.Assert.assertEquals
import org.junit.Test

class PackageMarkerTest {
    @Test
    fun appPackageNameIsStable() {
        assertEquals("com.windowsphonelauncher", BuildConfig.APPLICATION_ID)
    }
}
