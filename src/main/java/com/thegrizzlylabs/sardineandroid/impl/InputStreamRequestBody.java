package com.thegrizzlylabs.sardineandroid.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class InputStreamRequestBody extends RequestBody {
    private static final long UPLOAD_BUFFER_SIZE = 16384;

    private final InputStream inputStream;
    private final MediaType mediaType;

    InputStreamRequestBody(@NotNull InputStream inputStream, @Nullable MediaType mediaType) {
        this.inputStream = inputStream;
        this.mediaType = mediaType;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        Source source = Okio.source(inputStream);

        long read = 0L;
        while (read != -1L) {
            read = source.read(bufferedSink.getBuffer(), UPLOAD_BUFFER_SIZE);
            bufferedSink.flush();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
