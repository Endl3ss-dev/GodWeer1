// Screen-space sunlight and distance fog; no extra full-resolution render target.
// The volumetric sun rays are ported from the Atmosphere Fog post effect:
// a depth-occlusion march toward the sun, an angular falloff, plus a sun
// disc/streak, all screen-blended so they read as real shafts through terrain.
vec3 atmosphere(vec3 color, float depth) {
    bool sky=depth>=0.999999;
    vec3 view=positionAt(texCoord,sky?0.9999:depth);
    vec3 ray=normalize(mat3(InverseView)*normalize(view));
    vec3 world=(InverseView*vec4(view,1.0)).xyz+CameraTime.xyz;
    float distance=sky?Environment.y*3.0:length(view);

    if(Environment.w>3.5 && Environment.w<4.5) {
        float strength=Climate.x;
        vec3 gold=vec3(1.0,0.73,0.32);
        float horizon=exp(-abs(ray.y)*24.0);
        if(sky) {
            vec3 upper=mix(vec3(0.15,0.19,0.28),vec3(0.32,0.39,0.49),strength);
            float streak=noise(vec2(ray.x*8.0+ray.z*4.0,ray.y*3.0)+CameraTime.w*0.002);
            return mix(upper,gold,horizon*(0.65+0.35*strength))
                +vec3(0.15,0.12,0.07)*pow(streak,3.0)*exp(-abs(ray.y)*3.0)*strength;
        }
        vec3 lit=1.0-exp(-color*(1.3+strength));
        float edge=smoothstep(Environment.y*0.40,Environment.y*0.95,distance);
        return mix(mix(color,lit,strength),gold,edge*strength*0.72);
    }

    float elevation=mix(0.025,0.22,Climate.z)+sin(CameraTime.w*0.008)*0.008;
    vec3 sun=vec3(-cos(elevation),sin(elevation),0.0);
    float toward=max(dot(ray,sun),0.0);
    float strength=Atmosphere.x;
    vec3 warm=vec3(1.0,0.63,0.27);
    if(sky) {
        float height=smoothstep(-0.04,0.65,ray.y);
        vec3 dawn=mix(vec3(0.97,0.62,0.39),vec3(0.34,0.60,0.85),height);
        float clouds=noise(vec2(ray.x*5.0,ray.y*17.0)+vec2(CameraTime.w*0.003,0));
        dawn=mix(dawn,vec3(0.93,0.81,0.69),smoothstep(0.55,0.82,clouds)*0.28);
        color=mix(color,dawn,0.85);
        float halo=exp((toward-1.0)*70.0);
        color+=warm*halo*strength*0.22;
    } else {
        vec3 lit=1.0-exp(-color*(1.4+strength));
        lit=lit*vec3(1.06,1.0,0.91)+vec3(0.045,0.028,0.01)*strength;
        color=mix(color,lit,strength);
        float veil=(1.0-exp(-distance*0.004))*strength;
        color=mix(color,vec3(0.96,0.77,0.53),veil*0.32);
    }

    // ---- Sun glow + volumetric rays (Atmosphere Fog) ----
    vec4 viewSun=View*vec4(sun,0.0);
    float sunVisibility=clamp(-viewSun.z*4.0,0.0,1.0);
    if(sunVisibility>0.001) {
        vec4 clip=Projection*viewSun;
        if(clip.w>0.0001) {
            vec2 sunUv=clip.xy/clip.w*0.5+0.5;

            float verticalDelta=ray.y-sun.y;
            float angleSquared=max(2.0*(1.0-toward),0.0);
            float disc=exp(-angleSquared*2600.0);
            float horizontalSquared=max(angleSquared-verticalDelta*verticalDelta,0.0);
            float streak=exp(-(verticalDelta*verticalDelta*850.0+horizontalSquared*90.0));
            float lowSun=clamp(1.0-abs(sun.y)*2.2,0.0,1.0);
            float glowVisibility=sunVisibility*(0.35+0.65*lowSun)*(0.35+0.65*strength);
            float sunCanvas=sky?1.0:0.35;
            vec3 rayTint=mix(warm,vec3(1.0),0.24);
            vec3 glow=rayTint*(disc*1.05*sunCanvas+streak*0.42*mix(0.25,1.0,sunCanvas))*glowVisibility;

            if(Atmosphere.y>0.001) {
                int sampleCount=Performance.x<0.5?10:(Performance.x<1.5?18:26);
                float jitter=hash(gl_FragCoord.yx+vec2(17.0,59.0)+CameraTime.w*0.013);
                vec2 stepUv=(sunUv-texCoord)/float(sampleCount);
                vec2 sampleUv=texCoord+stepUv*jitter;
                float light=0.0;
                float weight=1.0;
                float total=0.0;
                for(int i=0;i<26;i++) {
                    if(i>=sampleCount) break;
                    vec2 safeUv=clamp(sampleUv,vec2(0.001),vec2(0.999));
                    light+=step(0.999999,texture(SceneDepth,safeUv).r)*weight;
                    total+=weight;
                    weight*=0.925;
                    sampleUv+=stepUv;
                }
                float occlusion=light/max(total,0.0001);
                float stylePower=Performance.x<0.5?2.35:(Performance.x<1.5?3.85:3.0);
                float angular=pow(clamp(toward*0.5+0.5,0.0,1.0),stylePower);
                float rays=occlusion*angular*sunVisibility*Atmosphere.y*strength;
                rays*=0.35+0.65*clamp(Environment.x,0.0,1.0);
                glow+=rayTint*rays*0.9;
            }

            glow=clamp(glow,vec3(0.0),vec3(1.0));
            color=vec3(1.0)-(vec3(1.0)-clamp(color,vec3(0.0),vec3(1.0)))*(vec3(1.0)-glow);
        }
    }
    return clamp(color,0.0,1.0);
}
