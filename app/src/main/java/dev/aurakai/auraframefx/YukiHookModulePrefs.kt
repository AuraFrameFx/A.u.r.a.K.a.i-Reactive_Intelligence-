package dev.aurakai.auraframefx

// Alias to the external YukiHook prefs class so unqualified references resolve during KSP processing.
import com.highcapable.yukihookapi.hook.xposed.prefs.data.YukiHookModulePrefs as ExternalYukiHookModulePrefs

/** Typealias used so code referencing `YukiHookModulePrefs` without package compiles. */
public typealias YukiHookModulePrefs = ExternalYukiHookModulePrefs

