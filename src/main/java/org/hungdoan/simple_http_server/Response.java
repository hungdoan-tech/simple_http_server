package org.hungdoan.simple_http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);

    private OutputStreamWriter writer;

    private int statusCode;

    private String statusMessage;

    private Map<String, String> headers = new HashMap<>();

    private String body;

    public Response(Socket client) throws IOException {
        this.writer = new OutputStreamWriter(client.getOutputStream());
    }

    public void setResponseCode(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public void setBody(String body) {
        headers.put("Content-Length", Integer.toString(body.length()));
        this.body = body;
    }

    public void setHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    public void respond(int statusCode, String msg) {
        try {
            String responseLine = "HTTP/1.1 " + statusCode + " " + msg + "\r\n\r\n";
            writer.write(responseLine);
            writer.flush();
            LOG.info(responseLine);
        } catch (IOException e) {
            LOG.error("Unable to write response back to client");
        }
    }

    public void send() {
        try {
            headers.put("Connection", "Close");
            StringBuilder builder = new StringBuilder();
            builder.append("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
            for (String headerName : headers.keySet()) {
                builder.append(headerName + ": " + headers.get(headerName) + "\r\n");
            }
            if (body != null) {
                builder.append("\r\n");
                builder.append(body);
            }
            String finalResponse = builder.toString();
            writer.write(finalResponse);
            writer.flush();
            LOG.info(finalResponse);
        } catch (IOException e) {
            LOG.error("Unable to write response back to client");
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
