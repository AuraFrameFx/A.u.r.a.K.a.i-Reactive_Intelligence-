package dev.aurakai.auraframefx.oracledrive

// This file previously declared a local `AccessCheck` type which duplicated the
// canonical model in `genesis.cloud`. Export the canonical type here to keep
// existing call-sites working without introducing duplicate symbols.

typealias AccessCheck = dev.aurakai.auraframefx.oracledrive.genesis.cloud.AccessCheck
