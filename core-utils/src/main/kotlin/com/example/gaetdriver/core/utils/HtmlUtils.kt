package com.example.gaetdriver.core.utils

/**
 * Utility for handling HTML content operations.
 */
object HtmlUtils {
    /**
     * Minifies HTML by stripping newlines, extra spaces, and tabs.
     * This keeps the database storage light.
     */
    fun minifyHtml(html: String): String {
        return html.replace(Regex(">\\s+<"), "><") // Remove space between tags
            .replace(Regex("\\s{2,}"), " ")         // Collapse multiple spaces
            .replace("\n", "")                       // Remove newlines
            .replace("\r", "")                       // Remove carriage returns
            .replace("\t", "")                       // Remove tabs
            .trim()
    }
}
