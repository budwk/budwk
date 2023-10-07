package com.budwk.starter.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class ElasticsearchStarter {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    protected PropertiesProxy conf;

    protected static final String PRE = "elasticsearch.";

    @PropDoc(value = "Elasticsearch的主机地址", defaultValue = "127.0.0.1:9200", need = true)
    public static final String ES_PROP_HOST = PRE + "host";

    @PropDoc(value = "Elasticsearch的用户名")
    public static final String ES_USERNAME = PRE + "username";

    @PropDoc(value = "Elasticsearch的用户名")
    public static final String ES_PASSWORD = PRE + "password";

    @IocBean(name = "restClient", depose = "close")
    public RestClient getRestClient() {
        log.debug("loading elasticsearchClient...");

        final String hosts_str = conf.get(ES_PROP_HOST, "127.0.0.1:9200");
        List<HttpHost> httpHostList = new ArrayList<>();
        if (hosts_str.contains(",")) {
            for (String ht : hosts_str.split(",")) {
                httpHostList.add(new HttpHost(ht.split(":")[0], Integer.parseInt(ht.split(":")[1])));
            }
        } else {
            httpHostList.add(new HttpHost(hosts_str.split(":")[0], Integer.parseInt(hosts_str.split(":")[1])));
        }

        HttpHost[] httpHosts = new HttpHost[httpHostList.size()];
        for (int i = 0; i < httpHostList.size(); i++) {
            httpHosts[i] = httpHostList.get(i);
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);

        if (Strings.isNotBlank(conf.get(ES_USERNAME)) && Strings.isNotBlank(conf.get(ES_PASSWORD))) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(conf.get(ES_USERNAME), conf.get(ES_PASSWORD)));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return builder.build();
    }

    @IocBean(name = "elasticsearchClient")
    public ElasticsearchClient getElasticsearchClient() {
        ElasticsearchTransport transport = new RestClientTransport(
                ioc.get(RestClient.class), new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);

    }
}
