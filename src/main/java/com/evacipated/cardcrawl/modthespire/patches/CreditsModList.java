package com.evacipated.cardcrawl.modthespire.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ReflectionHelper;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.credits.CreditLine;
import com.megacrit.cardcrawl.credits.CreditsScreen;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        cls="com.megacrit.cardcrawl.credits.CreditsScreen",
        method="ctor"
)
public class CreditsModList {
    private static String[] MTS_AUTHORS = new String[] {
            "kiooeht",
            "t-larson",
            "test447"
    };

    @SpireInsertPatch(
            rloc=5,
            localvars={"tmpY"}
    )
    public static void Insert(Object __obj_instance, @ByRef float[] tmpY)
    {
        try {
            Field f = CreditsScreen.class.getDeclaredField("lines");
            f.setAccessible(true);
            ArrayList<CreditLine> lines = (ArrayList<CreditLine>)f.get(__obj_instance);

            // ModTheSpire
            lines.add(new CreditLine("ModTheSpire", tmpY[0] -= 150.0F, true));
            // ModTheSpire authors
            for (String author : MTS_AUTHORS) {
                lines.add(new CreditLine(author, tmpY[0] -= 45.0F, false));
            }
            // Loaded mods
            for (ModInfo info : Loader.MODINFOS) {
                lines.add(new CreditLine(info.Name, tmpY[0] -= 150.0F, true));
                if (!info.Author.isEmpty()) {
                    String[] modAuthors = info.Author.split(",");
                    for (String author : modAuthors) {
                        lines.add(new CreditLine(author, tmpY[0] -= 45.0F, false));
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void Postfix(Object __obj_instance)
    {
        try {
            Field f = CreditsScreen.class.getDeclaredField("lines");
            f.setAccessible(true);
            ArrayList<CreditLine> lines = (ArrayList<CreditLine>) f.get(__obj_instance);

            int headers = 0;
            for (CreditLine line : lines) {
                Field color = CreditLine.class.getDeclaredField("color");
                color.setAccessible(true);
                if (color.get(line).equals(Settings.GOLD_COLOR)) {
                    ++headers;
                }
            }

            float NEW_END_OF_CREDITS_Y = 85.0F + (headers * 150.0F) + ((lines.size() - headers) * 45.0F);
            NEW_END_OF_CREDITS_Y *= Settings.scale;
            Field END_OF_CREDITS_Y = CreditsScreen.class.getDeclaredField("END_OF_CREDITS_Y");
            ReflectionHelper.setStaticFinalField(END_OF_CREDITS_Y, NEW_END_OF_CREDITS_Y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
