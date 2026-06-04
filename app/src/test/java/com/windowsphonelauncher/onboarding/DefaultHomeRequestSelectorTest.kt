package com.windowsphonelauncher.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultHomeRequestSelectorTest {
    @Test
    fun choosesRoleManagerWhenHomeRoleIsAvailableAndNotHeld() {
        val target = DefaultHomeRequestSelector.select(
            availability = DefaultHomeRequestAvailability(
                canRequestHomeRole = true,
                canOpenHomeSettings = true,
                canOpenDefaultAppsSettings = true,
            ),
        )

        assertEquals(DefaultHomeRequestTarget.RoleManager, target)
    }

    @Test
    fun ordersFallbackTargetsByPreference() {
        val targets = DefaultHomeRequestSelector.orderedTargets(
            availability = DefaultHomeRequestAvailability(
                canRequestHomeRole = true,
                canOpenHomeSettings = true,
                canOpenDefaultAppsSettings = true,
            ),
        )

        assertEquals(
            listOf(
                DefaultHomeRequestTarget.RoleManager,
                DefaultHomeRequestTarget.HomeSettings,
                DefaultHomeRequestTarget.DefaultAppsSettings,
            ),
            targets,
        )
    }

    @Test
    fun choosesHomeSettingsWhenRoleManagerIsUnavailable() {
        val target = DefaultHomeRequestSelector.select(
            availability = DefaultHomeRequestAvailability(
                canRequestHomeRole = false,
                canOpenHomeSettings = true,
                canOpenDefaultAppsSettings = true,
            ),
        )

        assertEquals(DefaultHomeRequestTarget.HomeSettings, target)
    }

    @Test
    fun choosesDefaultAppsSettingsWhenHomeSettingsCannotResolve() {
        val target = DefaultHomeRequestSelector.select(
            availability = DefaultHomeRequestAvailability(
                canRequestHomeRole = false,
                canOpenHomeSettings = false,
                canOpenDefaultAppsSettings = true,
            ),
        )

        assertEquals(DefaultHomeRequestTarget.DefaultAppsSettings, target)
    }

    @Test
    fun returnsNullWhenNoPlatformRouteCanResolve() {
        val target = DefaultHomeRequestSelector.select(
            availability = DefaultHomeRequestAvailability(
                canRequestHomeRole = false,
                canOpenHomeSettings = false,
                canOpenDefaultAppsSettings = false,
            ),
        )

        assertNull(target)
    }
}
