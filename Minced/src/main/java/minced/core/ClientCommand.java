/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.StringReader;
import java.util.Arrays;
import java.util.List;
import minced.core.ChatUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public abstract class ClientCommand
implements ArgumentType {
    static String Уь;

    List<String> field1235;
    static String field1236;

    public abstract void run(LiteralArgumentBuilder var1);

    @Override
    public String parse(StringReader reader) throws com.mojang.brigadier.exceptions.CommandSyntaxException { return reader.readUnquotedString(); }

    public String method416() {
        return (String)this.field1235.get(0);
    }

    public static RequiredArgumentBuilder method417(String param0, ArgumentType param1) {
        return RequiredArgumentBuilder.argument((String)param0, (ArgumentType)param1);
    }

    public ClientCommand(String ... param1) {
        this.field1235 = Arrays.asList(param1);
    }

    public void method418(CommandDispatcher param1) {
        for (String v3 : this.field1235) {
            LiteralArgumentBuilder v4 = LiteralArgumentBuilder.literal((String)v3);
            this.run(v4);
            param1.register(v4);
        }
    }

    protected int method419(CommandContext param1) {
        String v2 = param1.getInput();
        MutableText v3 = Text.literal((String)((Object)_4ZXYEo.в("makeConcatWithConstants", new Object[]{}, (String)v2))).setStyle(Style.EMPTY.withColor(Formatting.RED));
        ChatUtil.method3308(v3, new Object[0]);
        MutableText v4 = Text.literal((String)field1236).setStyle(Style.EMPTY.withColor(Formatting.RED));
        ChatUtil.method3308(v4, new Object[0]);
        return 1;
    }

    public static LiteralArgumentBuilder method420(String param0) {
        return LiteralArgumentBuilder.literal((String)param0);
    }

    static {
        Уь = "Неверно указаны аргументы - \"\u0001\"";
        field1236 = "Для помощи - .help";
    }
}



