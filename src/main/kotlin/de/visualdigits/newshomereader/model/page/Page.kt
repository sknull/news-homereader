package de.visualdigits.newshomereader.model.page

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.apache.commons.text.StringEscapeUtils

@JsonIgnoreProperties("parent")
class Page(
    val name: String = "",
    var parent: Page? = null,
    val children: List<Page> = listOf(),

    val url: String? = null,
    val filters: Map<String, PageFilter> = mapOf()
) {

    val childrenMap: Map<String, Page> = children.associateBy { c -> c.name }

    init {
        children.forEach { c ->
            c.parent = this
        }
    }

    override fun toString(): String {
        return toString("")
    }

    fun toString(indent: String): String {
        val sb = StringBuilder()
        sb.append("$indent[$name]\n")
        sb.append(children.joinToString("") { p -> p.toString("$indent  ") })

        return sb.toString()
    }

    fun isLeaf(): Boolean = children.isEmpty()

    fun allChildren(children: MutableList<Page> = mutableListOf()): List<Page> {
        children.addAll(this.children)
        this.children.forEach { c -> c.allChildren(children) }

        return children
    }

    fun toHtml(
        currentPage: Page? = null,
        theme: String = "",
        hideRead: Boolean,
        indent: String = "",
        level: Int = 0
    ): String {
        val sb = StringBuilder()
        val containerClazz = if (parent == null) " class=\"toplevel\"" else ""
        sb.append("$indent<ul$containerClazz>\n")
        children.forEach { c ->
            val itemClazz = determineStyleClass(c, currentPage)
            sb.append("$indent  <li class=\"$itemClazz\">\n")
                .append(pageLink(c, level, hideRead, "$indent    "))
                .append(c.toHtml(currentPage, theme, hideRead, "$indent    ", level + 1))
                .append("$indent  </li>\n")
        }
        sb.append("$indent</ul>\n")

        return sb.toString()
    }

    private fun pageLink(
        page: Page,
        level: Int = 0,
        hideRead: Boolean,
        indent: String = ""
    ): String {
        val html = StringBuilder()
        html.append("$indent<a href=\"/news/${StringEscapeUtils.escapeHtml4(page.path())}?hideRead=$hideRead&\" style=\"padding-left: ${10 + level * 10}px;\">")
            .append("<div class=\"nav-item\">")
        html.append("<div class=\"nav-text\">${page.name}</div>")
            .append("</div>")
            .append("</a>\n")

        return html.toString()
    }

    private fun determineStyleClass(page: Page, currentPage: Page?): String {
        val pagePath: String = page.path()
        val currentPagePath = currentPage?.path()
        val isFolder: Boolean = page.children.isNotEmpty()
        val isCurrent = pagePath == currentPagePath
        val inCurrentPath = currentPagePath?.contains(pagePath) == true
        var clazz = if (isFolder) "folder" else "page"
        if (isCurrent) {
            clazz += " current"
        } else if (inCurrentPath) {
            clazz += " parent"
            if (page.parent == null || page.parent?.name?.isEmpty() == true) {
                clazz += " ancestor"
            }
        }
        return clazz
    }

    fun getPage(path: String): Page? {
        return getPage(path.split("/"))
    }

    fun getPage(rootLine: List<String>): Page? {
        val firstOrNull = rootLine.firstOrNull()
        var page = childrenMap[firstOrNull]
        val nextRootLine = rootLine.drop(1)
        if (nextRootLine.isNotEmpty()) {
            page = page?.getPage(nextRootLine)
        }

        return page?:topPage()
    }

    fun topPage(): Page? {
        return allChildren().firstOrNull()
    }

    fun path(): String = rootLine().joinToString("/") { p -> p.name }

    fun rootLine(rootLine: MutableList<Page> = mutableListOf()): List<Page> {
        rootLine.addFirst(this)
        if (parent?.name?.isNotEmpty() == true) {
            parent?.rootLine(rootLine)
        }

        return rootLine
    }
}