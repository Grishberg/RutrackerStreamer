package com.rest.rutracker.rutrackerrestclient.data.model;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergeyb on 03.07.15.
 *
 * @author sergeyb
 */
public class RutrackerFeedParcer  {
    // We don't use namespaces
    private static final String ns = null;

    public static class Entry {
        public final String title;
        public final String link;
        public final String author;

        private Entry(String title, String author, String link) {
            this.title = title;
            this.author = author;
            this.link = link;
        }
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        int eventType = parser.getEventType();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, DD MMM yyyy HH:mm:ss");
        while ( parser.next() != XmlPullParser.END_TAG) {

            eventType = parser.getEventType();
            if (eventType == XmlPullParser.START_DOCUMENT || eventType != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name!=null && name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }

        }
        return entries;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String summary = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("author")) {
                summary = readAuthor(parser);
            } else if (name.equals("link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(title, summary, link);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();

        if (tag.equals("link")) {
            link = parser.getAttributeValue(null, "href");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readAuthor(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title="";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if(name.equals("name")){
                parser.require(XmlPullParser.START_TAG, ns, "name");
                title = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "name");

            } else {
                skip(parser);
            }
        }
        return title;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
