package com.torfiles.crawler.sites;

import java.util.Objects;

public class SiteMagnet {
    private String uri;
    private Long seeders, leechers;

    public SiteMagnet(String uri, Long seeders, Long leechers) {
        this.uri = uri;
        this.seeders = seeders;
        this.leechers = leechers;
    }

    public String getUri() {
        return uri;
    }

    public Long getSeeders() {
        return seeders;
    }

    public Long getLeechers() {
        return leechers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteMagnet siteMagnet = (SiteMagnet) o;
        return Objects.equals(uri, siteMagnet.uri);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uri);
    }
}
