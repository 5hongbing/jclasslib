/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AbstractStructure
import org.gjt.jclasslib.structures.Annotation
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.attributes.targettype.TargetInfo
import org.gjt.jclasslib.structures.attributes.targettype.UndefinedTargetInfo

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

/**
 * Describes an entry in a RuntimeVisibleTypeAnnotations or RuntimeInvisibleTypeAnnotations
 * attribute structure.
 */
class TypeAnnotation : AbstractStructure() {

    var targetType: TypeAnnotationTargetType = TypeAnnotationTargetType.UNDEFINED
    var targetInfo: TargetInfo = UndefinedTargetInfo
    var typePathEntries: Array<TypePathEntry> = emptyArray()
    var annotation: Annotation = Annotation()

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun read(input: DataInput) {
        targetType = TypeAnnotationTargetType.getFromTag(input.readUnsignedByte())
        targetInfo = targetType.createTargetInfo()
        targetInfo.read(input)

        val typePathLength = input.readUnsignedByte()
        typePathEntries = Array(typePathLength) {
            TypePathEntry().apply { read(input) }
        }
        annotation.read(input)

        if (isDebug) debug("read")
    }

    @Throws(InvalidByteCodeException::class, IOException::class)
    override fun write(output: DataOutput) {
        output.writeByte(targetType.tag)
        targetInfo.write(output)
        output.writeByte(typePathEntries.size)
        typePathEntries.forEach { it.write(output) }
        annotation.write(output)

        if (isDebug) debug("wrote")
    }

    override fun debug(message: String) {
        super.debug("$message TypeAnnotation entry")
    }

    val length: Int
        get() = 2 + targetInfo.length + typePathEntries.size * 2 + annotation.length

}
