#version 120
            
            uniform sampler2D font;
            uniform vec4 inColor;
            uniform float width;
            uniform float maxWidth;
            
            void main() {
                float fadeStart = 0.7;
                float fadeEnd = 1.0;
                float f = clamp(smoothstep(fadeStart, fadeEnd, 1 - (gl_FragCoord.x - maxWidth) / width), 0, 1);
                vec2 pos = gl_TexCoord[0].xy;
                vec4 color = texture2D(font, pos);
                if (color.a > 0) {
                    color.a = color.a * f;
                }
                gl_FragColor = color * inColor;
            }