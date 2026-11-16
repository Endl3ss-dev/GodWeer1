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
public class Header0529 implements Opcodes {

public static byte[] dump () throws Exception {

ClassWriter classWriter = new ClassWriter(0);
FieldVisitor fieldVisitor;
RecordComponentVisitor recordComponentVisitor;
MethodVisitor methodVisitor;
AnnotationVisitor annotationVisitor0;

classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, "dev/zprestige/prestige/loader/PrestigeClient", null, "java/security/SecureClassLoader", null);

classWriter.visitSource("PrestigeClient.java", null);

{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/ClassLoader;)V", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_NATIVE, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;", "(Ljava/lang/String;)Ljava/lang/Class<*>;", null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_NATIVE, "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_NATIVE, "getResource", "(Ljava/lang/String;)Ljava/net/URL;", null, null);
methodVisitor.visitEnd();
}
{
methodVisitor = classWriter.visitMethod(ACC_PROTECTED | ACC_NATIVE, "findClass", "(Ljava/lang/String;)Ljava/lang/Class;", "(Ljava/lang/String;)Ljava/lang/Class<*>;", null);
methodVisitor.visitEnd();
}
classWriter.visitEnd();

return classWriter.toByteArray();
}
}
