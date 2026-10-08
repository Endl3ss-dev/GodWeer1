#version 150

uniform sampler2D InSampler;
uniform vec2 TextureSize;
uniform float GlowStrength;
uniform vec4 GlowColor;
uniform float GlowRadius;

in vec2 texCoord;
out vec4 fragColor;

// РџСЂРѕРІРµСЂСЏРµС‚, СЏРІР»СЏРµС‚СЃСЏ Р»Рё РїРёРєСЃРµР»СЊ "СЃРІРµС‚СЏС‰РёРјСЃСЏ РѕР±СЉРµРєС‚РѕРј"
bool isGlowSource(vec4 color) {
    // РџСЂРѕРІРµСЂСЏРµРј, С‡С‚Рѕ С†РІРµС‚ РїРѕС‡С‚Рё Р±РµР»С‹Р№ Рё РїРёРєСЃРµР»СЊ РЅРµРїСЂРѕР·СЂР°С‡РЅС‹Р№
    return color.r > 0.9 && color.g > 0.9 && color.b > 0.9 && color.a > 0.9;
}

void main() {
    vec4 centerColor = texture(InSampler, texCoord);

    // Р•СЃР»Рё СЌС‚Рѕ СЃР°Рј РѕР±СЉРµРєС‚ вЂ” РѕСЃС‚Р°РІР»СЏРµРј РµРіРѕ Р±РµР»С‹Рј
    if (isGlowSource(centerColor)) {
        fragColor = centerColor;
        return;
    }

    // Р•СЃР»Рё СЌС‚Рѕ РїРѕР»РЅРѕСЃС‚СЊСЋ РїСЂРѕР·СЂР°С‡РЅС‹Р№ РїРёРєСЃРµР»СЊ вЂ” РґРµР»Р°РµРј РіР»РѕСѓ РІРѕРєСЂСѓРі
    float glow = 0.0;
    int radius = int(GlowRadius);
    float totalWeight = 0.0;

    for (int x = -radius; x <= radius; ++x) {
        for (int y = -radius; y <= radius; ++y) {
            float distance = length(vec2(x, y));
            if (distance > GlowRadius) continue;

            float weight = exp(-distance * distance / (2.0 * GlowRadius * GlowRadius));
            vec2 offset = vec2(x, y) / TextureSize;
            vec4 sampleColor = texture(InSampler, texCoord + offset);

            // РЈС‡РёС‚С‹РІР°РµРј С‚РѕР»СЊРєРѕ РїРёРєСЃРµР»Рё, РєРѕС‚РѕСЂС‹Рµ СЏРІР»СЏСЋС‚СЃСЏ РёСЃС‚РѕС‡РЅРёРєРѕРј СЃРІРµС‡РµРЅРёСЏ
            if (isGlowSource(sampleColor)) {
                glow += weight;
            }

            totalWeight += weight;
        }
    }

    // РќРѕСЂРјР°Р»РёР·СѓРµРј РіР»РѕСѓ
    glow = glow > 0.0 ? glow / totalWeight : 0.0;

    // РЈСЃРёР»РёРІР°РµРј
    float glowAlpha = min(glow * GlowStrength, 1.0);

    // Р•СЃР»Рё РіР»РѕСѓ СЃР»РёС€РєРѕРј СЃР»Р°Р±С‹Р№ вЂ” РЅРµ СЂРёСЃСѓРµРј
    if (glowAlpha < 0.01) {
        discard;
    }

    // РЈСЃС‚Р°РЅР°РІР»РёРІР°РµРј С†РІРµС‚ РіР»РѕСѓ
    fragColor = vec4(GlowColor.rgb, glowAlpha);
}