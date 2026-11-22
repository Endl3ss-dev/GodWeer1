package prestige.metadata;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.RecordComponentVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
public class Header0512 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, "dev/zprestige/prestige/iN", null, "java/lang/Object", null);

classWriter.visitSource("TransformWorldRenderer.java", null);

{
annotationVisitor0 = classWriter.visitAnnotation("Lnet/lenni0451/classtransform/annotations/CTransformer;", true);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("value");
annotationVisitor1.visit(null, Type.getType("Lnet/minecraft/class_761;"));
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
classWriter.visitInnerClass("dev/zprestige/prestige/gK", "dev/zprestige/prestige/dp", "RenderContext", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

classWriter.visitInnerClass("net/lenni0451/classtransform/annotations/CTarget$Shift", "net/lenni0451/classtransform/annotations/CTarget", "Shift", ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM);

{
fieldVisitor = classWriter.visitField(ACC_PRIVATE, "frustum", "Lnet/minecraft/class_4604;", null, null);
{
annotationVisitor0 = fieldVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/CShadow;", false);
annotationVisitor0.visit("value", "capturedFrustum");
annotationVisitor0.visitEnd();
}
fieldVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "b", "(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/class_243;Lnet/lenni0451/classtransform/InjectionCallback;)V", null, null);
methodVisitor.visitParameter("matrix4f", 0);
methodVisitor.visitParameter("matrix4f2", 0);
methodVisitor.visitParameter("vec3d", 0);
methodVisitor.visitParameter("ci", 0);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "setupFrustum");
annotationVisitor1.visitEnd();
}
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("target");
{
AnnotationVisitor annotationVisitor2 = annotationVisitor1.visitAnnotation(null, "Lnet/lenni0451/classtransform/annotations/CTarget;");
annotationVisitor2.visit("value", "RETURN");
annotationVisitor2.visitEnd();
}
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "c", "(Lnet/lenni0451/classtransform/InjectionCallback;)V", null, null);
methodVisitor.visitParameter("ci", 0);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "reload");
annotationVisitor1.visitEnd();
}
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("target");
{
AnnotationVisitor annotationVisitor2 = annotationVisitor1.visitAnnotation(null, "Lnet/lenni0451/classtransform/annotations/CTarget;");
annotationVisitor2.visit("value", "HEAD");
annotationVisitor2.visitEnd();
}
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(0, "a", "(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V", null, null);
methodVisitor.visitParameter("allocator", 0);
methodVisitor.visitParameter("tickCounter", 0);
methodVisitor.visitParameter("renderBlockOutline", 0);
methodVisitor.visitParameter("camera", 0);
methodVisitor.visitParameter("theCameraRotation", 0);
methodVisitor.visitParameter("actualRealProjMatrix", 0);
methodVisitor.visitParameter("projectionMatrix", 0);
methodVisitor.visitParameter("fogBuffer", 0);
methodVisitor.visitParameter("fogColor", 0);
methodVisitor.visitParameter("renderSky", 0);
{
annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/lenni0451/classtransform/annotations/injection/CInject;", false);
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("method");
annotationVisitor1.visit(null, "render");
annotationVisitor1.visitEnd();
}
{
AnnotationVisitor annotationVisitor1 = annotationVisitor0.visitArray("target");
{
AnnotationVisitor annotationVisitor2 = annotationVisitor1.visitAnnotation(null, "Lnet/lenni0451/classtransform/annotations/CTarget;");
annotationVisitor2.visit("value", "INVOKE");
annotationVisitor2.visit("target", "Lnet/minecraft/client/render/FrameGraphBuilder;run(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/FrameGraphBuilder$Profiler;)V");
annotationVisitor2.visitEnum("shift", "Lnet/lenni0451/classtransform/annotations/CTarget$Shift;", "AFTER");
annotationVisitor2.visitEnd();
}
annotationVisitor1.visitEnd();
}
annotationVisitor0.visitEnd();
}
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
