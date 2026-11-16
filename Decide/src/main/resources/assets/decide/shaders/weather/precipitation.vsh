#version 150
#moj_import <decide:weather.glsl>
in vec3 Position;
in vec2 UV0;
in vec4 Color;
out vec2 dropUv;
out float opacity;
out float snowMode;
out float sparkle;
out float splashMode;
out float petalLight;

void main() {
    float ceiling=floor(UV0.x*0.5);
    vec2 uv=vec2(UV0.x-ceiling*2.0,UV0.y);
    bool snow=abs(Environment.w-2.0)<0.1;
    bool eclipse=abs(Environment.w-6.0)<0.1;
    bool meteor=abs(Environment.w-7.0)<0.1;
    bool blood=abs(Environment.w-8.0)<0.1;
    bool spring=abs(Environment.w-9.0)<0.1;
    bool autumn=abs(Environment.w-11.0)<0.1;
    bool extra=Environment.w>11.5 && Environment.w<71.5;
    bool beyond=Environment.w>21.5 && Environment.w<71.5;
    bool mist=abs(Environment.w-12.0)<0.1;
    bool stormy=abs(Environment.w-13.0)<0.1;
    bool ash=abs(Environment.w-14.0)<0.1;
    bool sand=abs(Environment.w-15.0)<0.1;
    bool pollen=abs(Environment.w-16.0)<0.1;
    bool firefly=abs(Environment.w-17.0)<0.1;
    bool aurora=abs(Environment.w-18.0)<0.1;
    bool frost=abs(Environment.w-19.0)<0.1;
    bool monsoon=abs(Environment.w-20.0)<0.1;
    bool toxic=abs(Environment.w-21.0)<0.1;
    bool heavyRain=stormy||monsoon;
    bool motes=(Environment.w>2.5 && Environment.w<4.5) || eclipse || meteor || blood;
    bool splash=Color.a<0.5;
    splashMode=splash?1.0:0.0;
    snowMode=snow?1.0:0.0;
    float amount=snow?Environment.z:heavyRain?ExtraMotion.x:Effects.x;
    float storm=snow?Performance.y:0.0;
    float gust=0.65+0.35*sin(CameraTime.w*0.7+sin(CameraTime.w*0.19));
    vec3 world=Position+MeshOrigin.xyz;
    float seed=Color.r*0.997+0.001;
    petalLight=1.0;
    if(spring || autumn) {
        // Весна: мелкие нежные лепестки, плавный ветер. Осень: крупные
        // листья, сильный порывистый ветер и кувыркание вокруг своей оси.
        float floorY=Position.y+0.045;
        float top=max(floorY+0.3,min(ceiling-0.12,Position.y+(spring?17.0:20.0)+Color.g*(spring?7.0:10.0)));
        float height=top-floorY;
        float fallSpeed=spring?mix(0.62,1.15,Color.g):mix(1.05,1.75,Color.g);
        float fallTime=height/fallSpeed;
        float restTime=SakuraMotion.z*(spring?1.15:0.52);
        float cycle=fallTime+restTime;
        float age=mod(CameraTime.w+seed*cycle*7.17,cycle);
        bool resting=age>=fallTime;
        float motionTime=min(age,fallTime);
        float progress=clamp(motionTime/fallTime,0.0,1.0);
        float flutter=SakuraAppearance.w*(spring?0.85:1.35);
        float wind=SakuraMotion.w*(spring?0.72:1.42);
        float swirl=motionTime*(spring?1.65:2.55)+seed*43.0;
        world.y=floorY+height*(1.0-progress)
                +sin(swirl*(spring?1.7:2.2))*flutter*(spring?0.10:0.16)*(1.0-progress);
        world.x+=progress*wind*(spring?2.2:4.8+Color.g*1.6)
                +sin(swirl)*(spring?0.32:0.55);
        world.z+=progress*wind*(spring?0.7:1.4+Color.g*2.2)
                +cos(swirl*(spring?0.83:1.17))*(spring?0.28:0.48);
        vec3 relative=world-CameraTime.xyz;
        float distance=length(relative);
        opacity=smoothstep(0.4,1.4,distance)*(1.0-smoothstep(spring?42.0:48.0,spring?75.0:85.0,distance));
        opacity*=step(Position.y+0.45,ceiling);
        if(resting) opacity*=restTime>0.0 ? 1.0-(age-fallTime)/max(restTime,0.001) : 0.0;
        float size=mix(spring?0.020:0.030,spring?0.060:0.105,SakuraMotion.y)
                *mix(0.78,1.22,Color.g)*clamp(distance*0.025,1.0,1.9);
        vec3 right=normalize(InverseView[0].xyz);
        vec3 up=normalize(InverseView[1].xyz);
        float rotation=sin(swirl*(spring?0.72:0.55))*(spring?1.6:2.5)
                +swirl*(spring?0.38:0.62)*flutter;
        if(resting) {
            float angle=seed*6.28318;
            right=vec3(cos(angle),0.0,sin(angle));
            up=vec3(-sin(angle),0.0,cos(angle));
            petalLight=0.72+0.18*Color.g;
        } else {
            vec3 spun=right*cos(rotation)+up*sin(rotation);
            up=up*cos(rotation)-right*sin(rotation);
            right=spun;
            right*=mix(1.0,spring?0.42:0.16,abs(sin(swirl*(spring?1.15:1.45)))*flutter);
            petalLight=0.60+0.40*abs(cos(swirl*(spring?1.15:1.45)));
        }
        relative+=right*(uv.x-0.5)*size*(spring?2.0:2.3)
                +up*(uv.y-0.5)*size*(spring?2.4:2.9);
        gl_Position=opacity<0.001?vec4(2,2,2,1):Projection*View*vec4(relative,1.0);
        dropUv=uv; sparkle=Color.g; snowMode=spring?10.0:11.0; splashMode=0.0;
        return;
    }
    if(extra && !heavyRain) {
        // Alchemical / atmospheric effects share one weather mesh. The exact
        // motion and silhouette are selected by Environment.w.
        float variant=clamp(Environment.w-22.0,0.0,49.0);
        float style=mod(floor(variant),10.0);
        float floorY=Position.y+0.045;
        float height=beyond?(4.5+style*1.55):(mist?5.5:ash?13.0:sand?2.2:pollen?8.0:firefly?3.8:aurora?20.0:frost?12.0:8.0);
        float top=min(ceiling-0.10,floorY+height+Color.g*(mist?1.5:3.0));
        float span=max(0.2,top-floorY);
        float phase=CameraTime.w;
        float speed=beyond?((style<0.5||style>8.5)?0.10:((style>3.5&&style<4.5)?0.030:((style>4.5&&style<5.5)?0.060:((style>2.5&&style<3.5)?0.38:0.085))))
                :(mist?0.035:ash?0.10:sand?0.0:pollen?0.045:firefly?0.05:aurora?0.035:frost?0.10:0.045);
        float age=fract(seed+phase*speed);
        bool rising=beyond?((style>0.5&&style<1.5)||style>8.5):(ash||frost);
        world.y=floorY+(rising?(1.0-age):age)*span;
        if(beyond) {
            if(style<0.5) {
                world.x+=sin(phase*0.7+seed*43.0)*0.9;
                world.z+=cos(phase*0.53+seed*31.0)*0.7;
            } else if(style>0.5&&style<1.5) {
                world.x+=sin(phase*0.45+seed*61.0)*(0.4+ExtraMotion.w*1.6);
                world.z+=cos(phase*0.38+seed*37.0)*(0.3+ExtraMotion.w*1.2);
            } else if(style>1.5&&style<2.5) {
                world.x+=sin(phase*0.5+seed*31.0)*1.2+(age-0.5)*2.5;
                world.z+=cos(phase*0.43+seed*23.0)*0.9;
            } else if(style>2.5&&style<3.5) {
                world.x+=(age-0.5)*(6.0+ExtraMotion.w*16.0);
                world.z+=sin(phase*0.9+seed*47.0)*1.3;
            } else if(style>3.5&&style<4.5) {
                world.y=floorY+0.25+seed*1.6;
                world.x+=sin(phase*0.10+seed*53.0)*2.4;
                world.z+=cos(phase*0.08+seed*29.0)*2.0;
            } else if(style>4.5&&style<5.5) {
                float ang=phase*0.35+seed*6.28318;
                float radius=0.5+seed*3.4;
                world.x+=cos(ang)*radius;
                world.z+=sin(ang)*radius;
            } else if(style>5.5&&style<6.5) {
                world.x+=sin(phase*0.8+seed*19.0)*0.7;
                world.z+=cos(phase*0.7+seed*41.0)*0.7;
            } else if(style>6.5&&style<7.5) {
                world.x+=sin(phase*0.32+seed*73.0)*1.5;
                world.z+=cos(phase*0.28+seed*17.0)*1.2;
            } else if(style>7.5&&style<8.5) {
                world.y=floorY+2.4+seed*8.0+sin(phase*0.45+seed*37.0)*0.6;
                world.x+=sin(phase*0.18+seed*71.0)*2.6;
                world.z+=cos(phase*0.15+seed*43.0)*2.2;
            } else {
                world.x+=sin(phase*0.55+seed*11.0)*0.9;
                world.z+=cos(phase*0.47+seed*13.0)*0.9;
            }
        } else if(sand) {
            world.y=floorY+0.12+seed*1.3;
            float sweep=fract(seed+CameraTime.w*(0.8+ExtraMotion.w*1.8)*0.13);
            world.x+=(sweep-0.5)*14.0;
            world.z+=(fract(seed*1.73+CameraTime.w*0.11)-0.5)*8.0;
        } else if(firefly) {
            world.y=floorY+0.35+seed*2.8+sin(phase*0.8+seed*30.0)*0.35;
            world.x+=sin(phase*0.55+seed*43.0)*1.2;
            world.z+=cos(phase*0.47+seed*31.0)*1.2;
        } else {
            world.x+=sin(phase*(mist?0.08:0.25)+seed*83.0)*(mist?1.2:0.9);
            world.z+=cos(phase*(mist?0.07:0.21)+seed*37.0)*(mist?1.0:0.8);
            world.x+=sin(phase*0.11+seed*19.0)*0.35;
        }
        vec3 relative=world-CameraTime.xyz;
        float d=length(relative);
        opacity=step(Color.b,ExtraMotion.x)*smoothstep(0.25,1.5,d)
                *(1.0-smoothstep(ExtraMotion.x>0.5?34.0:48.0,ExtraMotion.x>0.5?72.0:88.0,d));
        if(firefly) opacity*=0.45+0.55*pow(max(0.0,sin(phase*(1.8+Color.g*1.2)+seed*41.0)),4.0);
        if(mist) opacity*=0.36+0.30*sin(phase*0.13+seed*20.0);
        float size=mix(0.014,0.055,ExtraMotion.y)*mix(0.72,1.35,Color.g);
        if(beyond) {
            if(style<0.5) size*=1.9;
            else if(style<1.5) size*=0.55;
            else if(style<2.5) size*=1.1;
            else if(style<3.5) size*=0.70;
            else if(style<4.5) size*=3.8;
            else if(style<5.5) size*=1.2;
            else if(style<6.5) size*=1.0;
            else if(style<7.5) size*=0.62;
            else if(style<8.5) size*=1.7;
            else size*=0.9;
        } else if(mist) size*=3.4;
        else if(sand) size*=0.55;
        else if(frost) size*=1.8;
        else if(aurora) size*=1.45;
        else if(pollen) size*=0.72;
        else if(toxic) size*=1.55;
        vec3 right=normalize(InverseView[0].xyz);
        vec3 up=normalize(InverseView[1].xyz);
        if(sand) {
            right=normalize(vec3(relative.z,0.0,-relative.x));
            up=vec3(0.0,1.0,0.0);
            size*=1.5;
        }
        relative+=right*(uv.x-0.5)*size*2.0+up*(uv.y-0.5)*size*2.0;
        gl_Position=opacity<0.001?vec4(2,2,2,1):Projection*View*vec4(relative,1.0);
        dropUv=uv; sparkle=Color.g;
        snowMode=beyond?(22.0+style):(mist?12.0:ash?14.0:sand?15.0:pollen?16.0:firefly?17.0:aurora?18.0:frost?19.0:21.0);
        splashMode=0.0;
        return;
    }
    if(abs(Environment.w-5.0)<0.1) {
        float floorY=Position.y+0.045;
        float top=max(floorY+0.3,min(ceiling-0.12,Position.y+18.0+Color.g*8.0));
        float height=top-floorY;
        float fallTime=height/mix(0.75,1.45,Color.g);
        float restTime=SakuraMotion.z;
        float cycle=fallTime+restTime;
        float age=mod(CameraTime.w+seed*cycle*7.17,cycle);
        bool resting=age>=fallTime;
        float motionTime=min(age,fallTime);
        float progress=clamp(motionTime/fallTime,0.0,1.0);
        float flutter=SakuraAppearance.w;
        float wind=SakuraMotion.w;
        float swirl=motionTime*(1.9+Color.g*1.3)+seed*43.0;
        world.y=floorY+height*(1.0-progress)
                +sin(swirl*1.7)*flutter*0.10*(1.0-progress);
        world.x+=progress*wind*(2.5+seed*3.0)+sin(swirl)*flutter*0.38;
        world.z+=progress*wind*(0.8+Color.g*1.8)+cos(swirl*0.83)*flutter*0.32;
        vec3 relative=world-CameraTime.xyz;
        float distance=length(relative);
        opacity=smoothstep(0.4,1.4,distance)*(1.0-smoothstep(45.0,80.0,distance));
        opacity*=step(Position.y+0.45,ceiling);
        if(resting) opacity*=restTime>0.0 ? 1.0-(age-fallTime)/max(restTime,0.001) : 0.0;
        float size=mix(0.024,0.075,SakuraMotion.y)*mix(0.78,1.22,Color.g)
                *clamp(distance*0.025,1.0,1.8);
        vec3 right=normalize(InverseView[0].xyz);
        vec3 up=normalize(InverseView[1].xyz);
        float rotation=sin(swirl*0.72)*1.6+swirl*0.38*flutter;
        if(resting) {
            float angle=seed*6.28318;
            right=vec3(cos(angle),0.0,sin(angle));
            up=vec3(-sin(angle),0.0,cos(angle));
            petalLight=0.72+0.18*Color.g;
        } else {
            vec3 spun=right*cos(rotation)+up*sin(rotation);
            up=up*cos(rotation)-right*sin(rotation);
            right=spun;
            right*=mix(1.0,0.25,abs(sin(swirl*1.15))*flutter);
            petalLight=0.65+0.35*abs(cos(swirl*1.15));
        }
        relative+=right*(uv.x-0.5)*size*2.0+up*(uv.y-0.5)*size*2.4;
        gl_Position=opacity<0.001?vec4(2,2,2,1):Projection*View*vec4(relative,1.0);
        dropUv=uv; sparkle=Color.g; snowMode=3.0; splashMode=0.0;
        return;
    }
    // A small subset of world-space seeds becomes nearby falling meteors.
    // Depth testing keeps these streaks behind terrain and buildings.
    if(meteor) {
        float identity=fract(sin(dot(Position.xz,vec2(127.1,311.7))
                +Position.y*74.7+seed*19.0)*43758.5453);
        float selection=MeteorSettings.x*0.0072;
        if(identity<selection) {
            float period=mix(21.0,7.0,MeteorSettings.x)*(0.75+Color.g*0.5);
            float age=mod(CameraTime.w+identity*2317.0+seed*period*17.0,period);
            float duration=mix(0.9,1.55,Color.g);
            float progress=clamp(age/duration,0.0,1.0);
            float azimuth=seed*6.28318+Color.g*3.0;
            vec3 direction=normalize(vec3(cos(azimuth)*0.75,-0.52,sin(azimuth)*0.75));
            float reach=mix(20.0,55.0,MeteorDetails.w);
            world.y=min(ceiling-2.0,max(Position.y+7.0,CameraTime.y+8.0+Color.g*16.0));
            world+=direction*((progress-0.5)*mix(12.0,24.0,Color.g));
            vec3 rel=world-CameraTime.xyz;
            float distance=length(rel);
            float variation=mix(0.05,0.82,MeteorDetails.z);
            float scale=mix(1.0-variation,1.0+variation,Color.g);
            float width=mix(0.07,0.21,MeteorDetails.y)*scale;
            float trail=mix(0.55,2.65,MeteorSettings.z)*scale;
            vec3 sideways=cross(direction,normalize(rel));
            sideways=length(sideways)>0.01?normalize(sideways):normalize(InverseView[0].xyz);
            rel+=sideways*(uv.x-0.5)*width*2.0+direction*(uv.y-0.5)*trail*2.0;
            opacity=step(age,duration)*step(Position.y+7.0,ceiling-2.0)
                    *smoothstep(5.0,11.0,distance)*(1.0-smoothstep(reach-10.0,reach,distance))
                    *(1.0-smoothstep(0.75,1.0,progress));
            gl_Position=opacity<0.001?vec4(2,2,2,1):Projection*View*vec4(rel,1);
            dropUv=uv; sparkle=Color.g; snowMode=8.0; splashMode=0.0;
            return;
        }
    }
    if(motes) {
        float density=eclipse?EclipseSettings.w:meteor?MeteorDetails.x:blood?BloodWorld.z
                :Environment.w>3.5?Atmosphere.w:Atmosphere.z;
        float bottom=Position.y+0.12;
        float top=min(ceiling-0.12,Position.y+(meteor?26.0:eclipse?22.0:blood?20.0:10.0));
        float phase=CameraTime.w*(meteor?0.20:eclipse?0.07:0.16+Color.g*0.12)+seed*83.0;
        world.y=bottom+seed*max(0.1,top-bottom)+sin(phase*0.7)*0.12;
        world.x+=sin(phase)*0.35;
        world.z+=cos(phase*0.83)*0.35;
        vec3 rel=world-CameraTime.xyz;
        float d=length(rel);
        opacity=step(Color.b,density)*smoothstep(0.3,1.4,d)*(1.0-smoothstep(18.0,60.0,d))
                *(0.88+0.12*sin(phase*0.41+seed*20.0));
        float size=mix(0.018,0.038,Color.g)*max(1.0,d*0.035);
        if(meteor) size*=mix(0.65,1.65,MeteorDetails.x);
        else if(eclipse) size*=mix(0.45,1.7,EclipseSettings.w);
        else if(Environment.w>3.5) size*=mix(0.35,3.0,Climate.w);
        rel+=normalize(InverseView[0].xyz)*(uv.x-0.5)*size*2.0
                +normalize(InverseView[1].xyz)*(uv.y-0.5)*size*2.0;
        gl_Position=opacity<0.001?vec4(2,2,2,1):Projection*View*vec4(rel,1);
        snowMode=blood?9.0:meteor?6.0:eclipse?4.0:2.0;
        splashMode=0.0; dropUv=uv; sparkle=Color.g;
        return;
    }
    float low=Position.y+0.03;
    float high=ceiling-0.03;
    float span=max(0.1,high-low);
    float speed=snow?mix(1.1,2.2,Color.g)*(1.0+storm*1.4):mix(21.0,32.0,Color.g);
    float fall=fract(seed-CameraTime.w*speed/span);
    world.y=low+fall*span;
    world.x+=sin(CameraTime.w*(snow?0.65:0.11)+seed*83.0)
            *Screen.w*(snow?1.7:0.25);
    world.z+=cos(CameraTime.w*0.43+seed*37.0)*(snow?0.7:0.08);
    if(snow) {
        float phase=CameraTime.w*(0.7+storm*1.1)+seed*83.0;
        float drift=(0.5-fall)*(Screen.w*3.0+storm*18.0);
        world.x+=drift+sin(phase)*storm*(0.4+gust*0.7);
        world.z+=drift*0.35+cos(phase*0.83)*storm*0.6;
        world.y+=sin(phase*1.4)*storm*0.25;
    }
    float splashAge=fract(CameraTime.w*(1.8+Color.g*0.7)+seed*37.0);
    if(splash) {
        world=Position+MeshOrigin.xyz;
        float flight=splashAge*0.42;
        float direction=seed*6.28318+Color.a*8.0*2.0944;
        float reach=flight*(0.55+Color.g*0.45);
        world.x+=cos(direction)*reach;
        world.z+=sin(direction)*reach;
        world.y+=0.018+max(0.0,2.05*flight-4.9*flight*flight);
    }
    vec3 relative=world-CameraTime.xyz;
    float distance=length(relative);
    opacity=step(Color.b,amount)*step(Position.y+0.06,world.y)
            *(1.0-smoothstep(Environment.y*0.75,Environment.y,distance));
    opacity*=smoothstep(0.3,1.7,distance);
    if(splash) opacity=Screen.z*amount*step(seed,Performance.z)*(1.0-smoothstep(0.5,1.0,splashAge))
            *smoothstep(0.3,1.7,distance)*(1.0-smoothstep(48.0,80.0,distance));
    if(opacity<0.001) {
        gl_Position=vec4(2.0,2.0,2.0,1.0);
        dropUv=uv; sparkle=Color.g;
        return;
    }
    float lod=max(1.0,distance*0.025);
    float width=snow?0.055:mix(0.009,0.022,Color.g);
    width*=lod;
    float length=snow?width:mix(0.45,0.95,Color.g)*sqrt(lod);
    if(splash) {
        width=mix(0.012,0.023,Color.g)*min(lod,1.8);
        length=width*(1.1+abs(1.0-splashAge*2.0));
    }
    vec3 right=normalize(vec3(relative.z,0,-relative.x));
    vec3 up=vec3(0,1,0);
    if(splash) {
        right=normalize(InverseView[0].xyz);
        up=normalize(InverseView[1].xyz);
    }
    if(snow) {
        right=normalize(InverseView[0].xyz);
        up=normalize(InverseView[1].xyz);
        float angle=sin(CameraTime.w*0.8+seed*31.0)*0.8+storm*gust;
        vec3 rotated=right*cos(angle)+up*sin(angle);
        up=up*cos(angle)-right*sin(angle);
        right=rotated;
        length*=1.0+storm*gust*0.10;
    }
    relative+=right*(uv.x-0.5)*width*2.0+up*(uv.y-0.5)*length*2.0;
    if(!snow) relative.x+=(uv.y-0.5)*length*(0.10+Screen.w*0.18);
    gl_Position=Projection*View*vec4(relative,1.0);
    dropUv=uv;
    sparkle=Color.g;
}
