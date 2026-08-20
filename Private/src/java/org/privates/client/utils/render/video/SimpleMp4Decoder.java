package org.privates.client.utils.render.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleMp4Decoder {

    private final List<BufferedImage> frames = new ArrayList<>();
    private int width;
    private int height;
    private double frameRate = 30.0;

    public List<BufferedImage> decodeFromFrames(File framesDirectory) throws IOException {
        frames.clear();

        File[] frameFiles = framesDirectory.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg")
        );

        if (frameFiles == null || frameFiles.length == 0) {
            throw new IOException("No frame files found in directory: " + framesDirectory);
        }

        java.util.Arrays.sort(frameFiles, (f1, f2) -> {
            String name1 = f1.getName().replaceAll("\\D+", "");
            String name2 = f2.getName().replaceAll("\\D+", "");

            if (name1.isEmpty() || name2.isEmpty()) {
                return f1.getName().compareTo(f2.getName());
            }

            try {
                return Integer.compare(Integer.parseInt(name1), Integer.parseInt(name2));
            } catch (NumberFormatException e) {
                return f1.getName().compareTo(f2.getName());
            }
        });

        for (File frameFile : frameFiles) {
            BufferedImage frame = javax.imageio.ImageIO.read(frameFile);
            if (frame != null) {
                if (width == 0) {
                    width = frame.getWidth();
                    height = frame.getHeight();
                }
                frames.add(frame);
            }
        }

        return frames;
    }

    public List<BufferedImage> decodeFromImageSequence(List<BufferedImage> imageSequence) {
        frames.clear();
        frames.addAll(imageSequence);

        if (!frames.isEmpty()) {
            width = frames.get(0).getWidth();
            height = frames.get(0).getHeight();
        }

        return frames;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public int getFrameCount() {
        return frames.size();
    }
}