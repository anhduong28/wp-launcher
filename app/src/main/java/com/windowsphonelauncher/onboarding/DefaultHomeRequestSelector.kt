package com.windowsphonelauncher.onboarding

data class DefaultHomeRequestAvailability(
    val canRequestHomeRole: Boolean,
    val canOpenHomeSettings: Boolean,
    val canOpenDefaultAppsSettings: Boolean,
)

enum class DefaultHomeRequestTarget {
    RoleManager,
    HomeSettings,
    DefaultAppsSettings,
}

object DefaultHomeRequestSelector {
    fun orderedTargets(availability: DefaultHomeRequestAvailability): List<DefaultHomeRequestTarget> =
        buildList {
            if (availability.canRequestHomeRole) {
                add(DefaultHomeRequestTarget.RoleManager)
            }
            if (availability.canOpenHomeSettings) {
                add(DefaultHomeRequestTarget.HomeSettings)
            }
            if (availability.canOpenDefaultAppsSettings) {
                add(DefaultHomeRequestTarget.DefaultAppsSettings)
            }
        }

    fun select(availability: DefaultHomeRequestAvailability): DefaultHomeRequestTarget? =
        orderedTargets(availability).firstOrNull()
}
