package org.teavm.libgdx.controllers.plugin;

import com.badlogic.gdx.controllers.Controllers;
import org.teavm.common.Mapper;
import org.teavm.diagnostics.Diagnostics;
import org.teavm.libgdx.controllers.emu.ControllersEmulator;
import org.teavm.model.*;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;

public class OverlayTransformer implements ClassHolderTransformer {
    @Override
    public void transformClass(ClassHolder cls, ClassReaderSource innerSource, Diagnostics diagnostics) {
        if (cls.getName().equals(Controllers.class.getName())) {
            transformControllers(cls, innerSource);
        }
    }

    private void transformControllers(final ClassHolder cls, ClassReaderSource classSource) {
        MethodDescriptor desc = new MethodDescriptor("initialize", void.class);
        cls.removeMethod(cls.getMethod(desc));
        ClassReader patchClass = classSource.get(ControllersEmulator.class.getName());
        MethodHolder patch = ModelUtils.copyMethod(patchClass.getMethod(desc));
        ClassRefsRenamer renamer = new ClassRefsRenamer(new Mapper<String, String>() {
            @Override
            public String map(String preimage) {
                if (preimage.equals(ControllersEmulator.class.getName())) {
                    return Controllers.class.getName();
                }
                return preimage;
            }
        });
        cls.addMethod(renamer.rename(patch));
    }
}
