package rxsearch.events

import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject

open class PojoToJsonCodec<T>(val clasz: Class<T>) : MessageCodec<T, T> {

    override fun encodeToWire(buffer: Buffer, o: T) {
        val entries = JsonObject.mapFrom(o)
        entries.writeToBuffer(buffer)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): T {
        return buffer.toJsonObject().mapTo(clasz)
    }

    override fun transform(o: T): T {
        return o
    }

    override fun name(): String {
        return clasz.simpleName
    }

    override fun systemCodecID(): Byte {
        return -1
    }
}