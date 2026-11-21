package dev.cyberware;

import com.mojang.blaze3d.vertex.VertexConsumer;

/** Wraps a vertex consumer and forces one flat colour on everything drawn through it. */
public class TintConsumer implements VertexConsumer {
    private final VertexConsumer delegate; // null = draw nothing
    private final int r, g, b, a;

    public TintConsumer(VertexConsumer delegate, int r, int g, int b, int a) {
        this.delegate = delegate; this.r = r; this.g = g; this.b = b; this.a = a;
    }

    @Override public VertexConsumer addVertex(float x, float y, float z) {
        if (delegate != null) delegate.addVertex(x, y, z);
        return this;
    }
    @Override public VertexConsumer setColor(int r2, int g2, int b2, int a2) {
        if (delegate != null) delegate.setColor(r, g, b, a);
        return this;
    }
    @Override public VertexConsumer setUv(float u, float v) {
        if (delegate != null) delegate.setUv(u, v);
        return this;
    }
    @Override public VertexConsumer setUv1(int u, int v) {
        if (delegate != null) delegate.setUv1(u, v);
        return this;
    }
    @Override public VertexConsumer setUv2(int u, int v) {
        if (delegate != null) delegate.setUv2(u, v);
        return this;
    }
    @Override public VertexConsumer setNormal(float x, float y, float z) {
        if (delegate != null) delegate.setNormal(x, y, z);
        return this;
    }
}
