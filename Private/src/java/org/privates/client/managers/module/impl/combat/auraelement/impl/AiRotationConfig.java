package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AiRotationConfig {
    private static final Path CONFIG_DIR = Paths.get("ai_neuro_core");
    private static final Path PROFILE_FILE = CONFIG_DIR.resolve("evolution_profile.json");
    private static final Path SAMPLES_FILE = CONFIG_DIR.resolve("training_data.json");
    private static final Path STATS_FILE = CONFIG_DIR.resolve("statistics.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private AIRotationProfile profile;
    private List<RotationSample> samples;
    private AIStatistics statistics;

    public AiRotationConfig() {
        this.samples = new ArrayList<>();
        this.statistics = new AIStatistics();
    }

    public void load() {
        try {
            Files.createDirectories(CONFIG_DIR);
            if (Files.exists(PROFILE_FILE))
                profile = GSON.fromJson(new FileReader(PROFILE_FILE.toFile()), AIRotationProfile.class);
            if (Files.exists(SAMPLES_FILE)) {
                Type type = new TypeToken<List<RotationSample>>() {
                }.getType();
                samples = GSON.fromJson(new FileReader(SAMPLES_FILE.toFile()), type);
            }
            if (Files.exists(STATS_FILE))
                statistics = GSON.fromJson(new FileReader(STATS_FILE.toFile()), AIStatistics.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile == null) createDefaultProfile();
        if (statistics == null) statistics = new AIStatistics();
        if (samples == null) samples = new ArrayList<>();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            if (profile != null) GSON.toJson(profile, new FileWriter(PROFILE_FILE.toFile()));
            if (samples != null && !samples.isEmpty()) GSON.toJson(samples, new FileWriter(SAMPLES_FILE.toFile()));
            if (statistics != null) GSON.toJson(statistics, new FileWriter(STATS_FILE.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDefaultProfile() {
        profile = new AIRotationProfile();
        profile.version = "1.0-Neuro";
        profile.createdTime = System.currentTimeMillis();
        profile.generalSettings = new GeneralSettings();
        profile.contextualProfiles = new HashMap<>();
        profile.contextualProfiles.put("DEFAULT", new ContextualProfile("DEFAULT"));
    }

    public void addSample(RotationSample sample) {
        if (samples == null) samples = new ArrayList<>();
        samples.add(sample);
        int maxSamples = profile.generalSettings.maxSamplesStored;
        while (samples.size() > maxSamples) samples.remove(0);
        if (statistics != null) {
            statistics.totalSamples++;
            statistics.lastSampleTime = System.currentTimeMillis();
        }
    }

    public void updateStatistics(boolean wasSuccessfulHit) {
        if (statistics == null) return;
        statistics.totalRotations++;
        if (wasSuccessfulHit) statistics.successfulHits++;
        statistics.lastUpdateTime = System.currentTimeMillis();
        if (statistics.totalRotations > 0)
            statistics.hitRate = (float) statistics.successfulHits / statistics.totalRotations;
    }

    public void clearSamples() {
        if (samples != null) samples.clear();
    }

    public void resetStatistics() {
        statistics = new AIStatistics();
    }

    public AIRotationProfile getProfile() {
        return profile;
    }

    public void setProfile(AIRotationProfile profile) {
        this.profile = profile;
    }

    public List<RotationSample> getSamples() {
        return samples;
    }

    public AIStatistics getStatistics() {
        return statistics;
    }

    @Data
    public static class AIRotationProfile {
        public String version;
        public long createdTime;
        public long lastModifiedTime;
        public GeneralSettings generalSettings;
        public Map<String, ContextualProfile> contextualProfiles;
    }

    @Data
    public static class GeneralSettings {
        public float learningRate = 0.05f;
        public float mutationChance = 0.02f;
        public float mutationStrength = 0.1f;
        public int minSamplesForLearning = 100;
        public int maxSamplesStored = 2500;
    }

    @Data
    @NoArgsConstructor
    public static class ContextualProfile {
        public String contextName;
        public float yawSpeedMean = 180f, yawSpeedVariance = 50f;
        public float pitchSpeedMean = 90f, pitchSpeedVariance = 30f;
        public float movementAggressivenessMean = 0.5f, movementAggressivenessVariance = 0.2f;
        public float movementStrengthMean = 0.7f, movementStrengthVariance = 0.2f;
        public float momentumMean = 0.4f, momentumVariance = 0.15f;
        public float jitterStrengthMean = 0.6f, jitterStrengthVariance = 0.3f;
        public float overshootChance = 0.08f;
        public float overshootStrength = 1.5f;
        public Map<String, Float> jitterPatternWeights = new HashMap<>();
        public Map<Integer, Float> aimPointWeights = new HashMap<>();

        public ContextualProfile(String name) {
            this.contextName = name;
            jitterPatternWeights.put("SINE", 1.0f);
            jitterPatternWeights.put("PERLIN", 0.7f);
            jitterPatternWeights.put("RANDOM_SPIKE", 0.4f);
            aimPointWeights.put(0, 0.8f);
            aimPointWeights.put(1, 1.0f);
            aimPointWeights.put(2, 0.5f);
        }
    }

    @Data
    public static class RotationSample {
        public long timestamp;
        public float distanceToTarget;
        public float angleDelta;
        public float targetVelocity;
        public float yawSpeed, pitchSpeed;
        public float movementAggressiveness, movementStrength;
        public float momentum;
        public float jitterStrength;
        public String jitterType;
        public int aimPointIndex;
        public boolean didOvershoot;
        public boolean wasSuccessfulHit;
    }

    @Data
    public static class AIStatistics {
        public long totalSamples, totalRotations, successfulHits;
        public float hitRate;
        public long lastSampleTime, lastUpdateTime, sessionStartTime;
        public int totalSessions;
    }
}