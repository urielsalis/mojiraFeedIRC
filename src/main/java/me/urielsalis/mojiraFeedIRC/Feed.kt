package me.urielsalis.mojiraFeedIRC

import com.sun.syndication.feed.synd.SyndEntryImpl
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils

data class Feed(var link: String, var title: String, var author: String) {
    constructor(entry: SyndEntryImpl) : this(entry.parseLink(), entry.parseTitle(), entry.parseAuthor())
}

fun SyndEntryImpl.parseLink(): String =
        this.link.replace("&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel", "") // shorten comment links

fun SyndEntryImpl.parseTitle(): String = StringEscapeUtils.unescapeHtml4(
        this.title
                .replace("<[^>]*>".toRegex(), "")
                .trim { it <= ' ' }
                .substring(this.author.length + 1)
                .replace("\\s+".toRegex(), " ")) //remove all html tags and extra spaces/new lines; unescape HTML entities

fun SyndEntryImpl.parseAuthor(): String = StringUtils.abbreviate(
        this.author
                .replace("\\[.*?]".toRegex(), "")
                .trim { it <= ' ' }
                .replace(" ".toRegex(), "_"), 20) // remove [Mod] prefixes; don't include spaces in usernames; make sure the length is no more than 20

