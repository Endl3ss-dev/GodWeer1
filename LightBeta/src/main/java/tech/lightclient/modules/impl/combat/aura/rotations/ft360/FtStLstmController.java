package tech.lightclient.modules.impl.combat.aura.rotations.ft360;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Arrays;

/** Independent fine-tuned LSTM used only by FT and ST rotations. */
public final class FtStLstmController {
    private static final int SEQUENCE = 30;
    private static final int INPUT = 8;
    private static final int HIDDEN_1 = 64;
    private static final int HIDDEN_2 = 32;
    private static final int OUTPUT = 2;
    private static final Model MODEL = loadModel();

    private final float[][] history = new float[SEQUENCE][INPUT];
    private final float[] hidden1 = new float[HIDDEN_1];
    private final float[] cell1 = new float[HIDDEN_1];
    private final float[] nextHidden1 = new float[HIDDEN_1];
    private final float[] gates1 = new float[HIDDEN_1 * 4];
    private final float[] hidden2 = new float[HIDDEN_2];
    private final float[] cell2 = new float[HIDDEN_2];
    private final float[] nextHidden2 = new float[HIDDEN_2];
    private final float[] gates2 = new float[HIDDEN_2 * 4];
    private final float[] output = new float[OUTPUT];
    private int historyIndex;
    private int historySize;
    private float slowYawMemory;
    private float slowPitchMemory;
    private float confidence;

    public float[] predict(float yawError, float pitchError) {
        yawError = clamp(yawError, -1.0f, 1.0f);
        pitchError = clamp(pitchError, -1.0f, 1.0f);
        int previousIndex = (this.historyIndex - 1 + SEQUENCE) % SEQUENCE;
        float previousYawDelta = this.historySize > 0 ? this.history[previousIndex][2] : 0.0f;
        float previousPitchDelta = this.historySize > 0 ? this.history[previousIndex][3] : 0.0f;
        float yawDelta = this.historySize > 0 ? yawError - this.history[previousIndex][0] : 0.0f;
        float pitchDelta = this.historySize > 0 ? pitchError - this.history[previousIndex][1] : 0.0f;

        if (this.historySize == 0) {
            this.slowYawMemory = yawError;
            this.slowPitchMemory = pitchError;
        } else {
            this.slowYawMemory += (yawError - this.slowYawMemory) * 0.035f;
            this.slowPitchMemory += (pitchError - this.slowPitchMemory) * 0.035f;
        }

        float[] features = this.history[this.historyIndex];
        features[0] = yawError;
        features[1] = pitchError;
        features[2] = clamp(yawDelta, -0.45f, 0.45f);
        features[3] = clamp(pitchDelta, -0.45f, 0.45f);
        features[4] = clamp(yawDelta - previousYawDelta, -0.30f, 0.30f);
        features[5] = clamp(pitchDelta - previousPitchDelta, -0.30f, 0.30f);
        features[6] = clamp(yawError - this.slowYawMemory, -0.65f, 0.65f);
        features[7] = clamp(pitchError - this.slowPitchMemory, -0.65f, 0.65f);

        this.historyIndex = (this.historyIndex + 1) % SEQUENCE;
        this.historySize = Math.min(SEQUENCE, this.historySize + 1);
        if (MODEL == null) {
            Arrays.fill(this.output, 0.0f);
            this.confidence = 0.0f;
            return this.output;
        }

        runStep(features, INPUT, HIDDEN_1, MODEL.layer1Weights, MODEL.layer1Bias,
                this.hidden1, this.cell1, this.nextHidden1, this.gates1);
        runStep(this.hidden1, HIDDEN_1, HIDDEN_2, MODEL.layer2Weights, MODEL.layer2Bias,
                this.hidden2, this.cell2, this.nextHidden2, this.gates2);

        float readiness = readiness();
        for (int axis = 0; axis < OUTPUT; ++axis) {
            float recurrent = MODEL.outputBias[axis];
            for (int hidden = 0; hidden < HIDDEN_2; ++hidden) {
                recurrent += this.hidden2[hidden] * MODEL.outputWeights[hidden * OUTPUT + axis];
            }
            float direct = MODEL.skipWeights[INPUT * OUTPUT * 2 + axis];
            for (int feature = 0; feature < INPUT; ++feature) {
                float current = features[feature];
                float previous = this.historySize > 1 ? this.history[previousIndex][feature] : current;
                direct += current * MODEL.skipWeights[feature * OUTPUT + axis]
                        + (current - previous) * MODEL.skipWeights[(INPUT + feature) * OUTPUT + axis];
            }
            float predicted = clamp(direct + (float)Math.tanh(recurrent) * 0.1f, -0.42f, 0.42f);
            float response = 0.28f + readiness * 0.18f;
            float maxChange = 0.025f + Math.abs(features[axis]) * 0.045f;
            this.output[axis] += clamp((predicted - this.output[axis]) * response, -maxChange, maxChange);
            this.output[axis] = clamp(this.output[axis], -0.42f, 0.42f);
        }

        float direction = this.output[0] * yawError >= -0.0015f
                && this.output[1] * pitchError >= -0.0015f ? 1.0f : 0.30f;
        float acceleration = Math.abs(features[4]) + Math.abs(features[5]);
        float stability = 1.0f - clamp((acceleration - 0.025f) * 2.2f, 0.0f, 0.42f);
        this.confidence = readiness * direction * stability;
        return this.output;
    }

    public void reset() {
        this.historyIndex = 0;
        this.historySize = 0;
        this.slowYawMemory = 0.0f;
        this.slowPitchMemory = 0.0f;
        this.confidence = 0.0f;
        Arrays.fill(this.output, 0.0f);
        Arrays.fill(this.hidden1, 0.0f);
        Arrays.fill(this.cell1, 0.0f);
        Arrays.fill(this.nextHidden1, 0.0f);
        Arrays.fill(this.hidden2, 0.0f);
        Arrays.fill(this.cell2, 0.0f);
        Arrays.fill(this.nextHidden2, 0.0f);
        for (float[] frame : this.history) {
            Arrays.fill(frame, 0.0f);
        }
    }

    public float readiness() {
        return MODEL == null ? 0.0f : clamp((this.historySize - 5.0f) / 25.0f, 0.0f, 1.0f);
    }

    public float confidence() {
        return MODEL == null ? 0.0f : this.confidence;
    }

    /**
     * Read-only inference snapshot for auxiliary motor shaping. Callers must
     * never retain or mutate the returned controller-owned array.
     */
    public float[] lastOutput() {
        return this.output;
    }

    public static boolean isLoaded() {
        return MODEL != null;
    }

    public static float validationMse() {
        return MODEL == null ? Float.NaN : MODEL.validationMse;
    }

    public static float validationMae() {
        return MODEL == null ? Float.NaN : MODEL.validationMae;
    }

    private static void runStep(float[] input, int inputSize, int hiddenSize,
                                float[] weights, float[] bias, float[] hidden, float[] cell,
                                float[] nextHidden, float[] gates) {
        int gateSize = hiddenSize * 4;
        System.arraycopy(bias, 0, gates, 0, gateSize);
        for (int feature = 0; feature < inputSize; ++feature) {
            float value = input[feature];
            int row = feature * gateSize;
            for (int gate = 0; gate < gateSize; ++gate) {
                gates[gate] += value * weights[row + gate];
            }
        }
        for (int state = 0; state < hiddenSize; ++state) {
            float value = hidden[state];
            int row = (inputSize + state) * gateSize;
            for (int gate = 0; gate < gateSize; ++gate) {
                gates[gate] += value * weights[row + gate];
            }
        }
        for (int state = 0; state < hiddenSize; ++state) {
            float inputGate = sigmoid(gates[state]);
            float forgetGate = sigmoid(gates[hiddenSize + state]);
            float candidate = (float)Math.tanh(gates[hiddenSize * 2 + state]);
            float outputGate = sigmoid(gates[hiddenSize * 3 + state]);
            cell[state] = forgetGate * cell[state] + inputGate * candidate;
            nextHidden[state] = outputGate * (float)Math.tanh(cell[state]);
        }
        System.arraycopy(nextHidden, 0, hidden, 0, hiddenSize);
    }

    private static Model loadModel() {
        try (InputStream resource = FtStLstmController.class.getResourceAsStream(
                "/assets/light/models/ftst_lstm.bin")) {
            if (resource == null) {
                return null;
            }
            try (DataInputStream input = new DataInputStream(new BufferedInputStream(resource))) {
                byte[] magic = new byte[8];
                input.readFully(magic);
                if (!Arrays.equals(magic, new byte[]{78, 76, 83, 84, 77, 50, 0, 0})) {
                    return null;
                }
                int version = input.readInt();
                int sequence = input.readInt();
                int features = input.readInt();
                int hidden1 = input.readInt();
                int hidden2 = input.readInt();
                int outputs = input.readInt();
                if (version != 1 || sequence != SEQUENCE || features != INPUT
                        || hidden1 != HIDDEN_1 || hidden2 != HIDDEN_2 || outputs != OUTPUT) {
                    return null;
                }
                return new Model(
                        readArray(input, (INPUT + HIDDEN_1) * HIDDEN_1 * 4),
                        readArray(input, HIDDEN_1 * 4),
                        readArray(input, (HIDDEN_1 + HIDDEN_2) * HIDDEN_2 * 4),
                        readArray(input, HIDDEN_2 * 4),
                        readArray(input, HIDDEN_2 * OUTPUT),
                        readArray(input, OUTPUT),
                        readArray(input, INPUT * OUTPUT * 2 + OUTPUT),
                        input.readFloat(), input.readFloat());
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private static float[] readArray(DataInputStream input, int expected) throws Exception {
        int length = input.readInt();
        if (length != expected) {
            throw new IllegalStateException("Unexpected FT/ST LSTM tensor length: "
                    + length + " != " + expected);
        }
        float[] values = new float[length];
        for (int i = 0; i < length; ++i) {
            values[i] = input.readFloat();
        }
        return values;
    }

    private static float sigmoid(float value) {
        value = clamp(value, -12.0f, 12.0f);
        return 1.0f / (1.0f + (float)Math.exp(-value));
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private record Model(float[] layer1Weights, float[] layer1Bias,
                         float[] layer2Weights, float[] layer2Bias,
                         float[] outputWeights, float[] outputBias,
                         float[] skipWeights, float validationMse, float validationMae) {
    }
}
