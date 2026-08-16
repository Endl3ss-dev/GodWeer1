#version 150

#moj_import <n2ver:common.glsl>

in vec2 FragCoord; // normalized fragment coord relative to the primitive
in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec2 Size; // rectangle size
uniform vec4 Radius; // radius for each vertex
uniform float Smoothness; // edge smoothness
uniform float BlurRadius;

out vec4 OutColor;

const float DPI = 6.28318530718;
const float STEP = DPI / 16.0;

// РџСЂРѕСЃС‚Р°СЏ С„СѓРЅРєС†РёСЏ РґР»СЏ СЃРѕР·РґР°РЅРёСЏ СЌС„С„РµРєС‚Р° "РїСЂРµР»РѕРјР»РµРЅРёСЏ", РєР°Рє Сѓ СЃС‚РµРєР»Р°
vec2 glassRefraction(vec2 uv, vec2 fragCoord, vec2 resolution) {
    // РќРѕСЂРјР°Р»РёР·СѓРµРј РєРѕРѕСЂРґРёРЅР°С‚С‹ РѕС‚РЅРѕСЃРёС‚РµР»СЊРЅРѕ С†РµРЅС‚СЂР°
    vec2 center = resolution * 0.5;
    vec2 dir = (fragCoord - center) / resolution;

    // РЎРѕР·РґР°РµРј РёСЃРєР°Р¶РµРЅРёРµ РЅР° РѕСЃРЅРѕРІРµ СЂР°СЃСЃС‚РѕСЏРЅРёСЏ РґРѕ С†РµРЅС‚СЂР°
    float strength = 0.08; // РЎРёР»Р° РёСЃРєР°Р¶РµРЅРёСЏ
    float factor = 1.0 - length(dir); // С‡РµРј Р±Р»РёР¶Рµ Рє С†РµРЅС‚СЂСѓ, С‚РµРј Р±РѕР»СЊС€Рµ РёСЃРєР°Р¶РµРЅРёРµ
    return uv + dir * strength * factor;
}

void main() {
    vec2 resolution = textureSize(Sampler0, 0);
    vec2 multiplier = BlurRadius / resolution;

    // РџРѕР»СѓС‡Р°РµРј РєРѕРѕСЂРґРёРЅР°С‚С‹ СЃ СЌС„С„РµРєС‚РѕРј РїСЂРµР»РѕРјР»РµРЅРёСЏ (РєР°Рє СЃС‚РµРєР»Рѕ)
//    vec2 refractedCoord = glassRefraction(TexCoord, FragCoord * resolution, resolution);
    vec2 refractedCoord = TexCoord;

    vec3 average = texture(Sampler0, refractedCoord).rgb;
    for (float d = 0.0; d < DPI; d += STEP) {
        for (float i = 0.2; i <= 1.0; i += 0.2) {
            average += texture(Sampler0, refractedCoord + vec2(cos(d), sin(d)) * multiplier * i).rgb;
        }
    }
    average /= 80.0;

    vec4 color = vec4(average, 1.0) * FragColor;
    color.a *= ralpha(Size, FragCoord, Radius, Smoothness);

    if (color.a == 0.0) { // alpha test
        discard;
    }
    OutColor = color;
}