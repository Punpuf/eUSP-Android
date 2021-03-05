package com.punpuf.e_bandejao.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import timber.log.Timber.d

class StringListTypeAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter?, value: String?) {
        out?.beginObject()
        out?.value(value)
        out?.endObject()
    }

    override fun read(reader: JsonReader?): String {
        var result = ""

        reader?.beginArray()
        while (reader?.hasNext() == true) {
            result += reader.nextString()  + ", "
        }
        reader?.endArray()

        return result.removeSuffix(", ")
    }
}