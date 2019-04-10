package me.urielsalis.mojiraFeedIRC.infrastructure.mojira

import com.sun.syndication.feed.synd.SyndEntryImpl
import me.urielsalis.mojiraFeedIRC.domain.Feed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils

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

fun SyndEntryImpl.toFeed(): Feed =  Feed(parseLink(), parseTitle(), parseAuthor())