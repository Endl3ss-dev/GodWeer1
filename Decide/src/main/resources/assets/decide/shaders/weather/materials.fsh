#version 150
#moj_import <decide:weather.glsl>
uniform sampler2D SceneColor;
uniform sampler2D SceneDepth;
uniform sampler2D TerrainDepth;
uniform sampler2D TopMap;
in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1,311.7))) * 43758.5453);
}
float noise(vec2 p) {
    vec2 i = floor(p), f = fract(p);
    f = f*f*(3.0-2.0*f);
    return mix(mix(hash(i),hash(i+vec2(1,0)),f.x),
               mix(hash(i+vec2(0,1)),hash(i+vec2(1,1)),f.x),f.y);
}
float dune(vec2 p) {
    return noise(p)*0.60+noise(p*2.3+11.0)*0.28+noise(p*5.1+31.0)*0.12;
}
vec3 positionAt(vec2 uv, float depth) {
    vec4 p = InverseProjection * vec4(uv*2.0-1.0, depth*2.0-1.0, 1.0);
    return p.xyz / p.w;
}
vec3 positionAt(vec2 uv) { return positionAt(uv,texture(SceneDepth,uv).r); }

// atmosphere.glsl uses positionAt/texCoord/noise/hash/SceneDepth, so it must be
// imported only after those are declared above.
#moj_import <decide:atmosphere.glsl>

float bloodHash3(vec3 p) {
    p=fract(p*0.1031);
    p+=dot(p,p.yzx+33.33);
    return fract((p.x+p.y)*p.z);
}
float bloodNoise3(vec3 p) {
    vec3 i=floor(p), f=fract(p);
    f=f*f*(3.0-2.0*f);
    return mix(
        mix(mix(bloodHash3(i),bloodHash3(i+vec3(1,0,0)),f.x),
            mix(bloodHash3(i+vec3(0,1,0)),bloodHash3(i+vec3(1,1,0)),f.x),f.y),
        mix(mix(bloodHash3(i+vec3(0,0,1)),bloodHash3(i+vec3(1,0,1)),f.x),
            mix(bloodHash3(i+vec3(0,1,1)),bloodHash3(i+vec3(1,1,1)),f.x),f.y),f.z);
}

vec3 eclipseColor(vec3 color, float depth) {
    bool sky=depth>=0.999999;
    vec3 view=positionAt(texCoord,sky?0.9999:depth);
    vec3 ray=normalize(mat3(InverseView)*normalize(view));
    float dark=EclipseSettings.x;
    if(sky) {
        float horizon=1.0-smoothstep(-0.04,0.55,ray.y);
        vec3 night=mix(vec3(0.014,0.016,0.052),vec3(0.25,0.14,0.38),horizon);
        color=mix(color,night,0.72+0.27*dark);
        vec2 starUv=vec2(atan(ray.z,ray.x)*92.0,asin(clamp(ray.y,-1.0,1.0))*125.0);
        vec2 cell=floor(starUv);
        float seed=hash(cell);
        vec2 star=fract(starUv)-vec2(hash(cell+17.0),hash(cell+41.0));
        float point=1.0-smoothstep(0.025,0.105,length(star));
        float twinkle=0.74+0.26*sin(CameraTime.w*(1.2+seed*2.0)+seed*41.0);
        color+=vec3(0.66,0.77,1.0)*point*step(1.0-EclipseSettings.z*0.075,seed)
                *EclipseSettings.z*twinkle;
        vec3 center=normalize(vec3(-0.18,0.48,-0.86));
        float angle=acos(clamp(dot(ray,center),-1.0,1.0));
        float radius=0.060;
        float disk=1.0-smoothstep(radius-0.002,radius+0.002,angle);
        float crown=exp(-pow((angle-radius)/0.011,2.0));
        float broad=exp(-max(angle-radius,0.0)*32.0)*(1.0-disk);
        vec3 tangent=normalize(cross(center,vec3(0,1,0)));
        vec3 bitangent=cross(center,tangent);
        float azimuth=atan(dot(ray,bitangent),dot(ray,tangent));
        float rays=pow(0.5+0.5*sin(azimuth*19.0+sin(azimuth*7.0)*1.5),4.0);
        float spikes=exp(-max(angle-radius,0.0)*72.0)*rays;
        color+=vec3(0.63,0.49,1.0)*(crown*1.2+broad*0.18+spikes*0.7)
                *EclipseSettings.y;
        color=mix(color,vec3(0.003,0.004,0.014),disk);
    } else {
        float distance=length(view);
        vec3 shadow=color*vec3(0.64,0.58,0.85)*(1.0-dark*0.52);
        float haze=1.0-exp(-distance*0.006);
        color=mix(color,shadow,dark*0.92);
        color=mix(color,vec3(0.24,0.16,0.39),haze*dark*0.31);
    }
    return clamp(color,0.0,1.0);
}

vec3 meteorColor(vec3 color, float depth) {
    bool sky=depth>=0.999999;
    vec3 view=positionAt(texCoord,sky?0.9999:depth);
    vec3 ray=normalize(mat3(InverseView)*normalize(view));
    if(sky) {
        float horizon=1.0-smoothstep(-0.05,0.52,ray.y);
        vec3 night=mix(vec3(0.008,0.018,0.044),vec3(0.045,0.067,0.12),horizon);
        // Directional 3D noise keeps the nebula seamless at the zenith.
        vec3 drift=vec3(CameraTime.w*0.0008,0.0,CameraTime.w*0.0003);
        float field=bloodNoise3(ray*5.2+drift)*0.62
                +bloodNoise3(ray*12.0-drift*1.4)*0.38;
        float nebula=smoothstep(0.39,0.73,field)*MeteorAtmosphere.w;
        vec3 nebulaColor=mix(vec3(0.060,0.035,0.13),vec3(0.022,0.105,0.15),field);
        night+=nebulaColor*nebula*(0.30+MeteorAtmosphere.z*0.85);
        night+=vec3(0.012,0.022,0.043)*MeteorAtmosphere.z*(0.55+0.45*horizon);
        vec3 horizonFog=vec3(0.074,0.094,0.15)+nebulaColor*0.22;
        night=mix(night,horizonFog,horizon*MeteorAtmosphere.x*0.38);
        color=mix(color,night,0.96);
        vec2 skyUv=vec2(atan(ray.z,ray.x),asin(clamp(ray.y,-1.0,1.0)));
        vec2 starUv=skyUv*vec2(97.0,132.0);
        vec2 cell=floor(starUv);
        float seed=hash(cell);
        vec2 point=fract(starUv)-vec2(hash(cell+13.0),hash(cell+71.0));
        float star=1.0-smoothstep(0.025,0.11,length(point));
        color+=vec3(0.65,0.75,1.0)*star*step(1.0-MeteorSettings.w*0.085,seed)
                *MeteorSettings.w*(1.0-nebula*0.40)
                *(0.78+0.22*sin(CameraTime.w*1.3+seed*37.0));
    } else {
        color=mix(color,color*vec3(0.59,0.69,0.91),0.74);
        vec3 world=(InverseView*vec4(view,1.0)).xyz+CameraTime.xyz;
        float distance=length(view);
        float pockets=bloodNoise3(world*0.034+vec3(CameraTime.w*0.006,0.0,0.0));
        float fogRange=mix(165.0,32.0,MeteorAtmosphere.y);
        float fog=1.0-exp(-max(distance-7.0,0.0)/fogRange
                *MeteorAtmosphere.x*1.8);
        fog=clamp(fog*mix(0.78,1.18,pockets),0.0,0.84);
        vec3 fogColor=mix(vec3(0.055,0.083,0.14),vec3(0.095,0.067,0.17),pockets);
        color=mix(color,fogColor,fog);
    }
    return clamp(color,0.0,1.0);
}

vec3 bloodMoonColor(vec3 color, float depth) {
    bool sky=depth>=0.999999;
    vec3 view=positionAt(texCoord,sky?0.9999:depth);
    vec3 ray=normalize(mat3(InverseView)*normalize(view));
    float red=BloodSky.z;
    if(sky) {
        float horizon=1.0-smoothstep(-0.08,0.62,ray.y);
        vec3 night=mix(vec3(0.025,0.010,0.018),vec3(0.18,0.027,0.041),horizon);
        night=mix(night,night*vec3(1.9,0.60,0.62),red*0.70);
        // Sample the direction in 3D, so cloud bands have no longitude seam or pole.
        vec3 drift=vec3(CameraTime.w*0.0023,0.0,CameraTime.w*0.0008);
        float broad=bloodNoise3(ray*5.5+drift)*0.60
                +bloodNoise3(ray*13.0-drift*1.4)*0.29
                +bloodNoise3(ray*28.0+drift*1.8)*0.11;
        float cloud=smoothstep(0.47-0.20*BloodSky.w,0.82-0.18*BloodSky.w,broad)
                *BloodSky.w;
        vec3 cloudColor=mix(vec3(0.11,0.018,0.028),vec3(0.33,0.047,0.056),broad)
                *(0.6+red*0.5);
        color=mix(night,cloudColor,cloud*0.78);

        vec3 center=normalize(vec3(-0.56,mix(0.18,0.88,BloodWorld.w),-0.76));
        vec3 tangent=normalize(cross(center,vec3(0.0,1.0,0.0)));
        vec3 bitangent=cross(center,tangent);
        float radius=mix(0.045,0.145,BloodSky.x);
        vec2 diskUv=vec2(dot(ray,tangent),dot(ray,bitangent))/radius;
        float radial=length(diskUv);
        float disk=1.0-smoothstep(0.985,1.015,radial);
        float angle=acos(clamp(dot(ray,center),-1.0,1.0));
        float aura=exp(-pow(max(angle-radius,0.0)/max(radius,0.01),2.0)*11.0)
                *(1.0-disk)*BloodSky.y;
        color+=vec3(0.55,0.045,0.064)*aura*0.44;
        if(disk>0.001) {
            float maria=noise(diskUv*2.8+vec2(7.1,2.3))*0.60
                    +noise(diskUv*6.5-vec2(3.5,8.2))*0.28
                    +noise(diskUv*16.0+vec2(5.7,4.9))*0.12;
            float crater=0.0;
            for(int i=0;i<11;i++) {
                float id=float(i);
                vec2 c=vec2(hash(vec2(id,3.4)),hash(vec2(id,9.7)))*1.55-0.775;
                float r=mix(0.055,0.20,hash(vec2(id,17.2)));
                float d=length(diskUv-c);
                crater+=exp(-pow((d-r)/max(r*0.26,0.01),2.0))*0.055
                        -exp(-pow(d/max(r*0.82,0.01),2.0))*0.14;
            }
            float limb=sqrt(max(0.0,1.0-radial*radial));
            float detail=clamp(0.50+maria*0.60+crater,0.18,1.15);
            vec3 moon=vec3(0.96,0.16,0.18)*detail*(0.58+0.42*limb)
                    +vec3(0.25,0.018,0.028)*BloodSky.y;
            moon+=vec3(0.18,0.012,0.014)*pow(max(dot(diskUv,vec2(-0.5,0.6)),0.0),2.0);
            color=mix(color,moon,disk*(1.0-cloud*0.35));
        }
    } else {
        float distance=length(view);
        vec3 tinted=color*vec3(1.0+BloodWorld.y*0.10,
                               1.0-BloodWorld.y*0.38,1.0-BloodWorld.y*0.31);
        color=mix(color,tinted,BloodWorld.y);
        float veil=1.0-exp(-distance*0.007);
        color=mix(color,vec3(0.35,0.045,0.060)*red,
                  veil*BloodWorld.x*0.62);
    }
    return clamp(color,0.0,1.0);
}


vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

float woodSurface(vec3 color) {
    vec3 hsv = rgb2hsv(color);
    float hue = smoothstep(0.012, 0.045, hsv.x) * (1.0 - smoothstep(0.145, 0.205, hsv.x));
    float sat = smoothstep(0.10, 0.23, hsv.y) * (1.0 - smoothstep(0.82, 0.98, hsv.y));
    float lum = smoothstep(0.045, 0.10, hsv.z) * (1.0 - smoothstep(0.72, 0.94, hsv.z));
    return hue * sat * lum;
}

float foliageSurface(vec3 color) {
    vec3 hsv = rgb2hsv(color);
    float hue = smoothstep(0.16, 0.27, hsv.x) * (1.0 - smoothstep(0.48, 0.58, hsv.x));
    float sat = smoothstep(0.14, 0.30, hsv.y);
    float lum = smoothstep(0.035, 0.09, hsv.z) * (1.0 - smoothstep(0.78, 0.96, hsv.z));
    return hue * sat * lum;
}

vec3 autumnWoodColor(vec3 color, vec3 world) {
    float n = noise(world.xz * 2.7 + world.y * 0.13);
    vec3 warm = mix(vec3(0.78, 0.25, 0.035), vec3(0.52, 0.10, 0.015), n);
    float l = dot(color, vec3(0.2126, 0.7152, 0.0722));
    vec3 stained = color * mix(vec3(1.22, 0.72, 0.32), vec3(1.58, 0.56, 0.16), n);
    stained = mix(stained, warm * (0.52 + l * 0.85), 0.36);
    return stained;
}

vec3 autumnFoliageColor(vec3 color, vec3 world) {
    float n = hash(floor(world.xz * 3.0) + floor(world.y * 3.0));
    vec3 warm = mix(vec3(0.98, 0.36, 0.035), vec3(0.66, 0.10, 0.012), n);
    float l = dot(color, vec3(0.2126, 0.7152, 0.0722));
    vec3 stained = color * mix(vec3(1.25, 0.62, 0.18), vec3(1.65, 0.38, 0.08), n);
    stained = mix(stained, warm * (0.42 + l * 1.10), 0.55);
    return stained;
}

float leafLitter(vec3 world, float upward) {
    vec2 cell = floor(world.xz * 7.0);
    vec2 local = fract(world.xz * 7.0) - 0.5;
    float seed = hash(cell);
    float shape = 1.0 - smoothstep(0.055, 0.115, length(local * vec2(1.0, 1.55)));
    // patch — зарезервированное слово GLSL, поэтому leafPatch
    float leafPatch = smoothstep(0.36, 0.78, noise(world.xz * 1.55 + seed * 3.0));
    return shape * leafPatch * (0.18 + 0.55 * upward);
}

vec3 normalAt(vec2 uv, vec3 p) {
    if(Performance.x<1.5) {
        vec3 n=normalize(cross(dFdx(p),dFdy(p)));
        return dot(n,-p)<0.0 ? -n : n;
    }
    vec2 px = 1.0/Screen.xy;
    vec3 left = positionAt(uv-vec2(px.x,0));
    vec3 right = positionAt(uv+vec2(px.x,0));
    vec3 down = positionAt(uv-vec2(0,px.y));
    vec3 up = positionAt(uv+vec2(0,px.y));
    vec3 dx = abs(left.z-p.z)<abs(right.z-p.z) ? p-left : right-p;
    vec3 dy = abs(down.z-p.z)<abs(up.z-p.z) ? p-down : up-p;
    vec3 n = normalize(cross(dx,dy));
    return dot(n,-p)<0.0 ? -n : n;
}

vec3 reflection(vec3 p, vec3 n, vec3 worldNormal) {
    vec3 ray = reflect(normalize(p), n);
    vec3 worldRay = normalize(mat3(InverseView)*ray);
    vec3 sky = mix(vec3(0.20,0.26,0.32),vec3(0.46,0.59,0.72),
                   pow(max(worldRay.y,0.0),0.35)) * (0.10+0.90*Environment.x);
    vec3 start = p+n*0.08;
    float t=0.12, previous=0.0;
    int steps=Performance.x<0.5?12:(Performance.x<1.5?24:40);
    int refinements=Performance.x<0.5?3:(Performance.x<1.5?5:7);
    for (int i=0;i<40;i++) {
        if(i>=steps) break;
        t += (0.10 + float(i)*0.085)*40.0/float(steps);
        vec3 q=start+ray*t;
        vec4 clip=Projection*vec4(q,1.0);
        if(clip.w<=0.0) break;
        vec2 uv=clip.xy/clip.w*0.5+0.5;
        if(any(lessThan(uv,vec2(0.002))) || any(greaterThan(uv,vec2(0.998)))) break;
        float depth=texture(SceneDepth,uv).r;
        if(depth>=0.999999) { previous=t; continue; }
        vec3 scene=positionAt(uv,depth);
        float delta=q.z-scene.z;
        if(i>2 && delta<0.0) {
            float lo=previous,hi=t;
            for(int j=0;j<7;j++) {
                if(j>=refinements) break;
                float mid=(lo+hi)*0.5;
                vec3 m=start+ray*mid;
                vec4 c=Projection*vec4(m,1);
                vec2 u=c.xy/c.w*0.5+0.5;
                if(m.z<positionAt(u).z) hi=mid; else lo=mid;
            }
            vec4 hit=Projection*vec4(start+ray*((lo+hi)*0.5),1);
            vec2 hitUv=hit.xy/hit.w*0.5+0.5;
            vec3 hitPosition=start+ray*((lo+hi)*0.5);
            float error=abs(hitPosition.z-positionAt(hitUv).z);
            if(error>0.15+t*0.01) { previous=t; continue; }
            float edgeFade=smoothstep(0.0,0.08,min(min(hitUv.x,hitUv.y),min(1.0-hitUv.x,1.0-hitUv.y)));
            return mix(sky,texture(SceneColor,hitUv).rgb,edgeFade);
        }
        previous=t;
    }
    return sky;
}

float wetField(vec2 p) {
    return noise(p*0.21)*0.70+noise(p*0.77)*0.30;
}
vec3 ripple(vec2 p) {
    if(Effects.x<0.001 || Performance.w<0.001) return vec3(0);
    const float scale=1.45;
    vec2 cell=floor(p*scale), local=fract(p*scale);
    float pixelWidth=clamp(length(fwidth(p))*scale,0.008,0.12);
    vec2 gradient=vec2(0);
    float highlight=0.0;
    for(int x=-1;x<=1;x++) for(int y=-1;y<=1;y++) {
        vec2 offset=vec2(x,y);
        float seed=hash(cell+offset);
        if(seed>min(1.0,0.10+Effects.x*0.90)) continue;
        vec2 center=vec2(0.15)+vec2(seed,hash(cell+offset+71.0))*0.70;
        vec2 delta=local-offset-center;
        float distance=length(delta);
        float age=fract(CameraTime.w*(0.58+seed*0.25)+seed*19.0);
        float radius=age*0.88;
        float q=distance-radius;
        if(q>0.08 || q< -0.31) continue;
        vec2 impact=(cell+offset+center)/scale;
        float threshold=mix(0.88,0.08,Effects.w);
        float inside=smoothstep(threshold-0.07,threshold+0.07,wetField(impact));
        float fade=smoothstep(0.0,0.08,age)*pow(1.0-age,1.35)*inside;
        float ringWidth=max(0.018,pixelWidth*1.25);
        float first=exp(-pow(q/ringWidth,2.0));
        float second=exp(-pow((q+0.115)/ringWidth,2.0))*smoothstep(0.22,0.42,age);
        float third=exp(-pow((q+0.225)/ringWidth,2.0))*smoothstep(0.48,0.67,age);
        float crests=(first+second*0.55+third*0.25)*fade;
        float slopes=(q*first+(q+0.115)*second*0.55+(q+0.225)*third*0.25)/ringWidth;
        gradient+=delta/max(distance,0.01)*slopes*fade;
        highlight+=crests;
    }
    float detailFade=1.0-smoothstep(0.09,0.16,pixelWidth);
    return vec3(gradient*0.12,highlight*0.68)*Effects.x*Performance.w*detailFade;
}


/**
 * Круги от капель по воде: ячейки с одной каплей, три расходящихся кольца
 * и короткая вспышка в точке удара. Плотность зависит от силы дождя (Effects.x).
 */
vec3 waterRipples(vec2 p) {
    if(Effects.x<0.001) return vec3(0);
    const float scale=1.9;
    vec2 cell=floor(p*scale), local=fract(p*scale);
    float pixelWidth=clamp(length(fwidth(p))*scale,0.006,0.09);
    vec2 gradient=vec2(0.0);
    float highlight=0.0;
    for(int x=-1;x<=1;x++) for(int y=-1;y<=1;y++) {
        vec2 offset=vec2(x,y);
        float seed=hash(cell+offset);
        if(seed>0.30+Effects.x*0.55) continue;
        vec2 center=vec2(0.20)+vec2(seed,hash(cell+offset+13.0))*0.60;
        vec2 delta=local-offset-center;
        float dist=length(delta);
        float life=fract(CameraTime.w*(0.85+seed*0.35)+seed*37.0);
        float radius=life*1.15;
        float q=dist-radius;
        if(q>0.10 || q<-0.45) continue;
        float fade=smoothstep(0.0,0.05,life)*pow(1.0-life,1.6);
        float ringWidth=max(0.012,pixelWidth*1.1);
        float first=exp(-pow(q/ringWidth,2.0));
        float second=exp(-pow((q+0.14)/ringWidth,2.0))*smoothstep(0.18,0.40,life);
        float third=exp(-pow((q+0.29)/ringWidth,2.0))*smoothstep(0.45,0.70,life);
        float crests=(first+second*0.5+third*0.22)*fade;
        float slopes=(q*first+(q+0.14)*second*0.5+(q+0.29)*third*0.22)/ringWidth;
        float flash=exp(-pow(dist/0.07,2.0))*(1.0-smoothstep(0.0,0.09,life));
        gradient+=delta/max(dist,0.01)*slopes*fade;
        highlight+=crests+flash*0.55;
    }
    float detailFade=1.0-smoothstep(0.10,0.18,pixelWidth);
    return vec3(gradient*0.10,highlight)*detailFade;
}

void main() {
    vec3 color=texture(SceneColor,texCoord).rgb;
    float depth=texture(SceneDepth,texCoord).r;
    if(abs(Environment.w-6.0)<0.1) { fragColor=vec4(eclipseColor(color,depth),1.0); return; }
    if(abs(Environment.w-7.0)<0.1) { fragColor=vec4(meteorColor(color,depth),1.0); return; }
    if(abs(Environment.w-8.0)<0.1) { fragColor=vec4(bloodMoonColor(color,depth),1.0); return; }
    float terrainDepthVal=texture(TerrainDepth,texCoord).r;

    float storm=Performance.y*Environment.z;
    bool winter=abs(Environment.w-2.0)<0.1;
    vec3 snowMist=vec3(0.58,0.68,0.81)*(0.18+Environment.x*0.82);

    // Рассвет и Пыль обрабатывают в том числе небо, поэтому атмосфера должна
    // вызываться ДО раннего возврата для пикселей неба (depth == 1.0).
    if(Environment.w>2.5 && Environment.w<4.5) {
        fragColor=vec4(atmosphere(color,depth),1.0);
        return;
    }

    if(depth>=0.999999) {
        if(winter) color=mix(color,snowMist,0.18+storm*0.35);
        fragColor=vec4(color,1.0);
        return;
    }

    vec3 p=positionAt(texCoord,depth);

    vec3 terrainP=positionAt(texCoord,terrainDepthVal);
    bool terrain=abs(p.z-terrainP.z)<max(0.06,abs(p.z)*0.0010);
    vec3 n=normalAt(texCoord,p);
    vec3 worldNormal=normalize(mat3(InverseView)*n);
    vec3 worldPosition=(InverseView*vec4(p,1.0)).xyz+CameraTime.xyz;
    float upward=smoothstep(0.75,0.98,worldNormal.y);

    // ── Дождь по воде ──
    // TerrainDepth снимается ДО отрисовки воды (BEFORE_ENTITIES), поэтому у
    // пикселей водной глади terrain == false — так вода и отличается от земли.
    if(!terrain && !winter && Effects.x>0.01 && upward>0.55) {
        float ringVis=clamp(Performance.w,0.0,1.0);
        if(ringVis>0.01) {
            vec3 drops=waterRipples(worldPosition.xz);
            float rim=clamp(drops.z,0.0,2.0);
            vec3 tint=mix(vec3(0.55,0.68,0.82),vec3(0.95,0.98,1.0),Environment.x*0.35);
            color+=tint*rim*0.70*ringVis;
        }
    }

    if(winter) {
        vec3 snow=vec3(0.94,0.96,1.0);
        float uneven=noise(worldPosition.xz*3.1)*0.10;
        float snowEdge=noise(worldPosition.xz*0.71+17.0);
        float side=1.0-upward;
        float lip=1.0-smoothstep(0.09,0.33,snowEdge+uneven);
        float sideCover=side*lip*0.93;

        vec2 mapUv=(worldPosition.xz-vec2(MeshOrigin.x-96.0,MeshOrigin.z-96.0))/192.0;
        vec4 topSample=texture(TopMap,clamp(mapUv,0.0,1.0));
        bool inMap=all(greaterThanEqual(mapUv,vec2(0.0))) && all(lessThanEqual(mapUv,vec2(1.0)));
        float mapTopY=(topSample.r*255.0+topSample.g*65280.0)-512.0;
        float exposed=(!inMap || topSample.b<0.5) ? 1.0
                : (1.0-step(0.8,abs(worldPosition.y-mapTopY)));
        float upExposure=SurfaceSettings.x>0.5 ? 1.0
                : mix(1.0,exposed,step(Climate.y,0.5));
        float cover=upward*0.96*upExposure;
        if(terrain) cover=max(cover,sideCover*exposed);

        float luma=dot(color,vec3(0.299,0.587,0.114));
        float grain=noise(worldPosition.xz*11.0)*0.045+noise(worldPosition.xz*31.0)*0.02;
        vec3 snowTint=snow*(0.74+0.44*clamp(luma*2.2,0.0,1.0))*(0.97+grain);
        color=mix(color,snowTint,cover);
        float sparkle=step(0.993,hash(floor(worldPosition.xz*24.0)+floor(worldPosition.y*24.0)));
        color+=vec3(0.50,0.60,0.78)*sparkle*cover*0.12;

        // ---- Snowfall: сугробы, прожимание, следы ----
        if(SnowfallSettings.x>0.5) {
            float snowDepth=clamp(SnowfallSettings.y,0.0,1.0);
            float drift=clamp(SnowfallSettings.z,0.0,1.0);
            bool crunch=SnowfallSettings.w>0.5;
            float footprintDepth=clamp(SnowfallSettings2.x,0.0,1.0);

            float driftHeight=snowDepth*3.0;
            vec2 driftUv=worldPosition.xz*1.35+vec2(drift*8.0);
            float driftNoise=noise(driftUv);
            float localDriftHeight=driftHeight*mix(0.30,1.0,driftNoise);
            vec3 terrainWorld=(InverseView*vec4(terrainP,1.0)).xyz+CameraTime.xyz;
            float heightAboveGround=max(worldPosition.y-terrainWorld.y,0.0);
            float groundGap=abs(p.z-terrainP.z);
            bool inDrift=(heightAboveGround<localDriftHeight || groundGap<0.40) && upward>0.10 && snowDepth>0.01;

            if(inDrift) {
                float driftCover=1.0-smoothstep(localDriftHeight*0.35,localDriftHeight,heightAboveGround);
                if(groundGap<0.40) driftCover=1.0;

                vec2 moundUv=worldPosition.xz*0.20+vec2(drift*7.0);
                float mC=dune(moundUv);
                float mX=dune(moundUv+vec2(0.30,0.0));
                float mZ=dune(moundUv+vec2(0.0,0.30));
                vec3 driftNormal=normalize(vec3((mC-mX)*3.0,1.0,(mC-mZ)*3.0));
                float shade=clamp(dot(driftNormal,normalize(vec3(-0.45,0.78,-0.42))),0.0,1.0);
                vec3 driftColor=mix(vec3(0.78,0.84,0.95),vec3(1.01,1.01,1.02),smoothstep(0.55,0.95,shade));
                float windRipple=sin(dot(worldPosition.xz,vec2(0.74,0.67))*3.2+mC*5.0)*0.5+0.5;
                driftColor*=0.985+0.025*windRipple;
                color=mix(color,driftColor,clamp(driftCover,0.0,1.0)*0.90*clamp(snowDepth*2.0,0.0,1.0));

                if(footprintDepth>0.0) {
                    vec2 fp=worldPosition.xz*2.2;
                    vec2 fpCell=floor(fp);
                    vec2 fpLocal=fract(fp)-0.5;
                    float fpSide=step(0.5,hash(fpCell));
                    vec2 fpOffset=vec2((fpSide-0.5)*0.34,0.0);
                    float dent=1.0-smoothstep(0.05,0.26,length((fpLocal-fpOffset)*vec2(1.0,1.5)));
                    float rim=1.0-smoothstep(0.22,0.32,length((fpLocal-fpOffset)*vec2(1.0,1.5)));
                    float trail=1.0-smoothstep(4.0,9.0,length(worldPosition.xz-CameraTime.xz));
                    float mask=clamp(dent*trail*footprintDepth,0.0,1.0);
                    float rimMask=clamp((rim-dent)*trail*footprintDepth,0.0,1.0);
                    color=mix(color,color*vec3(0.80,0.85,0.94),mask*0.65);
                    color+=vec3(0.03,0.035,0.045)*rimMask;
                }
            }

            float ambientDrift=1.0-smoothstep(0.0,3.0,heightAboveGround);
            color=mix(color,vec3(0.90,0.94,1.0),ambientDrift*upward*0.05*snowDepth);
        }
    } else if(terrain && upward>0.01 && (Effects.z>0.5 || Effects.x>0.0)) {
        float field=wetField(worldPosition.xz);
        float threshold=mix(0.88,0.08,Effects.w);
        float pool=smoothstep(threshold-0.09,threshold+0.09,field)*upward;
        if(Effects.w<0.001) pool=0.0;
        color*=1.0-upward*0.22*Effects.z;
        // Мокрый пол и лужи есть везде, а вот кольца от капель — только под
        // открытым небом: смотрим в TopMap, есть ли над точкой блоки.
        vec2 wmUv=(worldPosition.xz-vec2(MeshOrigin.x-96.0,MeshOrigin.z-96.0))/192.0;
        vec4 wmSample=texture(TopMap,clamp(wmUv,0.0,1.0));
        bool wmInside=all(greaterThanEqual(wmUv,vec2(0.0)))&&all(lessThanEqual(wmUv,vec2(1.0)));
        float wmCovered=0.0;
        if(wmInside&&wmSample.b>0.5) {
            float wmTopY=(wmSample.r*255.0+wmSample.g*65280.0)-512.0;
            wmCovered=step(worldPosition.y+0.6,wmTopY);
        }
        if(pool>0.01) {
            float drops=1.0-wmCovered;
            vec3 rings=ripple(worldPosition.xz)*drops;
            vec3 waterNormal=normalize(worldNormal+vec3(rings.x,0,rings.y));
            vec3 viewNormal=normalize(mat3(View)*waterNormal);
            float fresnel=0.04+0.96*pow(1.0-max(dot(viewNormal,normalize(-p)),0.0),5.0);
            vec3 reflected=reflection(p,viewNormal,waterNormal);
            float reflectivity=pool*(0.32+0.62*fresnel);
            color=mix(color*mix(1.0,0.66,pool),reflected,reflectivity);
            float ringContrast=clamp(rings.z*pool,0.0,0.82);
            color=mix(color,vec3(0.84,0.91,0.98)*(0.42+Environment.x*0.58),ringContrast);
        }
    }

    if(SurfaceSettings.y>0.5) {
        float wood=woodSurface(color);
        color=mix(color,autumnWoodColor(color,worldPosition),wood*0.92);
    }
    if(SurfaceSettings.z>0.5) {
        float foliage=foliageSurface(color);
        color=mix(color,autumnFoliageColor(color,worldPosition),foliage*0.88);
        float litter=leafLitter(worldPosition,upward);
        vec3 litterColor=mix(vec3(0.72,0.24,0.035),vec3(0.96,0.63,0.10),
                hash(floor(worldPosition.xz*7.0)));
        color=mix(color,litterColor,litter*0.62);
    }
    if(SurfaceSettings.w>0.5) {
        float wood=woodSurface(color);
        color=mix(color,color*vec3(0.84,0.93,1.05)+vec3(0.028,0.046,0.068),wood*0.55);
    }

    float haze=(1.0-exp(-length(p)*0.0028))*Effects.x;    color=mix(color,vec3(0.43,0.48,0.53)*(0.15+Environment.x*0.85),haze*0.25);
    if(winter) {
        color*=vec3(0.99,0.995,1.0);
        float snowHaze=1.0-exp(-length(p)*(0.0015+storm*0.012));
        color=mix(color,snowMist,snowHaze*(0.30+storm*0.45));
    }
    fragColor=vec4(clamp(color,0.0,1.0),1.0);
}
