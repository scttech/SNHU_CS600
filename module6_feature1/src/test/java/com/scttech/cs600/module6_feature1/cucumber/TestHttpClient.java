package com.scttech.cs600.module6_feature1.cucumber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A minimal, dependency-free HTTP client for the login Cucumber steps. Trusts this project's
 * self-signed dev certificate — the same posture as {@code curl -k} in
 * docs/module3/rest/README.md — and never follows redirects, so a 3xx from Spring Security is
 * always visible to the caller (redirect-to-login, redirect-to-error) instead of being silently
 * followed the way a browser would.
 */
final class TestHttpClient {

    record Response(int status, Map<String, List<String>> headers, String body) {

        List<String> headerValues(String name) {
            return headers.entrySet().stream()
                    .filter(e -> e.getKey() != null && e.getKey().equalsIgnoreCase(name))
                    .flatMap(e -> e.getValue().stream())
                    .toList();
        }

        String header(String name) {
            List<String> values = headerValues(name);
            return values.isEmpty() ? null : values.get(0);
        }
    }

    Response get(String url, Map<String, String> headers) throws IOException {
        return request("GET", url, headers, null);
    }

    Response postForm(String url, Map<String, String> headers, String formBody) throws IOException {
        Map<String, String> withContentType = new java.util.LinkedHashMap<>(headers);
        withContentType.put("Content-Type", "application/x-www-form-urlencoded");
        return request("POST", url, withContentType, formBody);
    }

    private Response request(String method, String url, Map<String, String> headers, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        if (connection instanceof HttpsURLConnection https) {
            https.setSSLSocketFactory(trustAllSslContext().getSocketFactory());
            https.setHostnameVerifier((hostname, session) -> true);
        }
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method);
        headers.forEach(connection::setRequestProperty);

        if (body != null) {
            connection.setDoOutput(true);
            try (OutputStream out = connection.getOutputStream()) {
                out.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }

        int status = connection.getResponseCode();
        InputStream stream = status < 400 ? connection.getInputStream() : connection.getErrorStream();
        String responseBody = stream == null ? "" : new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        return new Response(status, connection.getHeaderFields(), responseBody);
    }

    static String formEncode(Map<String, String> params) {
        StringBuilder body = new StringBuilder();
        params.forEach((key, value) -> {
            if (!body.isEmpty()) {
                body.append('&');
            }
            body.append(java.net.URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .append('=')
                    .append(java.net.URLEncoder.encode(value, StandardCharsets.UTF_8));
        });
        return body.toString();
    }

    static String basicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static SSLContext trustAllSslContext() {
        try {
            TrustManager[] trustAllCerts = { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new IllegalStateException("Could not build a trust-all SSLContext for tests", e);
        }
    }
}
