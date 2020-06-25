package com.github.zi_jing.cuckoolib.util;

import com.github.zi_jing.cuckoolib.client.render.GLUtils;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExternalImageTexture extends AbstractTexture {

    private BufferedImage image;

    public ExternalImageTexture(File file) throws FileNotFoundException, IOException {
        this.image = TextureUtil.readBufferedImage(new FileInputStream(file));
    }

    public ExternalImageTexture(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        GLUtils.deleteTempTexture();
        TextureUtil.uploadTextureImageAllocate(GLUtils.tmpGlTextureId, image, false, false);
    }

}