package de.visualdigits.newshomereader.model.configuration

import de.visualdigits.newshomereader.model.page.Page

class NewsFeedsConfiguration(
    var naviMain: Page
) {

    fun toHtml(
        currentPage: Page? = null,
        theme: String,
        hideRead: Boolean
    ): String {
        val html = StringBuilder()
        html
            .append("                        <span class=\"sidebar-title\">Newsfeeds</span>\n")
            .append(naviMain.toHtml(
                currentPage = currentPage,
                theme = theme,
                hideRead = hideRead,
                indent = "                        "
            ))

        return html.toString()
    }
}