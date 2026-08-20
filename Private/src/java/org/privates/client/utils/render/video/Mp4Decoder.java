package org.privates.client.utils.render.video;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Mp4Decoder {

    private final List<BufferedImage> frames = new ArrayList<>();
    private int width;
    private int height;
    private int frameRate = 30;

    public List<BufferedImage> decode(File file) throws IOException {
        frames.clear();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", file.getAbsolutePath(),
                "-vf", "fps=" + frameRate,
                "-f", "image2pipe",
                "-vcodec", "png",
                "-"
        );

        Process process = processBuilder.start();

        try (InputStream inputStream = process.getInputStream()) {
            decodeFrames(inputStream);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Decoding interrupted", e);
        }

        return frames;
    }

    public List<BufferedImage> decode(InputStream inputStream) throws IOException {
        frames.clear();

        File tempFile = File.createTempFile("video_", ".mp4");
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        return decode(tempFile);
    }

    private void decodeFrames(InputStream inputStream) throws IOException {
        while (true) {
            try {
                BufferedImage frame = ImageIO.read(inputStream);
                if (frame == null) break;

                if (width == 0) {
                    width = frame.getWidth();
                    height = frame.getHeight();
                }

                frames.add(frame);
            } catch (IOException e) {
                break;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
}