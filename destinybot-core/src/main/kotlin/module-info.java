module destinybot.core {
    requires java.base;
    requires kotlinx.coroutines.core.jvm;
    requires okhttp3;
    requires org.slf4j;
    requires java.desktop;
    requires ktor.client.core.jvm;
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

    exports net.origind.destinybot.core;
}
