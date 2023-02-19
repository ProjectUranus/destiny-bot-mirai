module destinybot.core {
    requires java.base;
    requires kotlinx.coroutines.core.jvm;
    requires org.slf4j;
    requires java.desktop;
    requires mirai.core.api.jvm;
    requires kotlin.stdlib;
    requires it.unimi.dsi.fastutil;
    requires suffixtree;

    requires transitive destinybot.api;
    requires transitive destinybot.features;

    requires com.squareup.moshi.kotlin;
    requires com.squareup.moshi;
    requires com.electronwill.nightconfig.core;
    requires com.electronwill.nightconfig.toml;
    requires kotlinx.serialization.json;
    requires kotlinx.serialization.core;

    exports net.origind.destinybot.core;
}
