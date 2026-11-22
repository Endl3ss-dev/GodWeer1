#version 150
#moj_import <decide:weather.glsl>
in vec2 dropUv;
in float opacity;
in float snowMode;
in float sparkle;
in float splashMode;
in float petalLight;
out vec4 fragColor;

vec3 hsvToRgb(float hue, float saturation) {
    vec3 channels=clamp(abs(fract(hue+vec3(0.0,0.666667,0.333333))*6.0-3.0)-1.0,0.0,1.0);
    return mix(vec3(1.0),channels,saturation);
}

void main() {
    vec2 p=dropUv*2.0-1.0;
    float alpha;
    vec3 tint;
    if(snowMode>21.5) {
        // Fifty atmospheric modes share ten silhouettes; hue, tier and scale
        // make every effect visually distinct.
        float variant=snowMode-22.0;
        float style=mod(floor(variant),10.0);
        float tier=floor(variant/10.0);
        float hue=fract(0.04+tier*0.19+style*0.083+ExtraAppearance.x*0.24);
        vec3 baseCol=hsvToRgb(hue,0.64);
        vec3 altCol=hsvToRgb(fract(hue+0.37),0.72);
        float r=length(p);
        float angle=atan(p.y,p.x);
        if(style<0.5) {
            // Ice: six-armed crystal.
            float arms=pow(abs(cos(angle*3.0)),16.0);
            float core=exp(-r*r*16.0);
            alpha=(arms*(1.0-smoothstep(0.20,0.92,r))*0.82+core*0.58)
                    *(1.0-smoothstep(0.78,1.0,r));
            tint=mix(baseCol,vec3(0.94,0.98,1.0),0.38+0.30*sparkle);
        } else if(style<1.5) {
            // Ember: hot core with a smoky halo.
            float core=exp(-dot(p,p)*38.0);
            float halo=exp(-dot(p,p)*5.0)*(1.0-smoothstep(0.55,1.0,r));
            alpha=(core*0.88+halo*0.30)*(0.55+0.45*sparkle);
            tint=mix(baseCol,altCol,sparkle);
        } else if(style<2.5) {
            // Petal: soft asymmetric leaf.
            float width=(0.55+0.18*cos(p.y*9.0+sparkle*8.0))*(1.0-abs(p.y)*0.72);
            float leaf=(1.0-smoothstep(width-0.045,width+0.045,abs(p.x)))
                    *smoothstep(-1.0,-0.88,p.y)*(1.0-smoothstep(0.92,1.0,p.y));
            float vein=exp(-p.x*p.x*190.0)*(1.0-smoothstep(0.45,0.92,abs(p.y)));
            alpha=leaf*0.86;
            tint=mix(baseCol,altCol,smoothstep(-0.8,0.85,p.y));
            tint=mix(tint,baseCol*0.45,vein*0.36);
        } else if(style<3.5) {
            // Rain: wind-tilted streak.
            float taper=smoothstep(-1.0,-0.8,p.y)*(1.0-smoothstep(0.78,1.0,p.y));
            alpha=exp(-p.x*p.x*9.0)*taper*0.92;
            tint=mix(baseCol,altCol,0.35+0.35*sparkle);
        } else if(style<4.5) {
            // Mist: broad, almost transparent puff.
            alpha=exp(-dot(p,p)*2.1)*(1.0-smoothstep(0.68,1.0,r))*0.34;
            tint=mix(baseCol,altCol,0.35+0.35*sparkle);
        } else if(style<5.5) {
            // Void: dark core inside a luminous ring.
            float ring=exp(-pow((r-0.48)/0.16,2.0));
            float core=1.0-smoothstep(0.12,0.34,r);
            alpha=(ring*0.52+core*0.22)*(1.0-smoothstep(0.82,1.0,r));
            tint=mix(baseCol,vec3(0.02,0.01,0.04),core*0.72);
        } else if(style<6.5) {
            // Prism: faceted diamond.
            float diamond=abs(p.x)*1.28+abs(p.y);
            float facet=1.0-smoothstep(0.10,0.86,diamond);
            alpha=facet*(0.52+0.34*sparkle);
            tint=mix(baseCol,altCol,0.30+0.55*fract(sparkle+angle));
        } else if(style<7.5) {
            // Ash: ragged flake with a darker heart.
            float wobble=1.0+0.14*sin(angle*8.0+sparkle*17.0);
            float flake=1.0-smoothstep(0.18,0.82,r*wobble);
            alpha=flake*(0.48+0.34*(1.0-r));
            tint=mix(baseCol*0.55,altCol,sparkle*0.55);
        } else if(style<8.5) {
            // Aurora: elongated diamond ribbon.
            float diamond=abs(p.x)*0.72+abs(p.y)*1.45;
            alpha=(1.0-smoothstep(0.12,0.95,diamond))*(0.34+0.40*sparkle);
            tint=mix(baseCol,altCol,0.25+0.55*sparkle);
        } else {
            // Sun: four-point flare.
            float cross=exp(-p.x*p.x*90.0)*exp(-abs(p.y)*2.4)
                    +exp(-p.y*p.y*90.0)*exp(-abs(p.x)*2.4);
            float core=exp(-dot(p,p)*48.0);
            alpha=clamp(cross*0.65+core*0.72,0.0,1.0);
            tint=mix(baseCol,vec3(1.0,0.96,0.82),0.35+0.35*sparkle);
        }
        alpha=clamp(alpha,0.0,1.0);
        tint*=0.72+0.28*Environment.x;
    } else if(snowMode>20.5) {
        // Toxic spores: soft green nucleus with a ragged halo.
        float r=length(p);
        float wobble=1.0+0.12*sin(atan(p.y,p.x)*7.0+sparkle*13.0);
        float core=1.0-smoothstep(0.18,0.78,r*wobble);
        float halo=exp(-r*r*3.2);
        alpha=(core*0.58+halo*0.22)*(1.0-smoothstep(0.80,1.0,r));
        tint=mix(vec3(0.22,0.55,0.08),vec3(0.78,0.96,0.30),smoothstep(0.1,0.9,sparkle));
        tint*=0.66+0.34*Environment.x;
    } else if(snowMode>18.5) {
        // Frost: six-armed ice crystal.
        float r=length(p);
        float angle=atan(p.y,p.x);
        float arms=pow(abs(cos(angle*3.0)),18.0);
        float core=exp(-r*r*18.0);
        alpha=(arms*(1.0-smoothstep(0.20,0.92,r))*0.80+core*0.60)
                *(1.0-smoothstep(0.78,1.0,r));
        tint=mix(vec3(0.48,0.68,0.94),vec3(0.90,0.98,1.0),sparkle);
        tint*=0.78+0.22*Environment.x;
    } else if(snowMode>17.5) {
        // Aurora: a soft diamond sparkle, teal-to-violet.
        float diamond=abs(p.x)*1.35+abs(p.y);
        alpha=(1.0-smoothstep(0.12,0.95,diamond))*(0.34+0.36*sparkle);
        tint=mix(vec3(0.12,0.92,0.58),vec3(0.55,0.38,1.0),0.35+0.45*sparkle);
        tint*=0.72+0.28*Environment.x;
    } else if(snowMode>16.5) {
        // Firefly: warm green core, wide soft halo.
        float r2=dot(p,p);
        float core=exp(-r2*55.0);
        float halo=exp(-r2*8.0)*(1.0-smoothstep(0.62,1.0,sqrt(r2)));
        alpha=(core*0.95+halo*0.34)*(0.55+0.45*sparkle);
        tint=mix(vec3(0.74,1.0,0.28),vec3(1.0,0.92,0.42),sparkle);
    } else if(snowMode>15.5) {
        // Pollen: tiny golden grain with a warm halo.
        float r2=dot(p,p);
        float core=exp(-r2*45.0);
        float halo=exp(-r2*6.0)*(1.0-smoothstep(0.55,1.0,sqrt(r2)));
        alpha=(core*0.80+halo*0.20)*0.80;
        tint=mix(vec3(0.95,0.72,0.22),vec3(1.0,0.96,0.60),sparkle);
        tint*=0.76+0.24*Environment.x;
    } else if(snowMode>14.5) {
        // Sand: wind-stretched tan streak.
        float streak=exp(-p.x*p.x*4.0)*exp(-p.y*p.y*42.0);
        float grain=exp(-dot(p,p)*35.0);
        alpha=streak*0.72+grain*0.28;
        tint=mix(vec3(0.52,0.34,0.16),vec3(0.90,0.68,0.36),sparkle);
    } else if(snowMode>13.5) {
        // Ash: irregular grey flake with a darker center.
        float r=length(p);
        float wobble=1.0+0.12*sin(atan(p.y,p.x)*9.0+sparkle*17.0);
        float flake=1.0-smoothstep(0.22,0.82,r*wobble);
        alpha=flake*(0.45+0.35*(1.0-r));
        tint=mix(vec3(0.22,0.22,0.22),vec3(0.67,0.62,0.56),sparkle);
        tint*=0.74+0.26*Environment.x;
    } else if(snowMode>11.5) {
        // Mist: large, extremely soft fog puff.
        float r2=dot(p,p);
        alpha=exp(-r2*2.4)*(1.0-smoothstep(0.70,1.0,sqrt(r2)))*0.30;
        tint=mix(vec3(0.62,0.69,0.78),vec3(0.84,0.90,0.96),sparkle);
        tint*=0.78+0.22*Environment.x;
    } else if(snowMode>10.5) {
        // Осень: резной лист с прожилкой и тёплым градиентом.
        float width=(0.78+0.13*cos(p.y*17.0+sparkle*8.0))*(1.0-abs(p.y));
        float leaf=(1.0-smoothstep(width-0.045,width+0.045,abs(p.x)))
                *smoothstep(-1.0,-0.91,p.y)*(1.0-smoothstep(0.90,1.0,p.y));
        float vein=exp(-p.x*p.x*210.0)*(1.0-smoothstep(0.55,0.94,abs(p.y)));
        float edge=1.0-smoothstep(width-0.10,width,abs(p.x));
        alpha=leaf*(0.76+0.20*edge);
        tint=mix(vec3(0.72,0.16,0.035),vec3(1.0,0.66,0.10),
                smoothstep(-0.8,0.85,p.y)+0.22*sparkle);
        tint=mix(tint,vec3(0.34,0.08,0.02),vein*0.42);
        tint*=petalLight*(0.72+0.28*Environment.x);
    } else if(snowMode>9.5) {
        // Весна: маленький пятилепестковый цветок, а не лепесток сакуры.
        float angle=atan(p.y,p.x);
        float r=length(p);
        float flower=0.64+0.16*cos(angle*5.0+sparkle*2.0);
        alpha=(1.0-smoothstep(flower-0.06,flower+0.06,r))
                *(0.80+0.18*cos(angle*5.0));
        float core=1.0-smoothstep(0.06,0.17,r);
        tint=mix(vec3(1.0,0.74,0.84),vec3(1.0,0.965,0.94),
                smoothstep(0.25,0.95,cos(angle*5.0)));
        tint=mix(tint,vec3(1.0,0.82,0.28),core*0.92);
        tint*=petalLight*(0.82+0.18*Environment.x);
        alpha=max(alpha,core*0.88);
    } else if(snowMode>8.5) {
        float radius=length(p);
        float core=exp(-radius*radius*18.0);
        float halo=exp(-radius*radius*3.2);
        alpha=(core*0.56+halo*0.12)*(1.0-smoothstep(0.78,1.0,radius));
        tint=mix(vec3(0.77,0.12,0.08),vec3(1.0,0.48,0.19),sparkle);
    } else if(snowMode>7.5) {
        float taper=smoothstep(-1.0,-0.75,p.y)*(1.0-smoothstep(0.82,1.0,p.y));
        float tail=exp(-p.x*p.x*24.0)*taper;
        float glow=exp(-p.x*p.x*4.5)*taper;
        float head=exp(-p.x*p.x*105.0-pow(p.y-0.66,2.0)*18.0);
        alpha=(tail*0.38+glow*0.10+head*0.92)*(0.58+MeteorSettings.y*0.72);
        tint=mix(vec3(1.0,0.38,0.17),vec3(1.0,0.96,0.77),
                smoothstep(-0.7,0.85,p.y)+head*0.15);
    } else if(snowMode>5.5) {
        float radius=length(p);
        float core=exp(-radius*radius*16.0);
        float halo=exp(-radius*radius*3.0);
        alpha=(core*0.78+halo*0.20)*(1.0-smoothstep(0.73,1.0,radius));
        tint=mix(vec3(1.0,0.65,0.31),vec3(1.0,0.94,0.74),sparkle)
                *(0.66+MeteorSettings.y*0.58);
    } else if(snowMode>3.5) {
        float radius=length(p);
        float core=exp(-radius*radius*18.0);
        float haze=exp(-radius*radius*3.8);
        alpha=(core*0.55+haze*0.17)*(1.0-smoothstep(0.72,1.0,radius));
        tint=mix(vec3(0.36,0.26,0.60),vec3(0.83,0.76,1.0),sparkle);
    } else if(snowMode>2.5) {
        // A broad, slightly asymmetric petal with a split tip and narrow base.
        vec2 q=p;
        q.x+=0.055*(1.0-q.y*q.y)*sin(sparkle*19.0);
        float t=clamp((q.y+0.96)*0.5,0.0,1.0);
        float halfWidth=(0.10+0.91*pow(max(sin(t*3.14159),0.0),0.72))
                *(0.92+0.12*sparkle);
        float notch=0.23*exp(-q.x*q.x*65.0)*smoothstep(0.52,0.91,q.y);
        float edge=min(halfWidth-abs(q.x),min(q.y+0.95,0.96-notch-q.y));
        alpha=smoothstep(-0.035,0.07,edge)*0.88;
        float hue=fract(SakuraAppearance.x+(sparkle-0.5)*0.025);
        vec3 pigment=hsvToRgb(hue,clamp(SakuraAppearance.y*(0.78+0.35*sparkle),0.0,1.0));
        float blush=smoothstep(-0.74,0.86,q.y);
        tint=mix(vec3(1.0,0.975,0.960),pigment,0.35+0.48*blush);
        float vein=exp(-pow(q.x+0.045*q.y,2.0)*175.0)*(1.0-smoothstep(0.60,0.94,q.y));
        tint=mix(tint,pigment*vec3(0.92,0.72,0.77),vein*0.13);
        tint*=petalLight*(0.74+0.26*Environment.x);
        alpha*=0.90+0.10*petalLight;
    } else if(snowMode>1.5) {
        float r2=dot(p,p);
        alpha=exp(-r2*5.8)*(1.0-smoothstep(0.72,1.0,r2))
                *(0.40+0.22*sparkle);
        tint=mix(vec3(0.77,0.69,0.56),vec3(1.0,0.97,0.88),sparkle)
                *(0.60+0.40*Environment.x);
    } else if(snowMode>0.5) {
        float radius=length(p);
        float softFlake=exp(-radius*radius*4.5)*(1.0-smoothstep(0.55,0.95,radius));
        alpha=softFlake*0.78;
        tint=mix(vec3(0.72,0.78,0.89),vec3(0.98,0.99,1.0),Environment.x)*0.95;
    } else {
        float taper=smoothstep(-1.0,-0.65,p.y)*(1.0-smoothstep(0.54,1.0,p.y));
        float shaft=exp(-p.x*p.x*11.0)*taper;
        float highlight=exp(-(p.x+0.13)*(p.x+0.13)*85.0)*taper;
        alpha=shaft*(0.34+0.16*sparkle)+highlight*0.10;
        tint=mix(vec3(0.31,0.39,0.47),vec3(0.76,0.84,0.91),Environment.x)
                +vec3(0.11,0.14,0.18)*highlight;
    }
    alpha*=opacity;
    if(splashMode>0.5) {
        float bead=1.0-smoothstep(0.15,0.9,dot(p,p));
        float glint=exp(-dot(p-vec2(-0.18,0.25),p-vec2(-0.18,0.25))*28.0);
        alpha=bead*opacity*0.72;
        tint=mix(vec3(0.27,0.38,0.50),vec3(0.74,0.84,0.93),Environment.x)+glint*0.22;
    }
    if(alpha<0.004) discard;
    fragColor=vec4(tint,alpha);
}
