package fun.volon.api.utils.ai;

import java.util.Random;

/**
 * Простая нейронная сеть для обучения ротаций в реальном времени
 */
public class SimpleNeuralNetwork {
    private final int inputSize;
    private final int hiddenSize;
    private final int outputSize;
    
    // Веса сети
    private double[][] weightsInputHidden;
    private double[][] weightsHiddenOutput;
    
    // Биасы
    private double[] biasHidden;
    private double[] biasOutput;
    
    // Скорость обучения
    private double learningRate = 0.01;
    
    // Для momentum
    private double[][] momentumInputHidden;
    private double[][] momentumHiddenOutput;
    private double momentumFactor = 0.9;
    
    private final Random random = new Random();
    
    public SimpleNeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        // Xavier initialization
        weightsInputHidden = new double[inputSize][hiddenSize];
        weightsHiddenOutput = new double[hiddenSize][outputSize];
        
        biasHidden = new double[hiddenSize];
        biasOutput = new double[outputSize];
        
        momentumInputHidden = new double[inputSize][hiddenSize];
        momentumHiddenOutput = new double[hiddenSize][outputSize];
        
        double limitInputHidden = Math.sqrt(6.0 / (inputSize + hiddenSize));
        double limitHiddenOutput = Math.sqrt(6.0 / (hiddenSize + outputSize));
        
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] = random.nextDouble() * 2 * limitInputHidden - limitInputHidden;
            }
        }
        
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenOutput[i][j] = random.nextDouble() * 2 * limitHiddenOutput - limitHiddenOutput;
            }
        }
    }
    
    /**
     * Прямое распространение
     */
    public double[] forward(double[] input) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException("Input size mismatch");
        }
        
        // Скрытый слой
        double[] hidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = biasHidden[i];
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weightsInputHidden[j][i];
            }
            hidden[i] = relu(sum);
        }
        
        // Выходной слой
        double[] output = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = biasOutput[i];
            for (int j = 0; j < hiddenSize; j++) {
                sum += hidden[j] * weightsHiddenOutput[j][i];
            }
            output[i] = tanh(sum);
        }
        
        return output;
    }
    
    /**
     * Обучение методом обратного распространения ошибки
     */
    public void train(double[] input, double[] targetOutput, double reward) {
        if (input.length != inputSize || targetOutput.length != outputSize) {
            throw new IllegalArgumentException("Size mismatch");
        }
        
        // Прямое распространение
        double[] hidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = biasHidden[i];
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weightsInputHidden[j][i];
            }
            hidden[i] = relu(sum);
        }
        
        double[] output = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = biasOutput[i];
            for (int j = 0; j < hiddenSize; j++) {
                sum += hidden[j] * weightsHiddenOutput[j][i];
            }
            output[i] = tanh(sum);
        }
        
        // Вычисление ошибки выходного слоя
        double[] outputError = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            outputError[i] = (targetOutput[i] - output[i]) * tanhDerivative(output[i]) * reward;
        }
        
        // Вычисление ошибки скрытого слоя
        double[] hiddenError = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double error = 0;
            for (int j = 0; j < outputSize; j++) {
                error += outputError[j] * weightsHiddenOutput[i][j];
            }
            hiddenError[i] = error * reluDerivative(hidden[i]);
        }
        
        // Обновление весов с momentum
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                double gradient = learningRate * outputError[j] * hidden[i];
                momentumHiddenOutput[i][j] = momentumFactor * momentumHiddenOutput[i][j] + gradient;
                weightsHiddenOutput[i][j] += momentumHiddenOutput[i][j];
            }
        }
        
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                double gradient = learningRate * hiddenError[j] * input[i];
                momentumInputHidden[i][j] = momentumFactor * momentumInputHidden[i][j] + gradient;
                weightsInputHidden[i][j] += momentumInputHidden[i][j];
            }
        }
        
        // Обновление биасов
        for (int i = 0; i < outputSize; i++) {
            biasOutput[i] += learningRate * outputError[i];
        }
        
        for (int i = 0; i < hiddenSize; i++) {
            biasHidden[i] += learningRate * hiddenError[i];
        }
    }
    
    // Функции активации
    private double relu(double x) {
        return Math.max(0, x);
    }
    
    private double reluDerivative(double x) {
        return x > 0 ? 1 : 0;
    }
    
    private double tanh(double x) {
        return Math.tanh(x);
    }
    
    private double tanhDerivative(double x) {
        return 1 - x * x;
    }
    
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
    
    public double getLearningRate() {
        return learningRate;
    }
}
