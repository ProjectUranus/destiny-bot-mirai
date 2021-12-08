module destinybot.api {
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;
    requires org.slf4j;
    requires com.electronwill.nightconfig.core;

    exports net.origind.destinybot.api.cache;
    exports net.origind.destinybot.api.command;
    exports net.origind.destinybot.api.plugin;
    exports net.origind.destinybot.api.util;
}
