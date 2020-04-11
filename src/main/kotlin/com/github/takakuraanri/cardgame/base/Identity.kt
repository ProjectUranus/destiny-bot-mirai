package com.github.takakuraanri.cardgame.base

sealed class Identity {
    object FARMER: Identity()
    object LANDLORD: Identity()
}
