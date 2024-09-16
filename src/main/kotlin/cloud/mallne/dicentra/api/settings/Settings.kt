package cloud.mallne.dicentra.api.settings

import cloud.mallne.dicentra.api.roles.Roles
import cloud.mallne.dicentra.api.settings.modules.vn.Instance

object Settings {
    object DC {
        object Dashboard {
            object OmitMenu {
                val AA =
                    SettingsDefinition("dc.dashboard.omitmenu.aa", false, requiresRole = setOf(Roles.AreaAssist.ACCESS))
                val AV =
                    SettingsDefinition("dc.dashboard.omitmenu.av", false, requiresRole = setOf(Roles.Aviator.ACCESS))
                val HL =
                    SettingsDefinition("dc.dashboard.omitmenu.hl", false, requiresRole = setOf(Roles.Herald.ACCESS))
                val WD =
                    SettingsDefinition("dc.dashboard.omitmenu.wd", false, requiresRole = setOf(Roles.Warden.ACCESS))
                val AT =
                    SettingsDefinition("dc.dashboard.omitmenu.at", false, requiresRole = setOf(Roles.Architect.ACCESS))
                val VN =
                    SettingsDefinition("dc.dashboard.omitmenu.vn", false, requiresRole = setOf(Roles.Venatio.ACCESS))
            }
        }

        object AA {
            val AutoSync =
                SettingsDefinition("dc.aa.autosync", false, requiresRole = setOf(Roles.AreaAssist.App.ACCESS))
        }

        object AV {
            object Display {
                val All = SettingsDefinition("dc.av.display.all", false, requiresRole = setOf(Roles.Aviator.ACCESS))
            }
        }

        object VN {
            val Instance =
                SettingsDefinition<Instance>("dc.vn.instance", null, requiresRole = setOf(Roles.Venatio.ACCESS))
        }
    }

    val allSettings: Set<SettingsDefinition<*>> = setOf(
        DC.Dashboard.OmitMenu.AA,
        DC.Dashboard.OmitMenu.AV,
        DC.Dashboard.OmitMenu.HL,
        DC.Dashboard.OmitMenu.WD,
        DC.Dashboard.OmitMenu.AT,
        DC.Dashboard.OmitMenu.VN,
        DC.AA.AutoSync,
        DC.AV.Display.All,
        DC.VN.Instance
    )
}