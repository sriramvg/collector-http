/* Copyright 2010-2014 Norconex Inc.
 * 
 * This file is part of Norconex HTTP Collector.
 * 
 * Norconex HTTP Collector is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Norconex HTTP Collector is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Norconex HTTP Collector. If not, 
 * see <http://www.gnu.org/licenses/>.
 */
package com.norconex.collector.http.crawler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.collector.core.checksum.IMetadataChecksummer;
import com.norconex.collector.core.crawler.AbstractCrawlerConfig;
import com.norconex.collector.core.data.store.impl.mapdb.MapDBCrawlDataStoreFactory;
import com.norconex.collector.http.checksum.impl.HttpMetadataChecksummer;
import com.norconex.collector.http.client.IHttpClientFactory;
import com.norconex.collector.http.client.impl.GenericHttpClientFactory;
import com.norconex.collector.http.delay.IDelayResolver;
import com.norconex.collector.http.delay.impl.GenericDelayResolver;
import com.norconex.collector.http.doc.IHttpDocumentProcessor;
import com.norconex.collector.http.fetch.IHttpDocumentFetcher;
import com.norconex.collector.http.fetch.IHttpMetadataFetcher;
import com.norconex.collector.http.fetch.impl.GenericDocumentFetcher;
import com.norconex.collector.http.robot.IRobotsMetaProvider;
import com.norconex.collector.http.robot.IRobotsTxtProvider;
import com.norconex.collector.http.robot.impl.StandardRobotsMetaProvider;
import com.norconex.collector.http.robot.impl.StandardRobotsTxtProvider;
import com.norconex.collector.http.sitemap.ISitemapResolverFactory;
import com.norconex.collector.http.sitemap.impl.StandardSitemapResolverFactory;
import com.norconex.collector.http.url.IURLExtractor;
import com.norconex.collector.http.url.IURLNormalizer;
import com.norconex.collector.http.url.impl.GenericURLExtractor;
import com.norconex.commons.lang.config.ConfigurationUtil;
import com.norconex.commons.lang.xml.EnhancedXMLStreamWriter;

/**
 * HTTP Crawler configuration.
 * @author Pascal Essiembre
 */
public class HttpCrawlerConfig extends AbstractCrawlerConfig {

    private static final Logger LOG = 
            LogManager.getLogger(HttpCrawlerConfig.class);
    
    private int maxDepth = -1;
    private String[] startURLs;
    private String[] urlsFiles;
    
    private boolean ignoreRobotsTxt;
    private boolean ignoreRobotsMeta;
    private boolean ignoreSitemap;
    private boolean keepDownloads;
    private String userAgent;

    private IURLNormalizer urlNormalizer;

    private IDelayResolver delayResolver = new GenericDelayResolver();
    
    private IHttpClientFactory httpClientFactory =
            new GenericHttpClientFactory();
    
    private IHttpDocumentFetcher documentFetcher =
            new GenericDocumentFetcher();

    private IHttpMetadataFetcher metadataFetcher;

    private IURLExtractor urlExtractor = new GenericURLExtractor();

    private IRobotsTxtProvider robotsTxtProvider =
            new StandardRobotsTxtProvider();
    private IRobotsMetaProvider robotsMetaProvider =
            new StandardRobotsMetaProvider();
    private ISitemapResolverFactory sitemapResolverFactory =
            new StandardSitemapResolverFactory();

    private IMetadataChecksummer metadataChecksummer = 
    		new HttpMetadataChecksummer();
	
    private IHttpDocumentProcessor[] preImportProcessors;
    private IHttpDocumentProcessor[] postImportProcessors;

    public HttpCrawlerConfig() {
        super();
        setCrawlDataStoreFactory(new MapDBCrawlDataStoreFactory());
    }
    public String[] getStartURLs() {
        return ArrayUtils.clone(startURLs);
    }
    public void setStartURLs(String[] startURLs) {
        this.startURLs = ArrayUtils.clone(startURLs);
    }
    public String[] getUrlsFiles() {
        return ArrayUtils.clone(urlsFiles);
    }
    public void setUrlsFiles(String[] urlsFiles) {
        this.urlsFiles = ArrayUtils.clone(urlsFiles);
    }
    public void setMaxDepth(int depth) {
        this.maxDepth = depth;
    }
    public int getMaxDepth() {
        return maxDepth;
    }
    public IHttpClientFactory getHttpClientFactory() {
        return httpClientFactory;
    }
    public void setHttpClientFactory(IHttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
    }
    public IHttpDocumentFetcher getDocumentFetcher() {
        return documentFetcher;
    }
    public void setDocumentFetcher(
    		IHttpDocumentFetcher documentFetcher) {
        this.documentFetcher = documentFetcher;
    }
    public IHttpMetadataFetcher getMetadataFetcher() {
        return metadataFetcher;
    }
    public void setMetadataFetcher(IHttpMetadataFetcher metadataFetcher) {
        this.metadataFetcher = metadataFetcher;
    }
    public IURLExtractor getUrlExtractor() {
        return urlExtractor;
    }
    public void setUrlExtractor(IURLExtractor urlExtractor) {
        this.urlExtractor = urlExtractor;
    }
    public IRobotsTxtProvider getRobotsTxtProvider() {
        return robotsTxtProvider;
    }
    public void setRobotsTxtProvider(IRobotsTxtProvider robotsTxtProvider) {
        this.robotsTxtProvider = robotsTxtProvider;
    }
    public IURLNormalizer getUrlNormalizer() {
        return urlNormalizer;
    }
    public void setUrlNormalizer(IURLNormalizer urlNormalizer) {
        this.urlNormalizer = urlNormalizer;
    }
    public IDelayResolver getDelayResolver() {
        return delayResolver;
    }
    public void setDelayResolver(IDelayResolver delayResolver) {
        this.delayResolver = delayResolver;
    }

    public IHttpDocumentProcessor[] getPreImportProcessors() {
        return ArrayUtils.clone(preImportProcessors);
    }
    public void setPreImportProcessors(
    		IHttpDocumentProcessor[] httpPreProcessors) {
        this.preImportProcessors = ArrayUtils.clone(httpPreProcessors);
    }
    public IHttpDocumentProcessor[] getPostImportProcessors() {
        return ArrayUtils.clone(postImportProcessors);
    }
    public void setPostImportProcessors(
    		IHttpDocumentProcessor[] httpPostProcessors) {
        this.postImportProcessors = ArrayUtils.clone(httpPostProcessors);
    }
    public boolean isIgnoreRobotsTxt() {
        return ignoreRobotsTxt;
    }
    public void setIgnoreRobotsTxt(boolean ignoreRobotsTxt) {
        this.ignoreRobotsTxt = ignoreRobotsTxt;
    }
    public boolean isKeepDownloads() {
        return keepDownloads;
    }
    public void setKeepDownloads(boolean keepDownloads) {
        this.keepDownloads = keepDownloads;
    }
    public IMetadataChecksummer getMetadataChecksummer() {
		return metadataChecksummer;
	}
	public void setMetadataChecksummer(
	        IMetadataChecksummer metadataChecksummer) {
		this.metadataChecksummer = metadataChecksummer;
	}
	public boolean isIgnoreRobotsMeta() {
        return ignoreRobotsMeta;
    }
    public void setIgnoreRobotsMeta(boolean ignoreRobotsMeta) {
        this.ignoreRobotsMeta = ignoreRobotsMeta;
    }
    public IRobotsMetaProvider getRobotsMetaProvider() {
        return robotsMetaProvider;
    }
    public void setRobotsMetaProvider(IRobotsMetaProvider robotsMetaProvider) {
        this.robotsMetaProvider = robotsMetaProvider;
    }
    public boolean isIgnoreSitemap() {
        return ignoreSitemap;
    }
    public void setIgnoreSitemap(boolean ignoreSitemap) {
        this.ignoreSitemap = ignoreSitemap;
    }
    public ISitemapResolverFactory getSitemapResolverFactory() {
        return sitemapResolverFactory;
    }
    public void setSitemapResolverFactory(
            ISitemapResolverFactory sitemapResolverFactory) {
        this.sitemapResolverFactory = sitemapResolverFactory;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    @Override
    protected void saveCrawlerConfigToXML(Writer out) throws IOException {
        try {
            EnhancedXMLStreamWriter writer = new EnhancedXMLStreamWriter(out);
            writer.writeElementString("userAgent", getUserAgent());
            writer.writeElementInteger("maxDepth", getMaxDepth());
            writer.writeElementBoolean("keepDownloads", isKeepDownloads());
            writer.writeStartElement("startURLs");
            for (String url : getStartURLs()) {
                writer.writeStartElement("url");
                writer.writeCharacters(url);
                writer.writeEndElement();
            }
            for (String path : getUrlsFiles()) {
                writer.writeStartElement("urlsFile");
                writer.writeCharacters(path);
                writer.writeEndElement();
            }
            writer.writeEndElement();
            
            writeObject(out, "urlNormalizer", getUrlNormalizer());
            writeObject(out, "delay", getDelayResolver());
            writeObject(out, "httpClientFactory", getHttpClientFactory());
            writeObject(out, "robotsTxt", 
                    getRobotsTxtProvider(), isIgnoreRobotsTxt());
            writeObject(out, "sitemapResolverFactory", 
                    getSitemapResolverFactory(), isIgnoreSitemap());
            writeObject(out, "metadataFetcher", getMetadataFetcher());
            writeObject(out, "metadataChecksummer", getMetadataChecksummer());
            writeObject(out, "documentFetcher", getDocumentFetcher());
            writeObject(out, "robotsMeta", 
                    getRobotsMetaProvider(), isIgnoreRobotsMeta());
            writeObject(out, "urlExtractor", getUrlExtractor());
            writeArray(out, "preImportProcessors", 
                    "processor", getPreImportProcessors());
            writeArray(out, "postImportProcessors", 
                    "processor", getPostImportProcessors());
        } catch (XMLStreamException e) {
            throw new IOException(
                    "Could not write to XML config: " + getId(), e);
        }
    }

    @Override
    protected void loadCrawlerConfigFromXML(XMLConfiguration xml) {
        //--- Simple Settings --------------------------------------------------
        loadSimpleSettings(xml);

        //--- HTTP Client Factory ----------------------------------------------
        setHttpClientFactory(ConfigurationUtil.newInstance(xml,
                "httpClientFactory", getHttpClientFactory()));

        //--- RobotsTxt provider -----------------------------------------------
        setRobotsTxtProvider(ConfigurationUtil.newInstance(xml,
                "robotsTxt", getRobotsTxtProvider()));
        setIgnoreRobotsTxt(xml.getBoolean("robotsTxt[@ignore]",
                isIgnoreRobotsTxt()));

        //--- Sitemap Resolver -------------------------------------------------
        setSitemapResolverFactory(ConfigurationUtil.newInstance(xml,
                "sitemap", getSitemapResolverFactory()));
        setIgnoreSitemap(xml.getBoolean("sitemap[@ignore]",
                isIgnoreSitemap()));

        //--- HTTP Headers Fetcher ---------------------------------------------
        setMetadataFetcher(ConfigurationUtil.newInstance(xml,
                "metadataFetcher", getMetadataFetcher()));

        //--- Metadata Checksummer -----------------------------------------
        setMetadataChecksummer(ConfigurationUtil.newInstance(xml,
                "metadataChecksummer", getMetadataChecksummer()));

        //--- HTTP Document Fetcher --------------------------------------------
        setDocumentFetcher(ConfigurationUtil.newInstance(xml,
                "documentFetcher", getDocumentFetcher()));

        //--- RobotsMeta provider ----------------------------------------------
        setRobotsMetaProvider(ConfigurationUtil.newInstance(xml,
                "robotsMeta", getRobotsMetaProvider()));
        setIgnoreRobotsMeta(xml.getBoolean("robotsMeta[@ignore]",
                isIgnoreRobotsMeta()));

        //--- URL Extractor ----------------------------------------------------
        setUrlExtractor(ConfigurationUtil.newInstance(xml,
                "urlExtractor", getUrlExtractor()));

        //--- HTTP Pre-Processors ----------------------------------------------
        IHttpDocumentProcessor[] preProcFilters = loadProcessors(xml,
                "preImportProcessors.processor");
        setPreImportProcessors(defaultIfEmpty(preProcFilters,
                getPreImportProcessors()));

        //--- HTTP Post-Processors ---------------------------------------------
        IHttpDocumentProcessor[] postProcFilters = loadProcessors(xml,
                "postImportProcessors.processor");
        setPostImportProcessors(defaultIfEmpty(postProcFilters,
                getPostImportProcessors()));

    }

    private void loadSimpleSettings(XMLConfiguration xml) {
        setUserAgent(xml.getString("userAgent", getUserAgent()));
        setUrlNormalizer(ConfigurationUtil.newInstance(
                xml, "urlNormalizer", getUrlNormalizer()));
        setDelayResolver(ConfigurationUtil.newInstance(
                xml, "delay", getDelayResolver()));
        setMaxDepth(xml.getInt("maxDepth", getMaxDepth()));
        setKeepDownloads(xml.getBoolean("keepDownloads", isKeepDownloads()));

        String[] startURLs = xml.getStringArray("startURLs.url");
        setStartURLs(defaultIfEmpty(startURLs, getStartURLs()));
        
        String[] urlsFiles = xml.getStringArray("startURLs.urlsFile");
        setUrlsFiles(defaultIfEmpty(urlsFiles, getUrlsFiles()));
    }

    private IHttpDocumentProcessor[] loadProcessors(XMLConfiguration xml,
            String xmlPath) {
        List<IHttpDocumentProcessor> filters = new ArrayList<>();
        List<HierarchicalConfiguration> filterNodes = xml
                .configurationsAt(xmlPath);
        for (HierarchicalConfiguration filterNode : filterNodes) {
            IHttpDocumentProcessor filter = ConfigurationUtil
                    .newInstance(filterNode);
            filters.add(filter);
            LOG.info("HTTP document processor loaded: " + filter);
        }
        return filters.toArray(new IHttpDocumentProcessor[] {});
    }
}
