#!/usr/bin/env python3
"""Compare the actual AlphaDLC/Swerve math blocks on Java 21, without launching Minecraft.

Run from any directory: python Swerve/tests/check_elytra_flight_parity.py
Requires java + javac on PATH; alternatively pass --java PATH --ecj PATH_TO_ECJ_JAR.
This is a math regression check, not an in-game or Mixin integration test.
"""
import argparse
from pathlib import Path
import re
import subprocess
import tempfile

SWERVE = Path(__file__).resolve().parents[1]
ROOT = SWERVE.parent
ALPHA = ROOT / "AlphaDLC/src/main/java/zov/alphadlc"
JAVA = SWERVE / "src/main/java/swerve"


def read(path):
    return path.read_text(encoding="utf-8")


def method_body(source, name):
    match = re.search(r"\b" + re.escape(name) + r"\([^)]*\)\s*\{", source)
    if match is None:
        raise AssertionError(f"Method not found: {name}")
    start = match.end()
    depth = 1
    for index in range(start, len(source)):
        depth += (source[index] == "{") - (source[index] == "}")
        if depth == 0:
            return source[start:index]
    raise AssertionError(f"Unclosed method: {name}")


def movement_body(source):
    body = source[source.index("        boolean forward ="):
                  source.index("        if (hasAnyInput && !killauraActive)")]
    keys = [("forwardKey.isPressed", "keyUp.isDown"),
            ("backKey.isPressed", "keyDown.isDown"),
            ("leftKey.isPressed", "keyLeft.isDown"),
            ("rightKey.isPressed", "keyRight.isDown"),
            ("jumpKey.isPressed", "keyJump.isDown"),
            ("sneakKey.isPressed", "keyShift.isDown")]
    for bit, variants in enumerate(keys):
        for key in variants:
            body = body.replace(f"mc.options.{key}()", f"(mask & {1 << bit}) != 0")
    body = body.replace("mc.gameRenderer.getCamera().getYaw()", "camera")
    body = body.replace("mc.gameRenderer.getMainCamera().getYRot()", "camera")
    body = body.replace("this.hasInput = hasAnyInput;", "result.input = hasAnyInput;")
    body = body.replace("hasInput = hasAnyInput;", "result.input = hasAnyInput;")
    body = body.replace("mc.player.setVelocity", "result.setVelocity")
    body = body.replace("player.setDeltaMovement", "result.setVelocity")
    return body + "\nresult.yaw = yaw; result.pitch = pitch; return result;\n"


def harness():
    original = read(ALPHA / "module/list/movement/ElytraFlight.java")
    port = read(JAVA / "modules/impl/movement/ElytraFlight.java")
    rotation_original = read(ALPHA / "util/rotation/RotationComponent.java")
    rotation_port = read(JAVA / "utils/movement/ElytraFlightRotation.java")
    gcd_original = read(ALPHA / "util/render/math/GCDFixer.java")
    gcd_a = method_body(gcd_original, "getGCD").replace(
        "mc.options.getMouseSensitivity().getValue()", "sensitivity")
    gcd_b = method_body(rotation_port, "getGCDValue").replace(
        "Minecraft.getInstance().options.sensitivity().get()", "mouseSensitivity")
    code = """
public class ElytraFlightParity {
    static class Motion {
        double x = 123, y = 456, z = 789;
        float yaw, pitch;
        boolean input;
        void setVelocity(double x, double y, double z) { this.x=x; this.y=y; this.z=z; }
    }
    static class Event { float forward, strafe; Event(float f,float s){forward=f;strafe=s;} }
    static float wrap(float angle) {
        float result = angle % 360;
        if (result >= 180) result -= 360;
        if (result < -180) result += 360;
        return result;
    }
    static double wrap(double angle) {
        double result = angle % 360;
        if (result >= 180) result -= 360;
        if (result < -180) result += 360;
        return result;
    }
"""
    for suffix, source in [("Original", original), ("Port", port)]:
        code += (f"static Motion motion{suffix}(int mask,float camera,boolean killauraActive) {{"
                 "Motion result = new Motion();" + movement_body(source) + "}\n")
    code += "static float rawGcd(double sensitivity) {" + gcd_a + "}\n"
    code += "static float gcdOriginal(double sensitivity) {return (float)(rawGcd(sensitivity)*0.15);}\n"
    code += "static float gcdPort(double mouseSensitivity) {" + gcd_b + "}\n"
    for suffix, source in [("Original", rotation_original), ("Port", rotation_port)]:
        code += (f"static double direction{suffix}(float rotationYaw,float moveForward,float moveStrafing) {{"
                 if suffix == "Original" else
                 f"static double direction{suffix}(float yaw,float forward,float strafe) {{")
        code += method_body(source, "direction") + "}\n"
        body = method_body(source, "fixMovement")
        body = body.replace("event.getForward()", "event.forward").replace("event.getStrafe()", "event.strafe")
        body = body.replace("MathHelper.wrapDegrees", "wrap").replace("Mth.wrapDegrees", "wrap")
        body = body.replace("direction(", f"direction{suffix}(")
        code += f"static void fix{suffix}(Event event,float yaw) {{" + body + "}\n"
    code += """
    public static void main(String[] args) {
        int movementCases = 0, correctionCases = 0;
        float[] yaws = {-1000000,-540,-180,-90,-0.1f,0,0.1f,45,90,180,360,540,1000000};
        for (float yaw : yaws) for (int mask=0;mask<64;mask++) for (boolean aura : new boolean[]{false,true}) {
            Motion a=motionOriginal(mask,yaw,aura), b=motionPort(mask,yaw,aura);
            if (Double.doubleToLongBits(a.x)!=Double.doubleToLongBits(b.x)
                    || Double.doubleToLongBits(a.y)!=Double.doubleToLongBits(b.y)
                    || Double.doubleToLongBits(a.z)!=Double.doubleToLongBits(b.z)
                    || a.yaw!=b.yaw || a.pitch!=b.pitch || a.input!=b.input)
                throw new AssertionError("movement mask="+mask+" yaw="+yaw+" aura="+aura);
            movementCases++;
        }
        for (int i=0;i<=100;i++) {
            if (Float.floatToIntBits(gcdOriginal(i/100.0))!=Float.floatToIntBits(gcdPort(i/100.0)))
                throw new AssertionError("GCD sensitivity="+i/100.0);
        }
        for (float yaw:yaws) for (float f:new float[]{-1,-0.3f,0,0.3f,1})
            for (float s:new float[]{-1,-0.3f,0,0.3f,1}) {
                Event a=new Event(f,s), b=new Event(f,s);
                fixOriginal(a,yaw); fixPort(b,yaw);
                if (a.forward!=b.forward || a.strafe!=b.strafe)
                    throw new AssertionError("input correction yaw="+yaw+" forward="+f+" strafe="+s);
                correctionCases++;
            }
        System.out.println("PASS: "+movementCases+" movement cases, 101 GCD values, "
                +correctionCases+" input corrections match AlphaDLC.");
    }
}
"""
    return code


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--ecj", type=Path)
    args = parser.parse_args()
    build = SWERVE / "build/elytra-parity"
    build.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(dir=build) as directory:
        source = Path(directory) / "ElytraFlightParity.java"
        source.write_text(harness(), encoding="utf-8")
        compiler = ([args.java, "-jar", str(args.ecj.resolve()), "-21", "-proc:none"]
                    if args.ecj else [args.javac, "--release", "21", "-proc:none"])
        subprocess.run([*compiler, "-encoding", "UTF-8", "-d", directory, str(source)], check=True)
        subprocess.run([args.java, "-cp", directory, "ElytraFlightParity"], check=True)


if __name__ == "__main__":
    main()
