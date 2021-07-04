package com.thegrizzlylabs.sardineandroid.impl;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * PipedOutputStream with synchronized input stream all data reading.
 * Method close() waiting when all data will be read from PipedInputStream
 */
public class PipedOutputStreamSynchronized extends PipedOutputStream {

    PipedOutputStreamSynchronized(PipedInputStream snk) throws IOException {
        super(snk);
    }

    @Override
    public void close() throws IOException {
        super.close();

        Field f = null; // NoSuchFieldException
        try {
            f = Objects.requireNonNull(getClass().getSuperclass()).getDeclaredField("sink");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (f != null) {
            f.setAccessible(true);
            PipedInputStream sink;
            try {
                sink = (PipedInputStream) f.get(this);
                assert sink != null;
                Field closedByReaderField = sink.getClass().getDeclaredField("closedByReader");
                closedByReaderField.setAccessible(true);
                int i = 50; // Max waiting >=1 sec (20*50)
                while (!getInField(sink, closedByReaderField) && i > 0) {
                    Thread.sleep(20);
                    i--;
                }
            } catch (IllegalAccessException | NoSuchFieldException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getInField(PipedInputStream sink, Field closedByReader) throws IllegalAccessException {
        Object field = closedByReader.get(sink);
        if (field != null)
            return (boolean) field;
        else
            return true;
    }
}
