package com.github.takakuraanri.cardgame.base

import java.util.*

var locale = Locale("zh_CN")
val localeCache: MutableMap<Locale, ResourceBundle> = mutableMapOf()

val resourceBundle
        get() = localeCache.getOrPut(locale) { ResourceBundle.getBundle("/lang/lang", locale) }

fun translate(key: String, vararg params: Any?) = String.format(locale, resourceBundle.getString(key), *params)
