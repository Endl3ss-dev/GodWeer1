const float MAX_PAIRS = 48.0;

vec4 gaussian(sampler2D source, vec2 uv, vec2 texel, float radius) {
    float sigma = max(radius * 0.5, 0.5);
    int pairs = int(ceil(min(radius * 0.5, MAX_PAIRS)));
    vec4 sum = texture(source, uv);
    float total = 1.0;
    for (int i = 1; i <= pairs; i++) {
        float first = float(2 * i - 1);
        float second = float(2 * i);
        float firstWeight = exp(-first * first / (2.0 * sigma * sigma));
        float secondWeight = exp(-second * second / (2.0 * sigma * sigma));
        float pair = firstWeight + secondWeight;
        vec2 shift = texel * (first * firstWeight + second * secondWeight) / pair;
        sum += (texture(source, uv + shift) + texture(source, uv - shift)) * pair;
        total += 2.0 * pair;
    }
    return sum / total;
}
