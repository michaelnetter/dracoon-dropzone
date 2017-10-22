package org.mn.dropzone.crypto.model;

public class PlainDataContainer {

    private byte[] content;

    public PlainDataContainer(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

}
