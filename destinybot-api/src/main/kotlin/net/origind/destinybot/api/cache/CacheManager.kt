package net.origind.destinybot.api.cache

import java.nio.file.Files
import java.nio.file.Paths

const val CACHEDIR_TAG = """Signature: 8a477f597d28d172789f06886806bc55
# This file is a cache directory tag created by Destiny Bot.
# For information about cache directory tags, see:
#	http://www.brynosaurus.com/cachedir/
"""

class CacheManager(val folder: String = "cache") {
    val path = Paths.get(folder)

    init {
        if (!Files.isDirectory(path)) {
            Files.createDirectory(path)
        }

        if (Files.notExists(path.resolve("CACHEDIR.TAG"))) {
            Files.writeString(path.resolve("CACHEDIR.TAG"), CACHEDIR_TAG)
        }
    }
}
