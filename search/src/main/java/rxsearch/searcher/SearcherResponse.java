package rxsearch.searcher;

import com.google.common.net.MediaType;

import java.nio.ByteBuffer;

public class SearcherResponse {

    private final int statusCode;
    private final String statusMessage;
    private MediaType mediaType;
    private final ByteBuffer content;

    public SearcherResponse(int statusCode, String statusMessage, MediaType mediaType, ByteBuffer content) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.mediaType = mediaType;
        this.content = content;
    }

    public int statusCode() {
        return statusCode;
    }

    public String statusMessage() {
        return statusMessage;
    }

    public ByteBuffer content() {
        return content;
    }

    public MediaType mediaType() {
        return mediaType;
    }
}
