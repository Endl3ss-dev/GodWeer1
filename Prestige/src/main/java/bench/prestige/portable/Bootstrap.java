package bench.prestige.portable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.lenni0451.classtransform.TransformerManager;
import net.lenni0451.classtransform.utils.tree.BasicClassProvider;
import net.lenni0451.classtransform.utils.tree.IClassProvider;
import org.objectweb.asm.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

public final class Bootstrap implements PreLaunchEntrypoint, ClientModInitializer {
    private static final class MemoryResources extends ClassLoader {
        MemoryResources(ClassLoader parent){super(parent);}
        @Override public URL getResource(String name) {
            URL source=getParent().getResource(name);
            if(source==null)return null;
            if(!name.startsWith("assets/prestige/"))return source;
            try {
                byte[] data;try(InputStream in=source.openStream()){data=in.readAllBytes();}
                return new URL(null,"prestige-memory:/"+name,new URLStreamHandler(){
                    @Override protected URLConnection openConnection(URL url){return new URLConnection(url){
                        @Override public void connect(){}
                        @Override public int getContentLength(){return data.length;}
                        @Override public long getContentLengthLong(){return data.length;}
                        @Override public InputStream getInputStream(){return new ByteArrayInputStream(data);}
                    };}
                });
            }catch(IOException error){throw new UncheckedIOException("Cannot read native resource "+name,error);}
        }
    }
    private static final class Provider implements IClassProvider {
        private final ClassLoader loader;
        private final Map<String,java.util.function.Supplier<byte[]>> classes = new HashMap<>();
        Provider(ClassLoader loader,Path jar) throws IOException {
            this.loader=loader;
            try(ZipFile zip=new ZipFile(jar.toFile())) {
                for(ZipEntry entry:Collections.list(zip.entries()))if(entry.getName().endsWith(".class")) {
                    byte[] data;try(InputStream in=zip.getInputStream(entry)){data=in.readAllBytes();}
                    classes.put(entry.getName().substring(0,entry.getName().length()-6).replace('/','.'),()->data.clone());
                }
            }
        }
        @Override public byte[] getClass(String name)throws ClassNotFoundException {
            String dotted=name.replace('/','.');var own=classes.get(dotted);if(own!=null)return own.get();
            try(InputStream in=loader.getResourceAsStream(dotted.replace('.','/')+".class")) {
                if(in==null)throw new ClassNotFoundException(name);return in.readAllBytes();
            }catch(IOException e){throw new ClassNotFoundException(name,e);}
        }
        @Override public Map<String,java.util.function.Supplier<byte[]>> getAllClasses(){return Collections.unmodifiableMap(classes);}
    }
    private static TransformerManager manager;
    private static boolean initialized;
    private static Object guiSettings;
    private static Field menuBind,settingValue;
    private static boolean lastVisible;
    public static boolean menuKey(int action,Object input) {
        if(!initialized)return false;
        try {
            int key=((Number)input.getClass().getMethod("comp_4795").invoke(input)).intValue();
            int binding=((Number)settingValue.get(menuBind.get(guiSettings))).intValue();
            int virtual=virtualKey(key);
            boolean close=key==256&&Bridge.uiVisible();
            if(virtual!=binding&&!close)return false;
            if(action==1){Bridge.toggleUi();syncCursor();System.out.println("[portable] MENU_KEY key="+key+" visible="+Bridge.uiVisible());}
            return true;
        }catch(ReflectiveOperationException e){throw new IllegalStateException("Menu input adapter failed",e);}
    }
    private static int virtualKey(int key) {
        if(key>=290&&key<=313)return key-290+112;
        return switch(key){case 340->160;case 344->161;case 341->162;case 345->163;case 342->164;case 346->165;case 343->91;case 347->92;case 256->27;case 257->13;case 258->9;case 259->8;case 260->45;case 261->46;case 262->39;case 263->37;case 264->40;case 265->38;case 266->33;case 267->34;case 268->36;case 269->35;default->key;};
    }
    private static void syncCursor() {
        if(!initialized)return;
        boolean visible=Bridge.uiVisible();
        if(visible==lastVisible)return;
        try {
            Class<?> type=Class.forName("net.minecraft.class_310");Object client=type.getMethod("method_1551").invoke(null);Object mouse=type.getField("field_1729").get(client);
            if(visible)mouse.getClass().getMethod("method_1610").invoke(mouse);
            else if(type.getField("field_1755").get(client)==null&&type.getField("field_1687").get(client)!=null)mouse.getClass().getMethod("method_1612").invoke(mouse);
            lastVisible=visible;
        }catch(ReflectiveOperationException e){throw new IllegalStateException("Menu cursor adapter failed",e);}
    }
    private static final ThreadLocal<Boolean> transforming = ThreadLocal.withInitial(() -> false);
    private static byte[] resource(String name) throws IOException {
        try (InputStream in = Bootstrap.class.getResourceAsStream("/portable/" + name)) {
            if (in == null) throw new FileNotFoundException(name);
            return in.readAllBytes();
        }
    }
    private static Path extract(Path directory, String name) throws Exception {
        byte[] bytes = resource(name);
        Path path = directory.resolve(name);
        if (Files.exists(path) && Arrays.equals(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)), MessageDigest.getInstance("SHA-256").digest(bytes))) return path;
        Path temporary = Files.createTempFile(directory, "extract-", ".tmp");
        try { Files.write(temporary, bytes); Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING); }
        finally { Files.deleteIfExists(temporary); }
        return path;
    }
    @Override public void onPreLaunch() {
        try {
            ClassLoader loader = Bootstrap.class.getClassLoader();
            Path directory = FabricLoader.getInstance().getGameDir().resolve("_internal/prestige");
            Files.createDirectories(directory);
            Path nativePath = extract(directory, "Prestige-native-local.dll");
            Path bridgePath = extract(directory, "portable-bridge.dll");
            Path registry = extract(directory, "modules-local.bin");
            Path fields = extract(directory,"fields-local.bin");
            List<String> names = new ArrayList<>(), values = new ArrayList<>();
            for (String line : new String(resource("names.txt"), java.nio.charset.StandardCharsets.UTF_8).split("\\R")) {
                int at = line.indexOf('='); if (at <= 0) throw new IOException("Invalid mapping record");
                names.add(line.substring(0, at)); values.add(line.substring(at + 1));
            }
            System.load(bridgePath.toAbsolutePath().toString());
            Class<?> natives = Class.forName("dev.zprestige.prestige.Native", false, loader);
            Bridge.open(nativePath.toAbsolutePath().toString(), registry.toAbsolutePath().toString(),fields.toAbsolutePath().toString(), natives, new MemoryResources(loader), names.toArray(String[]::new), values.toArray(String[]::new));
            Path ownJar = Path.of(Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            manager = new TransformerManager(new Provider(loader,ownJar), new ByteArrayInputStream(resource("mixin-mappings.bin")));
            List<String> transformers = new ArrayList<>();
            try (ZipFile zip = new ZipFile(ownJar.toFile())) {
                for (ZipEntry entry : Collections.list(zip.entries())) {
                    if (!entry.getName().startsWith("dev/zprestige/") || !entry.getName().endsWith(".class")) continue;
                    ClassReader reader;
                    try (InputStream in = zip.getInputStream(entry)) { reader = new ClassReader(in.readAllBytes()); }
                    boolean[] transformer = {false};
                    reader.accept(new ClassVisitor(Opcodes.ASM9) {
                        @Override public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.equals("Lnet/lenni0451/classtransform/annotations/CTransformer;")) transformer[0] = true;
                            return null;
                        }
                    }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    if (transformer[0]) transformers.add(reader.getClassName().replace('/', '.'));
                }
            }
            Collections.sort(transformers);
            for (String name : transformers) manager.addTransformer(name);
            if (transformers.isEmpty()) throw new IllegalStateException("Transformer inventory is empty");
            Field delegateField = loader.getClass().getDeclaredField("delegate"); delegateField.setAccessible(true);
            Object delegate = delegateField.get(loader);
            Field mixinField = delegate.getClass().getDeclaredField("mixinTransformer"); mixinField.setAccessible(true);
            Object original = mixinField.get(delegate);
            if (original == null) throw new IllegalStateException("Fabric transformer is not initialized");
            Class<?> api = mixinField.getType();
            Object proxy = java.lang.reflect.Proxy.newProxyInstance(api.getClassLoader(), new Class<?>[]{api}, (object, method, args) -> {
                Object result;
                try { result = method.invoke(original, args); } catch (InvocationTargetException e) { throw e.getCause(); }
                if (method.getName().equals("transformClassBytes") && result instanceof byte[] bytes && args[1] instanceof String name && name.startsWith("net.minecraft.") && !transforming.get()) {
                    transforming.set(true);
                    try { byte[] transformed = manager.transform(name, bytes); if (transformed != null) result = transformed; }
                    finally { transforming.set(false); }
                }
                return result;
            });
            mixinField.set(delegate, proxy);
            System.out.println("[portable] Transformers registered=" + transformers.size());
        } catch (Throwable error) { throw new IllegalStateException("Prestige prelaunch failed", error); }
    }
    @Override public void onInitializeClient() {
        initialize();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            Bridge.initializeUi();
            lastVisible=Bridge.uiVisible();
            System.out.println("[portable] CLIENT_READY TransformedTargets=" + manager.getTransformedClasses().size());
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> syncCursor());
    }
    private static synchronized void initialize() {
        if (initialized) throw new IllegalStateException("Client initialized twice");
        try {
            Class<?> core = Class.forName("dev.zprestige.prestige.client.Prestige");
            Object instance = core.getDeclaredConstructor().newInstance();
            core.getDeclaredMethod("init").invoke(instance);
            Object modules = core.getField("d").get(null);
            if (modules == null) throw new IllegalStateException("Module manager missing after init");
            Method list = Arrays.stream(modules.getClass().getMethods()).filter(m -> m.getName().equals("a") && m.getParameterCount() == 0 && List.class.isAssignableFrom(m.getReturnType())).findFirst().orElseThrow();
            int count = ((List<?>)list.invoke(modules)).size();
            if (count != 121) throw new IllegalStateException("Incomplete modules: " + count + "/121");
            Field gui=modules.getClass().getDeclaredField("a");gui.setAccessible(true);guiSettings=gui.get(modules);
            menuBind=guiSettings.getClass().getDeclaredField("f");menuBind.setAccessible(true);
            settingValue=Class.forName("dev.zprestige.prestige.dK").getDeclaredField("b");settingValue.setAccessible(true);
            initialized = true;
            System.out.println("[portable] CORE_READY Modules=" + count + " TransformedTargets=" + manager.getTransformedClasses().size());
        } catch (Throwable error) { throw new IllegalStateException("Prestige client initialization failed", error); }
    }
}
