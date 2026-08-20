package org.privates.client.utils.render.shader.glsl;

import org.privates.client.utils.render.shader.IShader;

public class BiomorphCellsGlsl implements IShader {
    @Override
    public String shader() {
        return """
                #version 120
                
                uniform sampler2D u_diffuse_sampler;
                uniform sampler2D u_other_sampler;
                uniform vec2      u_texel_size;
                uniform float     u_time;
                uniform float     u_cell_scale;
                uniform float     u_pulse_speed;
                
                float hash21(vec2 p) {
                    vec3 q = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
                    q += dot(q, q.yzx + 33.33);
                    return fract((q.x + q.y) * q.z);
                }
                
                vec2 hash22(vec2 p) {
                    vec3 q = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
                    q += dot(q, q.yzx + 33.33);
                    return fract((q.xx + q.yz) * q.zy);
                }
                
                // ─── SDF мягкого blob'а (superellipse) ───────────
                // Позволяет делать неровные органические формы
                float sdfBlob(vec2 p, float r, float id, float phase) {
                    float angle = atan(p.y, p.x);
                    // Неровный контур — 4-6 лопастей, медленно пульсирующих
                    float lobes = 4.0 + floor(id * 3.0);
                    float wobble = 1.0 + 0.07 * sin(lobes * angle + u_time * u_pulse_speed * 0.6 + id * 6.28)
                                       + 0.04 * sin((lobes + 1.0) * angle - u_time * u_pulse_speed * 0.4 + id * 3.1);
                    return length(p) - r * wobble;
                }
                
                // ─── Одна клетка ──────────────────────────────────
                // Возвращает: rgb-вклад + alpha-вклад в аккумуляторы
                // phase [0..1] — прогресс цикла деления
                void cellAt(vec2 uv, vec2 cellPos, float id, float phase,
                            inout vec3 accumColor, inout float accumAlpha) {
                
                    float angle  = id * 6.2831;
                    vec2  divDir = vec2(cos(angle), sin(angle));
                    float baseR  = 0.14 + id * 0.06;
                
                    // Индивидуальный цвет клетки — оттенки зелёного/бирюзового/голубого
                    // с лёгким разбросом
                    float hue = 0.35 + id * 0.25; // 0.35=зелёный, 0.6=голубой
                    // HSV→RGB упрощённо
                    float h6  = hue * 6.0;
                    float hi  = floor(h6);
                    float f   = h6 - hi;
                    vec3 cytoColor = vec3(0.0);
                    if      (hi < 1.0) cytoColor = vec3(1.0, f, 0.0);
                    else if (hi < 2.0) cytoColor = vec3(1.0-f, 1.0, 0.0);
                    else if (hi < 3.0) cytoColor = vec3(0.0, 1.0, f);
                    else if (hi < 4.0) cytoColor = vec3(0.0, 1.0-f, 1.0);
                    else if (hi < 5.0) cytoColor = vec3(f, 0.0, 1.0);
                    else               cytoColor = vec3(1.0, 0.0, 1.0-f);
                    // Приглушаем — клетки полупрозрачные, не кислотные
                    cytoColor = mix(vec3(0.1, 0.5, 0.4), cytoColor * 0.4 + vec3(0.1, 0.4, 0.35), id);
                
                    vec3  membrColor = cytoColor * 1.8 + vec3(0.1, 0.25, 0.15);
                    vec3  nucColor   = vec3(0.95, 0.8, 0.2) + vec3(0.0, 0.1, -0.1) * id;
                
                    // ── Фаза 0..0.30 — покой (G1/S/G2) ───────────
                    if (phase < 0.30) {
                        float t   = phase / 0.30;
                        float r   = baseR * (0.8 + 0.2 * t);
                        float d   = sdfBlob(uv - cellPos, r, id, phase);
                        float fill = smoothstep(0.015, -0.005, d);
                        float membr= smoothstep(0.02, 0.0, abs(d + 0.012));
                        float nuc  = smoothstep(r * 0.25, r * 0.05, length(uv - cellPos));
                
                        float cellAlpha = fill * 0.28 + membr * 0.55 + nuc * 0.5;
                        vec3  cellRgb   = cytoColor * fill
                                        + membrColor * membr
                                        + nucColor   * nuc;
                
                        // Альфа-аккумулятор — складываем прозрачно
                        float w = cellAlpha;
                        accumColor += cellRgb * w;
                        accumAlpha  = accumAlpha + w * (1.0 - accumAlpha);
                
                    // ── Фаза 0.30..0.62 — рост + хромосомы ───────
                    } else if (phase < 0.62) {
                        float t = (phase - 0.30) / 0.32;
                        // Клетка вытягивается
                        float elong  = 1.0 + t * 0.65;
                        float squish = 1.0 - t * 0.28;
                        vec2  diff   = uv - cellPos;
                        float along  = dot(diff, divDir);
                        float perp   = dot(diff, vec2(-divDir.y, divDir.x));
                        float d      = length(vec2(along / (baseR * elong),
                                                   perp  / (baseR * squish))) - 1.0;
                        float fill  = smoothstep(0.12, -0.02, d * baseR);
                        float membr = smoothstep(0.03,  0.0,  abs(d * baseR + 0.015));
                
                        // Хромосомы (метафаза) — появляются во второй половине
                        float chromo = 0.0;
                        if (t > 0.45) {
                            float ct = (t - 0.45) / 0.55;
                            for (int ci = -3; ci <= 3; ci++) {
                                float cp = float(ci) * 0.045;
                                float cd = abs(along - cp) / (0.009 + 0.003 * abs(float(ci)));
                                float ca = abs(perp)       / (0.055 + 0.015 * abs(float(ci)));
                                chromo += smoothstep(1.0, 0.0, cd) * smoothstep(1.0, 0.5, ca) * ct;
                            }
                        }
                        chromo = clamp(chromo, 0.0, 1.0);
                        vec3 chromoColor = vec3(0.4, 0.9, 1.0);
                
                        float cellAlpha = fill * 0.25 + membr * 0.55 + chromo * fill * 0.65;
                        vec3  cellRgb   = cytoColor * fill
                                        + membrColor * membr
                                        + chromoColor * chromo * fill;
                
                        float w = cellAlpha;
                        accumColor += cellRgb * w;
                        accumAlpha  = accumAlpha + w * (1.0 - accumAlpha);
                
                    // ── Фаза 0.62..0.88 — деление ─────────────────
                    } else if (phase < 0.88) {
                        float t = (phase - 0.62) / 0.26;
                
                        float sep = baseR * (0.15 + t * 1.35);
                        vec2  c1  = cellPos + divDir * sep;
                        vec2  c2  = cellPos - divDir * sep;
                        float cr  = baseR * (0.6 + t * 0.28);
                
                        float d1 = sdfBlob(uv - c1, cr, id,         phase);
                        float d2 = sdfBlob(uv - c2, cr, id + 0.137, phase);
                
                        float f1 = smoothstep(0.015, -0.005, d1);
                        float f2 = smoothstep(0.015, -0.005, d2);
                        // Убираем перекрытие в перетяжке
                        float pinch = length(uv - cellPos);
                        float groove = smoothstep(baseR * 0.5, 0.0, pinch) * t;
                        float fill   = clamp(f1 + f2, 0.0, 1.0) * (1.0 - groove * 0.7);
                
                        float m1 = smoothstep(0.022, 0.0, abs(d1 + 0.012));
                        float m2 = smoothstep(0.022, 0.0, abs(d2 + 0.012));
                        float membr = clamp(m1 + m2, 0.0, 1.0);
                
                        // Борозда деления — яркая белая линия
                        float gp   = abs(dot(uv - cellPos, vec2(-divDir.y, divDir.x)));
                        float ga   = abs(dot(uv - cellPos, divDir));
                        float grooveLine = smoothstep(0.01, 0.0, gp)
                                         * smoothstep(cr * 0.7, 0.0, ga)
                                         * t * fill;
                
                        // Ядра дочерних клеток
                        float n1 = smoothstep(cr * 0.3, cr * 0.05, length(uv - c1)) * (t > 0.5 ? (t-0.5)*2.0 : 0.0);
                        float n2 = smoothstep(cr * 0.3, cr * 0.05, length(uv - c2)) * (t > 0.5 ? (t-0.5)*2.0 : 0.0);
                        float nuc = clamp(n1 + n2, 0.0, 1.0) * fill;
                
                        float cellAlpha = fill * 0.22 + membr * 0.52 + grooveLine * 0.8 + nuc * 0.55;
                        vec3  cellRgb   = cytoColor  * fill
                                        + membrColor * membr
                                        + vec3(1.0, 1.0, 0.85) * grooveLine
                                        + nucColor   * nuc;
                
                        float w = cellAlpha;
                        accumColor += cellRgb * w;
                        accumAlpha  = accumAlpha + w * (1.0 - accumAlpha);
                
                    // ── Фаза 0.88..1.0 — две дочерних ─────────────
                    } else {
                        float t  = (phase - 0.88) / 0.12;
                        float sep = baseR * 1.5;
                        vec2  c1  = cellPos + divDir * sep;
                        vec2  c2  = cellPos - divDir * sep;
                        float cr  = baseR * 0.88;
                
                        float d1 = sdfBlob(uv - c1, cr, id,         phase);
                        float d2 = sdfBlob(uv - c2, cr, id + 0.137, phase);
                        float f1 = smoothstep(0.015, -0.005, d1);
                        float f2 = smoothstep(0.015, -0.005, d2);
                        float fill = clamp(f1 + f2, 0.0, 1.0);
                
                        float m1 = smoothstep(0.022, 0.0, abs(d1 + 0.012));
                        float m2 = smoothstep(0.022, 0.0, abs(d2 + 0.012));
                        float membr = clamp(m1 + m2, 0.0, 1.0);
                
                        float n1 = smoothstep(cr * 0.3, cr * 0.05, length(uv - c1));
                        float n2 = smoothstep(cr * 0.3, cr * 0.05, length(uv - c2));
                        float nuc = clamp(n1 + n2, 0.0, 1.0) * fill;
                
                        float cellAlpha = fill * 0.25 + membr * 0.55 + nuc * 0.5;
                        vec3  cellRgb   = cytoColor  * fill
                                        + membrColor * membr
                                        + nucColor   * nuc;
                
                        float w = cellAlpha;
                        accumColor += cellRgb * w;
                        accumAlpha  = accumAlpha + w * (1.0 - accumAlpha);
                    }
                }
                
                void main() {
                    vec2 uv = gl_TexCoord[0].st;
                    uv.y    = 1.0 - uv.y;
                
                    float maskAlpha = texture2D(u_other_sampler, uv).a;
                    if (maskAlpha < 0.01) discard;
                
                    vec3 bgColor = texture2D(u_diffuse_sampler, uv).rgb;
                
                    // Большая плотная сетка — много мелких клеток
                    vec2 scaled = uv * u_cell_scale;
                
                    float cyclePeriod = 7.0 / u_pulse_speed;
                
                    vec3  accumColor = vec3(0.0);
                    float accumAlpha = 0.0;
                
                    // Обход 5×5 — охватывает все соседей при плотном масштабе
                    vec2 cellOrigin = floor(scaled);
                    for (int cy = -2; cy <= 2; cy++) {
                        for (int cx = -2; cx <= 2; cx++) {
                            vec2  cell = cellOrigin + vec2(float(cx), float(cy));
                            float id   = hash21(cell);
                            vec2  seed = hash22(cell);
                
                            // Центр клетки — случайно смещён в ячейке, но не у края
                            vec2 cellPos = cell + 0.5 + (seed - 0.5) * 0.55;
                
                            float phase = fract(u_time / cyclePeriod + id);
                
                            cellAt(scaled, cellPos, id, phase, accumColor, accumAlpha);
                        }
                    }
                
                    // Нормализуем аккумулятор
                    vec3 cellColor = (accumAlpha > 0.001)
                        ? accumColor / accumAlpha
                        : vec3(0.0);
                
                    // Тёмный фон + клетки поверх как прозрачный слой
                    float bgBlend = 1.0 - accumAlpha * 0.82;
                    vec3  color   = bgColor * bgBlend + cellColor * accumAlpha * 0.82;
                
                    // Небольшое общее свечение
                    color += vec3(0.0, 0.08, 0.06) * accumAlpha * 0.3;
                
                    gl_FragColor = vec4(color, maskAlpha);
                }
                """;
    }
}