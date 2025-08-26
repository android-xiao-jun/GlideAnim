package com

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.Registry
import com.bumptech.glide.Registry.BUCKET_ANIMATION
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import com.github.penfeizhou.animation.decode.FrameSeqDecoder
import com.github.penfeizhou.animation.glide.ByteBufferAnimationDecoder
import com.github.penfeizhou.animation.glide.FrameBitmapTranscoder
import com.github.penfeizhou.animation.glide.FrameDrawableTranscoder
import com.github.penfeizhou.animation.glide.StreamAnimationDecoder
import com.opensource.svgaplayer.SVGACache
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.glideplugin.SVGAAssetLoaderFactory
import com.opensource.svgaplayer.glideplugin.SVGADrawableTranscoder
import com.opensource.svgaplayer.glideplugin.SVGAEntityFileDecoder
import com.opensource.svgaplayer.glideplugin.SVGAEntityStreamDecoder
import com.opensource.svgaplayer.glideplugin.SVGAFileEncoder
import com.opensource.svgaplayer.glideplugin.SVGAImageViewTargetFactory
import com.opensource.svgaplayer.glideplugin.SVGAResourceLoaderFactory
import com.opensource.svgaplayer.glideplugin.SVGAStringLoaderFactory
import com.opensource.svgaplayer.glideplugin.SVGAUriLoaderFactory
import com.opensource.svgaplayer.glideplugin.SVGAUriResourceLoaderFactory
import com.opensource.svgaplayer.glideplugin.SVGAUrlLoaderFactory
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * @author YvesCheung
 * 2018/11/26
 */
@GlideModule
class GlideAnimModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        hookTheImageViewFactory(glide)
        val resources = context.resources
        SVGACache.onCreate(context)
        //cachePath is equals to SVGACache.cacheDir
        val cachePath = context.cacheDir.absolutePath + File.separatorChar + "svga"
        val streamDecoder = SVGAEntityStreamDecoder(cachePath, glide.arrayPool)
        val resourceFactory = SVGAResourceLoaderFactory(resources, cachePath, registry::getRewinder)
        registry
            .register(SVGAVideoEntity::class.java, SVGADrawable::class.java, SVGADrawableTranscoder())
            .append(BUCKET_ANIMATION, InputStream::class.java, SVGAVideoEntity::class.java,
                streamDecoder)
            .append(BUCKET_ANIMATION, File::class.java, SVGAVideoEntity::class.java,
                SVGAEntityFileDecoder(glide.arrayPool)
            )
            // int/Uri for R.raw.resourceId
            .append(Int::class.java, File::class.java, resourceFactory)
            .append(Int::class.javaObjectType, File::class.java, resourceFactory)
            .append(Uri::class.java, InputStream::class.java, SVGAUriResourceLoaderFactory())
            // Uri for file://android_asset
            .append(Uri::class.java, File::class.java, SVGAAssetLoaderFactory(cachePath, registry::getRewinder))
            // String/Uri/GlideUrl for http:/https:
            .append(String::class.java, File::class.java, SVGAStringLoaderFactory())
            .append(Uri::class.java, File::class.java, SVGAUriLoaderFactory())
            .append(GlideUrl::class.java, File::class.java, SVGAUrlLoaderFactory(cachePath, registry::getRewinder))
            // encode to disk
            .append(File::class.java, SVGAFileEncoder())


        val byteBufferAnimationDecoder = ByteBufferAnimationDecoder()
        val streamAnimationDecoder = StreamAnimationDecoder(byteBufferAnimationDecoder)
        registry.prepend(
            InputStream::class.java,
            FrameSeqDecoder::class.java, streamAnimationDecoder
        )
        registry.prepend(
            ByteBuffer::class.java,
            FrameSeqDecoder::class.java, byteBufferAnimationDecoder
        )
        registry.register(
            FrameSeqDecoder::class.java,
            Drawable::class.java, FrameDrawableTranscoder()
        )
        registry.register(
            FrameSeqDecoder::class.java,
            Bitmap::class.java, FrameBitmapTranscoder(glide.bitmapPool)
        )
    }

    private fun hookTheImageViewFactory(glide: Glide) {
        try {
            val imageFactory = GlideContext::class.java.getDeclaredField("imageViewTargetFactory")
                ?: return
            val glideContext = Glide::class.java.getDeclaredField("glideContext")
                ?: return
            glideContext.isAccessible = true
            imageFactory.isAccessible = true

            imageFactory.set(glideContext.get(glide), SVGAImageViewTargetFactory())

        } catch (e: Exception) {
            Log.e("SVGAPlayer", e.message, e)
        }
    }
}

