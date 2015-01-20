package org.teavm.libgdx.plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.teavm.diagnostics.Diagnostics;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassReader;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.FieldHolder;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReader;
import org.teavm.model.util.ModelUtils;
import com.badlogic.gdx.assets.loaders.TextureLoader;

public class OverlayTransformer implements ClassHolderTransformer {
    private static final Set<String> classesToEmulate = new HashSet<>(Arrays.asList(TextureLoader.class.getName()));

    @Override
    public void transformClass(ClassHolder cls, ClassReaderSource innerSource, Diagnostics diagnostics) {
        if (!classesToEmulate.contains(cls.getName())) {
            return;
        }
        for (FieldHolder field : cls.getFields().toArray(new FieldHolder[0])) {
            cls.removeField(field);
        }
        for (MethodHolder method : cls.getMethods().toArray(new MethodHolder[0])) {
            cls.removeMethod(method);
        }
        String mappedName = "org.teavm.libgdx.emu" + cls.getName().substring("com.badlogic.gdx".length());
        ClassReader emuClass = innerSource.get(mappedName);
        for (FieldReader field : emuClass.getFields()) {
            cls.addField(ModelUtils.copyField(field));
        }
        for (MethodReader method : emuClass.getMethods()) {
            cls.addMethod(ModelUtils.copyMethod(method));
        }
    }
}
