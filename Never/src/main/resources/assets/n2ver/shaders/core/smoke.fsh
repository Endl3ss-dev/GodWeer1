#version 150

#moj_import <n2ver:common.glsl>

in vec2 FragCoord; // normalized fragment coord relative to the primitive
in vec4 FragColor;

uniform vec2 Size; // rectangle size
uniform vec4 Radius; // radius for each vertex
uniform float Smoothness; // edge smoothness

// РџР°СЂР°РјРµС‚СЂС‹ РґС‹РјР°
//uniform vec2 Size;          // Р Р°Р·РјРµСЂ РґС‹РјР° (С€РёСЂРёРЅРѕР№ Рё РІС‹СЃРѕС‚РѕР№)
//uniform vec4 Radius;        // Р Р°РґРёСѓСЃ РёСЃС‚РѕС‡РЅРёРєР° РґС‹РјР°
//uniform float Smoothness;   // Р“Р»Р°РґРєРѕСЃС‚СЊ РїРµСЂРµС…РѕРґР°
uniform float Time;         // Р’СЂРµРјСЏ РґР»СЏ Р°РЅРёРјР°С†РёРё
uniform vec3 Color;         // Р’СЂРµРјСЏ РґР»СЏ Р°РЅРёРјР°С†РёРё

out vec4 OutColor;

void main() {
    // РћРїСЂРµРґРµР»СЏРµРј С†РµРЅС‚СЂ РґС‹РјР°
    vec2 center = vec2(0.0, 0.0);

    // Р Р°СЃСЃС‚РѕСЏРЅРёРµ РѕС‚ С‚РµРєСѓС‰РµР№ С‚РѕС‡РєРё РґРѕ С†РµРЅС‚СЂР° РґС‹РјР°
    vec2 distanceToCenter = FragCoord.xy - center;

    // РќРѕСЂРјР°Р»РёР·СѓРµРј СЂР°СЃСЃС‚РѕСЏРЅРёРµ РЅР° РѕСЃРЅРѕРІРµ СЂР°Р·РјРµСЂР° РґС‹РјР°
    vec2 normalizedDistance = distanceToCenter / Size;

    // Р’С‹С‡РёСЃР»СЏРµРј РїР»РѕС‚РЅРѕСЃС‚СЊ РґС‹РјР°
    float density = exp(-length(normalizedDistance) * Smoothness + Time * 0.05);

    // РћРіСЂР°РЅРёС‡РёРІР°РµРј РїР»РѕС‚РЅРѕСЃС‚СЊ РґС‹РјРєР° РІ РґРёР°РїР°Р·РѕРЅРµ [0, 1]
    density = clamp(density, 0.0, 1.0);

    // РЎРјРµС€РёРІР°РµРј С†РІРµС‚ РґС‹РјР° СЃ РїСЂРѕР·СЂР°С‡РЅРѕСЃС‚СЊСЋ
    vec4 smokeColorRGBA = vec4(Color, density);

    // РЈСЃС‚Р°РЅР°РІР»РёРІР°РµРј С†РІРµС‚ С„СЂР°РіРјРµРЅС‚Р°
    OutColor = smokeColorRGBA;
}