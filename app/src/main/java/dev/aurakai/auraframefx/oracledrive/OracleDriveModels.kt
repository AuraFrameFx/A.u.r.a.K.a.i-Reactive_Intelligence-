package dev.aurakai.auraframefx.oracledrive

// Export canonical models from the genesis.cloud package to avoid duplicate
// definitions that cause KSP/compile-time redeclaration and type-mismatch errors.

public typealias OracleSyncResult = dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleSyncResult
public typealias DriveConsciousnessState = dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousnessState
public typealias SecurityValidation = dev.aurakai.auraframefx.oracledrive.genesis.cloud.SecurityValidation
public typealias AccessLevel = dev.aurakai.auraframefx.oracledrive.genesis.cloud.AccessLevel
public typealias ConflictStrategy = dev.aurakai.auraframefx.oracledrive.genesis.cloud.ConflictStrategy
public typealias FileOperation = dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileOperation
public typealias FileResult = dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult
public typealias DriveInitResult = dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveInitResult
// Keep local ConsciousnessAwakeningResult (defined in Consciousness.kt) — genesis.cloud imports it.
