package com.windowsphonelauncher.onboarding

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity

class DefaultHomeGateway(
    private val activity: ComponentActivity,
) {
    private val packageManager: PackageManager = activity.packageManager

    fun createRequestIntents(): List<Intent> {
        val roleIntent = createHomeRoleRequestIntent()
        val homeSettingsIntent = Intent(Settings.ACTION_HOME_SETTINGS)
        val defaultAppsIntent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        val targets = DefaultHomeRequestSelector.orderedTargets(
            availability = DefaultHomeRequestAvailability(
                canRequestHomeRole = roleIntent != null,
                canOpenHomeSettings = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP,
                canOpenDefaultAppsSettings = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N,
            ),
        )

        return targets.mapNotNull { target ->
            when (target) {
                DefaultHomeRequestTarget.RoleManager -> roleIntent
                DefaultHomeRequestTarget.HomeSettings -> homeSettingsIntent
                DefaultHomeRequestTarget.DefaultAppsSettings -> defaultAppsIntent
            }
        }
    }

    private fun createHomeRoleRequestIntent(): Intent? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null
        }

        val roleManager = activity.getSystemService(Context.ROLE_SERVICE) as? RoleManager
            ?: return null
        if (!roleManager.isRoleAvailable(RoleManager.ROLE_HOME) ||
            roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        ) {
            return null
        }

        return roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
    }

    fun isCurrentHomeApp(): Boolean {
        val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolvedHome = packageManager.resolveActivity(
            homeIntent,
            PackageManager.MATCH_DEFAULT_ONLY,
        )
        return resolvedHome?.activityInfo?.packageName == activity.packageName
    }
}
