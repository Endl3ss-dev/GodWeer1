package org.privates.client.utils.rotation;

import net.minecraft.util.math.vector.Vector2f;
import org.privates.client.utils.other.PerlinNoise;

import java.util.concurrent.ThreadLocalRandom;

public class JitterUtil {

    private static final PerlinNoise perlin = new PerlinNoise(System.currentTimeMillis());

    public enum JitterType {
        WAVE,
        RANDOM,
        CHAOTIC,
        LEGIT_PERLIN,
        SPIRAL,
        CIRCULAR,
        FAST_TREMOR,
        VERTICAL_OSCILLATION,
        ZIGZAG,
        COMPLEX_WAVE
    }


    public static Vector2f getJitter(JitterType type, long time, float strength, float speed, float radius) {
        if (strength <= 0) {
            return Vector2f.ZERO;
        }

        float jitterX = 0;
        float jitterY = 0;
        float timeFactor = time * 0.001f * speed;

        switch (type) {
            case WAVE:
                jitterX = (float) Math.sin(timeFactor) * strength;
                jitterY = (float) Math.cos(timeFactor * 0.8) * strength * 0.6f;
                break;

            case RANDOM:
                jitterX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * strength * 2.0f;
                jitterY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * strength;
                break;

            case CHAOTIC:
                float chaos1 = (float) Math.sin(timeFactor * 1.5) * 0.5f;
                float chaos2 = (float) Math.cos(timeFactor * 2.7) * 0.3f;
                float chaos3 = (float) Math.sin(timeFactor * 0.8) * 0.2f;
                jitterX = (chaos1 + chaos2 + chaos3) * strength;
                jitterY = ((float) Math.cos(timeFactor * 1.9) * 0.6f + (float) Math.sin(timeFactor * 3.1) * 0.4f) * strength * 0.5f;
                break;

            case LEGIT_PERLIN:
                jitterX = (float) perlin.noise(timeFactor, 0.1, 0) * strength;
                jitterY = (float) perlin.noise(0, 0.1, timeFactor) * strength * 0.5f;
                break;

            case SPIRAL:
                float dynamicRadius = radius * (0.5f + (float) (Math.sin(timeFactor * 0.5) * 0.5f));
                jitterX = (float) Math.cos(timeFactor * 3.0f) * dynamicRadius * strength;
                jitterY = (float) Math.sin(timeFactor * 3.0f) * dynamicRadius * strength * 0.7f;
                break;

            case CIRCULAR:
                jitterX = (float) Math.cos(timeFactor) * radius * strength;
                jitterY = (float) Math.sin(timeFactor) * radius * strength * 0.7f;
                break;

            case FAST_TREMOR:
                float highFrequency = time * 0.01f * speed;
                jitterX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * strength * 0.8f + (float) (Math.sin(highFrequency * 5.0) * strength * 0.2f);
                jitterY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * strength * 0.5f + (float) (Math.cos(highFrequency * 5.0) * strength * 0.15f);
                break;

            case VERTICAL_OSCILLATION:
                jitterX = (float) Math.sin(timeFactor * 0.5) * strength * 0.2f;
                jitterY = (float) Math.sin(timeFactor) * strength;
                break;

            case ZIGZAG:
                float phase = (timeFactor * 2.0f) % 4.0f;
                if (phase < 1.0f) {
                    jitterX = phase;
                    jitterY = phase;
                } else if (phase < 2.0f) {
                    jitterX = 1.0f - (phase - 1.0f);
                    jitterY = 1.0f - (phase - 1.0f);
                } else if (phase < 3.0f) {
                    jitterX = -(phase - 2.0f);
                    jitterY = phase - 2.0f;
                } else {
                    jitterX = -(1.0f - (phase - 3.0f));
                    jitterY = 1.0f - (phase - 3.0f);
                }
                jitterX *= strength;
                jitterY *= strength * 0.5f;
                break;

            case COMPLEX_WAVE:
                float wave1 = (float) Math.sin(timeFactor * 1.2) * 0.5f;
                float perlinFactor = (float) perlin.noise(timeFactor * 0.3, 0.2, 0) * 0.5f;
                jitterX = (wave1 + perlinFactor) * strength;

                float wave2 = (float) Math.cos(timeFactor * 0.9) * 0.6f;
                float perlinFactor2 = (float) perlin.noise(0, 0.2, timeFactor * 0.4) * 0.4f;
                jitterY = (wave2 + perlinFactor2) * strength * 0.6f;
                break;

            default:
                return Vector2f.ZERO;
        }

        return new Vector2f(jitterX, jitterY);
    }
}