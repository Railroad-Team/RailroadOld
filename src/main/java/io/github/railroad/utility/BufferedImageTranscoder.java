package io.github.railroad.utility;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class BufferedImageTranscoder extends ImageTranscoder {
    private BufferedImage image;

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
        this.image = img;
    }
}
