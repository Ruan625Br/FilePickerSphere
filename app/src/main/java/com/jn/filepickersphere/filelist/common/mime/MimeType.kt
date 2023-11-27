package com.jn.filepickersphere.filelist.common.mime

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.provider.DocumentsContract

/**
 * Represents a MIME type, which is a two-part identifier for file formats and format contents.
 *
 * @param value The string representation of the MIME type.
 */
@SuppressLint("ParcelCreator")
@JvmInline
value class MimeType(val value: String) : Parcelable {

    /** The primary type of the MIME type (e.g., "image"). */
    val type: String
        get() = value.substring(0, value.indexOf('/'))

    /** The subtype of the MIME type (e.g., "jpeg" in "image/jpeg"). */
    val subtype: String
        get() {
            val indexOfSlash = value.indexOf('/')
            val indexOfSemicolon = value.indexOf(';')
            return value.substring(
                indexOfSlash + 1, if (indexOfSemicolon != -1) indexOfSemicolon else value.length
            )
        }

    /** The optional suffix of the MIME type (e.g., "xml" in "image/svg+xml"). */
    val suffix: String?
        get() {
            val indexOfPlus = value.indexOf('+')
            if (indexOfPlus == -1) {
                return null
            }
            val indexOfSemicolon = value.indexOf(';')
            if (indexOfSemicolon != -1 && indexOfPlus > indexOfSemicolon) {
                return null
            }
            return value.substring(
                indexOfPlus + 1, if (indexOfSemicolon != -1) indexOfSemicolon else value.length
            )
        }

    /** The optional parameters of the MIME type (e.g., "charset=utf-8" in "text/plain;charset=utf-8"). */
    val parameters: String?
        get() {
            val indexOfSemicolon = value.indexOf(';')
            return if (indexOfSemicolon != -1) value.substring(indexOfSemicolon + 1) else null
        }

    /**
     * Checks if this MIME type matches another MIME type based on type, subtype, and parameters.
     *
     * @param mimeType The MIME type to compare.
     * @return `true` if the MIME types match; otherwise, `false`.
     */
    fun match(mimeType: MimeType): Boolean =
        type.let { it == "*" || mimeType.type == it }
                && subtype.let { it == "*" || mimeType.subtype == it }
                && parameters.let { it == null || mimeType.parameters == it }

    companion object {

        // Common MIME type constants
        val ANY = "*/*".asMimeType()
        val APK = "application/vnd.android.package-archive".asMimeType()
        val DIRECTORY = DocumentsContract.Document.MIME_TYPE_DIR.asMimeType()
        val IMAGE_ANY = "image/*".asMimeType()
        val IMAGE_JPEG = "image/jpeg".asMimeType()
        val IMAGE_PNG = "image/png".asMimeType()
        val IMAGE_WEBP = "image/webp".asMimeType()
        val IMAGE_GIF = "image/gif".asMimeType()
        val IMAGE_SVG_XML = "image/svg+xml".asMimeType()
        val VIDEO_MP4 = "video/mp4".asMimeType()
        val PDF = "application/pdf".asMimeType()
        val TEXT_PLAIN = "text/plain".asMimeType()
        val GENERIC = "application/octet-stream".asMimeType()

        /**
         * Creates a new MIME type with the specified type, subtype, and optional parameters.
         *
         * @param type The primary type of the MIME type.
         * @param subtype The subtype of the MIME type.
         * @param parameters The optional parameters of the MIME type.
         * @return The created MIME type.
         */
        fun of(type: String, subtype: String, parameters: String?): MimeType =
            "$type/$subtype${if (parameters != null) ";$parameters" else ""}".asMimeType()

        // Parcelable creator
        @JvmField
        val CREATOR: Parcelable.Creator<MimeType> = object : Parcelable.Creator<MimeType> {
            override fun createFromParcel(source: Parcel): MimeType {
                return MimeType(source.readString() ?: throw IllegalArgumentException("Invalid MimeType value"))
            }

            override fun newArray(size: Int): Array<MimeType?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(value)
    }
}

/**
 * Converts a string to a [MimeType] object, or returns `null` if the string is not a valid MIME type.
 *
 * @return The [MimeType] object, or `null` if the string is not a valid MIME type.
 */
fun String.asMimeTypeOrNull(): MimeType? = if (isValidMimeType) MimeType(this) else null

/**
 * Converts a string to a [MimeType] object, throwing an exception if the string is not a valid MIME type.
 *
 * @return The [MimeType] object.
 * @throws IllegalArgumentException if the string is not a valid MIME type.
 */
fun String.asMimeType(): MimeType {
    require(isValidMimeType)
    return MimeType(this)
}

/**
 * Checks if the string is a valid MIME type.
 *
 * @return `true` if the string is a valid MIME type; otherwise, `false`.
 */
private val String.isValidMimeType: Boolean
    get() {
        val indexOfSlash = indexOf('/')
        if (indexOfSlash == -1 || indexOfSlash !in 1 until length) {
            return false
        }
        val indexOfSemicolon = indexOf(';')
        if (indexOfSemicolon != -1) {
            if (indexOfSemicolon !in indexOfSlash + 2 until length) {
                return false
            }
        }
        val indexOfPlus = indexOf('+')
        if (indexOfPlus != -1 && !(indexOfSemicolon != -1 && indexOfPlus > indexOfSemicolon)) {
            if (indexOfPlus !in indexOfSlash + 2
                until if (indexOfSemicolon != -1) indexOfSemicolon - 1 else length) {
                return false
            }
        }
        return true
    }


/**
 * Checks if the string is a valid MIME type.
 *
 * @return `true` if the string is a valid MIME type; otherwise, `false`.
 */
fun MimeType.isASpecificTypeOfMime(mimeType: MimeType): Boolean = this.value == mimeType.value

/**
 * Checks if the [MimeType] represents a media type (e.g., image or video).
 *
 * @return `true` if the [MimeType] represents a media type; otherwise, `false`.
 */
fun MimeType.isMedia(): Boolean {
    val mediaMimeTypes = setOf(
        MimeType.IMAGE_ANY,
        MimeType.IMAGE_JPEG,
        MimeType.IMAGE_PNG,
        MimeType.IMAGE_WEBP,
        MimeType.IMAGE_GIF,
        MimeType.VIDEO_MP4
    )
    return this in mediaMimeTypes
}