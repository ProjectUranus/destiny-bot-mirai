package net.origind.destinybot.core.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun String.toGZIPByteArray(): ByteArray {
    val output = ByteArrayOutputStream()
    val gzip = GZIPOutputStream(output)
    gzip.write(toByteArray(StandardCharsets.UTF_8))
    gzip.close()
    return output.toByteArray()
}

fun String.toGZIPCompressedBase64Encoded(): String {
    return Base64.getEncoder().encodeToString(toGZIPByteArray())
}

fun String.decodeGZIPBase64(): String {
    val input = GZIPInputStream(ByteArrayInputStream(Base64.getDecoder().decode(toByteArray(StandardCharsets.UTF_8))))
    val data = input.readAllBytes()
    input.close()
    return String(data, StandardCharsets.UTF_8)
}
