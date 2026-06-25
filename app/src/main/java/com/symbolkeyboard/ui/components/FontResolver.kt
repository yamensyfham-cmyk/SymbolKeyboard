package com.symbolkeyboard.ui.components

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.res.ResourcesCompat

object FontResolver {

    private val fontCache = mutableMapOf<String, Typeface?>()

    fun resolveFontFamily(context: Context, fontName: String): FontFamily {
        if (fontName.isBlank()) return FontFamily.Default

        return try {
            val resourceName = fontName
                .replace("-", "_")
                .replace(" ", "_")
                .lowercase()

            val resourceId = context.resources.getIdentifier(
                resourceName,
                "font",
                context.packageName
            )

            if (resourceId != 0) {
                FontFamily.Default
            } else {
                FontFamily.Default
            }
        } catch (e: Exception) {
            FontFamily.Default
        }
    }

    fun resolveTypeface(context: Context, fontName: String): Typeface? {
        if (fontName.isBlank()) return null

        return fontCache.getOrPut(fontName) {
            try {
                val resourceName = fontName
                    .replace("-", "_")
                    .replace(" ", "_")
                    .lowercase()

                val resourceId = context.resources.getIdentifier(
                    resourceName,
                    "font",
                    context.packageName
                )

                if (resourceId != 0) {
                    ResourcesCompat.getFont(context, resourceId)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}
