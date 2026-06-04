package com.windowsphonelauncher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class PackageMarkerTest {
    @Test
    fun appPackageNameIsStable() {
        assertEquals("com.windowsphonelauncher", BuildConfig.APPLICATION_ID)
    }

    @Test
    fun manifestDeclaresMainActivityAsHomeLauncherWithoutSensitiveScope() {
        val manifest = parseManifest()
        val mainActivity = manifest
            .getElementsByTagName("activity")
            .asElements()
            .single { it.androidName == ".MainActivity" }

        val intentFilters = mainActivity.getElementsByTagName("intent-filter").asElements()
        val homeCategoryCount = intentFilters.sumOf {
            it.getElementsByTagName("category")
                .asElements()
                .count { category -> category.androidName == "android.intent.category.HOME" }
        }

        assertTrue(
            "MainActivity must remain openable from the regular launcher.",
            intentFilters.any {
                it.hasAction("android.intent.action.MAIN") &&
                    it.hasCategory("android.intent.category.LAUNCHER")
            },
        )
        assertEquals(
            "MainActivity must declare exactly one Android home intent filter.",
            1,
            intentFilters.count {
                it.hasAction("android.intent.action.MAIN") &&
                    it.hasCategory("android.intent.category.HOME") &&
                    it.hasCategory("android.intent.category.DEFAULT")
            },
        )
        assertEquals(
            "MainActivity must declare exactly one HOME category.",
            1,
            homeCategoryCount,
        )
        assertFalse(
            "Story 1.2 must not add package visibility declarations.",
            manifest.getElementsByTagName("queries").length > 0,
        )
        assertFalse(
            "Story 1.2 must not add permissions.",
            manifest.getElementsByTagName("uses-permission").length > 0,
        )
    }

    private fun parseManifest(): Element {
        val manifestFile = File("src/main/AndroidManifest.xml")
        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(manifestFile)
        return document.documentElement
    }

    private fun org.w3c.dom.NodeList.asElements(): List<Element> =
        (0 until length).mapNotNull { item(it) as? Element }

    private val Element.androidName: String
        get() = getAttribute("android:name")

    private fun Element.hasAction(name: String): Boolean =
        getElementsByTagName("action").asElements().any { it.androidName == name }

    private fun Element.hasCategory(name: String): Boolean =
        getElementsByTagName("category").asElements().any { it.androidName == name }
}
