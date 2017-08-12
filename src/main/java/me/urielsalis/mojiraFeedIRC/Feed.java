package me.urielsalis.mojiraFeedIRC;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * mojirafeed
 * <p>
 * Created by urielsalis on 08/08/17.
 */
public class Feed {
    String link;
    String title;
    String author;

    public Feed(SyndEntryImpl entry) {
        this.link = entry.getLink().replace("&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel", ""); // shorten comment links
        this.title = StringEscapeUtils.unescapeHtml4(entry.getTitle().replaceAll("\\<[^>]*>", "").trim().substring(entry.getAuthor().length()+1).replaceAll("\\s+", " ")); //remove all html tags and extra spaces/new lines; unescape HTML entities
        this.author = StringUtils.abbreviate(entry.getAuthor().replaceAll("\\[.*?\\]", "").trim().replaceAll(" ", "_"), 20); // remove [Mod] prefixes; don't include spaces in usernames; make sure the length is no more than 20
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (link != null ? !link.equals(feed.link) : feed.link != null) return false;
        if (title != null ? !title.equals(feed.title) : feed.title != null) return false;
        return author != null ? author.equals(feed.author) : feed.author == null;
    }

    @Override
    public int hashCode() {
        int result = link != null ? link.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }

}
