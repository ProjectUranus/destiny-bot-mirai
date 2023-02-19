import net.origind.destinybot.api.plugin.Plugin;
import net.origind.destinybot.features.FeaturesPlugin;

module destinybot.features {
    requires org.mongodb.driver.sync.client;
    requires com.squareup.moshi.kotlin;
    requires com.squareup.moshi;
    requires kmongo.core;
    requires org.mongodb.bson;

    requires transitive destinybot.api;
    requires kotlin.stdlib;
    requires okhttp3;
    requires kotlinx.coroutines.core.jvm;
    requires mcprotocollib;
    requires packetlib;
    requires io.ktor.client.core;
    requires java.desktop;
    requires org.slf4j;
    requires com.electronwill.nightconfig.core;
    requires suffixtree;
    requires it.unimi.dsi.fastutil;
    requires net.kyori.adventure;

    exports net.origind.destinybot.features;
    exports net.origind.destinybot.features.apex;
    exports net.origind.destinybot.features.bilibili;
    exports net.origind.destinybot.features.destiny;
    exports net.origind.destinybot.features.github;
    exports net.origind.destinybot.features.instatus;
    exports net.origind.destinybot.features.minecraft;
    exports net.origind.destinybot.features.yahtzee;
    exports net.origind.destinybot.features.romajitable;

    provides Plugin with FeaturesPlugin;
}
