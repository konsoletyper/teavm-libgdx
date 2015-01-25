package org.teavm.libgdx.emu;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.utils.BufferUtils;

public class VertexArrayEmulator implements VertexData {
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    int bufferHandle;
    final boolean isStatic;
    final int usage;
    boolean isDirty = false;
    boolean isBound = false;

    public VertexArrayEmulator(int numVertices, VertexAttribute... attributes) {
        this(numVertices, new VertexAttributes(attributes));
    }

    /**
     * Constructs a new interleaved VertexArray
     * 
     * @param numVertices the maximum number of vertices
     * @param attributes the {@link VertexAttributes}
     */
    public VertexArrayEmulator(int numVertices, VertexAttributes attributes) {
        this(false, numVertices, attributes);
    }

    /**
     * Constructs a new interleaved VertexBufferObject.
     * 
     * @param isStatic whether the vertex data is static.
     * @param numVertices the maximum number of vertices
     * @param attributes the {@link VertexAttribute}s.
     */
    public VertexArrayEmulator(boolean isStatic, int numVertices, VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }

    /**
     * Constructs a new interleaved VertexBufferObject.
     * 
     * @param isStatic whether the vertex data is static.
     * @param numVertices the maximum number of vertices
     * @param attributes the {@link VertexAttributes}.
     */
    public VertexArrayEmulator(boolean isStatic, int numVertices, VertexAttributes attributes) {
        this.isStatic = isStatic;
        this.attributes = attributes;

        byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        buffer = byteBuffer.asFloatBuffer();
        buffer.flip();
        byteBuffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
    }

    @Override
    public VertexAttributes getAttributes() {
        return attributes;
    }

    @Override
    public int getNumVertices() {
        return buffer.limit() * 4 / attributes.vertexSize;
    }

    @Override
    public int getNumMaxVertices() {
        return byteBuffer.capacity() / attributes.vertexSize;
    }

    @Override
    public FloatBuffer getBuffer() {
        isDirty = true;
        return buffer;
    }

    private void bufferChanged() {
        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
            isDirty = false;
        }
    }

    @Override
    public void setVertices(float[] vertices, int offset, int count) {
        isDirty = true;
        BufferUtils.copy(vertices, byteBuffer, count, offset);
        buffer.position(0);
        buffer.limit(count);
        bufferChanged();
    }

    @Override
    public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
        isDirty = true;
        final int pos = byteBuffer.position();
        byteBuffer.position(targetOffset * 4);
        BufferUtils.copy(vertices, sourceOffset, count, byteBuffer);
        byteBuffer.position(pos);
        buffer.position(0);
        bufferChanged();
    }

    /**
     * Binds this VertexBufferObject for rendering via glDrawArrays or
     * glDrawElements
     * 
     * @param shader the shader
     */
    @Override
    public void bind(ShaderProgram shader) {
        bind(shader, null);
    }

    @Override
    public void bind(ShaderProgram shader, int[] locations) {
        final GL20 gl = Gdx.gl20;

        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
        if (isDirty) {
            byteBuffer.limit(buffer.limit() * 4);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
            isDirty = false;
        }

        final int numAttributes = attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = shader.getAttributeLocation(attribute.alias);
                if (location < 0)
                    continue;
                shader.enableVertexAttribute(location);

                shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
            }

        } else {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = locations[i];
                if (location < 0)
                    continue;
                shader.enableVertexAttribute(location);

                shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
            }
        }
        isBound = true;
    }

    /**
     * Unbinds this VertexBufferObject.
     * 
     * @param shader the shader
     */
    @Override
    public void unbind(final ShaderProgram shader) {
        unbind(shader, null);
    }

    @Override
    public void unbind(final ShaderProgram shader, final int[] locations) {
        final GL20 gl = Gdx.gl20;
        final int numAttributes = attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                shader.disableVertexAttribute(attributes.get(i).alias);
            }
        } else {
            for (int i = 0; i < numAttributes; i++) {
                final int location = locations[i];
                if (location >= 0)
                    shader.disableVertexAttribute(location);
            }
        }
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        isBound = false;
    }

    /**
     * Invalidates the VertexBufferObject so a new OpenGL buffer handle is
     * created. Use this in case of a context loss.
     */
    public void invalidate() {
        bufferHandle = Gdx.gl20.glGenBuffer();
        isDirty = true;
    }

    /** Disposes of all resources this VertexBufferObject uses. */
    @Override
    public void dispose() {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(bufferHandle);
        bufferHandle = 0;
        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
    }
}
