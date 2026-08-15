package antileak.base.client.modules.impl.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.ModeSetting;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ItemReplacer extends Module {

    private static final SwordVariant[] SWORD_VARIANTS = createSwordVariants();
    private static final Map<String, SwordVariant> VARIANTS_BY_LABEL = createVariantMap();
    public static final ItemReplacer INSTANCE = new ItemReplacer();
    private static ItemReplacer instance;

    private static final ThreadLocal<PendingRender> PENDING = new ThreadLocal<>();

    private static final class PendingRender {
        final ItemStack stack;
        final ModelTransformationMode transformType;
        PendingRender(ItemStack stack, ModelTransformationMode transformType) {
            this.stack = stack;
            this.transformType = transformType;
        }
    }

    private final ModeSetting swordModel = new ModeSetting("Model", SWORD_VARIANTS[0].label, getVariantLabels());

    private String cachedSelectedLabel;
    private SwordVariant cachedSelectedVariant = SWORD_VARIANTS[0];

    public ItemReplacer() {
        super("Item Replacer", "Item Replacer", ModuleCategory.MISC);
        addSettings(swordModel);
        instance = this;
    }

    public static boolean captureForRender(ItemStack stack, ModelTransformationMode transformType) {
        PENDING.remove();
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof SwordItem)) return false;
        ItemReplacer replacer = instance;
        if (replacer == null || !replacer.isEnable()) return false;
        PENDING.set(new PendingRender(stack, transformType));
        return true;
    }

    public static boolean renderPending(MatrixStack matrixStack,
                                        VertexConsumerProvider buffer,
                                        int combinedLight,
                                        int combinedOverlay) {
        PendingRender pending = PENDING.get();
        if (pending == null) return false;
        PENDING.remove();
        ItemReplacer replacer = instance;
        if (replacer == null || !replacer.isEnable()) return false;
        return replacer.getSelectedVariant().model.render(
                pending.stack, pending.transformType,
                matrixStack, buffer, combinedLight, combinedOverlay);
    }

    public SwordVariant getSelectedVariant() {
        String selected = swordModel.getCurrent();
        if (selected == null) return SWORD_VARIANTS[0];
        if (selected.equalsIgnoreCase(cachedSelectedLabel)) return cachedSelectedVariant;
        SwordVariant variant = VARIANTS_BY_LABEL.get(selected.toLowerCase(Locale.ROOT));
        if (variant == null) variant = SWORD_VARIANTS[0];
        cachedSelectedLabel = selected;
        cachedSelectedVariant = variant;
        return variant;
    }

    @Override
    public void onDisable() {
        clearLoadedModels();
        PENDING.remove();
        super.onDisable();
    }

    public void clearLoadedModels() {
        for (SwordVariant variant : SWORD_VARIANTS) variant.model.clear();
    }

    public static String[] getVariantLabels() {
        String[] labels = new String[SWORD_VARIANTS.length];
        for (int i = 0; i < SWORD_VARIANTS.length; i++) labels[i] = SWORD_VARIANTS[i].label;
        return labels;
    }

    private static Map<String, SwordVariant> createVariantMap() {
        Map<String, SwordVariant> map = new HashMap<>(SWORD_VARIANTS.length * 2);
        for (SwordVariant v : SWORD_VARIANTS) map.put(v.label.toLowerCase(Locale.ROOT), v);
        return map;
    }

    private static SwordVariant[] createSwordVariants() {
        String[][] data = {
            {"Abominable Blade","abominableblade"},{"Abominable Greatsaber","abominablegreatsaber"},
            {"Abominable Scythe","abominablescythe"},{"Acid Demon","aciddemon"},
            {"Amethyst Shuriken","amethyst_shuriken"},{"Ancient Royal Greatsword","ancient_royal_great_sword"},
            {"Aquantic Sacred Blade","aquantic_sacred_blade"},{"Aquantic Trident","aquantictrident"},
            {"Arcanethyst","arcanethyst"},{"Ashura Blade","ashura_blade"},
            {"Awakened Lichblade","awakened_lichblade"},{"Blood Edge","bloodedge"},
            {"Bloody Death","bloodydeath"},{"Bramblethorn","bramblethorn"},
            {"Brimstone Claymore","brimstone_claymore"},{"Carian Sword","cariansword"},
            {"Chrono Blade","chrono_blade"},{"Corrupted Mythic Blade","corruptedmythicblade"},
            {"Creation Splitter","creationsplitter"},{"Crescent Rose","crescentrose"},
            {"Cyber Katana","cyberkatana"},{"Cyber Mantis Blade","cybermantisblade"},
            {"Cybernetic Katana","cybernetickatana"},{"Cybernetic Knife","cyberneticknife"},
            {"Cybernetic Sawblade","cyberneticsawblade"},{"Cyber Sword","cybersword"},
            {"Dainsleif","dainsleif"},{"Dark Blade","dark_blade"},{"Dark Cleaver","dark_cleaver"},
            {"Death Knight Dagger","death_knight_dagger"},{"Death Knight Sword","death_knight_sword"},
            {"Demigod's Unholy Blade","demigodsunholyblade"},{"Demigod's Unholy Halberd","demigodsunholyhalberd"},
            {"Demonic Blade","demonicblade"},{"Demonic Cleaver","demoniccleaver"},
            {"Demon Lord's Great Axe","demonlordsgreataxe"},{"Demon Lord's Sword","demonlordsword"},
            {"Divine Justice","divine_justice"},{"Divine Reaper","divine_reaper"},
            {"Divine Axe Rhitta","divineaxerhitta"},{"Divine Punisher","divinepunisher"},
            {"Dragon Slaying Blade","dragonslayingblade"},{"Edge of the Astral Plane","edgeoftheastralplane"},
            {"Emberblade","emberblade"},{"Enigma","enigma"},{"Epic Sword","epicsword"},
            {"Estoc","estoc"},{"Excalibur","excalibur"},{"Fallen God Spear","fallengodspear"},
            {"Fallen God Sword","fallengodsword"},{"Floral Longsword","floral_longsword"},
            {"Floral Sabre","floral_sabre"},{"Forest Guardian Glaive","forest_guardian_glaive"},
            {"Frost Axe","frostaxe"},{"Frostblade","frostblade"},{"Frost Scythe","frostscythe"},
            {"Green Scythe","greenscythe"},{"Hearthflame","hearthflame"},{"Hero Sword","herosword"},
            {"Holy Moonlight Sword","holymoonlightsword"},{"Hornet's Needle","hornetsneedle"},
            {"Ice Whisper","icewhisper"},{"Jade Halberd","jadehalberd"},{"Katana","katana"},
            {"Legendary Sword","legendarysword"},{"Longsword","longsword"},{"Magi Scythe","magiscythe"},
            {"Masamune","masamune"},{"Mjolnir","mjolnir"},{"Molten Blade","moltenblade"},
            {"Molten Sword","moltensword"},{"Muramasa","muramasa"},{"Mystical Spellblade","mysticalspellblade"},
            {"Mythic Blade","mythicblade"},{"Partisan","partisan"},{"Pharaoh's Treasure","pharaohs_treasure"},
            {"Phoenix Grace","pheonixgrace"},{"Powerfuse Hammer","powerfusehammer"},
            {"Powerfuse Sword","powerfusesword"},{"Requiem of Hell","requiem_of_hell"},
            {"Ribbon Cleaver","ribboncleaver"},{"Righteous Relic","righteous_relic"},
            {"Rivers of Blood","riversofblood"},{"Royal Chakram","royalchakram"},{"Royal Rapier","royalrapier"},
            {"Sabre","sabre"},{"Scissor Blade","scissorblade"},{"Sculk Cleaver","sculkcleaver"},
            {"Sculk Scythe","sculkscythe"},{"Sculk Sword","sculksword"},{"Sentinel's Will","sentinels_will"},
            {"Silverine Blade","silverine_blade"},{"Soul Claws","soulclaws"},{"Soul Edge","souledge"},
            {"Soul Harvester","soulharvester"},{"Soul Render","soulrender"},{"Soul Stealer","soulstealer"},
            {"Soul Collector","soul_collector"},{"Soul Devourer","soul_devourer"},{"Star's Edge","stars_edge"},
            {"Steel Sword","steelsword"},{"Stop Sign","stop_sign"},{"Stormbringer","stormbringer"},
            {"Storm's Edge","storms_edge"},{"Sunbreak","sunbreak"},{"Tengen's Blade","tengensblade"},
            {"Terrablade","terrablade"},{"Thousand Demon Daggers","thousanddemondaggers"},
            {"Thunderbrand","thunderbrand"},{"Thunderbringer","thunderbringer"},
            {"Toxic Longsword","toxic_longsword"},{"Vampiric Needle","vampiricneedle"},
            {"Wakizashi","wakizashi"},{"Watcher Claymore","watcher_claymore"},
            {"Watching Warglaive","watching_warglaive"},{"Waxweaver","waxweaver"},
            {"Whisperwind","whisperwind"},{"Wickpiercer","wickpiercer"},{"Yoru","yoru"}
        };
        SwordVariant[] variants = new SwordVariant[data.length];
        for (int i = 0; i < data.length; i++) variants[i] = new SwordVariant(data[i][0], data[i][1]);
        return variants;
    }

    // ─── SwordVariant ───────────────────────────────────────────────────────────

    public static final class SwordVariant {
        public final String label;
        public final String modelName;
        final RuntimeSwordModel model;

        private SwordVariant(String label, String modelName) {
            this.label = label;
            this.modelName = modelName;
            this.model = new RuntimeSwordModel(modelName);
        }
    }

    // ─── RuntimeSwordModel ──────────────────────────────────────────────────────

    private static final class RuntimeSwordModel {
        private static final String MODEL_ROOT = "item_replacer/sword/models/";
        private static final String TEXTURE_ROOT = "item_replacer/sword/textures/";
        private static final String NAMESPACE = "mre";
        private static final float PIXEL = 1.0f / 16.0f;
        private static final float MODEL_CENTER = 0.5f;
        private static final int VERTEX_STRIDE = 8;

        private final MinecraftClient mc = MinecraftClient.getInstance();
        private final String modelPath;
        private final ArrayList<ModelCube> cubes = new ArrayList<>();
        private final DisplayTransform[] displayTransforms = new DisplayTransform[8];
        private Identifier textureId;
        private RenderLayer renderLayer;
        private float[] bakedMesh = new float[0];
        private int vertexCount;
        private boolean loaded;

        private RuntimeSwordModel(String modelName) {
            this.modelPath = MODEL_ROOT + modelName + ".json";
        }

        private void clear() {
            loaded = false;
            textureId = null;
            renderLayer = null;
            bakedMesh = new float[0];
            vertexCount = 0;
            cubes.clear();
            for (int i = 0; i < displayTransforms.length; i++) displayTransforms[i] = null;
        }

        private boolean render(ItemStack stack,
                               ModelTransformationMode transformType,
                               MatrixStack matrixStack,
                               VertexConsumerProvider buffer,
                               int combinedLight,
                               int combinedOverlay) {
            loadIfNeeded();
            if (vertexCount == 0 || renderLayer == null) return false;

            VertexConsumer consumer = stack.hasGlint()
                    ? ItemRenderer.getItemGlintConsumer(buffer, renderLayer, true, true)
                    : buffer.getBuffer(renderLayer);

            matrixStack.push();
            applyDisplayTransform(matrixStack, transformType);
            MatrixStack.Entry entry = matrixStack.peek();
            drawMesh(consumer, entry.getPositionMatrix(), entry.getNormalMatrix(), combinedLight, combinedOverlay);
            matrixStack.pop();
            return true;
        }

        private void loadIfNeeded() {
            if (loaded) return;
            loaded = true;
            try (InputStream input = mc.getResourceManager().open(Identifier.of(NAMESPACE, modelPath));
                 Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                readModel(root);
            } catch (Exception e) {
                System.err.println("[ItemReplacer] Failed to load model " + modelPath + ": " + e.getMessage());
            }
        }

        private void readModel(JsonObject root) {
            JsonObject texturesObj = root.getAsJsonObject("textures");
            if (texturesObj == null) return;

            Map<String, String> textureMap = new HashMap<>();
            for (Map.Entry<String, JsonElement> e : texturesObj.entrySet())
                textureMap.put(e.getKey(), e.getValue().getAsString());

            String primaryKey = resolvePrimaryTextureKey(textureMap, root);
            String primaryPath = textureMap.get(primaryKey);
            if (primaryPath == null) return;

            String texName = primaryPath.contains("/")
                    ? primaryPath.substring(primaryPath.lastIndexOf('/') + 1)
                    : primaryPath;

            textureId = Identifier.of(NAMESPACE, TEXTURE_ROOT + texName + ".png");
            renderLayer = hasPartialAlpha(textureId)
                    ? RenderLayer.getItemEntityTranslucentCull(textureId)
                    : RenderLayer.getEntityCutout(textureId);

            JsonArray elements = root.getAsJsonArray("elements");
            if (elements == null) return;

            String primaryRef = "#" + primaryKey;
            for (JsonElement el : elements) {
                JsonObject obj = el.getAsJsonObject();
                JsonArray from = obj.getAsJsonArray("from");
                JsonArray to = obj.getAsJsonArray("to");
                if (from == null || to == null) continue;

                ModelCube cube = new ModelCube(
                        from.get(0).getAsFloat() * PIXEL, from.get(1).getAsFloat() * PIXEL, from.get(2).getAsFloat() * PIXEL,
                        to.get(0).getAsFloat() * PIXEL,   to.get(1).getAsFloat() * PIXEL,   to.get(2).getAsFloat() * PIXEL);

                JsonObject faces = obj.getAsJsonObject("faces");
                if (faces == null || !readFaces(cube, faces, primaryRef)) continue;
                if (obj.has("rotation")) readRotation(cube, obj.getAsJsonObject("rotation"));
                cubes.add(cube);
            }
            bakeMesh();
            readDisplayTransforms(root);
        }

        private boolean readFaces(ModelCube cube, JsonObject faces, String primaryRef) {
            boolean has = false;
            if (faces.has("north")) { cube.north = readFace(faces.getAsJsonObject("north"), primaryRef); has |= cube.north != null; }
            if (faces.has("south")) { cube.south = readFace(faces.getAsJsonObject("south"), primaryRef); has |= cube.south != null; }
            if (faces.has("east"))  { cube.east  = readFace(faces.getAsJsonObject("east"),  primaryRef); has |= cube.east  != null; }
            if (faces.has("west"))  { cube.west  = readFace(faces.getAsJsonObject("west"),  primaryRef); has |= cube.west  != null; }
            if (faces.has("up"))    { cube.up    = readFace(faces.getAsJsonObject("up"),    primaryRef); has |= cube.up    != null; }
            if (faces.has("down"))  { cube.down  = readFace(faces.getAsJsonObject("down"),  primaryRef); has |= cube.down  != null; }
            return has;
        }

        private FaceUv readFace(JsonObject face, String primaryRef) {
            if (face.has("texture") && !face.get("texture").getAsString().equals(primaryRef)) return null;
            JsonArray uv = face.getAsJsonArray("uv");
            if (uv == null || uv.size() < 4) return null;
            return new FaceUv(
                    uv.get(0).getAsFloat() * PIXEL, uv.get(1).getAsFloat() * PIXEL,
                    uv.get(2).getAsFloat() * PIXEL, uv.get(3).getAsFloat() * PIXEL);
        }

        private void readRotation(ModelCube cube, JsonObject rotation) {
            if (rotation == null || !rotation.has("origin") || !rotation.has("axis") || !rotation.has("angle")) return;
            JsonArray origin = rotation.getAsJsonArray("origin");
            cube.rotationOriginX = origin.get(0).getAsFloat() * PIXEL;
            cube.rotationOriginY = origin.get(1).getAsFloat() * PIXEL;
            cube.rotationOriginZ = origin.get(2).getAsFloat() * PIXEL;
            cube.setRotation(rotation.get("axis").getAsString(), rotation.get("angle").getAsFloat());
        }

        private String resolvePrimaryTextureKey(Map<String, String> textureMap, JsonObject root) {
            if (textureMap.containsKey("0")) return "0";
            Map<String, Integer> useCount = new HashMap<>();
            JsonArray elements = root.getAsJsonArray("elements");
            if (elements != null) {
                for (JsonElement el : elements) {
                    JsonObject faces = el.getAsJsonObject().getAsJsonObject("faces");
                    if (faces == null) continue;
                    for (Map.Entry<String, JsonElement> fe : faces.entrySet()) {
                        JsonObject f = fe.getValue().getAsJsonObject();
                        if (!f.has("texture")) continue;
                        String ref = f.get("texture").getAsString().replace("#", "");
                        useCount.merge(ref, 1, Integer::sum);
                    }
                }
            }
            String best = null; int bestCount = -1;
            for (Map.Entry<String, Integer> e : useCount.entrySet()) {
                if (!textureMap.containsKey(e.getKey()) || "particle".equals(e.getKey()) || e.getValue() <= bestCount) continue;
                best = e.getKey(); bestCount = e.getValue();
            }
            if (best != null) return best;
            for (String key : textureMap.keySet()) if (!"particle".equals(key)) return key;
            return textureMap.keySet().iterator().next();
        }

        private boolean hasPartialAlpha(Identifier id) {
            try (InputStream input = mc.getResourceManager().open(id);
                 NativeImage image = NativeImage.read(input)) {
                for (int y = 0; y < image.getHeight(); y++)
                    for (int x = 0; x < image.getWidth(); x++) {
                        int a = (image.getColorArgb(x, y) >> 24) & 0xFF;
                        if (a > 0 && a < 255) return true;
                    }
                return false;
            } catch (Exception ignored) { return true; }
        }

        private void bakeMesh() {
            int faceCount = 0;
            for (ModelCube c : cubes) faceCount += c.countFaces();
            float[] baked = new float[faceCount * 4 * VERTEX_STRIDE];
            int offset = 0;
            for (ModelCube c : cubes) offset = bakeCube(baked, offset, c);
            bakedMesh = baked;
            vertexCount = offset / VERTEX_STRIDE;
            cubes.clear();
            cubes.trimToSize();
        }

        private int bakeCube(float[] out, int offset, ModelCube cube) {
            float x1 = cube.x1, y1 = cube.y1, z1 = cube.z1;
            float x2 = cube.x2, y2 = cube.y2, z2 = cube.z2;
            FaceUv f;
            if (cube.north != null) { f = cube.north;
                offset = writeBakedVertex(out,offset,cube,x2,y1,z1,f.u1,f.v2,0,0,-1);
                offset = writeBakedVertex(out,offset,cube,x1,y1,z1,f.u2,f.v2,0,0,-1);
                offset = writeBakedVertex(out,offset,cube,x1,y2,z1,f.u2,f.v1,0,0,-1);
                offset = writeBakedVertex(out,offset,cube,x2,y2,z1,f.u1,f.v1,0,0,-1); }
            if (cube.south != null) { f = cube.south;
                offset = writeBakedVertex(out,offset,cube,x1,y1,z2,f.u1,f.v2,0,0,1);
                offset = writeBakedVertex(out,offset,cube,x2,y1,z2,f.u2,f.v2,0,0,1);
                offset = writeBakedVertex(out,offset,cube,x2,y2,z2,f.u2,f.v1,0,0,1);
                offset = writeBakedVertex(out,offset,cube,x1,y2,z2,f.u1,f.v1,0,0,1); }
            if (cube.east != null) { f = cube.east;
                offset = writeBakedVertex(out,offset,cube,x2,y1,z2,f.u1,f.v2,1,0,0);
                offset = writeBakedVertex(out,offset,cube,x2,y1,z1,f.u2,f.v2,1,0,0);
                offset = writeBakedVertex(out,offset,cube,x2,y2,z1,f.u2,f.v1,1,0,0);
                offset = writeBakedVertex(out,offset,cube,x2,y2,z2,f.u1,f.v1,1,0,0); }
            if (cube.west != null) { f = cube.west;
                offset = writeBakedVertex(out,offset,cube,x1,y1,z1,f.u1,f.v2,-1,0,0);
                offset = writeBakedVertex(out,offset,cube,x1,y1,z2,f.u2,f.v2,-1,0,0);
                offset = writeBakedVertex(out,offset,cube,x1,y2,z2,f.u2,f.v1,-1,0,0);
                offset = writeBakedVertex(out,offset,cube,x1,y2,z1,f.u1,f.v1,-1,0,0); }
            if (cube.up != null) { f = cube.up;
                offset = writeBakedVertex(out,offset,cube,x1,y2,z1,f.u1,f.v1,0,1,0);
                offset = writeBakedVertex(out,offset,cube,x1,y2,z2,f.u1,f.v2,0,1,0);
                offset = writeBakedVertex(out,offset,cube,x2,y2,z2,f.u2,f.v2,0,1,0);
                offset = writeBakedVertex(out,offset,cube,x2,y2,z1,f.u2,f.v1,0,1,0); }
            if (cube.down != null) { f = cube.down;
                offset = writeBakedVertex(out,offset,cube,x1,y1,z2,f.u1,f.v1,0,-1,0);
                offset = writeBakedVertex(out,offset,cube,x1,y1,z1,f.u1,f.v2,0,-1,0);
                offset = writeBakedVertex(out,offset,cube,x2,y1,z1,f.u2,f.v2,0,-1,0);
                offset = writeBakedVertex(out,offset,cube,x2,y1,z2,f.u2,f.v1,0,-1,0); }
            return offset;
        }

        private int writeBakedVertex(float[] out, int offset, ModelCube cube,
                                     float x, float y, float z, float u, float v,
                                     float nx, float ny, float nz) {
            if (cube.rotationAxis != ModelCube.NO_ROTATION) {
                float dx = x - cube.rotationOriginX;
                float dy = y - cube.rotationOriginY;
                float dz = z - cube.rotationOriginZ;
                float sin = cube.rotationSin, cos = cube.rotationCos;
                switch (cube.rotationAxis) {
                    case ModelCube.ROTATE_X: {
                        y = cube.rotationOriginY + dy * cos - dz * sin;
                        z = cube.rotationOriginZ + dy * sin + dz * cos;
                        float rny = ny * cos - nz * sin; nz = ny * sin + nz * cos; ny = rny; break; }
                    case ModelCube.ROTATE_Y: {
                        x = cube.rotationOriginX + dx * cos + dz * sin;
                        z = cube.rotationOriginZ - dx * sin + dz * cos;
                        float rnx = nx * cos + nz * sin; nz = -nx * sin + nz * cos; nx = rnx; break; }
                    case ModelCube.ROTATE_Z: {
                        x = cube.rotationOriginX + dx * cos - dy * sin;
                        y = cube.rotationOriginY + dx * sin + dy * cos;
                        float rnx = nx * cos - ny * sin; ny = nx * sin + ny * cos; nx = rnx; break; }
                }
            }
            out[offset++] = x - MODEL_CENTER;
            out[offset++] = y - MODEL_CENTER;
            out[offset++] = z - MODEL_CENTER;
            out[offset++] = u;
            out[offset++] = v;
            out[offset++] = nx;
            out[offset++] = ny;
            out[offset++] = nz;
            return offset;
        }

        private void readDisplayTransforms(JsonObject root) {
            if (!root.has("display")) return;
            JsonObject d = root.getAsJsonObject("display");
            displayTransforms[0] = readDT(d, "thirdperson_righthand");
            displayTransforms[1] = readDT(d, "thirdperson_lefthand");
            displayTransforms[2] = readDT(d, "firstperson_righthand");
            displayTransforms[3] = readDT(d, "firstperson_lefthand");
            displayTransforms[4] = readDT(d, "ground");
            displayTransforms[5] = readDT(d, "gui");
            displayTransforms[6] = readDT(d, "head");
            displayTransforms[7] = readDT(d, "fixed");
        }

        private DisplayTransform readDT(JsonObject display, String key) {
            if (!display.has(key)) return null;
            JsonObject obj = display.getAsJsonObject(key);
            float[] rot = {0,0,0}, trans = {0,0,0}, scale = {1,1,1};
            if (obj.has("rotation"))    readFA(obj.getAsJsonArray("rotation"), rot);
            if (obj.has("translation")) readFA(obj.getAsJsonArray("translation"), trans);
            if (obj.has("scale"))       readFA(obj.getAsJsonArray("scale"), scale);
            return new DisplayTransform(rot, trans, scale);
        }

        private void readFA(JsonArray arr, float[] target) {
            for (int i = 0; i < target.length && i < arr.size(); i++) target[i] = arr.get(i).getAsFloat();
        }

        private void applyDisplayTransform(MatrixStack ms, ModelTransformationMode type) {
            DisplayTransform dt = getDisplayTransform(type);
            if (dt == null) return;
            boolean left = type == ModelTransformationMode.THIRD_PERSON_LEFT_HAND
                    || type == ModelTransformationMode.FIRST_PERSON_LEFT_HAND;
            dt.apply(ms, left);
        }

        private DisplayTransform getDisplayTransform(ModelTransformationMode type) {
            return switch (type) {
                case THIRD_PERSON_RIGHT_HAND -> displayTransforms[0];
                case THIRD_PERSON_LEFT_HAND  -> displayTransforms[1];
                case FIRST_PERSON_RIGHT_HAND -> displayTransforms[2];
                case FIRST_PERSON_LEFT_HAND  -> displayTransforms[3];
                case GROUND                  -> displayTransforms[4];
                case GUI                     -> displayTransforms[5];
                case HEAD                    -> displayTransforms[6];
                case FIXED                   -> displayTransforms[7];
                default                      -> null;
            };
        }

        private void drawMesh(VertexConsumer vc, Matrix4f matrix, Matrix3f normalMatrix, int light, int overlay) {
            int limit = vertexCount * VERTEX_STRIDE;
            org.joml.Vector4f pos = new org.joml.Vector4f();
            org.joml.Vector3f norm = new org.joml.Vector3f();
            for (int i = 0; i < limit; i += VERTEX_STRIDE) {
                pos.set(bakedMesh[i], bakedMesh[i+1], bakedMesh[i+2], 1.0f);
                matrix.transform(pos);
                norm.set(bakedMesh[i+5], bakedMesh[i+6], bakedMesh[i+7]);
                normalMatrix.transform(norm);
                vc.vertex(pos.x, pos.y, pos.z)
                  .color(255, 255, 255, 255)
                  .texture(bakedMesh[i+3], bakedMesh[i+4])
                  .overlay(overlay)
                  .light(light)
                  .normal(norm.x, norm.y, norm.z);
            }
        }
    }

    // ─── DisplayTransform ───────────────────────────────────────────────────────

    private static final class DisplayTransform {
        private final float txN, txM, ty, tz;
        private final float rx, ry, rz;
        private final float sx, sy, sz;
        private final boolean hasTrans, hasRot, hasScale;

        private DisplayTransform(float[] rotation, float[] translation, float[] scale) {
            this.txN = translation[0] / 16.0f;
            this.txM = -this.txN;
            this.ty  = translation[1] / 16.0f;
            this.tz  = translation[2] / 16.0f;
            this.rx  = rotation[0];
            this.ry  = rotation[1];
            this.rz  = rotation[2];
            this.sx  = scale[0];
            this.sy  = scale[1];
            this.sz  = scale[2];
            this.hasTrans = translation[0] != 0 || translation[1] != 0 || translation[2] != 0;
            this.hasRot   = rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0;
            this.hasScale = scale[0] != 1 || scale[1] != 1 || scale[2] != 1;
        }

        private void apply(MatrixStack ms, boolean leftHand) {
            if (hasTrans) ms.translate(leftHand ? txM : txN, ty, tz);
            if (hasRot) {
                float yaw   = leftHand ? -ry : ry;
                float pitch = leftHand ? -rx : rx;
                float roll  = rz;
                if (yaw   != 0) ms.multiply(new org.joml.Quaternionf().rotationY(org.joml.Math.toRadians(yaw)));
                if (pitch != 0) ms.multiply(new org.joml.Quaternionf().rotationX(org.joml.Math.toRadians(pitch)));
                if (roll  != 0) ms.multiply(new org.joml.Quaternionf().rotationZ(org.joml.Math.toRadians(roll)));
            }
            if (hasScale) ms.scale(sx, sy, sz);
        }
    }

    // ─── ModelCube ──────────────────────────────────────────────────────────────

    private static final class ModelCube {
        static final int NO_ROTATION = 0, ROTATE_X = 1, ROTATE_Y = 2, ROTATE_Z = 3;
        final float x1, y1, z1, x2, y2, z2;
        FaceUv north, south, east, west, up, down;
        float rotationOriginX, rotationOriginY, rotationOriginZ;
        int rotationAxis = NO_ROTATION;
        float rotationSin, rotationCos = 1.0f;

        ModelCube(float x1,float y1,float z1,float x2,float y2,float z2) {
            this.x1=x1; this.y1=y1; this.z1=z1; this.x2=x2; this.y2=y2; this.z2=z2;
        }

        int countFaces() {
            int n = 0;
            if (north!=null)n++; if (south!=null)n++; if (east!=null)n++;
            if (west!=null)n++;  if (up!=null)n++;    if (down!=null)n++;
            return n;
        }

        void setRotation(String axis, float angle) {
            switch (axis) {
                case "x" -> rotationAxis = ROTATE_X;
                case "y" -> rotationAxis = ROTATE_Y;
                case "z" -> rotationAxis = ROTATE_Z;
                default  -> { rotationAxis = NO_ROTATION; return; }
            }
            float rad = (float) Math.toRadians(angle);
            rotationSin = (float) Math.sin(rad);
            rotationCos = (float) Math.cos(rad);
        }
    }

    // ─── FaceUv ─────────────────────────────────────────────────────────────────

    private static final class FaceUv {
        final float u1, v1, u2, v2;
        FaceUv(float u1,float v1,float u2,float v2) { this.u1=u1; this.v1=v1; this.u2=u2; this.v2=v2; }
    }
}
