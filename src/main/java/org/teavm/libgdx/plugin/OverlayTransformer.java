package org.teavm.libgdx.plugin;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.teavm.diagnostics.Diagnostics;
import org.teavm.libgdx.emu.BufferUtilsEmulator;
import org.teavm.libgdx.emu.TextureDataEmulator;
import org.teavm.model.*;
import org.teavm.model.util.ModelUtils;

public class OverlayTransformer implements ClassHolderTransformer {
    @Override
    public void transformClass(ClassHolder cls, ClassReaderSource innerSource, Diagnostics diagnostics) {
        if (cls.getName().equals(BufferUtils.class.getName())) {
            transformBufferUtils(cls, innerSource);
        } else if (cls.getName().equals(TextureData.Factory.class.getName())) {
            transformTextureData(cls, innerSource);
        } else if (cls.getName().equals(FileHandle.class.getName())) {

        }
    }

    private void transformBufferUtils(ClassHolder cls, ClassReaderSource innerSource) {
        List<MethodDescriptor> descList = new ArrayList<>();
        descList.add(new MethodDescriptor("freeMemory", ByteBuffer.class, void.class));
        descList.add(new MethodDescriptor("newDisposableByteBuffer", int.class, ByteBuffer.class));
        replaceMethods(cls, BufferUtilsEmulator.class, innerSource, descList);
    }

    private void transformTextureData(ClassHolder cls, ClassReaderSource innerSource) {
        List<MethodDescriptor> descList = new ArrayList<>();
        descList.add(new MethodDescriptor("loadFromFile", FileHandle.class, Format.class, boolean.class,
                TextureData.class));
        replaceMethods(cls, TextureDataEmulator.class, innerSource, descList);
    }

    private void replaceMethods(ClassHolder cls, Class<?> emuType, ClassReaderSource innerSource,
            List<MethodDescriptor> descList) {
        ClassReader emuCls = innerSource.get(emuType.getName());
        for (MethodDescriptor methodDesc : descList) {
            cls.removeMethod(cls.getMethod(methodDesc));
            cls.addMethod(ModelUtils.copyMethod(emuCls.getMethod(methodDesc)));
        }
    }
}
